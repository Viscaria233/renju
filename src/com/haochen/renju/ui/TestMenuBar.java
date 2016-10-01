package com.haochen.renju.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.haochen.renju.control.Mediator;
import com.haochen.renju.control.player.AIPlayer;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.control.player.Player;
import com.haochen.renju.control.player.PlayerSet;
import com.haochen.renju.main.Config;
import com.haochen.renju.storage.PieceColor;

public class TestMenuBar extends JMenuBar {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private TestFrame mainFrame;

    private JMenuItem newFile;
    private JMenuItem save;
    private JMenuItem open;
    private JMenuItem exit;

    private JMenuItem findVCT;
    private JMenuItem findVCF;
    private JMenuItem stopFinding;

    private JMenuItem about;

    private JMenuItem testItem;
    private JMenuItem breakPoint;
    private JMenuItem continueEnd;
    private JMenuItem continueLength;
    private JMenuItem findAliveFour;
    private JMenuItem findAsleepFour;
    private JMenuItem findFour;
    private JMenuItem findAliveThree;
    private JMenuItem findAsleepThree;
    private JMenuItem separator;

    private JCheckBoxMenuItem usingForbidden;
    
    private JCheckBoxMenuItem aiBlack;
    private JCheckBoxMenuItem aiWhite;
    
    private JCheckBoxMenuItem aiNoUsed;
    private JCheckBoxMenuItem lvLow;
    private JCheckBoxMenuItem lvNormal;
    private JCheckBoxMenuItem lvHigh;
    
    private Mediator mediator;
    
    public TestMenuBar(TestFrame frame) {
        this.mainFrame = frame;

        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        JMenu test = new JMenu("Test");
        JMenu setting = new JMenu("Setting");
        JMenu aiColor = new JMenu("AI PieceColor");
//        ButtonGroup colorGroup = new ButtonGroup();
        JMenu aiLevel = new JMenu("AI Level");
        ButtonGroup levelGroup = new ButtonGroup();

        newFile = new JMenuItem("New");
        save = new JMenuItem("Save");
        open = new JMenuItem("Open");
        exit = new JMenuItem("Exit");

        file.add(newFile);
        file.add(save);
        file.add(open);
        file.addSeparator();
        file.add(exit);

        findVCT = new JMenuItem("Find VCT");
        findVCF = new JMenuItem("Find VCF");
        stopFinding = new JMenuItem("Stop finding");
        
        edit.add(findVCT);
        edit.add(findVCF);
        edit.add(stopFinding);

        about = new JMenuItem("About");
        help.add(about);

        testItem = new JMenuItem("Test");
        breakPoint = new JMenuItem("Break Point");
        continueEnd = new JMenuItem("Continue End");
        continueLength = new JMenuItem("Continue Length");
        findAliveFour = new JMenuItem("Find alive Four");
        findAsleepFour = new JMenuItem("Find asleep Four");
        findAliveThree = new JMenuItem("Find alive Three");
        findAsleepThree = new JMenuItem("Find asleep Three");
        findFour = new JMenuItem("Find Four");
        separator = new JMenuItem("separator");

        test.add(testItem);
        test.addSeparator();
        test.add(breakPoint);
        test.add(continueEnd);
        test.add(continueLength);
        test.addSeparator();
        test.add(findAliveFour);
        test.add(findAsleepFour);
        test.add(findFour);
        test.addSeparator();
        test.add(findAliveThree);
        test.add(findAsleepThree);
        test.addSeparator();
        test.add(separator);
        
        usingForbidden = new JCheckBoxMenuItem("Using Forbidden");
//        usingForbidden.addItemListener(new ItemListener() {
//            
//            @Override
//            public void itemStateChanged(ItemEvent arg0) {
//                boolean isUsingForbidden = TestMenuBar.this.usingForbidden.getState();
//                mainFrame.setUsingForbidden(isUsingForbidden);
//                mainFrame.chessForm.setUsingForbidden(isUsingForbidden);
//                mainFrame.updateForbiddenMarks();
//            }
//        });
        usingForbidden.setState(Config.usingForbiddenMove);
//        mainFrame.setUsingForbidden(true);
//        mainFrame.chessForm.setUsingForbidden(true);
        aiBlack = new JCheckBoxMenuItem("AI Uses Black");
        aiWhite = new JCheckBoxMenuItem("AI Uses White");
        aiNoUsed = new JCheckBoxMenuItem("No AI");
        lvLow = new JCheckBoxMenuItem("Low");
        lvNormal = new JCheckBoxMenuItem("Normal");
        lvHigh = new JCheckBoxMenuItem("High");
        
        aiColor.add(aiBlack);
        aiColor.add(aiWhite);
//        colorGroup.add(aiBlack);
//        colorGroup.add(aiWhite);

        aiLevel.add(aiNoUsed);
        aiLevel.add(lvLow);
        aiLevel.add(lvNormal);
        aiLevel.add(lvHigh);
        levelGroup.add(aiNoUsed);
        levelGroup.add(lvLow);
        levelGroup.add(lvNormal);
        levelGroup.add(lvHigh);
        
        setting.add(usingForbidden);
        setting.add(aiColor);
        setting.add(aiLevel);

        this.add(file);
        this.add(edit);
        this.add(help);
        this.add(test);
        this.add(setting);

        eventPerform();
    }
    
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    private void eventPerform() {
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        findAliveFour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Test------Find Alive Four");
                mediator.getOperator().showAliveFour();
            }
        });

        findAsleepFour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Test------Find Asleep Four");
                mediator.getOperator().showAsleepFour();
            }
        });

        findFour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Find Four");

            }
        });

        separator.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("----------------------------------");
            }
        });

        testItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        mediator.response("show five", null);
