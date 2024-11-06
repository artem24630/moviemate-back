package com.example.filmtinder.unit.lobby;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "lobby_films")
public class LobbyFilmEntity {

    @Id
    private String id;

    private String lobbyId;

    private Long filmId;

    private int count;

    private int position;
}