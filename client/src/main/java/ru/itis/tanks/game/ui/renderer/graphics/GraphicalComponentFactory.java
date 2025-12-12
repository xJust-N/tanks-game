package ru.itis.tanks.game.ui.renderer.graphics;

import ru.itis.tanks.game.model.GameObject;

public class GraphicalComponentFactory {

    private static final TextureManager MANAGER = new TextureManager();

    public static GraphicalComponent createComponent(GameObject obj){
        GraphicalComponent component = new GraphicalComponent(obj);
        component.setImg(MANAGER.getTexture(obj.getTexture()));
        return component;
    }
}
