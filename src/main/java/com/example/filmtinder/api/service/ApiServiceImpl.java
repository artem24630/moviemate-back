package com.example.filmtinder.api.service;

import com.example.filmtinder.api.transformer.LobbyTransformer;
import com.example.filmtinder.api.utils.ApiUtils;
import com.example.filmtinder.db.film.FilmEntity;
import com.example.filmtinder.db.film.FilmRepository;
import com.example.filmtinder.db.genre.GenreEntity;
import com.example.filmtinder.db.genre.GenreRepository;
import com.example.filmtinder.db.genre.GenreService;
import com.example.filmtinder.openapi.model.Film;
import com.example.filmtinder.openapi.model.LobbyInfo;
import com.example.filmtinder.parser.FilmInfoDto;
import com.example.filmtinder.topOfFilmsFormer.TopOfFilmsFormerService;
import com.example.filmtinder.unit.lobby.*;
import com.example.filmtinder.user.context.UserContext;
import com.example.filmtinder.user.context.UserContextHolder;
import com.example.filmtinder.utils.*;
import dev.mccue.guava.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class ApiServiceImpl implements ApiService {

    private final UserContextHolder userContextHolder;
    private final LobbyRepository lobbyRepository;
    private final LobbyTransformer lobbyTransformer;
    private final LobbyGenresRepository lobbyGenresRepository;
    private final LobbyFilmRepository lobbyFilmRepository;
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final TopOfFilmsFormerService topOfFilmsFormerService;
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private List<GenreEntity> genres;

    @Override
    public Optional<LobbyInfo> createLobby() {
        UserContext userContext = userContextHolder.getContext();
        if (userContext == null) {
            return Optional.empty();
        }

        String userDeviceToken = userContext.deviceToken();
        if (userDeviceToken == null || userDeviceToken.isEmpty()) {
            return Optional.empty();
        }

        LobbyEntity lobbyEntity = new LobbyEntity(
                UUID.randomUUID().toString(),
                generateCode(),
                LobbyStatus.NOT_READY_TO_START,
                userDeviceToken,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0,
                Set.of(),
                Set.of(userDeviceToken)
        );
        LobbyEntity savedLobbyEntity = lobbyRepository.save(lobbyEntity);
        return Optional.of(lobbyTransformer.transform(savedLobbyEntity));
    }

    @NotNull
    public static String generateCode() {
        return ApiUtils.toUpperCase(UUID.randomUUID().toString().substring(0, 6));
    }

    @Override
    public Optional<LobbyInfo> getLobbyInfo(String lobbyId) {
        Optional<LobbyEntity> lobbyEntityO = lobbyRepository.findById(lobbyId);
        if (lobbyEntityO.isEmpty()) {
            log.error("No lobby with lobbyId = {}", lobbyId);
            return Optional.empty();
        }
        LobbyEntity lobbyEntity = lobbyEntityO.get();
        Optional<Film> checkForMatchO = checkForMatch(lobbyId);
        return Optional.of(lobbyTransformer.transform(lobbyEntity, checkForMatchO.orElse(null)));
    }

    @Override
    public Optional<LobbyInfo> joinLobby(String lobbyToken) {
        String upperCaseToken = ApiUtils.toUpperCase(lobbyToken);
        Optional<LobbyEntity> lobbyEntityO =
                lobbyRepository.findLobbyEntityByCodeOrderByStartedDateDesc(upperCaseToken);

        if (lobbyEntityO.isEmpty()) {
            log.error("Can't find lobby with token = {}", lobbyToken);
            return Optional.empty();
        }

        LobbyEntity lobbyEntity = lobbyEntityO.get();

        String userDeviceToken = userContextHolder.getContext().deviceToken();
        if (userDeviceToken == null || userDeviceToken.isEmpty()) {
            log.error("When trying to join to lobby with lobbyToken = {}: UserDeviceToken is null!", lobbyToken);
            return Optional.empty();
        }

        if (lobbyEntity.getJoinedPersons().contains(userDeviceToken)) {
            log.warn("User with deviceToken = {} try to join to lobby with id = {} for several times",
                    userDeviceToken, lobbyEntity.getLobbyId());
            return Optional.of(lobbyTransformer.transform(lobbyEntity));
        }

        lobbyEntity.getJoinedPersons().add(userDeviceToken);

        if (lobbyEntity.getJoinedPersons().size() >= 2) {
            lobbyEntity.setStatus(LobbyStatus.READY_TO_START);
        }

        lobbyEntity.setLastChangeStatusTime(LocalDateTime.now());
        lobbyRepository.save(lobbyEntity);

        scheduler.schedule(new SessionStartTimeoutRunnable(lobbyEntity, lobbyRepository), 1, TimeUnit.MINUTES);
        return Optional.of(lobbyTransformer.transform(lobbyEntity));
    }

    @Override
    public Optional<LobbyInfo> startLobby(String lobbyId) {
        LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyId).orElseThrow();
        String userDeviceToken = userContextHolder.getContext().deviceToken();

        Set<String> lobbyJoinedPersons = lobbyEntity.getJoinedPersons();
        if (!lobbyJoinedPersons.contains(userDeviceToken)) {
            log.error("User with device token = {} has not enough rights to change status of lobby with lobbyId = {}"
                    , userDeviceToken, lobbyId);
            return Optional.empty();
        }

        LobbyStatus lobbyStatus = lobbyEntity.getStatus();
        if (!lobbyStatus.equals(LobbyStatus.READY_TO_START)) {
            log.error("Lobby with lobbyId = {} has invalid status = {} for start", lobbyId, lobbyStatus);
            return Optional.empty();
        }

        if (lobbyJoinedPersons.size() < 2) {
            log.error("Lobby with lobbyId = {} has not enough people to change status to = {}", lobbyId,
                    LobbyStatus.READY_TO_START);
            return Optional.empty();
        }

        lobbyEntity.setStatus(LobbyStatus.GENRES_CHOOSING);
        lobbyEntity.setLastChangeStatusTime(LocalDateTime.now());
        lobbyRepository.save(lobbyEntity);

        scheduler.schedule(
                new GenresChoosingTimeoutRunnable(lobbyEntity, lobbyRepository),
                1,
                TimeUnit.MINUTES
        );
        return Optional.of(lobbyTransformer.transform(lobbyEntity));
    }

    @Override
    public List<GenreEntity> getGenres() {
        if (this.genres != null && !this.genres.isEmpty()) {
            return this.genres;
        }

        boolean cache = true;

        if (cache) {
            this.genres = Cache.VALID_GENRE_NAMES
                    .stream()
                    .map(genreName -> genreRepository.findByName(genreName).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            this.genres = genreRepository
                    .findAll()
                    .stream()
                    .filter(genre -> !topOfFilmsFormerService.formGenreTop(genre).isEmpty())
                    .sorted(Comparator.comparing(GenreEntity::getName))
                    .toList();

            log.info("All valid genres: {}", this.genres);
        }

        return this.genres;
    }

    @Override
    public Optional<LobbyInfo> saveGenresForLobby(String lobbyId, List<String> ids) {
        String deviceToken = userContextHolder.getContext().deviceToken();
        LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyId).orElseThrow();

        if (lobbyEntity.getStatus() != LobbyStatus.GENRES_CHOOSING) {
            log.error("Lobby with lobbyId = {} has not enough people to change status to = {}", lobbyId,
                    LobbyStatus.GENRES_CHOOSING);
            return Optional.empty();
        }

        lobbyGenresRepository.save(
                new LobbyGenresEntity(
                        UUID.randomUUID().toString(),
                        lobbyId,
                        deviceToken,
                        new HashSet<>(ids)
                )
        );

        List<LobbyGenresEntity> chosenGenres = lobbyGenresRepository.findLobbyGenresEntitiesByLobbyId(lobbyId);
        if (chosenGenres.size() == lobbyEntity.getJoinedPersons().size()) {
            List<LobbyGenresEntity> lobbyGenresEntitiesByLobbyId =
                    lobbyGenresRepository.findLobbyGenresEntitiesByLobbyId(lobbyId);

            List<String> listOfGenres = lobbyGenresEntitiesByLobbyId
                    .stream()
                    .flatMap(x -> x.getChosenGenres().stream())
                    .distinct()
                    .toList();

            Set<String> genreIds = lobbyGenresEntitiesByLobbyId
                    .stream()
                    .map(LobbyGenresEntity::getChosenGenres)
                    .reduce(Sets::intersection)
                    .orElse(Collections.emptySet());

            if (genreIds.isEmpty()) {
                if (listOfGenres.isEmpty()) {
                    log.error("Can't get random genre: empty list");
                    return Optional.empty();
                } else {
                    int randomGenre = random.nextInt(0, listOfGenres.size());
                    genreIds = Set.of(listOfGenres.get(randomGenre));
                }
            }

            if (genreIds.size() > 1) {
                genreIds = Set.of(
                        genreIds
                                .stream()
                                .toList()
                                .get(random.nextInt(0, genreIds.size()))
                );
            }

            lobbyEntity.setStatus(LobbyStatus.FILMS_CHOOSING);
            lobbyEntity.setChosenGenres(genreIds);
            lobbyRepository.save(lobbyEntity);

            Optional<String> chosenGenreIdO = genreIds.stream().findFirst();
            if (chosenGenreIdO.isEmpty()) {
                log.error("Can't save genre for lobby: chosenGenreId is empty");
                return Optional.empty();
            }
            GenreEntity chosenGenre = genreRepository.findByName(chosenGenreIdO.get()).orElseThrow();
            List<FilmInfoDto> filmInfoDtos = topOfFilmsFormerService.formGenreTop(chosenGenre);
            int i = 0;
            for (FilmInfoDto filmInfoDto : filmInfoDtos) {
                lobbyFilmRepository.save(new LobbyFilmEntity(
                        UUID.randomUUID().toString(),
                        lobbyId,
                        filmInfoDto.getId(),
                        0,
                        i++
                ));
            }
        }

        return Optional.of(lobbyTransformer.transform(lobbyEntity));
    }

    @Override
    public List<Film> getFilmsForLobby(String lobbyId) {
        Optional<LobbyEntity> lobbyEntityO = lobbyRepository.findById(lobbyId);
        if (lobbyEntityO.isEmpty()) {
            log.error("Can't find lobby with id = {}", lobbyId);
            return Collections.emptyList();
        }
        LobbyEntity lobbyEntity = lobbyEntityO.orElseThrow();

        LobbyStatus lobbyStatus = lobbyEntity.getStatus();
        if (lobbyStatus != LobbyStatus.FILMS_CHOOSING) {
            log.error("Not valid status = {} for final choosing in lobby with lobbyId = {}", lobbyStatus, lobbyId);
            return Collections.emptyList();
        }

        List<LobbyFilmEntity> lobbyFilmEntities =
                lobbyFilmRepository.findLobbyFilmEntitiesByLobbyIdOrderByPositionAsc(lobbyId);
        List<Long> listOfFilmIds = lobbyFilmEntities.stream().map(LobbyFilmEntity::getFilmId).toList();
        List<FilmEntity> films = filmRepository.findAllById(listOfFilmIds);
        reduceGenres(films);
        ApiUtils.roundRatings(films);

        List<Film> result = films.stream().map(FilmConverter::convertFilm).toList();
        scheduler.schedule(new FilmsChoosingTimeoutRunnable(lobbyEntity, lobbyRepository), 3, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public Optional<Film> likeFilm(String lobbyId, Integer filmId) {
        Optional<LobbyFilmEntity> lobbyFilmEntityO =  lobbyFilmRepository.findLobbyFilmEntityByLobbyIdAndFilmId(
                lobbyId,
                Long.valueOf(filmId)
        );
        if (lobbyFilmEntityO.isEmpty()) {
            log.error("Can't find lobbyFilmEntity with lobbyId = {} and filmId {}", lobbyId, filmId);
            return Optional.empty();
        }
        LobbyFilmEntity lobbyFilmEntity = lobbyFilmEntityO.get();

        lobbyFilmEntity.setCount(lobbyFilmEntity.getCount() + 1);
        lobbyFilmRepository.save(lobbyFilmEntity);

        return checkForMatch(lobbyId);
    }

    @Override
    public Optional<Film> deleteLike(String lobbyId, Integer filmId) {
        Optional<LobbyFilmEntity> lobbyFilmEntityO = lobbyFilmRepository.findLobbyFilmEntityByLobbyIdAndFilmId(
                lobbyId,
                Long.valueOf(filmId)
        );

        if (lobbyFilmEntityO.isEmpty()) {
            return Optional.empty();
        }

        LobbyFilmEntity lobbyFilmEntity = lobbyFilmEntityO.get();
        lobbyFilmEntity.setCount(Math.max(0, lobbyFilmEntity.getCount() - 1));
        lobbyFilmRepository.save(lobbyFilmEntity);

        return checkForMatch(lobbyId);
    }

    @Override
    public Optional<Film> userFinishChoosing(String lobbyId) {
        Optional<LobbyEntity> lobbyEntityO = lobbyRepository.findById(lobbyId);
        if (lobbyEntityO.isEmpty()) {
            log.error("Can't find lobby with id = {}", lobbyId);
            return Optional.empty();
        }
        LobbyEntity lobbyEntity = lobbyEntityO.get();

        lobbyEntity.setFinishedCount(lobbyEntity.getFinishedCount() + 1);
        lobbyRepository.save(lobbyEntity);

        if (lobbyEntity.getFinishedCount() >= lobbyEntity.getJoinedPersons().size()) {
            Optional<Film> matchResponse = checkForMatch(lobbyId);
            try {
                if (matchResponse.isPresent()) {
                    lobbyEntity.setStatus(LobbyStatus.FINISHED);
                    return matchResponse;
                }
                lobbyEntity.setStatus(LobbyStatus.FILMS_MATCH_ERROR);
            } finally {
                lobbyRepository.save(lobbyEntity);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<LobbyInfo> getFinishedLobbyInfo(String lobbyId) {
        LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyId).orElseThrow();
        return Optional.of(lobbyTransformer.transform(lobbyEntity));
    }

    @Override
    public void deleteLobbyInfo(String lobbyId) {
        lobbyRepository.deleteById(lobbyId);
    }

    @Override
    public Optional<LobbyInfo> restartLobby(String lobbyId) {
        LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyId).orElseThrow();
        if (!lobbyEntity.getJoinedPersons().contains(userContextHolder.getContext().deviceToken())) {
            throw new RuntimeException("Not enough rights to change status");
        }

        if (lobbyEntity.getStatus() != LobbyStatus.NOT_READY_TO_START) {
            lobbyEntity.setStatus(LobbyStatus.READY_TO_START);
        }

        lobbyEntity.setStatus(LobbyStatus.GENRES_CHOOSING);
        lobbyEntity.setLastChangeStatusTime(LocalDateTime.now());
        lobbyEntity.setStartedDate(LocalDateTime.now());
        lobbyEntity.setFinishedCount(0);

        log.info("data before {}", lobbyFilmRepository.findLobbyFilmEntitiesByLobbyIdOrderByPositionAsc(lobbyId));
        lobbyFilmRepository.saveAll(lobbyFilmRepository.findLobbyFilmEntitiesByLobbyIdOrderByPositionAsc(lobbyId)
                .stream()
                .peek(item -> item.setLobbyId("-1")).toList()
        );
        lobbyGenresRepository.saveAll(
                lobbyGenresRepository.findLobbyGenresEntitiesByLobbyId(lobbyId).stream().peek(item -> item.setLobbyId("-1")).toList()
        );

        log.info("data after {}", lobbyFilmRepository.findLobbyFilmEntitiesByLobbyIdOrderByPositionAsc(lobbyId));

        lobbyRepository.save(lobbyEntity);

        return Optional.of(lobbyTransformer.transform(lobbyEntity));
    }

    private Optional<Film> checkForMatch(String lobbyId) {
        Optional<LobbyEntity> lobbyEntityO = lobbyRepository.findById(lobbyId);
        if (lobbyEntityO.isEmpty()) {
            log.error("Can't find lobby with id = {}", lobbyId);
            return Optional.empty();
        }
        LobbyEntity lobbyEntity = lobbyEntityO.orElseThrow();
        int joinedPersonsCount = lobbyEntity.getJoinedPersons().size();
        List<LobbyFilmEntity> lobbyFilmEntitiesByLobbyId =
                lobbyFilmRepository.findLobbyFilmEntitiesByLobbyIdOrderByPositionAsc(lobbyId);
        Optional<LobbyFilmEntity> match =
                lobbyFilmEntitiesByLobbyId.stream().filter(entry -> entry.getCount() >= joinedPersonsCount).findFirst();
        if (match.isPresent()) {
            lobbyEntity.setStatus(LobbyStatus.FINISHED);
            lobbyRepository.save(lobbyEntity);

            FilmEntity film = filmRepository.findById(match.get().getFilmId()).orElseThrow();
            return Optional.of(FilmConverter.convertFilm(film));
        }
        return Optional.empty();
    }

    private void reduceGenres(List<FilmEntity> films) {
        films.forEach(film -> {
            List<GenreEntity> genres = film.getGenres();
            List<GenreEntity> popularGenres = genres.stream()
                    .filter(genre -> !Cache.NOT_POPULAR_GENRES.contains(genre.getName()))
                    .toList();
            int popularGenresSize = popularGenres.size();
            film.setGenres(popularGenres.subList(0, Math.min(popularGenresSize, 3)));
        });
    }
}
