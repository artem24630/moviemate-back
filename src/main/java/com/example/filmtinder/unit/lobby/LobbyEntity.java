package com.example.filmtinder.unit.lobby;

import com.example.filmtinder.utils.SetToStringConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Getter
@Table(name = "lobbies")
public class LobbyEntity {

    @Id
    private String lobbyId;

    private String code;
    private LobbyStatus status;

    private String authorId;

    private LocalDateTime lastChangeStatusTime;

    private LocalDateTime startedDate;

    private Integer finishedCount;

    @Convert(converter = SetToStringConverter.class)
    private Set<String> chosenGenres = new HashSet<>();
    @Convert(converter = SetToStringConverter.class)
    private Set<String> joinedPersons = new HashSet<>();

}
