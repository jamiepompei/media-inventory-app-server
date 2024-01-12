package com.inventory.app.server.entity.media;

import jdk.jfr.Enabled;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
@Entity(name = "TelevisionShow")
@Table(name = "televisiion_show")
public class TelevisionShow {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> writers;
    @Column(name = "season")
    private Integer season;
    @Column(name = "release_date")
    private LocalDate releaseDate;
}
