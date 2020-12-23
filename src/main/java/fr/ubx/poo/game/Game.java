/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import fr.ubx.poo.model.decor.DoorNextOpened;
import fr.ubx.poo.model.decor.DoorPrevOpened;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Game {

    private final List<World> worldList = new ArrayList<>();
    private Player player;
    private final List<Monster> monsters = new ArrayList<>() ;
    private final String worldPath;
    private int level = 0;
    private boolean levelChanged = false;
    public int initPlayerLives;
    public String initWorldPrefix;
    public int initWorldLevels;

    public Game(String worldPath) {
        Position positionPlayer;
        this.worldPath = worldPath;
        loadConfig(worldPath);
        for (int i = 0; i < this.initWorldLevels; i++ ) {
            this.worldList.add(new World(this.loadLevel(i + 1, this.worldPath)));
        }
        try {
            for (Position p : worldList.get(this.level).findMonsters()) {
                Monster monster = new Monster(this,p) ;
                monsters.add(monster) ;
            }
            positionPlayer = worldList.get(this.level).findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    private void loadConfig(String path) {
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            this.initWorldPrefix = prop.getProperty("prefix", "level");
            this.initWorldLevels = Integer.parseInt(prop.getProperty("levels", "0"));
        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }

    private WorldEntity[][] loadLevel(int level_id, String path) {
        WorldEntity[][] defaultMapEntities =
                    {
                            {WorldEntity.Empty, WorldEntity.Empty, WorldEntity.Empty},
                            {WorldEntity.Empty, WorldEntity.Player, WorldEntity.Empty},
                            {WorldEntity.Empty, WorldEntity.Empty, WorldEntity.Empty}
                    };
        String FILEPATH = path + "/" + this.initWorldPrefix + level_id + ".txt";

        try {
            BufferedReader input = new BufferedReader(new FileReader(FILEPATH));
            String line;
            int n_line = 0;

            while (input.readLine() != null) {
                n_line++;
            }
            input.close();

            WorldEntity[][] mapEntities = new WorldEntity[n_line][];
            input = new BufferedReader(new FileReader(FILEPATH));
            n_line = 0;

            while ((line = input.readLine())!= null){
                WorldEntity[] entities_inline = new WorldEntity[line.length()];
                for (int i = 0; i < line.length(); i++) {
                    switch (line.charAt(i)) {
                        case 'B' -> entities_inline[i] = WorldEntity.Box;
                        case 'H' -> entities_inline[i] = WorldEntity.Heart;
                        case 'K' -> entities_inline[i] = WorldEntity.Key;
                        case 'M' -> entities_inline[i] = WorldEntity.Monster;
                        case 'V' -> entities_inline[i] = WorldEntity.DoorPrevOpened;
                        case 'N' -> entities_inline[i] = WorldEntity.DoorNextOpened;
                        case 'n' -> entities_inline[i] = WorldEntity.DoorNextClosed;
                        case 'P' -> entities_inline[i] = WorldEntity.Player;
                        case 'S' -> entities_inline[i] = WorldEntity.Stone;
                        case 'T' -> entities_inline[i] = WorldEntity.Tree;
                        case 'W' -> entities_inline[i] = WorldEntity.Princess;
                        case '>' -> entities_inline[i] = WorldEntity.BombRangeInc;
                        case '<' -> entities_inline[i] = WorldEntity.BombRangeDec;
                        case '+' -> entities_inline[i] = WorldEntity.BombNumberInc;
                        case '-' -> entities_inline[i] = WorldEntity.BombNumberDec;
                        case '_' -> entities_inline[i] = WorldEntity.Empty;
                        default -> throw new IllegalStateException("Unexpected value: " + line.charAt(i));
                    }
                }
                mapEntities[n_line] = entities_inline;
                n_line++;
            }

            input.close();
            return  mapEntities;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return defaultMapEntities;
    }

    public World getWorld() {
        return worldList.get(this.level);
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Monster> getMonsters() {
        return this.monsters;
    }

    public int getLevel() {
        return level;
    }

    public boolean isLevelChanged() {
        return this.levelChanged;
    }

    public  void setLevelChanged(boolean change) {
        this.levelChanged = change;
    }

    public void incLevel() throws LevelOutOfRangeException {
        if (this.level < this.initWorldLevels) {
            this.level ++;
            this.setLevelChanged(true);
        } else {
            String message = "Can't reach next level.";
            throw new LevelOutOfRangeException(message);
        }
    }

    public void decLevel() throws LevelOutOfRangeException {
        if (this.level > 0) {
            this.level--;
            this.setLevelChanged(true);
        } else {
            String message = "Can't reach previous level.";
            throw new LevelOutOfRangeException(message);
        }
    }

    public void nextWorld(int mode) {
        try {
            switch (mode) {
                case 0 -> {
                    this.incLevel();
                    this.worldList.get(this.level).forEach((position, decor) -> {
                        if (decor instanceof DoorPrevOpened) {
                            this.setPlayer(this, position);
                        }
                    });
                }
                case 1 -> {
                    this.decLevel();
                    this.worldList.get(this.level).forEach((position, decor) -> {
                        if (decor instanceof DoorNextOpened) {
                            this.setPlayer(this, position);
                        }
                    });
                }
                default -> throw new Exception("Wrong mode !");
            }
            this.monsters.clear();
            this.worldList.get(this.level).findMonsters()
                    .forEach(position -> this.monsters.add(new Monster(this, position)));

        } catch (LevelOutOfRangeException | Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void setPlayer(Game game, Position position) {
        Player player = new Player(game, position);
        player.setBombrange(this.player.getBombrange());
        player.setLives(this.player.getLives());
        player.setNumberofBombs(this.player.getNumberofBombs());
        player.setNumberOfKeys(this.player.getNumberOfKeys());
        this.player = player;
    }
}
