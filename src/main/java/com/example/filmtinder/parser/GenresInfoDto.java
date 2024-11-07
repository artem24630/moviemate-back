package com.example.filmtinder.parser;

import com.example.filmtinder.db.genre.GenreEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class GenresInfoDto {
    private final Long id;
    private final String name;


    public static GenresInfoDto convert(GenreEntity genre) {
        return new GenresInfoDto(genre.getId(), genre.getName());
    }
}
