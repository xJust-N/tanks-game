package ru.itis.tanks.game.model.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class IdManager {

    private static final AtomicInteger ID_SEQUENCE = new AtomicInteger(Integer.MIN_VALUE);

    public static int getNextId(){
        return ID_SEQUENCE.getAndIncrement();
    }
}
