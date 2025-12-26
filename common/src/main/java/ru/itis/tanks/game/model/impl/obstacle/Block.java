package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractGameObject;
import ru.itis.tanks.game.model.impl.Texture;

public class Block extends AbstractGameObject {

    protected final static int DEFAULT_BLOCK_SIZE = 32;

    public Block(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Block(int x, int y, int width, int height, Texture texture) {
        super(x, y, width, height, texture);
    }

    public Block(int x, int y) {
        this(x, y, DEFAULT_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
    }
}
