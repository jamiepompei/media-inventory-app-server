package com.inventory.app.server.entity.media;

import javax.persistence.*;
import java.util.List;
@Entity(name = "Book")
@Table(name = "book")
public class Book extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> authors;
    @Column(name = "copyright_year")
    private Integer copyrightYear;
    @Column(name = "edition")
    private Integer edition;
}
