package ru.itis.tanks.game.model;

import ru.itis.tanks.game.model.impl.Texture;

//Верхушка иерархии сущностей
public interface GameObject {

    long getX();

    long getY();

    int getWidth();

    int getHeight();

    void setX(long x);

    void setY(long y);

    Texture getTexture();

    void setTexture(Texture texture);

    boolean intersects(GameObject other);
}
