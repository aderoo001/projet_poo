package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Player;

public class Key extends Decor {
    @Override
    public String toString() {
        return "KEY";
    }

    @Override
    public boolean canWalkOn(Player player) {
        return true;
    }
    public void action (Player player, Game game, Position pos) {
        player.setNumberOfKeys(player.getNumberOfKeys() + 1);
        game.getWorld().clear(pos);
        game.getWorld().setChanged(true);
    }
}
