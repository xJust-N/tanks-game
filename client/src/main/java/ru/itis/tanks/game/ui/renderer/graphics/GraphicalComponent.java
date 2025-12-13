package ru.itis.tanks.game.ui.renderer.graphics;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.AbstractGameObject;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.ui.renderer.graphics.overlays.Overlay;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GraphicalComponent {

    private final GameObject object;

    @Setter
    private Image img;

    private final List<Overlay> overlays;

    public void addOverlay(Overlay overlay) {
        overlays.add(overlay);
    }

    public GraphicalComponent(GameObject object) {
        this.object = object;
        overlays = new ArrayList<>();
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

    public double getCurrentImageAngle() {
        boolean moveable = object instanceof MovingObject;
        if (!moveable)
            return 0;
        Direction dir = ((MovingObject) object).getDirection();
        return Math.atan2(dir.getY(), dir.getX()) + Math.toRadians(90);
    }
}


