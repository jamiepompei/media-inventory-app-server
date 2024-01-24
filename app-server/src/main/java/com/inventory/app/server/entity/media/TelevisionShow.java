package com.inventory.app.server.entity.media;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;

import java.time.LocalDate;
import java.util.List;
@Entity(name = "TelevisionShow")
@Table(name = "televisiion_show")
@Getter
@Setter
public class TelevisionShow extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> writers;
    @Column(name = "season")
    private Integer season;
    @Column(name = "release_date")
    private LocalDate releaseDate;
}
