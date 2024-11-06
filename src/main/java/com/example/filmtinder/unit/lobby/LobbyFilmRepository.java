package com.example.filmtinder.unit.lobby;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LobbyFilmRepository extends JpaRepository<LobbyFilmEntity, String> {
    List<LobbyFilmEntity> findLobbyFilmEntitiesByLobbyIdOrderByPositionAsc(String lobbyId);

    Optional<LobbyFilmEntity> findLobbyFilmEntityByLobbyIdAndFilmId(String lobbyId, Long filmId);
}
