package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Game;
import com.inventory.app.server.repository.media.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    List<Game> findAllByNumberOfPlayers(Integer numberOfPlayers){
        return gameRepository.findByNumberOfPlayers(numberOfPlayers);
    }

    List<Game> findByConsole(String console){
        return gameRepository.findByConsole(console);
    }

    List<Game> findByTitle(String title){
        return gameRepository.findByTitle(title);
    }
}
