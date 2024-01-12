package com.inventory.app.server.entity.media;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
@Entity(name = "Media")
@Table(name = "movie")
@Getter
@Setter
public class Movie extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> directors;
    @Column(name = "release_date")
    private LocalDate releaseDate;
}
