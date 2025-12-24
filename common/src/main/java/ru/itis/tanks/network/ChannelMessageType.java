package ru.itis.tanks.network;

import lombok.Getter;

//TODO
@Getter
public enum ChannelMessageType {
    JOIN_REQUEST(0),
    ALL_MAP(1),
    ENTITY_UPDATE(2),
    MOVING_UPDATE(3),
    TANK_COMMAND(4),
    ADDED_OBJECT(5),
    REMOVED_OBJECT(6);

    private final int code;

    ChannelMessageType(int code){
        this.code = code;
    }

    public static ChannelMessageType fromCode(int i) {
        return ChannelMessageType.values()[i];
    }
}
