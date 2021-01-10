/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.go.Bomb.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.view.sprite.Sprite;
import fr.ubx.poo.view.sprite.SpriteBomb;
import fr.ubx.poo.view.sprite.SpriteFactory;
import fr.ubx.poo.view.sprite.SpriteMonster;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private List<Monster> monsters = new ArrayList<>();
    private final List<Sprite> sprites = new ArrayList<>();
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;
    private final List<SpriteMonster> spriteMonsters = new ArrayList<>();
    private final List<SpriteBomb> spriteBombs = new ArrayList<>();

    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        this.monsters.addAll(this.game.getMonsters());
        initialize(stage, game);
        buildAndSetGameLoop();
    }

    private void initialize(Stage stage, Game game) {
        this.stage = stage;
        Group root = new Group();
        layer = new Pane();

        int height = game.getWorld().dimension.height;
        int width = game.getWorld().dimension.width;
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);
        // Create decor sprites
        game.getWorld().forEach( (pos,d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        spritePlayer = SpriteFactory.createPlayer(layer, player);
        for (Monster m : monsters)
            spriteMonsters.add(SpriteFactory.createMonster(layer,m)) ;
    }

    protected final void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);

                // Graphic update
                render();
            }
        };
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        }
        if (this.input.isKey()) {
            this.player.action(this.player, this.game, this.player.getPosition());
        }
        if (input.isMoveDown()) {
            player.requestMove(Direction.S);
        }
        if (input.isMoveLeft()) {
            player.requestMove(Direction.W);
        }
        if (input.isMoveRight()) {
            player.requestMove(Direction.E);
        }
        if (input.isMoveUp()) {
            player.requestMove(Direction.N);
        }
        if (input.isBomb()) {
            player.requestBomb(now);
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    /**
     * Update window when level changed. Remove all sprite and call
     * initialize method.
     */
    private void updateWindow() {
        this.spritePlayer.remove();
        Iterator<SpriteMonster> monsterIterator = this.spriteMonsters.iterator();
        while (monsterIterator.hasNext()) {
            monsterIterator.next().remove();
        }
        this.spriteMonsters.clear();
        Iterator<Sprite> spriteIterator = this.sprites.iterator();
        while (spriteIterator.hasNext()) {
            spriteIterator.next().remove();
        }
        this.sprites.clear();
        Iterator<SpriteBomb> bombIterator = this.spriteBombs.iterator();
        while (bombIterator.hasNext()) {
            bombIterator.next().remove();
        }
        this.spriteBombs.clear();

        this.initialize(this.stage, this.game);
    }

    private void update(long now) {
        player.update(now);
        for (int i = 0 ; i < game.initWorldLevels ; i++) {
            Iterator<Bomb> bombIterator = game.getAllBombs().get(i).iterator();
            while (bombIterator.hasNext()){
                bombIterator.next().update(now);
            }
        }
        List<Bomb> to_remove = new ArrayList<>();
        Iterator<Monster> monsterIterator = this.monsters.iterator();
        while (monsterIterator.hasNext()) {
            monsterIterator.next().update(now);
        }
        if (game.isDeadmonster()) {
            spriteMonsters.forEach(SpriteMonster::remove);
            this.spriteMonsters.clear();
            monsters = game.getMonsters() ;
            for (Monster m : monsters)
                spriteMonsters.add(SpriteFactory.createMonster(layer,m));
            game.setDeadmonster(false);
        }
        if (game.getWorld().isChanged()) {
            if (this.game.update()) {
                this.player.setPosition(this.game.getPlayer().getPosition());
                this.monsters = this.game.getMonsters();
                this.updateWindow();
            } else {
                sprites.forEach(Sprite::remove);
                sprites.clear();
                game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
                game.getWorld().setChanged(false);
            }
        }
        game.getBombs().forEach(b -> {
            if (b.isBomb_is_set() && (b.getLevel() == game.getLevel())) {
                spriteBombs.add(SpriteFactory.createBomb(layer, b));
            }
            else {
                to_remove.add(b);
            }
        });
        game.getBombs().removeAll(to_remove);
        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }
        this.statusBar.update(game);
    }

    private void render() {
        sprites.forEach(Sprite::render);
        spriteMonsters.forEach(Sprite::render);
        spriteBombs.forEach(SpriteBomb::render);
        // last rendering to have player in the foreground
        spritePlayer.render();
    }

    public void start() {
        gameLoop.start();
    }
}
