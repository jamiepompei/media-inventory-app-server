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
@Entity(name = "Music")
@Table(name = "music")
@Getter
@Setter
public class Music extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> artists;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> songList;
    @Column(name = "release_date")
    private LocalDate releaseDate;
}
