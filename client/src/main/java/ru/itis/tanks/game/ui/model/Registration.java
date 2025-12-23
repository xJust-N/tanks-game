package ru.itis.tanks.game.ui.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

@AllArgsConstructor
@Getter
public class Registration {

    private final String username;

    private final String host;

    private final String port;
}
