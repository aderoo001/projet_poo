package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.GameObject;

public class Monster extends GameObject {

    public Monster(Game game, Position position){
        super(game,position) ;
    }
    public void action (Player player, Game game, Position pos){
        player.setLives (player.getLives() - 1);
    }
}
