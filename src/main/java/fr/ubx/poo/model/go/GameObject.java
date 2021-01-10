/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.Entity;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

/***
 * A GameObject can acces the game and knows its position in the grid.
 */
public abstract class GameObject extends Entity {
    protected final Game game;
    private Position position;


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public GameObject(Game game, Position position) {
        this.game = game;
        this.position = position;
    }

    public boolean canWalkOn(Player player) {
        return false;
    }

    public boolean canWalkOn(Monster monster) {
        return false;
    }

    public void action (Player Player, Game game, Position pos){}
}