//                        mediator.response("show alive four", null);
//                        mediator.response("show asleep four", null);
//                        mediator.response("show alive three", null);
//                        mediator.response("show asleep three", null);
//                        mediator.response("show long continue", null);
//                        mediator.response("is it double four", null);
//                        mediator.response("is it double three", null);
//                        mediator.getOperator().launch();
                        mediator.getOperator().getContinueTypes();
                    }
                }).start();
            }
        });
        
        breakPoint.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Break Point");
//                Point[] side = new Point[2];
//                side = mainFrame.chessForm.getContinueAttribute(mainFrame.chessForm.getLastChess().getColor(),
//                        mainFrame.chessForm.getLastChess().getLocation(), Direction.horizontal).getContinue(Direction.horizontal).getBreakPoint();
//                System.out.println("side[0] = " + side[0]);
//                System.out.println("side[1] = " + side[1]);
                mediator.getOperator().showBreakPoint();
            }
        });

        continueEnd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        continueLength.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        
        findAliveThree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Test------Find Alive Three");
                mediator.getOperator().showAliveThree();
            }
        });

        findAsleepThree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Test------Find Asleep Three");
                mediator.getOperator().showAsleepThree();
            }
        });

        usingForbidden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Config.usingForbiddenMove = usingForbidden.getState();
                mediator.getOperator().updateConfig();
            }
        });

        aiBlack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player player;
                if (aiBlack.getState()) {
                    player = new AIPlayer("Computer_01", PieceColor.BLACK);
                } else {
                    player = new HumanPlayer("Human_01", PieceColor.BLACK);
                }
                player.setMediator(mediator);
                final PlayerSet set = mediator.getPlayerSet();
                set.addPlayer(player);
                if (set.getMovingPlayer().getColor().equals(PieceColor.BLACK)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            set.move();
                        }
                    }).start();
                }
            }
        });

        aiWhite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player player;
                if (aiWhite.getState()) {
                    player = new AIPlayer("Computer_02", PieceColor.WHITE);
                } else {
                    player = new HumanPlayer("Human_02", PieceColor.WHITE);
                }
                player.setMediator(mediator);
                final PlayerSet set = mediator.getPlayerSet();
                set.addPlayer(player);
                if (set.getMovingPlayer().getColor().equals(PieceColor.WHITE)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            set.move();
                        }
                    }).start();
                }
            }
        });
    }
}

