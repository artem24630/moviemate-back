package com.example.filmtinder.parser;

import com.example.filmtinder.db.film.FilmDto;
import com.example.filmtinder.db.film.FilmService;
import com.example.filmtinder.db.genre.GenreDto;
import com.example.filmtinder.db.genre.GenreEntity;
import com.example.filmtinder.db.genre.GenreRepository;
import com.example.filmtinder.db.genre.GenreService;
import com.example.filmtinder.openapi.kinopoisk.model.MovieDocsResponseDtoV13;
import com.example.filmtinder.openapi.kinopoisk.model.MovieDtoV13;
import com.example.filmtinder.openapi.kinopoisk.model.PossibleValueDto;
import com.example.filmtinder.utils.Cache;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ParserService {
    public static final int TOTAL_LIMIT = 10;
    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);
    public static final String X_API_KEY = "X-API-KEY";
    private final FilmService filmService;
    private final GenreService genreService;
    @Autowired
    private GenreRepository genreRepository;
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> future;

    @Value("${apiBaseUrl}")
    private String apiBaseUrl;

    @Value("${apiKey}")
    private String apiKey;

    @Autowired
    public ParserService(FilmService filmService, GenreService genreService) {
        this.filmService = filmService;
        this.genreService = genreService;
    }

    public void parseMePasha() {
        this.clearParsed();
        this.future = new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(this::parseFilms, 0, 365,
                TimeUnit.DAYS);
    }

    public void startParsing() {
        this.clearParsed();
        this.future = new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(this::stopParsing, 0, 365,
                TimeUnit.DAYS);
    }

    public void stopParsing() {
        if (future != null) {
            future.cancel(true);
        }
    }

    public void clearParsed() {
        filmService.deleteAll();
        genreService.deleteAll();
    }

    private void parseFilms() {
        RestTemplate rest = new RestTemplate();
        String url = apiBaseUrl + "/v1.3/movie?page={page}&limit={limit}&isSeries=false&selectFields=id&" +
                "selectFields=name&" +
                "selectFields=description&" +
                "selectFields=movieLength&" +
                "selectFields=rating&" +
                "selectFields=year&" +
                "selectFields=poster&" +
                "selectFields=sequelsAndPrequels&" +
                "selectFields=genres";

        HttpEntity<?> entity = getAuthHeaders(apiKey);

        final int limit = 100;
        Map<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));
        int page = 1;
        int pageTotal = -1;

        Map<String, GenreEntity> genres = getGenresMap();

        while (pageTotal < 0 || page <= Math.min(pageTotal, TOTAL_LIMIT)) {
            params.put("page", String.valueOf(page));
            ResponseEntity<MovieDocsResponseDtoV13> exchange = rest.exchange(url, HttpMethod.GET, entity,
                    MovieDocsResponseDtoV13.class, params);

            MovieDocsResponseDtoV13 movieResponse = checkMovieResponse(exchange);

            List<FilmDto> movies = getFilmDtos(movieResponse, genres);

            pageTotal = getPageTotal(movieResponse);

            filmService.createAll(movies);

            logger.info("Parsed {} films", movies.size());

            page++;
        }
    }

    private Map<String, GenreEntity> getGenresMap() {
        List<GenreEntity> allGenres = genreRepository.saveAll(Cache.VALID_GENRE_NAMES
                .stream()
                .map(gname -> new GenreEntity((long) gname.hashCode(), gname, Collections.emptyList()))
                .toList());
        Map<String, GenreEntity> genres = allGenres.stream().collect(Collectors.toMap(k -> k.getName(), v -> v));
        return genres;
    }


    @Nullable
    public static List<GenreDto> getGenreDtos(ResponseEntity<PossibleValueDto[]> exchange) {
        if (exchange == null) {
            return null;
        }
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            logger.warn("Failed to get genres, response: {}", exchange);
            return null;
        }

        PossibleValueDto[] possibleValueDtos = exchange.getBody();

        List<GenreDto> genreDtos = new ArrayList<>();
        for (PossibleValueDto genre : possibleValueDtos) {
            if (StringUtils.isEmpty(genre.getName())) {
                continue;
            }
            GenreDto genreDto = new GenreDto();
            genreDto.setName(genre.getName());
            genreDtos.add(genreDto);
        }
        return genreDtos;
    }

    public static int getPageTotal(MovieDocsResponseDtoV13 movieResponse) {
        return movieResponse == null ? 0 : movieResponse.getPages().intValue();
    }

    @Nullable
    public static MovieDocsResponseDtoV13 checkMovieResponse(ResponseEntity<MovieDocsResponseDtoV13> exchange) {
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            logger.warn("Failed to get films, response: {}", exchange);
            return null;
        }

        MovieDocsResponseDtoV13 movieResponse = exchange.getBody();

        if (movieResponse == null) {
            logger.warn("Empty body of response");
            return null;
        }

        return movieResponse;
    }

    @NotNull
    public static List<FilmDto> getFilmDtos(MovieDocsResponseDtoV13 movieResponse, Map<String, GenreEntity> genres) {
        if (movieResponse == null || genres == null) {
            return Collections.emptyList();
        }
        List<FilmDto> movies = new ArrayList<>();
        for (MovieDtoV13 movie : movieResponse.getDocs()) {
            if (movie.getSequelsAndPrequels() != null && !movie.getSequelsAndPrequels().isEmpty()) {
                continue;
            }

            FilmDto dto = new FilmDto();
            dto.setName(movie.getName());
            dto.setDescription(movie.getDescription());

            if (movie.getMovieLength() == null) {
                continue;
            }

            dto.setDuration(movie.getMovieLength().intValue());
            dto.setRating_kp(movie.getRating().getKp().floatValue());
            dto.setRating_imdb(movie.getRating().getImdb().floatValue());
            dto.setRelease_year(movie.getYear().intValue());
            dto.setPoster_url(movie.getPoster().getPreviewUrl());

            List<GenreEntity> genresList = null;
            if (movie.getGenres() != null) {
                genresList = movie.getGenres().stream()
                        .map(item -> genres.get(item.getName()))
                        .filter(Objects::nonNull).toList();
            }
            if (CollectionUtils.isEmpty(genresList)) {
                continue;
            }
            dto.setGenres(genresList);

            movies.add(dto);
        }
        return movies;
    }

    public static HttpEntity<?> getAuthHeaders(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Api key can't be empty");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_API_KEY, key);
        return new HttpEntity<>(headers);
    }

    //for tests
    public Future<?> getFuture() {
        return this.future;
    }
}
