package ru.itis.tanks.game.controller;

import lombok.SneakyThrows;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.ServerTankController;

import java.util.Random;

public class AITank implements Runnable {

    private static final int ACTION_MIN_DELAY = 200;

    private static final int ACTION_MAX_DELAY = 1_000;

    private final Random rand = new Random();

    private final ServerTankController controller;

    private Thread aiThread;

    public AITank(ServerTankController controller) {
        this.controller = controller;
    }

    public void start() {
        if (aiThread != null && aiThread.isAlive()) {
            return;
        }
        aiThread = new Thread(this);
        aiThread.start();
    }

    public void stop() {
        if (aiThread != null) {
            aiThread.interrupt();
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
            Command command = getRandomCommand();
            controller.enqueueCommand(getRandomCommand());
            if(command == Command.DIRECTION_CHANGE){
                controller.setDirection(getRandomDirection());
                continue;
            }
            Thread.sleep(getRandomSleepTime());
            if(command == Command.START_MOVING) {
                controller.enqueueCommand(Command.STOP_MOVING);
            }
        }
    }

    private Command getRandomCommand() {
        return Command.values()[rand.nextInt(Command.values().length)];
    }

    private int getRandomSleepTime(){
        return ACTION_MIN_DELAY +
                rand.nextInt(ACTION_MAX_DELAY - ACTION_MIN_DELAY);
    }
    private Direction getRandomDirection(){
        return Direction.values()[rand.nextInt(Direction.values().length)];
    }
}
