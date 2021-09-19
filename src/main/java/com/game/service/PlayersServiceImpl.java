package com.game.service;

import com.game.entity.*;
import com.game.repository.PlayerCriteriaRepository;
import com.game.repository.PlayersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class PlayersServiceImpl implements PlayersService {

    private final PlayersRepository playersRepository;

    private final PlayerCriteriaRepository playerCriteriaRepository;

    public PlayersServiceImpl(PlayersRepository playersRepository,
                              PlayerCriteriaRepository playerCriteriaRepository) {
        this.playersRepository = playersRepository;
        this.playerCriteriaRepository = playerCriteriaRepository;
    }

    // getAll   +
    @Override
    public List<Player> getPlayers(PlayerSearchCriteria params) {

        return playerCriteriaRepository.findAllWithFilters(params).getContent();
    }

    // getCount     +
    @Override
    public Integer getAllPlayersCount(PlayerSearchCriteria params) {

        return playerCriteriaRepository.findAllWithFiltersCount(params);
    }

    // create player +
    @Override
    public Player createPlayer(Player player) {

        Integer exp = player.getExperience();
        player.setLevel(getCurrentLevel(exp));
        player.setUntilNextLevel(getUntilNextLevel(exp, player.getLevel()));

        return playersRepository.save(player);
    }

    // get player   +
    @Override
    public Player getPlayerByID(Long id) {
        return playersRepository.findById(id).orElse(null);
    }


    // update player    +
    @Override
    public ResponseEntity<Player> updatePlayer(
            Long id, Player player) {
/*
        if(!playersRepository.existsById(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404

        Player oldPlayer = playersRepository.findById(id).get();

        //name
        if (player.getName() != null && player.getName().length() > 12)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else if (player.getName() != null) oldPlayer.setName(player.getName());

        // title
        if (player.getTitle() != null && player.getTitle().length() > 30)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else if (player.getTitle() != null) oldPlayer.setTitle(player.getTitle());

        // race
        if (player.getRace() != null)
            oldPlayer.setRace(player.getRace());

        // profession
        if (player.getProfession() != null)
            oldPlayer.setProfession(player.getProfession());

        // Birthday
        if (player.getBirthday() != null && (player.getBirthday().getTime() <= 0
                || getYearFromLong(player.getBirthday().getTime()) > 3000
                || getYearFromLong(player.getBirthday().getTime()) < 2000))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else if (player.getBirthday() != null)
            oldPlayer.setBirthday(player.getBirthday());

        // Banned
        if (player.getBanned() != null)
            oldPlayer.setBanned(player.getBanned());

        // experience
        if (player.getExperience() != null
                && (player.getExperience() < 0
                || player.getExperience() > 10000000))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else if(player.getExperience() != null)
        {

            oldPlayer.setExperience(player.getExperience());
            oldPlayer.setLevel(getCurrentLevel(player.getExperience()));
            oldPlayer.setUntilNextLevel(getUntilNextLevel(
                    getCurrentLevel(player.getExperience()),
                    oldPlayer.getLevel()));
        }
        System.out.println("player from request " + player);
        System.out.println("Player to save " + oldPlayer);

       // oldPlayer.setId(player.getId());


        return new ResponseEntity<>(playersRepository.save(oldPlayer),HttpStatus.OK);*/
        Player oldPlayer;
        boolean areParamsValid = true;
        if (playersRepository.existsById(id))
            oldPlayer = playersRepository.findById(id).get();
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        //name
        if (player.getName() != null) {
            if (isNameValid(player.getName()))
                oldPlayer.setName(player.getName());
            else areParamsValid = false;
        }
        //title
        if (player.getTitle() != null) {
            if (isTitleValid(player.getTitle()))
                oldPlayer.setTitle(player.getTitle());
            else areParamsValid = false;
        }
        //race
        if (player.getRace() != null)
            oldPlayer.setRace(player.getRace());
        //profession
        if (player.getProfession() != null)
            oldPlayer.setProfession(player.getProfession());
        //birthday
        if (player.getBirthday() != null) {
            if (isBirthdayValid(player.getBirthday()))
                oldPlayer.setBirthday(player.getBirthday());

            else areParamsValid = false;
        }
        //banned
        if (player.getBanned() != null)
            oldPlayer.setBanned(player.getBanned());
        //experience
        if (player.getExperience() != null) {
            if (isExperianceValid(player.getExperience())) {
                int newExp = player.getExperience();
                int newLvl = getCurrentLevel(newExp);
                int newUntilNextLvl = getUntilNextLevel(newExp, newLvl);

                oldPlayer.setExperience(newExp);
                oldPlayer.setLevel(newLvl);
                oldPlayer.setUntilNextLevel(newUntilNextLvl);
            }
            else areParamsValid = false;
        }

        if(!areParamsValid) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(playersRepository.saveAndFlush(oldPlayer), HttpStatus.OK);
    }




//3. При обновлении или создании игрока игнорируем параметры “id”, “level” и “untilNextLevel” из тела запроса.


    // delete player    +
    @Override
    public Player deletePlayer(Long id) {

        Player player = playersRepository.findById(id).orElse(null);
        if (player == null) return null;

        playersRepository.deleteById(id);

        return player;
    }

    // -----------------------------------------------------helpers

    // params validators
    // name
    private boolean isNameValid(String name) {
        return !name.isEmpty() && name.length() <= 12;
    }
    //title
    private boolean isTitleValid(String name) {
        return name.length() <= 30;
    }
    // birthday
    private boolean isBirthdayValid(Date birthday) {
        return !(birthday.getTime() < 0
                || getYearFromLong(birthday.getTime()) > 3000
                || getYearFromLong(birthday.getTime()) < 2000);
    }
    // experience
    private boolean isExperianceValid(Integer experience) {
        return !(experience < 0 || experience > 10000000);
    }

    // other stuff

    private Integer getCurrentLevel(Integer exp) {
        return (int) ((Math.sqrt(2500d + 200d * exp) - 50) / 100);
    }

    private Integer getUntilNextLevel(Integer exp, Integer lvl) {
        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }

    private int getYearFromLong(Long dateInLong) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateInLong);
        return cal.get(Calendar.YEAR);
    }

}
//“name”:[String], --optional
//“title”:[String], --optional
//“race”:[Race], --optional
//“profession”:[Profession], --optional
//“birthday”:[Long], --optional
//“banned”:[Boolean], --optional
//“experience”:[Integer] --optional