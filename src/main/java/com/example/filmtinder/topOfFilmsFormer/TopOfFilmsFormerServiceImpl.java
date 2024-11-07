package com.example.filmtinder.topOfFilmsFormer;

import com.example.filmtinder.db.film.FilmEntity;
import com.example.filmtinder.db.genre.GenreEntity;
import com.example.filmtinder.db.genre.GenreService;
import com.example.filmtinder.parser.FilmInfoDto;
import com.example.filmtinder.utils.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class TopOfFilmsFormerServiceImpl implements TopOfFilmsFormerService {

    private final GenreService genreService;

    @Autowired
    public TopOfFilmsFormerServiceImpl(GenreService genreService) {
        this.genreService = genreService;
    }

    public List<FilmInfoDto> formGenreTop(GenreEntity genre) {
        return formGenreTop(genre.getName());
    }

    public List<FilmInfoDto> formGenreTop(String genreName) {
        if (Cache.NOT_POPULAR_GENRES.contains(genreName)) {
            log.warn("GenreEntity {} is not popular so its top of films will be ignored", genreName);
            return Collections.emptyList();
        }

        Optional<GenreEntity> genreO = genreService.getByName(genreName);
        AtomicReference<List<FilmInfoDto>> genreTop = new AtomicReference<>(Collections.emptyList());
        genreO.ifPresentOrElse(genre -> {
            List<FilmEntity> genreFilms = genre.getFilms();
            if (genreFilms.size() >= Cache.N_TOP_FILMS) {
                List<FilmInfoDto> topGenreFilms = genreFilms.subList(0, Cache.N_TOP_FILMS)
                        .stream()
                        .map(FilmInfoDto::convert)
                        .toList();
                genreTop.set(topGenreFilms);
                log.info("Formed top of films by genre {}", genre);
            } else {
                log.warn("Ignored genre {} - not enough films ({} < {})", genre, genreFilms.size(), Cache.N_TOP_FILMS);
            }
        }, () -> log.error("GenreEntity with name {} does not exist", genreName));
        return genreTop.get();
    }
}
