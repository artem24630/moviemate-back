package com.example.filmtinder.utils;

import com.example.filmtinder.unit.lobby.LobbyEntity;
import com.example.filmtinder.unit.lobby.LobbyRepository;
import com.example.filmtinder.unit.lobby.LobbyStatus;

public class SessionStartTimeoutRunnable implements Runnable {

    private final LobbyEntity lobbyEntity;
    private final LobbyRepository lobbyRepository;

    public SessionStartTimeoutRunnable(LobbyEntity lobbyEntity, LobbyRepository lobbyRepository) {
        this.lobbyEntity = lobbyEntity;
        this.lobbyRepository = lobbyRepository;
    }

    @Override
    public void run() {
        String lobbyId = lobbyEntity.getLobbyId();
        LobbyEntity currentLobbyEntity = lobbyRepository
                .findById(lobbyId)
                .orElseThrow(() -> new RuntimeException("Lobby entity with id " + lobbyId + " wasn't found after the waiting for the session's start!"));
        if (currentLobbyEntity.getStatus() == LobbyStatus.READY_TO_START) {
            currentLobbyEntity.setStatus(LobbyStatus.SESSION_START_TIMEOUT);
            lobbyRepository.save(currentLobbyEntity);
        }
    }
}
