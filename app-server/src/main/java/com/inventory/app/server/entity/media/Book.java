package com.inventory.app.server.entity.media;

import com.inventory.app.server.config.converter.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity(name = "book")
@Table(name = "book")
@Data
public class Book extends Media implements Serializable {
    @Convert(converter = StringListConverter.class)
    @Column(name = "authors")
    private List<String> authors;
    @Column(name = "copyright_year")
    private Integer copyrightYear;
    @Column(name = "edition")
    private Integer edition;

    @Override
    public String toString() {
        return "Book{" +
                "authors=" + authors +
                ", copyrightYear=" + copyrightYear +
                ", edition=" + edition +
                " " +
                super.toString() +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Book book)) return false;

        // Compare fields from the superclass (Media)
        if (!super.equals(obj)) return false;

        // Compare fields specific to Book
        return Objects.equals(getAuthors(), book.getAuthors()) &&
                Objects.equals(getCopyrightYear(), book.getCopyrightYear()) &&
                Objects.equals(getEdition(), book.getEdition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getAuthors(),
                getCopyrightYear(),
                getEdition()
        );
    }
}
