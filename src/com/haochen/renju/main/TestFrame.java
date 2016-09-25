package com.haochen.renju.main;

import com.haochen.renju.common.Mediator;
import com.haochen.renju.exception.ReadFileException;
import com.haochen.renju.ui.BoardPanel;
import com.haochen.renju.ui.GBC;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JFrame {

    /**  
     * @Fields serialVersionUID :  
     */ 
    private static final long serialVersionUID = 1L;
    
//    private AI ai;
    private BoardPanel panel;
//    private Board board;
    private Mediator mediator;
    
    private TestMenuBar menuBar;
    private GridBagLayout g = new GridBagLayout();
    
    public TestFrame() throws ReadFileException {
        this.setLayout(g);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        menuBar = new TestMenuBar(this);
        this.setJMenuBar(menuBar);
        
//        ai = new AI();
        panel = new BoardPanel();
//        board = new Board();
        mediator = new Mediator(panel);
        menuBar.setMediator(mediator);

        this.add(panel);
        g.setConstraints(panel, new GBC(0, 0).setFill(GBC.BOTH));
        
        pack();
    }
    
    public void luanch() {
        mediator.response("launch", null);
    }
    
}
