package ru.itis.tanks.game;

import lombok.Getter;
import ru.itis.tanks.game.controller.AITank;
import ru.itis.tanks.game.controller.AlternativeTankKeyHandler;
import ru.itis.tanks.game.controller.TankKeyHandler;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.tank.TankController;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.model.map.GameWorldGenerator;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventListener;
import ru.itis.tanks.game.model.map.updates.GameEventType;
import ru.itis.tanks.game.ui.GameWindow;
import ru.itis.tanks.game.ui.renderer.GameRenderer;

import java.util.List;

public class LocalMultiplayerGame implements Runnable, GameEventListener {

    private static final long UPDATE_INTERVAL_MS = 16;

    @Getter
    private final GameWorld gameWorld;

    private final Thread gameThread = new Thread(this);

    private final long updateInterval;

    private boolean isRunning;

    public LocalMultiplayerGame(GameWorld gameWorld, long updateInterval) {
        this.gameWorld = gameWorld;
        this.updateInterval = updateInterval;
        this.isRunning = false;
    }

    public static void main(String[] args) {
        new LocalMultiplayerGame(GameWorldGenerator.generate(), UPDATE_INTERVAL_MS).startGame();
    }

    public void startGame() {
        GameRenderer gameRenderer = new GameRenderer(gameWorld);
        GameWindow gameWindow = new GameWindow(gameRenderer);
        List<TankController> tankControllers = gameWorld.getTanks().stream()
                .map(TankController::new)
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
        gameThread.start();
        isRunning = true;
        while (isRunning) {
            gameWindow.updateGameWorld();
            tankControllers.forEach(TankController::processCommands);
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void run() {
        long lastUpdateTime = System.currentTimeMillis();
        while (isRunning) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastUpdateTime;

            if (deltaTime >= updateInterval) {
                update(deltaTime);
                lastUpdateTime = currentTime;
            }
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop(){
        gameThread.interrupt();
        isRunning = false;
    }

    private void update(long delta) {
        gameWorld.getUpdatables().forEach(updatable -> {
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