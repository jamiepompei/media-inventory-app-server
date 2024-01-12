package com.inventory.app.server.entity.media;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
@Entity(name = "Music")
@Table(name = "music")
public class Music extends Media {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> artists;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> songList;
    @Column(name = "release_date")
    private LocalDate releaseDate;
}
