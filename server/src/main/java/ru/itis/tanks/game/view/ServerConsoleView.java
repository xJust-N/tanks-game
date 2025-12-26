package ru.itis.tanks.game.view;

import java.util.Scanner;

public class ServerConsoleView implements ServerView {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public int getPort() {
        show("Enter port: ");
        String intStr = scanner.nextLine();
        try {
            int port = Integer.parseInt(intStr);
            if (port < 0 || port > 65535)
                throw new NumberFormatException();
            return port;
        } catch (NumberFormatException e) {
            show("Enter a valid port");
            return getPort();
        }
    }

    @Override
    public void show(String s) {
        System.out.println(s);
    }

    @Override
    public void showError(String s) {
        show("Error: " + s);
    }
}