package com.inventory.app.server.entity;


import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
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
