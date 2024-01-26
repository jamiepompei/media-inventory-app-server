package com.inventory.app.server.entity.media;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity(name = "Music")
@Table(name = "music")
@Getter
@Setter
public class Music extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> artists;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> songList;
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Override
    public String toString() {
        return "Music{" +
                "artists=" + artists +
                ", songList=" + songList +
                ", releaseDate=" + releaseDate +
                " " +
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
