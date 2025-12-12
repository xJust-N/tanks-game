package ru.itis.tanks.game.model.impl;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.Collideable;
import ru.itis.tanks.game.model.GameObject;

@Getter
@Setter
public class Block extends GameObject implements Collideable{

    private final static int DEFAULT_BLOCK_SIZE = 32;

    public Block(long x, long y) {
        this(x, y, DEFAULT_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
    }

    public Block(long x, long y, int width, int height) {
        super(x, y, width, height);
    }

}
