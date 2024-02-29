package com.inventory.app.server.entity.media;

import com.inventory.app.server.config.converter.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

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
    @Column(name = "release_year")
    private Integer releaseYear;

    @Override
    public String toString() {
        return "Music{" +
                "artists=" + artists +
                ", songList=" + songList +
                ", releaseYear=" + releaseYear +
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
                Objects.equals(getReleaseYear(), music.getReleaseYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getArtists(),
                getSongList(),
                getReleaseYear());
    }
}
