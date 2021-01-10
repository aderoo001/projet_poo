package fr.ubx.poo.model;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

public abstract class Entity {
    public abstract boolean canWalkOn (Player player) ;

    /**
     * @param monster object monster
     * @return true if monster can walk n
     */
    public abstract boolean canWalkOn (Monster monster) ;
    public abstract void action (Player player, Game game, Position pos) ;
}
