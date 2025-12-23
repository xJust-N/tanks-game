package ru.itis.tanks.game.ui.graphics;

import lombok.Getter;
import ru.itis.tanks.game.model.impl.Texture;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import static ru.itis.tanks.game.model.impl.Texture.*;

public class TextureManager {

    private static final String PATH_PREFIX = "/textures/";

    private final Map<Texture, String> files = new EnumMap<>(Texture.class);

    private final Map<Texture, BufferedImage> images = new EnumMap<>(Texture.class);

    @Getter
    private BufferedImage missingTexture;

    public TextureManager() {
        createMissingTexture();
        initPaths();
        loadImages();
    }

    public BufferedImage getTexture(Texture texture) {
        BufferedImage result = images.get(texture);
        //todo убрать это потом
//        if(result == null)
//            System.out.printf("Нету текстуры для %s%n", texture);
        return result != null ? result : missingTexture;
    }

    private void createMissingTexture() {
        missingTexture = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = missingTexture.createGraphics();
        try {
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, 32, 32);
            g.setColor(Color.BLACK);
            g.drawLine(0, 0, 31, 31);
            g.drawLine(31, 0, 0, 31);
        } finally {
            g.dispose();
        }
    }

    private void initPaths() {
        files.put(PLAYER_TANK, "player_tank.png");
        files.put(ENEMY_TANK, "enemy_tank.png");
        files.put(BULLET, "default_bullet.png");
        files.put(ROCKET_BULLET, "rocket_bullet.png");
        files.put(HP_POWERUP, "hp_powerup.png");
        files.put(SPEED_POWERUP, "speed_powerup.png");
        files.put(ROCKET_GUN_POWERUP, "rocket_gun_powerup.png");
        files.put(GRASS, "grass.png");
        files.put(ROCK, "rock.png");
        files.put(STEEL, "steel.png");
        files.put(COLLIDEABLE_GLASS, "collideable_grass.png");
        files.put(WOOD, "wood_planks.png");
        files.put(BRICK, "bricks.png");
    }

    private void loadImages() {
        for(Texture texture : files.keySet()) {
            try {
                //fixme хз так можно или нет но работает
                URL resource = this.getClass().getResource(PATH_PREFIX + files.get(texture));
                if(resource == null)
                    throw new IOException("File %s for image %s not found"
                            .formatted(files.get(texture), texture));
                BufferedImage img = ImageIO.read(resource);
                images.put(texture, img);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
