package com.inventory.app.server.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;


import java.io.Serializable;
import java.util.List;
@Entity(name = "Book")
@Table(name = "book")
@Getter
@Setter
public class Book extends Media implements Serializable {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> authors;
    @Column(name = "copyright_year")
    private Integer copyrightYear;
    @Column(name = "edition")
    private Integer edition;
}
