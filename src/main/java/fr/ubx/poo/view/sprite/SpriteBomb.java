package fr.ubx.poo.view.sprite;

import fr.ubx.poo.model.go.Bomb.Bomb;
import fr.ubx.poo.model.go.Bomb.State;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;

public class SpriteBomb extends SpriteGameObject{
    public SpriteBomb(Pane layer, Bomb bomb) {
        super(layer, null, bomb);
        updateImage();
    }

    @Override
    public void updateImage() {
        Bomb bomb = (Bomb) go;
        setImage(ImageFactory.getInstance().getBomb(bomb.getState()));
    }
}

