package ru.itis.tanks.protocol;

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
