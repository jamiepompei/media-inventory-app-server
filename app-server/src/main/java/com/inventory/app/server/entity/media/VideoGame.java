package com.inventory.app.server.entity.media;

import com.inventory.app.server.config.converter.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Entity(name = "videogame")
@Table(name = "videogame")
@Data
public class VideoGame extends Media {
    @Convert(converter = StringListConverter.class)
    @Column(name = "consoles")
    private List<String> consoles;
    @Column(name = "num_of_players")
    private Integer numberOfPlayers;
    @Column(name = "release_year")
    private Integer releaseYear;

    @Override
    public String toString() {
        return "Game{" +
                "consoles=" + consoles +
                ", numberOfPlayers=" + numberOfPlayers +
                ", releaseDate=" + releaseYear +
                " " +
                super.toString() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoGame game)) return false;

        // Compare fields from the superclass (Media)
        if(!super.equals(o)) return false;
        // Compare fields specific to Game
        return Objects.equals(getConsoles(), game.getConsoles()) &&
                Objects.equals(getNumberOfPlayers(), game.getNumberOfPlayers()) &&
                Objects.equals(getReleaseYear(), game.getReleaseYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getConsoles(),
                getNumberOfPlayers(),
                getReleaseYear());
    }
}
