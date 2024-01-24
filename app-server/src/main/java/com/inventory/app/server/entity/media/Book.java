package com.inventory.app.server.entity.media;

import com.inventory.app.server.config.converter.StringListConverter;
import com.inventory.app.server.entity.media.Media;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

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
}
