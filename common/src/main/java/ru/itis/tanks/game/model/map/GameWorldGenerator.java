package ru.itis.tanks.game.model.map;

import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.*;
import ru.itis.tanks.game.model.impl.obstacle.BlockFactory;
import ru.itis.tanks.game.model.impl.obstacle.HealthPowerup;
import ru.itis.tanks.game.model.impl.obstacle.RocketGunPowerup;
import ru.itis.tanks.game.model.impl.obstacle.SpeedPowerup;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.DefaultGun;

import java.util.Random;

public class GameWorldGenerator {

    private static final int WORLD_WIDTH = 1024;
    private static final int WORLD_HEIGHT = 1024;
    private static final int TANK_SIZE = 32;
    private static final int BLOCK_SIZE = 32;
    private static final int MARGIN = 100;

    private static final Random random = new Random();

    public static ServerGameWorld generate() {
        ServerGameWorld world = new ServerGameWorld(WORLD_WIDTH, WORLD_HEIGHT);
        createBorderWalls(world);
        generateObstacles(world);
        int tankCount = 5 + random.nextInt(2);
        for (int i = 0; i < tankCount; i++) {
            createRandomTank(world, i);
        }
        world.addObject(new HealthPowerup(world, 300, 400));
        world.addObject(new RocketGunPowerup(world, 400, 500));
        world.addObject(new SpeedPowerup(world, 500, 700));
        return world;
    }

    private static void createRandomTank(ServerGameWorld world, int index) {
        int x, y;
        int attempts = 0;
        int maxAttempts = 100;
        x = (random.nextInt((int) WORLD_HEIGHT) % (WORLD_WIDTH - 2 * MARGIN)) + MARGIN;
        y = (random.nextInt((int) WORLD_HEIGHT) % (WORLD_HEIGHT - 2 * MARGIN)) + MARGIN;
        while (attempts < maxAttempts && isOverlapping(world, x, y, TANK_SIZE, TANK_SIZE)) {
            attempts++;
            x = (random.nextInt((int) WORLD_HEIGHT) % (WORLD_WIDTH - 2 * MARGIN)) + MARGIN;
            y = (random.nextInt((int) WORLD_HEIGHT) % (WORLD_HEIGHT - 2 * MARGIN)) + MARGIN;
            x = (x / BLOCK_SIZE) * BLOCK_SIZE;
            y = (y / BLOCK_SIZE) * BLOCK_SIZE;

        }
        Texture texture = Texture.ENEMY_TANK;
        Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
        Tank tank = new Tank(world, x, y);
        DefaultGun gun = new DefaultGun(tank);
        tank.setGun(gun);
        tank.setTexture(texture);
        tank.setDirection(direction);
        tank.setCurrentPlayerTank(index == 0);
        world.addObject(tank);
    }

    private static void generateObstacles(ServerGameWorld world) {
        int totalBlocks = 100 + random.nextInt(50);
        int placedBlocks = 0;
        int attempts = 0;
        int maxAttempts = totalBlocks * 10;
        while (placedBlocks < totalBlocks && attempts < maxAttempts) {
            attempts++;
            int x = (random.nextInt() % (WORLD_WIDTH - 2 * BLOCK_SIZE)) + BLOCK_SIZE;
            int y = (random.nextInt() % (WORLD_HEIGHT - 2 * BLOCK_SIZE)) + BLOCK_SIZE;
            x = (x / BLOCK_SIZE) * BLOCK_SIZE;
            y = (y / BLOCK_SIZE) * BLOCK_SIZE;
            if (!isOverlapping(world, x, y, BLOCK_SIZE, BLOCK_SIZE)) {
                world.addObject(BlockFactory.createRandomBlock(world, x, y));
                placedBlocks++;
            }
        }
    }

    private static void createBorderWalls(ServerGameWorld world) {
        for (int x = 0; x < WORLD_WIDTH; x += BLOCK_SIZE) {
            world.addObject(BlockFactory.createCollideableBlock(x, 0));
            world.addObject(BlockFactory.createCollideableBlock(x, WORLD_HEIGHT - BLOCK_SIZE));
        }
        for (int y = BLOCK_SIZE; y < WORLD_HEIGHT - BLOCK_SIZE; y += BLOCK_SIZE) {
            world.addObject(BlockFactory.createCollideableBlock(0, y));
            world.addObject(BlockFactory.createCollideableBlock(WORLD_WIDTH - BLOCK_SIZE, y));
        }
    }

    private static boolean isOverlapping(ServerGameWorld world, int x, int y, int width, int height) {
        return world.getAllObjects().stream().anyMatch(obj ->
                obj.getX() < x + width &&
                        obj.getX() + obj.getWidth() > x &&
                        obj.getY() < y + height &&
                        obj.getY() + obj.getHeight() > y
        );
    }
}