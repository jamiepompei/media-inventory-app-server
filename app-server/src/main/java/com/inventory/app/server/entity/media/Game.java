package com.inventory.app.server.entity.media;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity(name = "game")
@Table(name = "game")
@Data
public class Game extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> consoles;
    @Column(name = "num_of_players")
    private Integer numberOfPlayers;
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Override
    public String toString() {
        return "Game{" +
                "consoles=" + consoles +
                ", numberOfPlayers=" + numberOfPlayers +
                ", releaseDate=" + releaseDate +
                " " +
                super.toString() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game game)) return false;

        // Compare fields from the superclass (Media)
        if(!super.equals(o)) return false;
        // Compare fields specific to Game
        return Objects.equals(getConsoles(), game.getConsoles()) &&
                Objects.equals(getNumberOfPlayers(), game.getNumberOfPlayers()) &&
                Objects.equals(getReleaseDate(), game.getReleaseDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getConsoles(),
                getNumberOfPlayers(),
                getReleaseDate());
    }
}
