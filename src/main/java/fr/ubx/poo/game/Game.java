/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.*;
import java.util.Properties;

import fr.ubx.poo.model.go.character.Player;

public class Game {

    private final World world;
    private final Player player;
    private final String worldPath;
    public int initPlayerLives;
    public String initWorldPrefix;
    public int initWorldLevels;

    public Game(String worldPath) {
        this.worldPath = worldPath;
        loadConfig(worldPath);
        world = new World(this.loadLevel(1, this.worldPath));
        Position positionPlayer = null;
        try {
            positionPlayer = world.findPlayer();
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
                    if (line.charAt(i) == 'B') entities_inline[i] = WorldEntity.Box;
                    else if (line.charAt(i) == 'H') entities_inline[i] = WorldEntity.Heart;
                    else if (line.charAt(i) == 'K') entities_inline[i] = WorldEntity.Key;
                    else if (line.charAt(i) == 'M') entities_inline[i] = WorldEntity.Monster;
                    else if (line.charAt(i) == 'V') entities_inline[i] = WorldEntity.DoorPrevOpened;
                    else if (line.charAt(i) == 'N') entities_inline[i] = WorldEntity.DoorNextOpened;
                    else if (line.charAt(i) == 'n') entities_inline[i] = WorldEntity.DoorNextClosed;
                    else if (line.charAt(i) == 'P') entities_inline[i] = WorldEntity.Player;
                    else if (line.charAt(i) == 'S') entities_inline[i] = WorldEntity.Stone;
                    else if (line.charAt(i) == 'T') entities_inline[i] = WorldEntity.Tree;
                    else if (line.charAt(i) == 'W') entities_inline[i] = WorldEntity.Princess;
                    else if (line.charAt(i) == '>') entities_inline[i] = WorldEntity.BombRangeInc;
                    else if (line.charAt(i) == '<') entities_inline[i] = WorldEntity.BombRangeDec;
                    else if (line.charAt(i) == '+') entities_inline[i] = WorldEntity.BombNumberInc;
                    else if (line.charAt(i) == '-') entities_inline[i] = WorldEntity.BombNumberDec;
                    else entities_inline[i] = WorldEntity.Empty;
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
        return world;
    }

    public Player getPlayer() {
        return this.player;
    }


}
