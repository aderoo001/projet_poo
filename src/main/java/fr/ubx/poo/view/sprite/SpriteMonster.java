package fr.ubx.poo.view.sprite;

import fr.ubx.poo.model.go.character.Monster;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteMonster extends SpriteGameObject {

    public SpriteMonster(Pane layer, Image image , Monster monster) { super(layer , image, monster);    }

    @Override
    public void updateImage() {}

}
