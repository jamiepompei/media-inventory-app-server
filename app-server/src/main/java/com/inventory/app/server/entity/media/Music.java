package com.inventory.app.server.entity.media;

import java.time.LocalDate;
import java.util.List;

public class Music extends Media {
    private List<String> artists;
    private List<String> songList;
    private LocalDate releaseDate;
}
