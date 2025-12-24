package ru.itis.tanks.game;

import lombok.Getter;
import ru.itis.tanks.game.controller.AITank;
import ru.itis.tanks.game.controller.AlternativeTankKeyHandler;
import ru.itis.tanks.game.controller.TankKeyHandler;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.tank.ServerTankController;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.model.map.GameWorldGenerator;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventListener;
import ru.itis.tanks.game.model.map.updates.GameEventType;
import ru.itis.tanks.game.ui.GameWindow;
import ru.itis.tanks.game.ui.panels.GameWorldPanel;

import java.util.List;

public class LocalMultiplayerGame implements GameEventListener {

    private static final int UPDATE_INTERVAL_MS = 16;

    @Getter
    private final GameWorld gameWorld;

    private final GameWindow gameWindow;

    private final Thread windowThread;

    private final int updateInterval;

    private boolean isRunning;

    public LocalMultiplayerGame(GameWorld gameWorld, GameWindow window, int updateInterval) {
        this.gameWorld = gameWorld;
        this.gameWindow = window;
        this.updateInterval = updateInterval;
        this.isRunning = false;
        windowThread = new Thread(this::gameWindowUpdate);
    }

    public LocalMultiplayerGame(GameWindow window){
        this(GameWorldGenerator.generate(), window, UPDATE_INTERVAL_MS);
    }

    public void startGame() {
        GameWorldPanel gameRenderer =
                new GameWorldPanel(gameWorld.getAllObjects(), gameWorld.getWidth(), gameWorld.getHeight());
        gameWindow.changePanel(gameRenderer);
        List<ServerTankController> tankControllers = gameWorld.getTanks().values().stream()
                .map(ServerTankController::new)
                .toList();
        gameWindow.addKeyListener(new TankKeyHandler(tankControllers.get(0)));
        gameWindow.addKeyListener(new AlternativeTankKeyHandler(tankControllers.get(1)));
        Tank firstPlayerTank = tankControllers.get(0).getTank();
        firstPlayerTank.setTexture(Texture.PLAYER_TANK);
        Tank secondPlayerTank = tankControllers.get(1).getTank();
        secondPlayerTank.setTexture(Texture.PLAYER_TANK);
        gameRenderer.updateGraphicalComponents();
        for(int i = 2; i < tankControllers.size(); i++) {
            new AITank(tankControllers.get(i)).start();
        }
        gameWorld.addWorldUpdateListener(gameRenderer);
        isRunning = true;
        windowThread.start();
        gameRenderer.updateGraphicalComponents();
        long lastUpdateTime = System.currentTimeMillis();
        while (isRunning) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastUpdateTime;
            if (deltaTime >= updateInterval) {
                update(Math.toIntExact(deltaTime));
                lastUpdateTime = currentTime;
            }
            tankControllers.forEach(ServerTankController::processCommands);
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void gameWindowUpdate(){
        while(isRunning){
            gameWindow.update();
            try{
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop(){
        isRunning = false;
    }

    private void update(int delta) {
        gameWorld.getUpdatables().values().forEach(updatable -> {
            updatable.update(delta);
        });
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if(event.getType() == GameEventType.GAME_OVER){
            stop();
        }
    }
}