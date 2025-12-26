package ru.itis.tanks.game.ui.graphics.overlays;


import ru.itis.tanks.game.model.Destroyable;

import java.awt.*;

public class HealthBarOverlay implements Overlay {

    private static final int WIDTH_DIVIDER = 100;

    private static final int HEALTH_HEALTH_OFFSET_Y = 10;

    private final Destroyable obj;

    public HealthBarOverlay(Destroyable obj) {
        this.obj = obj;
    }

    @Override
    public void drawObjectOverlay(Graphics2D g2d) {
        int healthBarWidth = obj.getWidth() * obj.getMaxHp() / WIDTH_DIVIDER;
        int healthBarHeight = 5;
        int healthBarX = (int) (obj.getX() - Math.abs(healthBarWidth - obj.getWidth()) / 2);
        int healthBarY = (int) (obj.getY() - HEALTH_HEALTH_OFFSET_Y);
        g2d.setColor(Color.RED);
        g2d.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        int currentHealth = obj.getHp();
        int maxHealth = obj.getMaxHp();
        int currentHealthWidth = (int) ((double) currentHealth / maxHealth * healthBarWidth);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(healthBarX, healthBarY, currentHealthWidth, healthBarHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
    }
}