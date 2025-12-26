package ru.itis.tanks.game.model.map;

import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.obstacle.BlockFactory;
import ru.itis.tanks.game.model.impl.obstacle.HealthPowerup;
import ru.itis.tanks.game.model.impl.obstacle.RocketGunPowerup;
import ru.itis.tanks.game.model.impl.obstacle.SpeedPowerup;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.DefaultGun;

import java.util.Random;

public class GameWorldGenerator {

    private static final int DEFAULT_WIDTH = 1030;
    private static final int DEFAULT_HEIGHT = 1030;
    private static final int TANK_SIZE = 32;
    private static final int BLOCK_SIZE = 32;
    private static final int MARGIN = 100;

    private static final int MIN_BOTS = 5;
    private static final int MAX_BOTS = 10;

    private static final int MIN_POWERUPS_PER_TYPE = 3;
    private static final int MAX_POWERUPS_PER_TYPE = 6;

    private static final int MIN_DESTROYABLE_BLOCKS = 20;
    private static final int MAX_DESTROYABLE_BLOCKS = 100;
    private static final int MIN_COLLIDEABLE_BLOCKS = 5;
    private static final int MAX_COLLIDEABLE_BLOCKS = 20;

    private static final Random random = new Random();

    public static GameWorld generate() {
        return generate(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static GameWorld generate(int width, int height) {
        GameWorld world = new GameWorld(width, height);
        createBorderWalls(world, width, height);
        generateObstacles(world, width, height);
        createBots(world, width, height);
        generatePowerups(world, width, height);
        return world;
    }

    private static void createBots(GameWorld world, int worldWidth, int worldHeight) {
        int botCount = MIN_BOTS + random.nextInt(MAX_BOTS - MIN_BOTS + 1);

        for (int i = 0; i < botCount; i++) {
            Tank bot = createRandomBot(world, worldWidth, worldHeight);
            if (bot != null) {
                world.addObject(bot);
            }
        }
    }

    private static Tank createRandomBot(GameWorld world, int worldWidth, int worldHeight) {
        int attempts = 0;
        int maxAttempts = 50;

        while (attempts < maxAttempts) {
            attempts++;

            int x = MARGIN + random.nextInt(worldWidth - 2 * MARGIN - TANK_SIZE);
            int y = MARGIN + random.nextInt(worldHeight - 2 * MARGIN - TANK_SIZE);

            x = (x / BLOCK_SIZE) * BLOCK_SIZE;
            y = (y / BLOCK_SIZE) * BLOCK_SIZE;

            if (isNotOverlapping(world, x, y, TANK_SIZE, TANK_SIZE)) {
                Direction direction = Direction.values()[random.nextInt(Direction.values().length)];
                Texture texture = Texture.ENEMY_TANK;
                Tank bot = new Tank(world, x, y);
                bot.setTexture(texture);
                bot.setDirection(direction);
                bot.setUsername("Bot_" + random.nextInt(1000));
                DefaultGun gun = new DefaultGun(bot);
                bot.setGun(gun);
                bot.setCurrentPlayerTank(false);
                return bot;
            }
        }

        return null;
    }

    private static void generateObstacles(GameWorld world, int worldWidth, int worldHeight) {
        int destroyableBlocks = MIN_DESTROYABLE_BLOCKS +
                random.nextInt(MAX_DESTROYABLE_BLOCKS - MIN_DESTROYABLE_BLOCKS + 1);
        generateBlocks(world, worldWidth, worldHeight, destroyableBlocks, true);

        int collideableBlocks = MIN_COLLIDEABLE_BLOCKS +
                random.nextInt(MAX_COLLIDEABLE_BLOCKS - MIN_COLLIDEABLE_BLOCKS + 1);
        generateBlocks(world, worldWidth, worldHeight, collideableBlocks, false);
    }

    private static void generateBlocks(GameWorld world, int worldWidth, int worldHeight,
                                       int blockCount, boolean destroyable) {
        int placedBlocks = 0;
        int attempts = 0;
        int maxAttempts = blockCount * 20;

        while (placedBlocks < blockCount && attempts < maxAttempts) {
            attempts++;
            int x = BLOCK_SIZE + random.nextInt(worldWidth - 3 * BLOCK_SIZE);
            int y = BLOCK_SIZE + random.nextInt(worldHeight - 3 * BLOCK_SIZE);
            x = (x / BLOCK_SIZE) * BLOCK_SIZE;
            y = (y / BLOCK_SIZE) * BLOCK_SIZE;
            if (isNotOverlapping(world, x, y, BLOCK_SIZE, BLOCK_SIZE)) {
                if (destroyable) {
                    int maxHp = 100 + random.nextInt(201);
                    world.addObject(BlockFactory.createDestroyableBlock(world, maxHp, x, y));
                } else {
                    world.addObject(BlockFactory.createCollideableBlock(x, y));
                }
                placedBlocks++;
            }
        }
    }

    private static void generatePowerups(GameWorld world, int worldWidth, int worldHeight) {
        int healthPowerupCount = MIN_POWERUPS_PER_TYPE +
                random.nextInt(MAX_POWERUPS_PER_TYPE - MIN_POWERUPS_PER_TYPE + 1);
        generatePowerupType(world, worldWidth, worldHeight, healthPowerupCount, "health");
        int rocketPowerupCount = MIN_POWERUPS_PER_TYPE +
                random.nextInt(MAX_POWERUPS_PER_TYPE - MIN_POWERUPS_PER_TYPE + 1);
        generatePowerupType(world, worldWidth, worldHeight, rocketPowerupCount, "rocket");
        int speedPowerupCount = MIN_POWERUPS_PER_TYPE +
                random.nextInt(MAX_POWERUPS_PER_TYPE - MIN_POWERUPS_PER_TYPE + 1);
        generatePowerupType(world, worldWidth, worldHeight, speedPowerupCount, "speed");
    }

    private static void generatePowerupType(GameWorld world, int worldWidth, int worldHeight,
                                            int count, String type) {
        int placed = 0;
        int attempts = 0;
        int maxAttempts = count * 30;
        while (placed < count && attempts < maxAttempts) {
            attempts++;
            int x = MARGIN + random.nextInt(worldWidth - 2 * MARGIN);
            int y = MARGIN + random.nextInt(worldHeight - 2 * MARGIN);
            if (isNotOverlapping(world, x, y, BLOCK_SIZE, BLOCK_SIZE)) {
                switch (type) {
                    case "health":
                        world.addObject(new HealthPowerup(world, x, y));
                        placed++;
                        break;
                    case "rocket":
                        world.addObject(new RocketGunPowerup(world, x, y));
                        placed++;
                        break;
                    case "speed":
                        world.addObject(new SpeedPowerup(world, x, y));
                        placed++;
                        break;
                }
            }
        }
    }
    private static void createBorderWalls(GameWorld world, int worldWidth, int worldHeight) {
        for (int x = 0; x < worldWidth; x += BLOCK_SIZE) {
            world.addObject(BlockFactory.createCollideableBlock(x, 0));
            world.addObject(BlockFactory.createCollideableBlock(x, worldHeight - BLOCK_SIZE));
        }
        for (int y = BLOCK_SIZE; y < worldHeight - BLOCK_SIZE; y += BLOCK_SIZE) {
            world.addObject(BlockFactory.createCollideableBlock(0, y));
            world.addObject(BlockFactory.createCollideableBlock(worldWidth - BLOCK_SIZE, y));
        }
    }
    private static boolean isNotOverlapping(GameWorld world, int x, int y, int width, int height) {
        return world.getAllObjects().stream().noneMatch(obj ->
                obj.getX() < x + width &&
                        obj.getX() + obj.getWidth() > x &&
                        obj.getY() < y + height &&
                        obj.getY() + obj.getHeight() > y
        );
    }
}