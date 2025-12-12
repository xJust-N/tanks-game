package ru.itis.tanks.game.ui.renderer;

import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.ui.renderer.graphics.GraphicalComponent;
import ru.itis.tanks.game.ui.renderer.graphics.GraphicalComponentFactory;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;

public class GameRenderer extends JPanel {
    
    private final GameWorld gameWorld;

    private List<GraphicalComponent> graphicalComponents;
    
    public GameRenderer(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        init();
    }
    
    private void init() {
        setPreferredSize(
                new Dimension(toInt(gameWorld.getWidth()), toInt(gameWorld.getHeight()))
        );
        setBackground(Color.GRAY);
        updateGraphicalComponents();
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
                Image img = component.getImg();
                g2d.drawImage(img, x, y, w, h, this);
            }
        } finally {
            g2d.dispose();
        }
    }

    private int toInt(long x) {
        return Math.toIntExact(x);
    }

    public void render() {
        repaint();
    }

    private void updateGraphicalComponents() {
        graphicalComponents = gameWorld.getAllObjects()
                .stream()
                .map(GraphicalComponentFactory::createComponent)
                .toList();
    }
}