package ru.itis.tanks.game.model.impl.obstacle;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.Collideable;
import ru.itis.tanks.game.model.impl.Texture;

@Getter
@Setter
public class CollideableBlock extends Block implements Collideable{

    public CollideableBlock(int x, int y) {
        this(x, y, DEFAULT_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
    }

    public CollideableBlock(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public CollideableBlock(int x, int y, int width, int height, Texture texture) {
        super(x, y, width, height, texture);
    }
}
