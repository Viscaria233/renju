package com.haochen.renju.ui;

import com.haochen.renju.control.Mediator;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Haochen on 2017/3/21.
 * TODO:
 */
class StateBar extends JPanel implements Mediator.Printer {
//    private static final String LOADING = "Loading......";
//    private static final String READY = "Ready";

    private JLabel label = new JLabel("", SwingConstants.LEFT);

    private Mediator mediator;

    StateBar() {
        setPreferredSize(new Dimension(500, 20));
        setSize(new Dimension(500, 20));
        setBackground(Color.GRAY);
        setLayout(new BorderLayout());
        add(label);
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void setMessage(String message, Color color) {
        label.setForeground(color);
        label.setText(message);
    }
}
