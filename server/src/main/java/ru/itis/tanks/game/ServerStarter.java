package ru.itis.tanks.game;

import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.view.ServerConsoleView;
import ru.itis.tanks.game.view.ServerView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@RequiredArgsConstructor
public class ServerStarter {

    public static void main(String[] args) {
        ServerView view = new ServerConsoleView();
        boolean flag = true;
        while (flag) {
            try{
                int port = view.getPort();
                SelectorSocketServer server = new SelectorSocketServer();
                server.start(new InetSocketAddress(InetAddress.getLocalHost(), port));
                flag = false;
            } catch (UnknownHostException e) {
                view.showError("Unknown host, try again");
            } catch(IllegalArgumentException e){
                view.showError("Port must be between 0 and 65535");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
