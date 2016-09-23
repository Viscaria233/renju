package com.haochen.renju.main;

import java.awt.GridBagLayout;

import javax.swing.JFrame;

import com.haochen.renju.ai.AI;
import com.haochen.renju.common.Controller;
import com.haochen.renju.exception.ReadFileException;
import com.haochen.renju.form.Board;
import com.haochen.renju.ui.BoardPanel;
import com.haochen.renju.ui.GBC;

public class TestFrame extends JFrame {

    /**  
     * @Fields serialVersionUID :  
     */ 
    private static final long serialVersionUID = 1L;
    
    private AI ai;
    private BoardPanel panel;
    private Board board;
    private Controller controller;
    
    private TestMenuBar menuBar;
    private GridBagLayout g = new GridBagLayout();
    
    public TestFrame() throws ReadFileException {
        this.setLayout(g);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        menuBar = new TestMenuBar(this);
        this.setJMenuBar(menuBar);
        
        ai = new AI();
        panel = new BoardPanel();
        board = new Board();
        controller = new Controller(ai, panel, board, menuBar);
        
        this.add(panel);
        g.setConstraints(panel, new GBC(0, 0).setFill(GBC.BOTH));
        
        pack();
    }
    
    public void luanch() {
        controller.response("launch", null);
    }
    
}
