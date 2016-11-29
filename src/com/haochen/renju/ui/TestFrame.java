package com.haochen.renju.ui;

import com.haochen.renju.control.Mediator;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private BoardPanel panel;
    private Mediator mediator;
    
    private TestMenuBar menuBar;
    private GridBagLayout g = new GridBagLayout();
    
    public TestFrame() {
        this.setLayout(g);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        menuBar = new TestMenuBar(this);
        this.setJMenuBar(menuBar);

        panel = new BoardPanel();
        mediator = new Mediator(panel);
        menuBar.setMediator(mediator);

        this.add(panel);
        g.setConstraints(panel, new GBC(0, 0).setFill(GBC.BOTH));
        
        pack();
    }
    
    public void launch() {
        mediator.getOperator().launch();
    }

    public Mediator getMediator() {
        return mediator;
    }
}
