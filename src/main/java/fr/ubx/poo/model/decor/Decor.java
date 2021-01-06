/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Entity;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

/***
 * A decor is an element that does not know its own position in the grid.
 */
public class Decor extends Entity {
    public boolean canWalkOn (Player player){
        return false ;
    }

    public boolean canWalkOn (Monster monster){
        return false ;
    }

    public void action (Player player, Game game, Position pos) {}

    public boolean destroy (Game game,Position pos) {return false ;}
}
