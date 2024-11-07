package com.example.filmtinder.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cache {

    public static final Set<String> NOT_POPULAR_GENRES = new HashSet<>(List.of(
            "детский",
            "история",
            "музыка",
            "спорт",
            "фэнтези"
    ));
    public static final int N_MINUTES_TO_WAITING_CONNECTION_TO_LOBBY_AFTER_CREATION = 1;
    public static final int N_TOP_FILMS = 20;
    public static final List<String> VALID_GENRE_NAMES = List.of(
            //"аниме",
            "биография",
            "боевик",
            //"вестерн",
            "военный",
            "детектив",
            "драма",
            "комедия",
            //"короткометражка",
            "криминал",
            "мелодрама",
            //"мультфильм",
            //"мюзикл",
            "приключения",
            "семейный",
            "триллер",
            "ужасы",
            "фантастика"
    );
}
