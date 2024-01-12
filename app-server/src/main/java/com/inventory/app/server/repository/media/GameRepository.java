package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.media.Game;

import java.util.List;

public interface GameRepository {
    List<Game> findByNumberOfPlayers(Integer numberOfPlayers);
    List<Game> findByCOnsole(String console);
    List<Game> findByTitle(String title);
}
