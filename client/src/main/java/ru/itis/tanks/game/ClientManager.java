package ru.itis.tanks.game;

import ru.itis.tanks.game.ui.GameWindow;
import ru.itis.tanks.game.ui.model.GameMode;
import ru.itis.tanks.game.ui.model.Registration;
import ru.itis.tanks.game.ui.panels.GameModeSelectPanel;
import ru.itis.tanks.game.ui.panels.RegistrationPanel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientManager implements RegistrationListener, GameModeSelectListener {

    private final GameWindow gameWindow;

    public ClientManager() {
        gameWindow = new GameWindow(new GameModeSelectPanel(this));
    }

    public static void main(String[] args) {
        new ClientManager();
    }

    @Override
    public void onGameModeSelected(GameMode gameMode) {
        switch (gameMode) {
            case LOCAL_MULTIPLAYER -> {
                LocalMultiplayerGame game = new LocalMultiplayerGame(gameWindow);
                new Thread(game::startGame).start();
            }
            case JOIN_GAME -> gameWindow.changePanel(new RegistrationPanel(this));
        }
    }

    @Override
    public void onRegistration(Registration reg) {
        String username = reg.getUsername();
        String hostStr = reg.getHost();
        int port;
        try{
            port = parsePort(reg.getPort());
        } catch (IllegalArgumentException e) {
            onError(e.getMessage());
            return;
        }
        InetSocketAddress host;
        try {
            if ("localhost".equals(hostStr) || hostStr.isBlank())
                host = new InetSocketAddress(InetAddress.getLocalHost(), port);
            else
                host = new InetSocketAddress(InetAddress.getByName(hostStr), port);
        } catch (UnknownHostException e) {
            onError("Unknown host, try again");
            return;
        }
        if(!isAvailableServer(host)){
            onError("Server does not exists or unavailable");
            return;
        }
        gameWindow.setTitle("Tanks game - %s".formatted(username));

    }

    private void onError(String er) {
        gameWindow.showError(er);
    }
    
    private int parsePort(String portStr) throws IllegalArgumentException{
        int port;
        try{
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Port is incorrect");
        }
        if(port < 0 || port > 65535)
            throw new IllegalArgumentException("Port must be between 0 and 65535");
        return port;
    }
    
    private boolean isAvailableServer(InetSocketAddress address) {
        try (Socket socket = new Socket()) {
            socket.connect(address, 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
