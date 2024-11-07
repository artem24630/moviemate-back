package com.example.filmtinder.db.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<GenreEntity> create(GenreDto genreDto) {
        GenreEntity genre = GenreEntity.builder()
                .name(genreDto.getName())
                .build();
        try {
            return Optional.of(genreRepository.save(genre));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<GenreEntity> createAll(List<GenreDto> genreDtos) {
        List<GenreEntity> list = genreDtos.stream().map(g -> GenreEntity.builder()
                .name(g.getName())
                .build()).toList();
        try {
            return genreRepository.saveAll(list);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<GenreEntity> getAll() {
        return genreRepository.findAll();
    }


    @Override
    public Optional<GenreEntity> getByName(String name) {
        return genreRepository.findByName(name);
    }

    @Override
    public void delete(Long id) {
        genreRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        genreRepository.deleteAll();
    }
}

    