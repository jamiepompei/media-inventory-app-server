package com.inventory.app.server.entity.media;

import com.inventory.app.server.config.converter.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Entity(name = "televisionShow")
@Table(name = "television_show")
@Data
public class TelevisionShow extends Media {
    @Convert(converter = StringListConverter.class)
    @Column(name = "episodes")
    private List<String> episodes;
    @Column(name = "season")
    private Integer season;
    @Column(name = "release_year")
    private Integer releaseYear;

    @Override
    public String toString() {
        return "TelevisionShow{" +
                "episodes=" + episodes +
                ", season=" + season +
                ", releaseDate=" + releaseYear +
                ", " +
                super.toString() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TelevisionShow that)) return false;

        // Compare fields from the superclass (Media)
        if (!super.equals(o)) return false;
        // Compare fields specific to Television Show
        return Objects.equals(getEpisodes(), that.getEpisodes()) &&
                Objects.equals(getSeason(), that.getSeason()) &&
                Objects.equals(getReleaseYear(), that.getReleaseYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getEpisodes(),
                getSeason(),
                getReleaseYear());
    }
}
