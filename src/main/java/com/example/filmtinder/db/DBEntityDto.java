package com.example.filmtinder.db;

import com.example.filmtinder.db.film.FilmEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonSubTypes(
        {
                @Type(value = FilmEntity.class, name = "film")
        })
public interface DBEntityDto {
}
