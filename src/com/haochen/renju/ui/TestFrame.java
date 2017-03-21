package com.haochen.renju.ui;

import com.haochen.renju.calculate.ai.AI;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.storage.Board;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private Mediator mediator;

    public TestFrame() {
        GridBagLayout g = new GridBagLayout();
        this.setLayout(g);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        TestMenuBar menuBar = new TestMenuBar(this);
        this.setJMenuBar(menuBar);

        BoardPanel panel = new BoardPanel();
        StateBar stateBar = new StateBar();

        mediator = new Mediator();
        mediator.setDisplay(panel);
        mediator.setCalculate(new AI());
        mediator.setPrinter(stateBar);
        mediator.setStorage(new Board());

        menuBar.setMediator(mediator);

        this.add(panel);
        this.add(stateBar);
        g.setConstraints(panel, new GBC(0, 0).setFill(GBC.BOTH));
        g.setConstraints(stateBar, new GBC(0, 1).setFill(GBC.BOTH).setAnchor(GBC.WEST));

        pack();
    }
    
    public void launch() {
        mediator.getOperator().launch();
    }

    public Mediator getMediator() {
        return mediator;
    }
}
