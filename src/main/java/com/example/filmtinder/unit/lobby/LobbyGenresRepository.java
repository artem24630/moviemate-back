package com.example.filmtinder.unit.lobby;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LobbyGenresRepository extends JpaRepository<LobbyGenresEntity, String> {
    Optional<LobbyGenresEntity> findLobbyGenresEntityByLobbyIdAndUserId(String lobbyId, String userId);

    List<LobbyGenresEntity> findLobbyGenresEntitiesByLobbyId(String lobbyId);
}
