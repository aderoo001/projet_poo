/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.DoorNextClosed;
import fr.ubx.poo.model.decor.DoorNextOpened;
import fr.ubx.poo.model.go.GameObject;

import java.util.List;

public class Player extends GameObject implements Movable {

    private boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private boolean isDamaged = false;
    private long invicibleTimer = 0;
    private int numberOfKeys = 0;
    private int lives;
    private int numberofBombs = 1;

    public void setDamaged(boolean damaged) {
        if (this.invicibleTimer == 0) {
            this.isDamaged = damaged;
        }
    }
    private int Bombrange = 1;
    private boolean winner;

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }

    public int getLives() {
        return lives;
    }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    @Override
    public void action(Player Player, Game game, Position pos) {
        Decor decor = game.getWorld().get(Player.getDirection().nextPosition(pos));
        if ((decor instanceof DoorNextClosed)
                && (Player.getNumberOfKeys() > 0)) {
            game.getWorld().set(Player.getDirection().nextPosition(pos), new DoorNextOpened());
            Player.setNumberOfKeys(Player.getNumberOfKeys() - 1);
            game.getWorld().setChanged(true);
        }
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextpos = direction.nextPosition(getPosition());
        if (nextpos.inside(game.getWorld().dimension)) {
            Decor decor = game.getWorld().get(nextpos) ;
            if ( decor == null)
                return true ;
            if (decor.canWalkOn(this))
                return true ;
            if (decor instanceof Box) {
                Position next_to_nextpos = direction.nextPosition(nextpos) ;
                if (next_to_nextpos.inside(game.getWorld().dimension)) {
                    List<Monster> monsters = game.getMonsters() ;
                    for (Monster m : monsters) {
                        if (m.getPosition().equals(next_to_nextpos)) {
                            ((Box) decor).setCanMove(false);
                            return false;
                        }
                    }
                    if (game.getWorld().isEmpty(next_to_nextpos)) {
                        ((Box) decor).setCanMove(true);
                        return true;
                    }
                }
                else {
                    ((Box) decor).setCanMove(false);
                    return false ;
                }
            }
        }
        return false;
    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        List<Monster> monsters = game.getMonsters() ;
        for (Monster m : monsters){
            if ( m.getPosition().equals(nextPos) ){
                m.action(this,game,nextPos);
                setPosition(nextPos);
            }
        }
        Decor decor = game.getWorld().get(nextPos) ;
        if (decor == null)
            setPosition(nextPos);
        else if (decor.canWalkOn(this)) {
            decor.action(this,game,nextPos);
            setPosition(nextPos);
        }
        else if (decor instanceof Box) {
            if (((Box)decor).canMove(direction)) {
                ((Box)decor).action(this,game,nextPos);
                ((Box)decor).action(this,game, direction.nextPosition(nextPos));
            }
        }
        setPosition(nextPos);
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
            moveRequested = false;
        }
        if (this.isDamaged) {
            this.setDamaged(false);
            this.setLives(this.lives - 1);
            this.invicibleTimer = now;
        }
        if (now - this.invicibleTimer >= Math.pow(10, 9)) {
            this.invicibleTimer = 0;
        }
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isAlive() {
        if (lives <= 0)
            alive = false;
        return alive;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getNumberOfKeys() {
        return numberOfKeys;
    }

    public void setNumberOfKeys(int numberOfKeys) {
        this.numberOfKeys = numberOfKeys;
    }

    public int getNumberofBombs() {
        return numberofBombs;
    }

    public void setNumberofBombs(int numberofBombs) {
        this.numberofBombs = numberofBombs;
    }

    public int getBombrange() {
        return Bombrange;
    }

    public void setBombrange(int bombrange) {
        Bombrange = bombrange;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}
