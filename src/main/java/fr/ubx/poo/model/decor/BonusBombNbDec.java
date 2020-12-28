package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Player;

public class BonusBombNbDec extends Decor{
    @Override
    public String toString() {
        return "BONUS_BOMB_NB_DEC";
    }
    public boolean canWalkOn(Player player) {
        return true;
    }
    public void action (Player player, Game game, Position pos) {
        if (player.getNumberofBombs() > 1)
            player.setNumberofBombs(player.getNumberofBombs() - 1);
        game.getWorld().clear(pos);
        game.getWorld().setChanged(true);
    }
}
