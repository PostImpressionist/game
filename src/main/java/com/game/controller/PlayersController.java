package com.game.controller;

import com.game.entity.Player;
import com.game.entity.PlayerSearchCriteria;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(path = "/rest/players")
public class PlayersController {

    final Logger logger = LoggerFactory.getLogger(PlayersController.class);


    private final PlayersService playersServiceImpl;

    @Autowired
    public PlayersController(PlayersService playersServiceImpl) {
        this.playersServiceImpl = playersServiceImpl;
    }

    // ------------------------------- version 4 17.09.21

    //Get players list
    @GetMapping(path = "")
    public ResponseEntity<List<Player>> getPlayersWithFilters(

            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") Race race,
            @RequestParam(required = false, defaultValue = "") Profession profession,
            @RequestParam(required = false, defaultValue = "") Long after,
            @RequestParam(required = false, defaultValue = "") Long before,
            @RequestParam(required = false, defaultValue = "") Boolean banned,
            @RequestParam(required = false, defaultValue = "") Integer minExperience,
            @RequestParam(required = false, defaultValue = "") Integer maxExperience,
            @RequestParam(required = false, defaultValue = "") Integer minLevel,
            @RequestParam(required = false, defaultValue = "") Integer maxLevel,
            @RequestParam(required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize
    ) {
        PlayerSearchCriteria playerSearchCriteria = getPlayerSearchCriteriaInstance(
                name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel
        );
        playerSearchCriteria.setOrder(order);
        playerSearchCriteria.setPageNumber(pageNumber);
        playerSearchCriteria.setPageSize(pageSize);

        logger.info(" logger params for getPlayersWithFilters are:\n" + playerSearchCriteria);
        //System.out.println(" params are:\n" + playerSearchCriteria);

        return new ResponseEntity<>(playersServiceImpl.getPlayers(playerSearchCriteria), HttpStatus.OK);
    }

    //Get players count
    @GetMapping(path = "/count")
    public ResponseEntity<Integer> getPlayersCountWithFilters(

            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") Race race,
            @RequestParam(required = false, defaultValue = "") Profession profession,
            @RequestParam(required = false, defaultValue = "") Long after,
            @RequestParam(required = false, defaultValue = "") Long before,
            @RequestParam(required = false, defaultValue = "") Boolean banned,
            @RequestParam(required = false, defaultValue = "") Integer minExperience,
            @RequestParam(required = false, defaultValue = "") Integer maxExperience,
            @RequestParam(required = false, defaultValue = "") Integer minLevel,
            @RequestParam(required = false, defaultValue = "") Integer maxLevel

    ) {
        PlayerSearchCriteria playerSearchCriteria = getPlayerSearchCriteriaInstance(
                name,
                title,
                race,
                profession,
                after,
                before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel
        );

        return new ResponseEntity<>(playersServiceImpl.getAllPlayersCount(playerSearchCriteria), HttpStatus.OK);
    }

    //Post create player
    @PostMapping(path = "")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {

        if (!validatePlayerPostParams(player))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400

        return new ResponseEntity<>(playersServiceImpl.createPlayer(player), HttpStatus.OK);
    }

    //Get player extraction by id
    @GetMapping(path = "{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable(value = "id") String idString ) {

        Long id = validatePlayerId(idString);
        if(id < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400

        Player player = playersServiceImpl.getPlayerByID(id);

        if(player == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404

        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    //Post update player
    @PostMapping(path = "{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable(value = "id") String idString, @RequestBody Player player) {
       /* Long id = validatePlayerId(idString);
        if(id < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400

        if(checkForNull(player))
            return new ResponseEntity<>(playersServiceImpl.getPlayerByID(id),HttpStatus.OK);

        System.out.println("id from path = " + id);
        System.out.println("id from param = " + player.getId());

        return playersServiceImpl.updatePlayer(id, player);*/

        // ------------------------------------------- new version 20/09/21
        Long id = validatePlayerId(idString);
        if(id < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400

        return playersServiceImpl.updatePlayer(id, player);
    }

    //Delete player
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable(value = "id") String idString) {

        Long id = validatePlayerId(idString);
        if(id < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400

        Player player = playersServiceImpl.deletePlayer(id);

        if(player == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404

        return new ResponseEntity<>(HttpStatus.OK);
    }


    // ------ helpers

    private boolean checkForNull(Player player) {
        return  player == null
                && player.getBanned() == null
                && player.getBirthday() == null
                && player.getLevel() == null
                && player.getTitle() == null
                && player.getName() == null
                && player.getExperience() == null
                && player.getProfession() == null
                && player.getRace() == null;
    }

    private Long validatePlayerId(String idString) {
        long id;
        try{
           id = Long.parseLong(idString);
        } catch (NumberFormatException ignore){
            return -1L;
        }
        if(id <= 0 )   return -1L;

        return id;
    }

    private boolean validatePlayerPostParams(Player player) {

        // name
        if (player.getName() == null
                || player.getName().length() > 12
                || player.getName().isEmpty())
            return false;

        // title
        if (player.getTitle() == null
                || player.getTitle().length() > 30)
            return false;

        // race and profession
        if (player.getRace() == null || player.getProfession() == null)
            return false;

        // birthday
        if (player.getBirthday() == null || player.getBirthday().getTime() < 0
                || getYearFromLong(player.getBirthday().getTime()) > 3000
                || getYearFromLong(player.getBirthday().getTime()) < 2000)
            return false;

        // experience
        if (player.getExperience() == null
                || player.getExperience() < 0
                || player.getExperience() > 10000000)
            return false;
        logger.info("input params for player creation are valid:\n" + player);
        return true;


    }

    private PlayerSearchCriteria getPlayerSearchCriteriaInstance(
            String name,
            String title,
            Race race,
            Profession profession,
            Long after,
            Long before,
            Boolean banned,
            Integer minExperience,
            Integer maxExperience,
            Integer minLevel,
            Integer maxLevel
    ) {
        PlayerSearchCriteria playerSearchCriteria = new PlayerSearchCriteria();
        if (name != null) playerSearchCriteria.setName(name);
        if (title != null) playerSearchCriteria.setTitle(title);
        if (race != null) playerSearchCriteria.setRace(race);
        if (profession != null) playerSearchCriteria.setProfession(profession);
        if (after != null) playerSearchCriteria.setAfter(after);
        if (before != null) playerSearchCriteria.setBefore(before);
        if (banned != null) playerSearchCriteria.setBanned(banned);
        if (minExperience != null) playerSearchCriteria.setMinExperience(minExperience);
        if (maxExperience != null) playerSearchCriteria.setMaxExperience(maxExperience);
        if (maxExperience != null) playerSearchCriteria.setMaxExperience(maxExperience);
        if (minLevel != null) playerSearchCriteria.setMinLevel(minLevel);
        if (maxLevel != null) playerSearchCriteria.setMaxLevel(maxLevel);

        return playerSearchCriteria;

    }

    private int getYearFromLong(Long dateInLong) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateInLong);
        return cal.get(Calendar.YEAR);
    }
}
//4. Если параметр order не указан – нужно использовать значение PlayerOrder.ID.
//5. Если параметр pageNumber не указан – нужно использовать значение 0.
//6. Если параметр pageSize не указан – нужно использовать значение 3.
//7. Не валидным считается id, если он:
//- не числовой
//- не целое число
//- не положительный
//8. При передаче границ диапазонов (параметры с именами,
//      которые начинаются на «min» или «max») границы нужно использовать включительно.