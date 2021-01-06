package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Monster;
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

    @Override
    public boolean canWalkOn(Monster monster) {
        return !super.canWalkOn(monster);
    }

    public void action (Player player, Game game, Position pos) {
        player.setNumberOfKeys(player.getNumberOfKeys() + 1);
        game.getWorld().clear(pos);
        game.getWorld().setChanged(true);
    }
}
