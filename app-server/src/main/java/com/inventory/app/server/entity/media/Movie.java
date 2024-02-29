package com.inventory.app.server.entity.media;


import com.inventory.app.server.config.converter.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Entity(name = "movie")
@Table(name = "movie")
@Data
public class Movie extends Media {
    @Convert(converter = StringListConverter.class)
    @Column(name = "directors")
    private List<String> directors;
    @Column(name = "release_year")
    private Integer releaseYear;

    @Override
    public String toString() {
        return "Movie{" +
                "directors=" + directors +
                ", releaseYear=" + releaseYear +
                ", " +
                super.toString() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;

        // Compare fields from the superclass (Media)
        if (!super.equals(o)) return false;
        // Compare fields specific to Movie
        return Objects.equals(getDirectors(), movie.getDirectors()) &&
                Objects.equals(getReleaseYear(), movie.getReleaseYear());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getDirectors(),
                getReleaseYear());
    }
}
