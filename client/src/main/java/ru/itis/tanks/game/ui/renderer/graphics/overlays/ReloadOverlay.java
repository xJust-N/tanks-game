package ru.itis.tanks.game.ui.renderer.graphics.overlays;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.impl.tank.Tank;

import java.awt.*;

@AllArgsConstructor
public class ReloadOverlay implements Overlay {
    
    private final Tank tank;

    private static final int TEXT_Y_OFFSET = 13;

    private static final int FONT_SIZE = 12;

    @Override
    public void drawObjectOverlay(Graphics2D g2d) {
        if (tank.getGun() == null)
            return;
        long currentReload = tank.getReloadRemaining();
        long maxReload = tank.getGun().getReloadDelay();
        if (maxReload == 0 || currentReload <= 0)
            return;
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        String reloadText = "%d.%d"
                .formatted(currentReload / 1000, (currentReload % 1000 - currentReload % 100) / 100);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(reloadText);
        int textX = (int) (tank.getX() + tank.getWidth() / 2 - textWidth / 2);
        int textY = (int) (tank.getY() - TEXT_Y_OFFSET);
        g2d.drawString(reloadText, textX, textY);
    }
}