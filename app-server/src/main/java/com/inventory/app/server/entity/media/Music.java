package com.inventory.app.server.entity.media;

import com.inventory.app.server.config.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity(name = "music")
@Table(name = "music")
@Data
public class Music extends Media {
    @Convert(converter = StringListConverter.class)
    @Column(name = "artists")
    private List<String> artists;
    @Convert(converter = StringListConverter.class)
    @Column(name = "song_list")
    private List<String> songList;
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Override
    public String toString() {
        return "Music{" +
                "artists=" + artists +
                ", songList=" + songList +
                ", releaseDate=" + releaseDate +
                ", " +
                super.toString() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Music music)) return false;

        // Compare fields from the superclass (Media)
        if (!super.equals(o)) return false;
        // Compare fields specific to Movie
        return Objects.equals(getArtists(), music.getArtists()) &&
                Objects.equals(getSongList(), music.getSongList()) &&
                Objects.equals(getReleaseDate(), music.getReleaseDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getArtists(),
                getSongList(),
                getReleaseDate());
    }
}
