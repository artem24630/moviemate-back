package com.example.filmtinder.unit.lobby;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyRepository extends JpaRepository<LobbyEntity, String> {

    Optional<LobbyEntity> findLobbyEntityByCodeOrderByStartedDateDesc(String code);

}
