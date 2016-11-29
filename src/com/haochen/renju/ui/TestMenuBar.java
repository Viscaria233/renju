package com.haochen.renju.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.control.ai.AI;
import com.haochen.renju.control.player.AIPlayer;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.control.player.Player;
import com.haochen.renju.control.player.PlayerSet;

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

    private JMenuItem newGame;
    private JCheckBoxMenuItem black;
    private JMenuItem testItem;
    private JMenuItem separator;

    private JCheckBoxMenuItem usingForbidden;

    private JCheckBoxMenuItem aiBlack;
    private JCheckBoxMenuItem aiWhite;

    private JCheckBoxMenuItem bLvLow;
    private JCheckBoxMenuItem bLvNormal;
    private JCheckBoxMenuItem bLvHigh;
    private JCheckBoxMenuItem wLvLow;
    private JCheckBoxMenuItem wLvNormal;
    private JCheckBoxMenuItem wLvHigh;

    private Mediator mediator;

    TestMenuBar(TestFrame frame) {
        this.mainFrame = frame;

        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        JMenu test = new JMenu("Test");
        JMenu setting = new JMenu("Setting");
        JMenu aiColor = new JMenu("AI Color");
        JMenu aiLevel = new JMenu("AI Level");
        JMenu bLevel = new JMenu("Black");
        JMenu wLevel = new JMenu("Wlack");
        ButtonGroup bLvGroup = new ButtonGroup();
        ButtonGroup wLvGroup = new ButtonGroup();

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

        newGame = new JMenuItem("New Game");
        black = new JCheckBoxMenuItem("black");
        testItem = new JMenuItem("Test");
        separator = new JMenuItem("separator");

        test.add(newGame);
        test.addSeparator();
        test.add(black);
        test.add(testItem);
        test.addSeparator();
        test.add(separator);

        usingForbidden = new JCheckBoxMenuItem("Using Forbidden");
        usingForbidden.setState(AI.usingForbiddenMove);
        aiBlack = new JCheckBoxMenuItem("AI Uses Black");
//        aiBlack.setState(true);
        aiWhite = new JCheckBoxMenuItem("AI Uses White");
        bLvLow = new JCheckBoxMenuItem("Low");
        bLvNormal = new JCheckBoxMenuItem("Normal");
        bLvHigh = new JCheckBoxMenuItem("High");
        wLvLow = new JCheckBoxMenuItem("Low");
        wLvNormal = new JCheckBoxMenuItem("Normal");
        wLvHigh = new JCheckBoxMenuItem("High");

        aiColor.add(aiBlack);
        aiColor.add(aiWhite);

        bLvGroup.add(bLvLow);
        bLvGroup.add(bLvNormal);
        bLvGroup.add(bLvHigh);
        bLevel.add(bLvLow);
        bLevel.add(bLvNormal);
        bLevel.add(bLvHigh);

        wLvGroup.add(wLvLow);
        wLvGroup.add(wLvNormal);
        wLvGroup.add(wLvHigh);
        wLevel.add(wLvLow);
        wLevel.add(wLvNormal);
        wLevel.add(wLvHigh);

        aiLevel.add(bLevel);
        aiLevel.add(wLevel);

        setting.add(usingForbidden);
        setting.add(aiColor);
        setting.add(aiLevel);

        this.add(file);
        this.add(edit);
        this.add(help);
        this.add(test);
        this.add(setting);

        eventPerformed();
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    private void eventPerformed() {
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        separator.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("----------------------------------");
            }
        });

        findVCF.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mediator.getOperator().findVCF();
                    }
                }
        );

        findVCT.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mediator.getOperator().findVCT();
                    }
                }
        );

        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediator.getOperator().clearScreen();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mediator.getOperator().newGame();
                    }
                }).start();
            }
        });

        testItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    }
                }).start();
            }
        });

        usingForbidden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AI.usingForbiddenMove = usingForbidden.getState();
                mediator.getOperator().updateConfig();
            }
        });

        aiBlack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player player;
                if (aiBlack.getState()) {
                    player = new AIPlayer("Computer_01", Cell.BLACK);
                } else {
                    player = new HumanPlayer("Human_01", Cell.BLACK);
                }
                player.setMediator(mediator);
                final PlayerSet set = mediator.getPlayerSet();
                set.addPlayer(player);
                if (set.getMovingPlayer().getColor() == Cell.BLACK) {
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
                    player = new AIPlayer("Computer_02", Cell.WHITE);
                } else {
                    player = new HumanPlayer("Human_02", Cell.WHITE);
                }
                player.setMediator(mediator);
                final PlayerSet set = mediator.getPlayerSet();
                set.addPlayer(player);
                if (set.getMovingPlayer().getColor() == Cell.WHITE) {
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

