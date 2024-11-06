// This file was automatically generated.
package com.example.filmtinder.db.genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    Optional<GenreEntity> create(GenreDto genreDto);

    List<GenreEntity> createAll(List<GenreDto> genreDtos);

    List<GenreEntity> getAll();

    Optional<GenreEntity> getByName(String name);

    void delete(Long id);

    void deleteAll();
}

    