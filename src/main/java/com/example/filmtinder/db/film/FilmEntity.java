package com.example.filmtinder.db.film;

import com.example.filmtinder.db.genre.GenreEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "films")
public class FilmEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Setter(value = AccessLevel.NONE)
    private Long id;

    @Column(name = "name", columnDefinition = "text")
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "poster_url")
    private String poster_url;

    @Column(name = "rating_kp")
    private Float rating_kp;

    @Column(name = "rating_imdb")
    private Float rating_imdb;

    @Column(name = "release_year")
    private Integer release_year;

    @Column(name = "duration")
    private Integer duration;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "film_genres",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id"))
    private List<GenreEntity> genres;

    @Override
    public String toString() {
        return "FilmEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", poster_url='" + poster_url + '\'' +
                ", rating_kp=" + rating_kp +
                ", rating_imdb=" + rating_imdb +
                ", release_year=" + release_year +
                ", duration=" + duration +
                '}';
    }

    public static FilmEntity convert(FilmDto filmDto) {
        return FilmEntity.builder()
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .poster_url(filmDto.getPoster_url())
                .rating_kp(filmDto.getRating_kp())
                .rating_imdb(filmDto.getRating_imdb())
                .release_year(filmDto.getRelease_year())
                .duration(filmDto.getDuration())
                .build();
    }
}
