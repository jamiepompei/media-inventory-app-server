package com.inventory.app.server.entity.media;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Game extends Media {
    private List<String> consoles;
    private Integer numberOfPlayers;
    private LocalDate releaseDate;
}
