package com.example.filmtinder.topOfFilmsFormer;

import com.example.filmtinder.parser.FilmInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class TopOfFilmsFormerController {

    private final TopOfFilmsFormerService topOfFilmsFormerService;

    @Autowired
    public TopOfFilmsFormerController(TopOfFilmsFormerService topOfFilmsFormerService) {
        this.topOfFilmsFormerService = topOfFilmsFormerService;
    }

    @GetMapping("/api/v1/form_genre_top")
    public ResponseEntity<?> formGenreTop(@RequestBody String genreName) {
        List<FilmInfoDto> genreTop = topOfFilmsFormerService.formGenreTop(genreName);
        return ResponseEntity.ok(genreTop);
    }
}
