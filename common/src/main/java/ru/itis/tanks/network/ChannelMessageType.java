package ru.itis.tanks.network;

import lombok.Getter;

@Getter
public enum ChannelMessageType {
    REGISTER(0),
    ALL_MAP(1),
    ENTITY_UPDATE(2),
    MOVING_UPDATE(3),
    TANK_COMMAND(4),
    ADDED_ENTITY(5),
    REMOVED_ENTITY(6),
    GAME_OVER(7),
    COMMAND(8);


    private final int code;

    ChannelMessageType(int code){
        this.code = code;
    }

    public static ChannelMessageType fromCode(int i) {
        return ChannelMessageType.values()[i];
    }
}
