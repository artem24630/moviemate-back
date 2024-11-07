package com.example.filmtinder.api.transformer;

import com.example.filmtinder.openapi.model.Film;
import com.example.filmtinder.openapi.model.LobbyInfo;
import com.example.filmtinder.unit.lobby.LobbyEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.example.filmtinder.unit.lobby.LobbyStatus.READY_TO_START;

@Component
public class LobbyTransformer {

    public LobbyInfo transform(LobbyEntity entity) {
        return transform(entity, null);
    }

    public LobbyInfo transform(LobbyEntity entity, Film matchedFilm) {
        return new LobbyInfo()
                .lobbyId(entity.getLobbyId())
                .code(entity.getCode())
                .status(entity.getStatus().name())
                .isAvailableToStart(entity.getStatus().equals(READY_TO_START))
                .joinedPersons(entity.getJoinedPersons().stream().toList())
                .chosenGenres(new ArrayList<>(entity.getChosenGenres()))
                .finishedCount(entity.getFinishedCount())
                .matchedFilm(matchedFilm);
    }
}
