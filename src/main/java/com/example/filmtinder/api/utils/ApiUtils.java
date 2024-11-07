package com.example.filmtinder.api.utils;

import com.example.filmtinder.db.film.FilmEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class ApiUtils {

    public static void roundRatings(List<FilmEntity> films) {
        films.forEach(film -> {
            float rating_kp = film.getRating_kp();
            film.setRating_kp(formatRating(rating_kp));

            float rating_imdb = film.getRating_imdb();
            film.setRating_imdb(formatRating(rating_imdb));
        });
    }

    public static float formatRating(float rating) {
        return BigDecimal.valueOf(rating).setScale(1, RoundingMode.HALF_DOWN).floatValue();
    }

    public static String toUpperCase(String s) {
        return s.toUpperCase(Locale.ROOT);
    }

}
