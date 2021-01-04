package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Player;

public class BonusBombRangeDec extends Decor{
    @Override
    public String toString() {
        return "BONUS_BOMB_RANGE_DEC";
    }
    public boolean canWalkOn(Player player) {
        return true;
    }
    public void action (Player player, Game game, Position pos) {
        if (player.getBombrange() > 1)
            player.setBombrange(player.getBombrange() - 1);
        game.getWorld().clear(pos);
        game.getWorld().setChanged(true);
    }
    @Override
    public boolean destroy (Game game,Position pos) {
        game.getWorld().clear(pos);
        game.getWorld().setChanged(true);
        return true ;
    }
}
