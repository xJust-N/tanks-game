package ru.itis.tanks.game.ui.panels;

import ru.itis.tanks.game.ClientManager;
import ru.itis.tanks.game.RegistrationListener;
import ru.itis.tanks.game.ui.model.Registration;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationPanel extends JPanel {

    private final List<RegistrationListener> listeners;

    private final JTextField usernameField;

    private final JTextField hostField;

    private final JTextField portField;

    private final JButton joinButton;

    public RegistrationPanel() {
        this.listeners = new ArrayList<>();
        this.usernameField = new JTextField();
        this.hostField = new JTextField();
        this.portField = new JTextField();
        this.joinButton = new JButton("Join");
        init();
    }

    public RegistrationPanel(RegistrationListener listener) {
        this();
        this.listeners.add(listener);
    }

    private void init() {
        setVisible(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);
        Dimension fieldSize = new Dimension(200, 40);
        Dimension d = new Dimension(400, 400);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        usernameField.setPreferredSize(fieldSize);
        usernameField.setMaximumSize(fieldSize);
        hostField.setPreferredSize(fieldSize);
        hostField.setMaximumSize(fieldSize);
        portField.setPreferredSize(fieldSize);
        portField.setMaximumSize(fieldSize);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setAlignmentX(CENTER_ALIGNMENT);
        usernameField.setPreferredSize(fieldSize);
        usernameField.setMaximumSize(fieldSize);
        usernameField.setAlignmentX(CENTER_ALIGNMENT);
        usernameField.setAlignmentY(CENTER_ALIGNMENT);

        JLabel hostLabel = new JLabel("Host:");
        hostLabel.setAlignmentX(CENTER_ALIGNMENT);
        hostLabel.setAlignmentY(CENTER_ALIGNMENT);

        hostField.setPreferredSize(fieldSize);
        hostField.setMaximumSize(fieldSize);
        hostField.setAlignmentX(CENTER_ALIGNMENT);
        hostField.setAlignmentY(CENTER_ALIGNMENT);

        JLabel portLabel = new JLabel("Port:");
        portLabel.setAlignmentX(CENTER_ALIGNMENT);
        portLabel.setAlignmentY(CENTER_ALIGNMENT);

        portField.setPreferredSize(fieldSize);
        portField.setMaximumSize(fieldSize);
        portField.setAlignmentX(CENTER_ALIGNMENT);
        portField.setAlignmentY(CENTER_ALIGNMENT);

        joinButton.setAlignmentX(CENTER_ALIGNMENT);
        joinButton.addActionListener(_ -> {
            if (!usernameField.getText().isBlank() && !portField.getText().isBlank()) {
                Registration registration = new Registration(
                        usernameField.getText(), hostField.getText(), portField.getText());
                List<RegistrationListener> listenersCopy = new ArrayList<>(listeners);
                listenersCopy.forEach(l -> l.onRegistration(registration));
                usernameField.setText("");
                hostField.setText("");
                portField.setText("");
            }
        });

        int height = 10;
        add(Box.createVerticalStrut(height * 10));
        add(usernameLabel);
        add(usernameField);
        add(Box.createVerticalStrut(height));

        add(hostLabel);
        add(hostField);
        add(Box.createVerticalStrut(height));

        add(portLabel);
        add(portField);
        add(Box.createVerticalStrut(height));

        add(joinButton);
    }
}
