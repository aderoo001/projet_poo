package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.character.Player;

public class Box extends Decor implements Movable {
    public boolean canMove = false ;

    @Override
    public String toString() {
        return "Box";
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
    @Override
    public boolean canMove(Direction direction) {
        return canMove;
    }

    public void action (Player player, Game game, Position pos){
        Decor decor = game.getWorld().get(pos);
        if (decor == null)
            game.getWorld().set(pos,this);
        else {
            game.getWorld().clear(pos);
            game.getWorld().setChanged(true);
        }
    }
    @Override
    public void doMove(Direction direction) {
    }
    @Override
    public boolean destroy (Game game,Position pos,int level) {
        game.getWorld(level).clear(pos);
        game.getWorld(level).setChanged(true);
        return true ;
    }
}
