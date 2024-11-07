package com.example.filmtinder.parser;

import com.example.filmtinder.db.film.FilmEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class FilmInfoDto {
    private Long id;
    private String name;
    private String description;
    private String poster_url;
    private Float rating_kp;
    private Float rating_imdb;
    private Integer release_year;
    private Integer duration;

    public static FilmInfoDto convert(FilmEntity film) {
        return new FilmInfoDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getPoster_url(),
                film.getRating_kp(),
                film.getRating_imdb(),
                film.getRelease_year(),
                film.getDuration()
        );
    }
}
