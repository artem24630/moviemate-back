package com.example.filmtinder.topOfFilmsFormer;

import com.example.filmtinder.db.genre.GenreEntity;
import com.example.filmtinder.parser.FilmInfoDto;

import java.util.List;

public interface TopOfFilmsFormerService {

    List<FilmInfoDto> formGenreTop(GenreEntity genre);

    List<FilmInfoDto> formGenreTop(String genreName);
}
