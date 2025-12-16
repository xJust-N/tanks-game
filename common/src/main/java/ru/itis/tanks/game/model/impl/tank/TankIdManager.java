package ru.itis.tanks.game.model.impl.tank;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TankIdManager {

    private static final AtomicInteger ids = new AtomicInteger(Integer.MIN_VALUE);

    public static int getNextId(){
        return ids.getAndIncrement();
    }
}
