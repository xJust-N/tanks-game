package ru.itis.tanks.game.view;

public interface ServerView {
    int getPort();

    void show(String s);

    void showError(String s);
}
