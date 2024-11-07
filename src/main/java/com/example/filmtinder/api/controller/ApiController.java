package com.example.filmtinder.api.controller;

import com.example.filmtinder.api.service.ApiService;
import com.example.filmtinder.db.genre.GenreEntity;
import com.example.filmtinder.openapi.api.V1ApiDelegate;
import com.example.filmtinder.openapi.model.Film;
import com.example.filmtinder.openapi.model.LobbyInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@Slf4j
public class ApiController implements V1ApiDelegate {

    @Autowired
    private ApiService apiService;

    @Override
    public ResponseEntity<LobbyInfo> createLobby() {
        Optional<LobbyInfo> lobbyInfoO = apiService.createLobby();
        return lobbyInfoO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<LobbyInfo> getLobbyInfo(String lobbyId) {
        Optional<LobbyInfo> lobbyInfoO = apiService.getLobbyInfo(lobbyId);
        return lobbyInfoO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<LobbyInfo> joinLobby(String token) {
        Optional<LobbyInfo> lobbyInfoO = apiService.joinLobby(token);
        return lobbyInfoO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<LobbyInfo> startLobby(String lobbyId) {
        Optional<LobbyInfo> lobbyInfoO = apiService.startLobby(lobbyId);
        return lobbyInfoO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<String>> getGenres() {
        List<GenreEntity> genres = apiService.getGenres();
        List<String> genreNames = genres.stream().map(GenreEntity::getName).toList();
        return ResponseEntity.ok(genreNames);
    }

    @Override
    public ResponseEntity<LobbyInfo> saveGenresForLobby(String lobbyId, List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("Empty genres");
        }
        Optional<LobbyInfo> lobbyInfoO = apiService.saveGenresForLobby(lobbyId, ids);
        return lobbyInfoO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Film>> getAllFilmsForLobby(String lobbyId) {
        List<Film> films = apiService.getFilmsForLobby(lobbyId);
        return ResponseEntity.ok(films);
    }

    @Override
    public ResponseEntity<Film> likeFilm(String lobbyId, Integer filmId) {
        Optional<Film> filmO = apiService.likeFilm(lobbyId, filmId);
        return filmO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Film> deleteLike(String lobbyId, Integer filmId) {
        Optional<Film> filmO = apiService.deleteLike(lobbyId, filmId);
        if (filmO.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Film> userFinishChoosing(String lobbyId) {
        Optional<Film> filmO = apiService.userFinishChoosing(lobbyId);
        return filmO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<LobbyInfo> getFinishedLobbyInfo(String lobbyId) {
        Optional<LobbyInfo> lobbyInfoO = apiService.getFinishedLobbyInfo(lobbyId);
        return lobbyInfoO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<LobbyInfo> restartLobby(String lobbyId) {
        Optional<LobbyInfo> lobbyInfoO = apiService.restartLobby(lobbyId);
        return lobbyInfoO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteLobbyInfo(String lobbyId) {
        apiService.deleteLobbyInfo(lobbyId);
        return ResponseEntity.ok().build();
    }
}
