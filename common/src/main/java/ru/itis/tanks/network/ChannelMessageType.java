package ru.itis.tanks.network;

import lombok.Getter;

//TODO
@Getter
public enum ChannelMessageType {
    JOIN(0),
    UPDATE(1);

    private final int code;

    ChannelMessageType(int code){
        this.code = code;
    }
}
