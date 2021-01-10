/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import fr.ubx.poo.model.decor.DoorNextOpened;
import fr.ubx.poo.model.decor.DoorPrevOpened;
import fr.ubx.poo.model.go.Bomb.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Game {

    private final List<World> worldList = new ArrayList<>();
    private final Player player;
    private final List<List<Bomb>> bombList = new ArrayList<>() ;
    private final List<List<Monster>> monsterList = new ArrayList<>();
    private int level = 0;
    private boolean[] levelChanged = {false, false};
    public int initPlayerLives;
    public String initWorldPrefix;
    public int initWorldLevels;
    private boolean deadmonster ;

    public Game(String worldPath) {
        Position positionPlayer;
        loadConfig(worldPath);
        try {
            for (int i = 0; i < this.initWorldLevels; i++) {
                this.worldList.add(new World(this.loadLevel(i + 1, worldPath)));
            }
            for (int i = 0; i < this.initWorldLevels; i++) {
                List<Bomb> bombs = new ArrayList<>();
                List<Monster> monsters = new ArrayList<>();
                this.worldList.get(i).findMonsters().forEach(position -> monsters.add(new Monster(this, position)));
                this.monsterList.add(monsters);
                this.bombList.add(bombs);
            }
            positionPlayer = worldList.get(this.level).findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public int getLevel() {
        return level;
    }

    /**
     * Return first element of levelChanged : false when there is no level change and
     * true otherwise.
     *
     * @return boolean
     */
    public boolean isLevelChanged() {
        return levelChanged[0];
    }

    public void setLevelChanged(boolean[] levelChanged) {
        this.levelChanged = levelChanged;
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

    /**
     * Construct two-dimensional WorlEntity array from a file accesible
     * from a given path. This two-dimensional array represents the game
     * world at the given level.
     * Return default two-dimensional array when path or file is wrong.
     *
     * @param level_id level to be loaded
     * @param path path of the file to be readed
     * @return the two-dimensional WorldEntity array
     */
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

    /**
     * Custom getter for two-dimensional List worldList. Return the simple List
     * at the position corresponding to the object's level field.
     *
     * @return ArrayList corresponding to the current world
     */
    public World getWorld() {
        return this.worldList.get(this.level);
    }

    public World getWorld (int level) {
        return this.worldList.get(level) ;
    }

    public Player getPlayer() {
        return this.player;
    }

    /**
     *  Custom getter for two-dimensional List monsterList. Return monster List
     *  corresponding to the current level.
     *
     * @return monster List corresponding to the current level.
     */
    public List<Monster> getMonsters() {
        return this.monsterList.get(this.level);
    }

    public List<Monster> getMonsters(int level) {
        return this.monsterList.get(level);
    }

    public List<Bomb> getBombs() {
        return this.bombList.get(this.level);
    }

    public List<List<Bomb>> getAllBombs() {return this.bombList ;}

    /**
     * Increment the object's level field by one.
     *
     * @throws LevelOutOfRangeException if level field is equal to {@code initWorldLevels}
     */
    public void incLevel() throws LevelOutOfRangeException {
        if (this.level < this.initWorldLevels) {
            this.level ++;

        } else {
            String message = "Can't reach next level.";
            throw new LevelOutOfRangeException(message);
        }
    }

    /**
     * Decrement the object's level field by one.
     *
     * @throws LevelOutOfRangeException if level field is equal to 0
     */
    public void decLevel() throws LevelOutOfRangeException {
        if (this.level > 0) {
            this.level--;
        } else {
            String message = "Can't reach previous level.";
            throw new LevelOutOfRangeException(message);
        }
    }

    /**
     * Increment or decrement object's level and set player's position
     * respectively on prevOpenendDoor or nextDoorOpened.
     *
     * @param mode an integer that takes the value 0 and 1, 0 correspond
     *             to an increment of the level and 1 to a decrement, in
     *             otherwise it print message on error output.
     */
    public void nextWorld(int mode) {
        try {
            switch (mode) {
                case 0 -> {
                    this.incLevel();
                    this.worldList.get(this.level).forEach((position, decor) -> {
                        if (decor instanceof DoorPrevOpened) {
                            this.player.setPosition(position);
                        }
                    });
                }
                case 1 -> {
                    this.decLevel();
                    this.worldList.get(this.level).forEach((position, decor) -> {
                        if (decor instanceof DoorNextOpened) {
                            this.player.setPosition(position);
                        }
                    });
                }
                default -> throw new Exception("Wrong mode !");
            }
        } catch (LevelOutOfRangeException | Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Update game objet's fields and call nextWorld method when levelChanged
     * is set to {true,...}. The second element of levelChanged is used to
     * determine the mode : true => inc and false => dec.
     *
     * @return true when update make change
     */
    public boolean update() {
        boolean test = false;
        if (this.isLevelChanged()) {
            int mode = 0;
            if (this.levelChanged[1]) {
                mode = 1;
            }
            this.nextWorld(mode);
            test = true;
        }
        this.levelChanged[0] = false;
        this.levelChanged[1] = false;
        return test;
    }

    public boolean isDeadmonster() {
        return deadmonster;
    }

    public void setDeadmonster(boolean deadmonster) {
        this.deadmonster = deadmonster;
    }
}
