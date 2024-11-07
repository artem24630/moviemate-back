package com.example.filmtinder.api.service;


import com.example.filmtinder.db.genre.GenreEntity;
import com.example.filmtinder.openapi.model.Film;
import com.example.filmtinder.openapi.model.LobbyInfo;

import java.util.List;
import java.util.Optional;

public interface ApiService {

    Optional<LobbyInfo> createLobby();

    Optional<LobbyInfo> getLobbyInfo(String lobbyId);

    Optional<LobbyInfo> joinLobby(String token);

    Optional<LobbyInfo> startLobby(String lobbyId);

    List<GenreEntity> getGenres();

    Optional<LobbyInfo> saveGenresForLobby(String lobbyId, List<String> ids);

    List<Film> getFilmsForLobby(String lobbyId);

    Optional<Film> likeFilm(String lobbyId, Integer filmId);

    Optional<Film> deleteLike(String lobbyId, Integer filmId);

    Optional<Film> userFinishChoosing(String lobbyId);

    Optional<LobbyInfo> getFinishedLobbyInfo(String lobbyId);

    Optional<LobbyInfo> restartLobby(String lobbyId);

    void deleteLobbyInfo(String lobbyId);
}
