package ru.itis.tanks.game.model;

import ru.itis.tanks.game.model.impl.Texture;

//Верхушка иерархии сущностей
public interface GameObject {

    int getX();

    int getY();

    int getWidth();

    int getHeight();

    void setX(int x);

    void setY(int y);

    Texture getTexture();

    void setTexture(Texture texture);

    boolean intersects(GameObject other);
}
