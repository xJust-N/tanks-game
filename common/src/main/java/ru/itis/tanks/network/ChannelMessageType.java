package ru.itis.tanks.network;

import lombok.Getter;

//TODO
@Getter
public enum ChannelMessageType {
    JOIN_REQUEST(0),
    ALL_MAP(1),
    ENTITY_UPDATE(2),
    COORDINATE_UPDATE(3);

    private final int code;

    ChannelMessageType(int code){
        this.code = code;
    }
}
