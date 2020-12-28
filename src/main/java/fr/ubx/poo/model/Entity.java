package fr.ubx.poo.model;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Player;

public abstract class Entity {
    public abstract boolean canWalkOn (Player player) ;
    public abstract void action (Player player, Game game, Position pos) ;
}
