package ru.itis.tanks;

import ru.itis.tanks.game.controller.AITank;
import ru.itis.tanks.game.controller.AlternativeTankKeyHandler;
import ru.itis.tanks.game.model.impl.tank.TankController;
import ru.itis.tanks.game.controller.TankKeyHandler;
import ru.itis.tanks.game.LocalMultiplayerGame;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.model.map.GameWorldGenerator;
import ru.itis.tanks.game.ui.renderer.GameRenderer;
import ru.itis.tanks.game.ui.GameWindow;

import java.util.List;

public class Main {

    private static final long UPDATE_INTERVAL_MS = 16;

    public static void main(String[] args) {
        GameWorld gameWorld = new GameWorldGenerator().generate();
        GameRenderer gameRenderer = new GameRenderer(gameWorld);
        GameWindow gameWindow = new GameWindow(gameRenderer);
        List<TankController> tankControllers = gameWorld.getTanks().stream()
                .map(TankController::new)
                .toList();
        gameWindow.addKeyListener(new TankKeyHandler(tankControllers.get(0)));
        gameWindow.addKeyListener(new AlternativeTankKeyHandler(tankControllers.get(1)));
        for(int i = 2; i < tankControllers.size(); i++) {
            new AITank(tankControllers.get(i)).start();
        }
        gameWorld.addWorldUpdateListener(gameRenderer);
        LocalMultiplayerGame game = new LocalMultiplayerGame(gameWorld, UPDATE_INTERVAL_MS);
        game.start();
        while (true) {
            gameWindow.updateGameWorld();
            tankControllers.forEach(TankController::processCommands);
            try {
                Thread.sleep(UPDATE_INTERVAL_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}