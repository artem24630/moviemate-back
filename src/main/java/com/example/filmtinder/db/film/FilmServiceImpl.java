package com.example.filmtinder.db.film;

import com.example.filmtinder.db.genre.GenreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FilmServiceImpl implements FilmService {
    private final static Logger log = LoggerFactory.getLogger(FilmServiceImpl.class);

    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private GenreRepository genreRepository;


    @Override
    public Optional<FilmEntity> create(FilmDto filmDto) {
        FilmEntity film = getFilm(filmDto);
        try {
            return Optional.of(filmRepository.save(film));
        } catch (Exception e) {
            log.warn("Exception while saving film", e);
            return Optional.empty();
        }
    }

    public static FilmEntity getFilm(FilmDto filmDto) {
        return FilmEntity.builder()
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .poster_url(filmDto.getPoster_url())
                .rating_kp(filmDto.getRating_kp())
                .rating_imdb(filmDto.getRating_imdb())
                .release_year(filmDto.getRelease_year())
                .duration(filmDto.getDuration())
                .genres(filmDto.getGenres())
                .build();
    }

    @Override
    public List<FilmEntity> createAll(List<FilmDto> filmDtos) {
        List<FilmEntity> films = filmDtos.stream().map(FilmServiceImpl::getFilm).toList();
        try {
            return filmRepository.saveAll(films);
        } catch (Exception e) {
            log.warn("Exception while saving film", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(Long filmId) {
        filmRepository.deleteById(filmId);
    }

    @Override
    public void deleteAll() {
        filmRepository.deleteAll();
    }
}
