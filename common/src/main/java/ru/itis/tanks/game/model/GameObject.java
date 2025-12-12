package ru.itis.tanks.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.impl.Texture;

@AllArgsConstructor
@Getter
@Setter
public abstract class GameObject {

    //Позиция левого верхнего угла объекта
    protected long x;

    protected long y;

    protected int width;

    protected int height;

    protected Texture texture;

    public GameObject(long x, long y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = Texture.MISSING;
    }

    public boolean intersects(GameObject other) {
        return x < other.x + other.getWidth() &&
                x + getWidth() > other.x &&
                y < other.y + other.getHeight() &&
                y + height > other.y;
    }
}
