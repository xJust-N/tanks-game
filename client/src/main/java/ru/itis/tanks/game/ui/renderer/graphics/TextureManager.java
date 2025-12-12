package ru.itis.tanks.game.ui.renderer.graphics;

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

    private static final Map<Texture, String> FILES = Map.of(
            PLAYER_TANK, "player_tank.png",
            ENEMY_TANK, "enemy_tank.png",
            ROCK, "rock.png",
            STEEL, "steel.png",
            GRASS, "grass.png"
    );

    private final Map<Texture, BufferedImage> images = new EnumMap<>(Texture.class);

    @Getter
    private BufferedImage missingTexture;

    public TextureManager() {
        createMissingTexture();
        loadImages();
    }

    public BufferedImage getTexture(Texture texture) {
        BufferedImage result = images.get(texture);
        //todo убрать это потом
        if(result == null)
            System.out.printf("Нету текстуры для %s%n", texture);
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

    private void loadImages() {
        for(Texture texture : FILES.keySet()) {
            try {
                //fixme хз так можно или нет но работает
                URL resource = this.getClass().getResource(PATH_PREFIX + FILES.get(texture));
                if(resource == null)
                    throw new IOException("File %s for image %s not found"
                            .formatted(FILES.get(texture), texture));
                BufferedImage img = ImageIO.read(resource);
                images.put(texture, img);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
