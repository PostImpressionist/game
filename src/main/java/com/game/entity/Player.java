package com.game.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Table(name = "player")
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //IDENTITY
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "name", length = 12)
    private String name;

    @Column(name = "title", length = 30)
    private String title;

    @Column(name = "race", length = 20)
    @Enumerated(EnumType.STRING)
    private Race race;

    @Column(name = "profession", length = 20)
    @Enumerated(EnumType.STRING)
    private Profession profession;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "banned")
    private Boolean banned;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "level")
    private Integer level;

    @Column(name = "untilNextLevel")
    private Integer untilNextLevel;

    public Player() {
    }

    public Player(Long id, String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience, Integer level, Integer untilNextLevel) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.birthday = birthday;
        this.banned = banned;
        this.experience = experience;
        this.level = level;
        this.untilNextLevel = untilNextLevel;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Date getBirthday() {

        return birthday;
    }

    public void setBirthday(Date birthdayLong) {
        this.birthday = birthdayLong;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Players{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", race='" + race + '\'' +
                ", profession='" + profession + '\'' +
                ", birthday=" + birthday +
                ", banned=" + banned +
                ", experience=" + experience +
                ", level=" + level +
                ", untilNextLevel=" + untilNextLevel +
                '}';
    }


}