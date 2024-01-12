package com.inventory.app.server.entity.media;


import java.time.LocalDate;
import java.util.List;

public class Movie extends Media {
    private List<String> directors;
    private LocalDate releaseDate;
}
