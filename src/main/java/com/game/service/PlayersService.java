package com.game.service;

import com.game.entity.Player;
import com.game.entity.PlayerSearchCriteria;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayersService {


    List<Player> getPlayers(PlayerSearchCriteria params);
    Integer getAllPlayersCount(PlayerSearchCriteria params);
    Player createPlayer(Player player);
    Player getPlayerByID(Long id);
    ResponseEntity<Player> updatePlayer(Long id, Player player);
    Player deletePlayer(Long id);

    //ResponseEntity<Player> updatePlayer(Long id, String name, String title, Race race, Profession profession, Long birthday, Boolean banned, Integer experience);
}
