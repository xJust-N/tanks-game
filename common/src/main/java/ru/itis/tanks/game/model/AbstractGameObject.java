package ru.itis.tanks.game.model;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.impl.Texture;

@Getter
@Setter
public abstract class AbstractGameObject implements GameObject{

    //Позиция левого верхнего угла объекта
    protected int x;

    protected int y;

    protected int width;

    protected int height;

    protected Texture texture;

    public AbstractGameObject(int x, int y, int width, int height, Texture texture) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public AbstractGameObject(int x, int y, int width, int height) {
        this(x, y, width, height, Texture.MISSING);
    }


    @Override
    public boolean intersects(GameObject other) {
        return x < other.getX() + other.getWidth() &&
                x + getWidth() > other.getX() &&
                y < other.getY() + other.getHeight() &&
                y + height > other.getY();
    }
}
