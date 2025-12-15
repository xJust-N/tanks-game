package ru.itis.tanks.game.ui.renderer.graphics;

import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.ui.renderer.graphics.overlays.HealthBarOverlay;
import ru.itis.tanks.game.ui.renderer.graphics.overlays.ReloadOverlay;

public class GraphicalComponentFactory {

    private static final TextureManager manager = new TextureManager();

    public static GraphicalComponent createComponent(GameObject obj){
        GraphicalComponent component = new GraphicalComponent(obj);
        component.setImg(manager.getTexture(obj.getTexture()));
        if (obj instanceof Tank tank) {
            component.addOverlay(new HealthBarOverlay(tank));
            component.addOverlay(new ReloadOverlay(tank));
        }
        return component;
    }
}
