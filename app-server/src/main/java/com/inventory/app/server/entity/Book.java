package com.inventory.app.server.entity;

import com.inventory.app.server.config.converter.StringListConverter;
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
    private int copyrightYear;
    @Column(name = "edition")
    private int edition;

}
