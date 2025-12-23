package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.ServerGameWorld;

import java.util.List;
import java.util.Random;

import static ru.itis.tanks.game.model.impl.Texture.*;

public class BlockFactory {

    private static final double NON_COLLIDE_BLOCK_CHANCE = 0.1;

    private static final double COLLIDEABLE_BLOCK_CHANCE = 0.6;

    private static final double WOOD_MATERIAL_CHANCE = 0.4;

    private static final int WOOD_BLOCK_MAX_HP = 50;

    private static final int BRICK_BLOCK_MAX_HP = 250;

    private static final Random RAND = new Random();

    public static Block createRandomBlock(ServerGameWorld world, int x, int y) {
        double chance = RAND.nextDouble();
        double counter = 0d;
        if (chance <= (counter += NON_COLLIDE_BLOCK_CHANCE))
            return createNonCollideableBlock(x, y);
        else if (chance < (counter += COLLIDEABLE_BLOCK_CHANCE))
            return createCollideableBlock(x, y);
        return createDestroyableBlock(world, x, y);
    }

    public static CollideableBlock createCollideableBlock(int x, int y) {
        CollideableBlock block = new CollideableBlock(x, y);
        block.setTexture(getTextureForCollideableBlock());
        return block;
    }

    public static DestroyableBlock createDestroyableBlock(ServerGameWorld world, int x, int y) {
        double chance = RAND.nextDouble();
        DestroyableBlock block;
        if(chance <= WOOD_MATERIAL_CHANCE){
            block = new DestroyableBlock(world, WOOD_BLOCK_MAX_HP, x, y);
            block.setTexture(WOOD);
        }
        else{
            block = new DestroyableBlock(world, BRICK_BLOCK_MAX_HP, x, y);
            block.setTexture(BRICK);
        }
        return block;
    }

    public static Block createNonCollideableBlock(int x, int y) {
        Block nonCollideableBlock = new Block(x, y);
        nonCollideableBlock.setTexture(getTextureForNonCollideableBlock());
        return nonCollideableBlock;
    }

    private static Texture getTextureForNonCollideableBlock() {
        return getRandomTexture(List.of(
                GRASS
        ));
    }

    private static Texture getTextureForCollideableBlock() {
        return getRandomTexture(List.of(
                ROCK,
                STEEL,
                COLLIDEABLE_GLASS
        ));
    }

    private static Texture getRandomTexture(List<Texture> textures) {
        return textures.get(RAND.nextInt(textures.size()));
    }
}
