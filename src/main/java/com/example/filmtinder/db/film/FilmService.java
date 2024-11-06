package com.example.filmtinder.db.film;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    Optional<FilmEntity> create(FilmDto filmDto);

    List<FilmEntity> createAll(List<FilmDto> filmDtos);


    void delete(Long id);

    void deleteAll();
}
