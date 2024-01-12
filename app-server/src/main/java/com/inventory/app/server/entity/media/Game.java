package com.inventory.app.server.entity.media;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity(name = "Game")
@Table(name = "game")
@Getter
@Setter
public class Game extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> consoles;
    @Column(name = "num_of_players")
    private Integer numberOfPlayers;
    @Column(name = "release_date")
    private LocalDate releaseDate;
}
