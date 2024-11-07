package com.example.filmtinder.utils;

import com.example.filmtinder.db.film.FilmEntity;
import com.example.filmtinder.db.genre.GenreEntity;
import com.example.filmtinder.openapi.model.Film;

import java.util.List;

public class FilmConverter {
    public static Film convertFilm(FilmEntity film) {
        Film result = new Film();
        result.setFilmId(String.valueOf(film.getId()));
        result.setImageUrl(film.getPoster_url());
        result.setName(film.getName());
        result.setDescription(film.getDescription());
        result.setRatingKp(film.getRating_kp().toString());
        result.setRatingImdb(film.getRating_imdb().toString());
        List<String> genres = film.getGenres().stream()
                .map(GenreEntity::getName)
                .toList();
        result.setGenres(genres);
        result.setReleaseYear(film.getRelease_year().toString());
        result.setDuration(film.getDuration().toString());
        return result;
    }
}
