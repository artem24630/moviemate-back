package com.example.filmtinder.unit.lobby;

import com.example.filmtinder.utils.SetToStringConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "lobby_genres")
public class LobbyGenresEntity {

    @Id
    private String id;

    private String lobbyId;

    private String userId;

    @Convert(converter = SetToStringConverter.class)
    private Set<String> chosenGenres = new HashSet<>();
}
