package com.example.filmtinder.utils;

import com.example.filmtinder.unit.lobby.LobbyEntity;
import com.example.filmtinder.unit.lobby.LobbyRepository;
import com.example.filmtinder.unit.lobby.LobbyStatus;

import java.util.Optional;

public class GenresChoosingTimeoutRunnable implements Runnable {

    private final LobbyEntity lobbyEntity;
    private final LobbyRepository lobbyRepository;

    public GenresChoosingTimeoutRunnable(LobbyEntity lobbyEntity, LobbyRepository lobbyRepository) {
        this.lobbyEntity = lobbyEntity;
        this.lobbyRepository = lobbyRepository;
    }

    @Override
    public void run() {
        Optional<LobbyEntity> currentLobbyEntityO = lobbyRepository.findById(lobbyEntity.getLobbyId());
        LobbyEntity currentLobbyEntity = currentLobbyEntityO
                .orElseThrow(() -> new RuntimeException("Lobby entity wasn't found after 1 minute of genre choosing!"));
        if (currentLobbyEntity.getStatus() == LobbyStatus.GENRES_CHOOSING) {
            currentLobbyEntity.setStatus(LobbyStatus.GENRES_TIMEOUT);
            lobbyRepository.save(currentLobbyEntity);
        }
    }
}
