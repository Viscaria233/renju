package com.haochen.renju.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.haochen.renju.control.Mediator;

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
    
    private JCheckBoxMenuItem uiBlack;
    private JCheckBoxMenuItem uiWhite;
    
    private JCheckBoxMenuItem uiNoUsed;
    private JCheckBoxMenuItem lvNoob;
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
        JMenu uiColor = new JMenu("UI Color");
//        ButtonGroup colorGroup = new ButtonGroup();
        JMenu uiLevel = new JMenu("UI Level");
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
        usingForbidden.setState(true);
//        mainFrame.setUsingForbidden(true);
//        mainFrame.chessForm.setUsingForbidden(true);
        uiBlack = new JCheckBoxMenuItem("UI Uses Black");
        uiWhite = new JCheckBoxMenuItem("UI Uses White");
        uiNoUsed = new JCheckBoxMenuItem("No UI");
        lvNoob = new JCheckBoxMenuItem("Noob");
        lvNormal = new JCheckBoxMenuItem("Normal");
        lvHigh = new JCheckBoxMenuItem("High");
        
        uiColor.add(uiBlack);
        uiColor.add(uiWhite);
//        colorGroup.add(uiBlack);
//        colorGroup.add(uiWhite);

        uiLevel.add(uiNoUsed);
        uiLevel.add(lvNoob);
        uiLevel.add(lvNormal);
        uiLevel.add(lvHigh);
        levelGroup.add(uiNoUsed);
        levelGroup.add(lvNoob);
        levelGroup.add(lvNormal);
        levelGroup.add(lvHigh);
        
        setting.add(usingForbidden);
        setting.add(uiColor);
        setting.add(uiLevel);

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

//        findAliveFour.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Find Alive Four");
//                System.out.println(mainFrame.chessForm.findAliveFour(
//                        mainFrame.chessForm.getLastChess().getColor(),
//                        mainFrame.chessForm.getLastChess().getLocation(), Direction.all));
//            }
//        });

//        findAsleepFour.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Find Asleep Four");
//                System.out.println(mainFrame.chessForm.findAsleepFour(
//                        mainFrame.chessForm.getLastChess().getColor(),
//                        mainFrame.chessForm.getLastChess().getLocation(), Direction.all));
//            }
//        });

//        findFour.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Find Four");
//                System.out.println(mainFrame.chessForm.getFourQuantity(
//                        mainFrame.chessForm.getChess(mainFrame.chessForm.getChessNumber()).getColor(),
//                        mainFrame.chessForm.getLastChess().getLocation(), Direction.all));
//            }
//        });

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
                        mediator.response("launch", null);
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
                mediator.response("show break point", null);
            }
        });

//        continueEnd.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Continue End");
//                Point[] side = new Point[2];
//                side = mainFrame.chessForm.getContinueAttribute(mainFrame.chessForm.getLastChess().getColor(),
//                        mainFrame.chessForm.getLastChess().getLocation(), Direction.horizontal).getContinue(Direction.horizontal).getContinueEnd();
//                System.out.println("side[0] = " + side[0]);
//                System.out.println("side[1] = " + side[1]);
//            }
//        });

//        continueLength.addActionListener(new ActionListener() {
//            
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Continue Length");
//                int length = mainFrame.chessForm.getContinueAttribute(mainFrame.chessForm.getLastChess().getColor(),
//                        mainFrame.chessForm.getLastChess().getLocation(), Direction.horizontal).getContinue(Direction.horizontal).getLength();
//                System.out.println(length);
//            }
//        });
        
//        findAliveThree.addActionListener(new ActionListener() {
//            
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("Test------Find Alive Three");
//                System.out.println(mainFrame.chessForm.findAliveThree(
//                        mainFrame.chessForm.getLastChess().getColor(),
//                        mainFrame.chessForm.getLastChess().getLocation(), Direction.all));
//            }
//        });
    }
}

