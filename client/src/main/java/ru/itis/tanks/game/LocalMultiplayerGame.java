package ru.itis.tanks.game;

import lombok.Getter;
import ru.itis.tanks.game.model.impl.tank.AITank;
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
import ru.itis.tanks.game.ui.panels.GameWorldRenderer;

import java.util.ArrayList;
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
        GameWorldRenderer gameRenderer =
                new GameWorldRenderer(gameWorld);
        gameWindow.changePanel(gameRenderer);
        List<ServerTankController> tankControllers = new ArrayList<>(
                gameWorld.getTanks().values().stream()
                .map(ServerTankController::new)
                .toList());
        tankControllers.forEach(c -> new AITank(c).start());

        Tank firstPlayerTank = new Tank(gameWorld, gameWorld.getWidth() / 2 - 20, gameWorld.getHeight() / 2);
        firstPlayerTank.setTexture(Texture.PLAYER_TANK);
        tankControllers.add(new ServerTankController(firstPlayerTank));
        gameWindow.addKeyListener(new TankKeyHandler(tankControllers.getLast()));

        Tank secondPlayerTank = new Tank(gameWorld, gameWorld.getWidth() / 2 + 20, gameWorld.getHeight() / 2);
        secondPlayerTank.setTexture(Texture.PLAYER_TANK);
        tankControllers.add(new ServerTankController(secondPlayerTank));
        gameWindow.addKeyListener(new AlternativeTankKeyHandler(tankControllers.getLast()));

        gameWorld.addObject(firstPlayerTank);
        gameWorld.addObject(secondPlayerTank);

        gameRenderer.updateGraphicalComponents();
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