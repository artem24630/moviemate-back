package com.example.filmtinder.db.film;

import com.example.filmtinder.db.DBEntityDto;
import com.example.filmtinder.db.genre.GenreEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FilmDto implements DBEntityDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Pattern(regexp = "https://.*")
    private String poster_url;

    @DecimalMin("1.0")
    @DecimalMax("10.0")
    private Float rating_kp;

    private Float rating_imdb;

    @Min(1900)
    @Max(2023)
    private Integer release_year;

    @Min(60)
    @Max(180)
    private Integer duration;

    private List<GenreEntity> genres;
}
