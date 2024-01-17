package com.inventory.app.server.entity;

import com.inventory.app.server.config.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;
import java.util.List;

@Entity(name = "book")
@Table(name = "book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
                "} " + super.toString();
    }
}
