package ru.itis.tanks.game.model.impl.obstacle;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.Collideable;

@Getter
@Setter
public class CollideableBlock extends Block implements Collideable{

    public CollideableBlock(long x, long y) {
        this(x, y, DEFAULT_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
    }

    public CollideableBlock(long x, long y, int width, int height) {
        super(x, y, width, height);
    }

}
