package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Player;

public class Princess extends Decor{
    @Override
    public String toString() {
        return "Princess";
    }
    public boolean canWalkOn(Player player) {
        return true;
    }
    public void action (Player player, Game game, Position pos){
        player.setWinner(true);
    }
}
