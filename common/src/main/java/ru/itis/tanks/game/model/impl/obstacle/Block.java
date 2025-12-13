package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractGameObject;

public class Block extends AbstractGameObject {

    protected final static int DEFAULT_BLOCK_SIZE = 32;

    public Block(long x, long y, int width, int height) {
        super(x, y, width, height);
    }

    public Block(long x, long y) {
        this(x, y, DEFAULT_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
    }
}
