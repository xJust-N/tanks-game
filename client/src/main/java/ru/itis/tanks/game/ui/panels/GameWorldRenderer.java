package ru.itis.tanks.game.ui.panels;

import lombok.Getter;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventListener;
import ru.itis.tanks.game.model.map.updates.GameEventType;
import ru.itis.tanks.game.ui.graphics.GraphicalComponent;
import ru.itis.tanks.game.ui.graphics.GraphicalComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class GameWorldRenderer extends JPanel implements GameEventListener {

    private final GameWorld world;

    private List<GraphicalComponent> graphicalComponents;

    public GameWorldRenderer(GameWorld world) {
        this.world = world;
        graphicalComponents = new ArrayList<>();
        init();
    }
    private void init() {
        setPreferredSize(
                new Dimension(world.getWidth(), world.getHeight())
        );
        setVisible(true);
        setBackground(Color.GRAY);
    }

    //Использование графикс согласно требованиям
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            for (GraphicalComponent component : graphicalComponents) {
                int x = toInt(component.getX());
                int y = toInt(component.getY());
                int w = toInt(component.getWidth());
                int h = toInt(component.getHeight());
                drawComponentImage(g2d, component, x, y, w, h);
                component.getOverlays().forEach(overlay -> overlay.drawObjectOverlay(g2d));
            }
        } finally {
            g2d.dispose();
        }
    }

    private void drawComponentImage(Graphics2D g2d, GraphicalComponent component, int x, int y, int w, int h) {
        double radianAngle = component.getCurrentImageAngle();
        //немного линейной геометрии: перенос и поворот
        AffineTransform tx = new AffineTransform();
        tx.translate(x + w / 2d, y + h / 2d);
        tx.rotate(radianAngle);
        Image img = component.getImg();
        int imgWidth = img.getWidth(null);
        int imgHeight = img.getHeight(null);
        tx.scale((double) w / imgWidth, (double) h / imgHeight);
        tx.translate(-imgWidth / 2.0, -imgHeight / 2.0);
        g2d.drawImage(img, tx, this);
    }

    private int toInt(int x) {
        return Math.toIntExact(x);
    }


    public void updateGraphicalComponents() {
        graphicalComponents = world.getAllObjects()
                .stream()
                .map(GraphicalComponentFactory::createComponent)
                .toList();
    }

    public void refresh(){
        repaint();
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event.getType() == GameEventType.REMOVED_OBJECT
                || event.getType() == GameEventType.ADDED_OBJECT
                || event.getType() == GameEventType.MODIFIED_OBJECT)
            updateGraphicalComponents();
    }
}