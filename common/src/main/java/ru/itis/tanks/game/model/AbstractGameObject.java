package ru.itis.tanks.game.model;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.impl.Texture;

@Getter
@Setter
public abstract class AbstractGameObject implements GameObject{

    //Позиция левого верхнего угла объекта
    protected long x;

    protected long y;

    protected int width;

    protected int height;

    protected Texture texture;

    public AbstractGameObject(Texture texture, long x, long y, int width, int height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public AbstractGameObject(long x, long y, int width, int height) {
        this(Texture.MISSING, x, y, width, height);
    }


    @Override
    public boolean intersects(GameObject other) {
        return x < other.getX() + other.getWidth() &&
                x + getWidth() > other.getX() &&
                y < other.getY() + other.getHeight() &&
                y + height > other.getY();
    }
}
