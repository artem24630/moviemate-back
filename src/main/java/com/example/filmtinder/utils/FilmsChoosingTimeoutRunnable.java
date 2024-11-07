package com.example.filmtinder.utils;

import com.example.filmtinder.unit.lobby.LobbyEntity;
import com.example.filmtinder.unit.lobby.LobbyRepository;
import com.example.filmtinder.unit.lobby.LobbyStatus;

import java.util.Optional;

public class FilmsChoosingTimeoutRunnable implements Runnable {

    private final LobbyEntity lobbyEntity;
    private final LobbyRepository lobbyRepository;

    public FilmsChoosingTimeoutRunnable(LobbyEntity lobbyEntity, LobbyRepository lobbyRepository) {
        this.lobbyEntity = lobbyEntity;
        this.lobbyRepository = lobbyRepository;
    }

    @Override
    public void run() {
        Optional<LobbyEntity> currentLobbyEntityO = lobbyRepository.findById(lobbyEntity.getLobbyId());
        LobbyEntity currentLobbyEntity = currentLobbyEntityO
                .orElseThrow(() -> new RuntimeException("Lobby entity wasn't found after 1 minute of films choosing!"));
        if (currentLobbyEntity.getStatus() == LobbyStatus.FILMS_CHOOSING) {
            currentLobbyEntity.setStatus(LobbyStatus.FILMS_TIMEOUT);
            lobbyRepository.save(currentLobbyEntity);
        }
    }
}
