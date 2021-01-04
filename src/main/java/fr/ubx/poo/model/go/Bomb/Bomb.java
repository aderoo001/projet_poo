package fr.ubx.poo.model.go.Bomb;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.model.go.character.Player;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;


public class Bomb extends GameObject {

    public boolean bomb_is_set ;
    public long timer ;
    public State state ;
    public int range ;
    public boolean bomb_has_exploded ;

    public Bomb(Game game, Position position,long now,int range) {
        super(game, position);
        bomb_is_set = true ;
        timer = now ;
        state = State.BOMB4;
        this.range = range ;
        game.getPlayer().setNumberofBombs(game.getPlayer().getNumberofBombs() - 1);
    }

    @Override
    public boolean canWalkOn(Player player) {
        return false ;
    }
    public void action (Player Player,Game game,Position pos){}

    public void update (long now) {
        long duration = now - timer ;
        double dur = duration / Math.pow(10,9) ;
        if (dur < 1)
            state = State.BOMB4;
        if ((dur >= 1) && (dur <2))
            state = State.BOMB3;
        if ((dur >= 2) && (dur <3))
            state = State.BOMB2;
        if ((dur >= 3) && (dur <4))
            state = State.BOMB1;
        if (dur >= 4)
            state = State.EXPLOSION;
        if (dur >= 4.5) {
            state = State.DONE;
            bomb_is_set = false;
        }
        if ((state == State.EXPLOSION)&&(!bomb_has_exploded)) {
            game.getPlayer().setNumberofBombs(game.getPlayer().getNumberofBombs() + 1);
            List <Boolean> destroyed = new ArrayList<>();
            List<Position> positions = new ArrayList<>();
            Direction dir[] = {Direction.N, Direction.E, Direction.S, Direction.W};
            int j = 0 ;
            for (Direction d : dir)
                positions.add(d.nextPosition(getPosition()));
            Player player = game.getPlayer();
            if (player.getPosition().equals(this.getPosition()))
                player.setLives(player.getLives() - 1);
            for (Position p : positions) {
                Decor decor = game.getWorld().get(p);
                if (player.getPosition().equals(p))
                    player.setLives(game.getPlayer().getLives() - 1);
                if (decor == null)
                    destroyed.add(j,false);
                else {
                    decor.destroy(game, p);
                    destroyed.add(j, true);
                }
                j++;
            }
            if (range == 2) {
                List<Position> second_positions = new ArrayList<>() ;
                for (Direction d : dir) {
                    second_positions.add(d.nextPosition(d.nextPosition(getPosition())));
                }
                int i = 0 ;
                for (Position p1 : second_positions){
                    Decor decor1 = game.getWorld().get(p1);
                    if ((player.getPosition().equals(p1)) && (!destroyed.get(i)))
                        player.setLives(game.getPlayer().getLives() - 1);
                    if (decor1 == null)
                        ;
                    else if (!destroyed.get(i))
                        decor1.destroy(game, p1);
                    i++;
                }
            }
            bomb_has_exploded = true ;
        }
    }

    public State getState() {
        return state;
    }

    public boolean isBomb_is_set() {
        return bomb_is_set;
    }


}
