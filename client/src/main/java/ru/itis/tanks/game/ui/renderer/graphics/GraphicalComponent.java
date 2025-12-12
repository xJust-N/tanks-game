package ru.itis.tanks.game.ui.renderer.graphics;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.GameObject;

import java.awt.Image;

@Getter
@Setter
public class GraphicalComponent {

    private final GameObject object;

    private Image img;

    public GraphicalComponent(GameObject object) {
        this.object = object;
    }

    public long getX() {
        return object.getX();
    }

    public long getY() {
        return object.getY();
    }

    public long getWidth() {
        return object.getWidth();
    }

    public long getHeight() {
        return object.getHeight();
    }
}
