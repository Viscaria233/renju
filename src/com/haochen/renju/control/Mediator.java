package com.haochen.renju.control;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import com.haochen.renju.storage.Cell;
import com.haochen.renju.calculate.ai.AI;
import com.haochen.renju.calculate.ai.Chess5;
import com.haochen.renju.calculate.ai.GameTree;
import com.haochen.renju.main.Config;
import com.haochen.renju.storage.*;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.control.player.Player;
import com.haochen.renju.control.player.PlayerSet;
import com.haochen.renju.storage.Point;
import com.haochen.renju.ui.Dialogs;
import com.haochen.renju.util.CellUtils;
import com.haochen.renju.util.PointUtils;

public class Mediator {
    private Display display;
    private Printer printer;
    private Storage storage;
    private Calculate calculate;
//    protected TestMenuBar menuBar;

    private Chess5 chess5Black = new Chess5(Cell.BLACK);
    private Chess5 chess5White = new Chess5(Cell.WHITE);

    private PlayerSet playerSet;

    private Operator operator = new Operator();

    public Mediator() {
        initPlayerSet();
    }

    public void setDisplay(Display display) {
        this.display = display;
        display.setMediator(this);
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
        printer.setMediator(this);
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
        storage.setMediator(this);
    }

    public void setCalculate(Calculate calculate) {
        this.calculate = calculate;
        calculate.setMediator(this);
    }

    private void initPlayerSet() {
        playerSet = new PlayerSet();

        Player player = new HumanPlayer("Human_01", Cell.BLACK);
//        Player player = new AIPlayer("Computer_01", Cell.BLACK);
        player.setMediator(this);
        playerSet.addPlayer(player);

        player = new HumanPlayer("Human_02", Cell.WHITE);
//        player = new AIPlayer("Computer_02", Cell.WHITE);
        player.setMediator(this);
        playerSet.addPlayer(player);
    }

    public PlayerSet getPlayerSet() {
        return playerSet;
    }

    public Operator getOperator() {
        return operator;
    }

    public interface Display {
        void setMediator(Mediator mediator);
        void drawPiece(Cell cell);
        void removePiece(Point currentLocation, Point lastLocation);
        void drawRecord(Point location, int color);
        void removeRecord(Point location);
        void removeRecord();
        void drawForbiddenMark(Point location);
        void clearForbiddenMark();
        void drawGameTree(GameTree tree);
        void clearGameTree();
        void clear(Point location);
        void clear();
        void commit();
    }

    public interface Printer {
        Color LOADING = Color.RED;
        Color READY = Color.CYAN;
        Color MESSAGE = Color.BLUE;
        Color BLACK = Color.BLACK;
        Color WHITE = Color.WHITE;
        void setMediator(Mediator mediator);
        void setMessage(String message, Color color);
    }
    
    public interface Storage extends Iterable<Point>, Serializable {
        void setMediator(Mediator mediator);
        int getNumber();
        boolean available(Point boardLocation);
        Cell getCell(Point point);
        void addCell(Cell cell);
        void removeCell(Point point);
        void removeCurrentCell();
        Cell getCurrentCell();
        /**
         * @return  当前局面下，对方曾经出现过的落子记录
         */
        List<Cell> getRecords();
        void clear();
        void display();
    }

    public interface Calculate {
        void setMediator(Mediator mediator);
        Point getMove(Storage storage, int color);
        GameTree findVCF(Storage storage, int color);
        GameTree findVCT(Storage storage, int color);
        void stopAndReturn();
        int findWinner(Storage storage, Point lastMove, int color);
        boolean isForbiddenMove(Storage storage, Point point, Direction direction);
    }

    public class Operator {
        public void move(Point point) {
            if (!point.isValid() || !storage.available(point)) {
                return;
            }

            hideFindingResult();

            int index = storage.getNumber() + 1;
            int color = playerSet.getMovingPlayer().getColor();
            Cell piece = new Cell(index, point, color);
            //先判断这个棋子是否能使某一方胜利
            int winner = calculate.findWinner(storage, point, color);
            //然后落子

            Player moving = playerSet.getMovingPlayer();
            if (moving instanceof HumanPlayer) {
                chess5Black.humanMove(piece.getPoint());
                chess5White.humanMove(piece.getPoint());
            } else {
                if (moving.getColor() == Cell.WHITE) {
                    chess5Black.humanMove(piece.getPoint());
                } else {
                    chess5White.humanMove(piece.getPoint());
                }
            }
//            System.out.println("------------------------------------");
//            chess5Black.drawchess();


            storage.addCell(piece);
            display.drawPiece(piece);
            drawRecords();
            display.commit();


            if (winner != 0) {
                Player player;
                if (winner == playerSet.getMovingPlayer().getColor()) {
                    player = playerSet.getMovingPlayer();
                    playerSet.exchangePlayer();
                } else {
                    playerSet.exchangePlayer();
                    player = playerSet.getMovingPlayer();
                }
                showWinMessage(player);
                Config.GAME_OVER = true;
            } else {
                if (storage.getNumber() < 225) {
                    if (AI.usingForbiddenMove) {
                        drawForbiddenMark();
                    }
                    playerSet.exchangePlayer();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playerSet.move();
                        }
                    }).start();
                } else {
                    Dialogs.messageDialog("Draw game");
                }
            }
        }

        public void withdraw() {
            Cell current = storage.getCurrentCell();
            if (current == null) {
                return;
            }

            hideFindingResult();
            /**
             *
             */
            chess5Black.withdraw();
            chess5White.withdraw();
//            System.out.println("------------------------------------");
//            chess5Black.drawchess();


            storage.removeCurrentCell();
            Cell last = storage.getCurrentCell();
            display.removePiece(current.getPoint(), last == null ? null : last.getPoint());
            drawRecords();
            display.commit();
            if (AI.usingForbiddenMove) {
                drawForbiddenMark();
            }

            playerSet.exchangePlayer();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    playerSet.move();
                }
            }).start();
        }

        public void clearScreen() {
            display.clear();
            display.commit();
        }

        public void newGame() {
            storage.clear();
            chess5Black = new Chess5(Cell.BLACK);
            chess5White = new Chess5(Cell.WHITE);
            Config.GAME_OVER = false;
//            initPlayerSet();
            playerSet.newGame();
            storage.display();
            launch();
        }

        public void drawForbiddenMark() {
            display.clearForbiddenMark();

            for (Point point : storage) {
                if (storage.available(point)
                        && calculate.isForbiddenMove(Mediator.this.storage, point, Direction.all)) {
                    display.drawForbiddenMark(point);
                }
            }
            display.commit();
        }

        public void launch() {
            playerSet.move();
        }

        public void humanMove() {
            String color = playerSet.getMovingPlayer().getColorString();
            printer.setMessage(color + ": Please move",
                    "black".equals(color) ? Color.BLACK : Color.WHITE);
        }

        public void aiMove() {
            printer.setMessage("AI is thinking......", Printer.LOADING);
            long start = new Date().getTime();
            try {
                Thread.sleep(800 + (int) (Math.random() * 400));
//                PieceMap map = storage.pieceMap();
//                int color = playerSet.getMovingPlayer().getColor();
//                Point point = calculate.getCloseMove(map, type, storage.getCurrentCell());
//                Point point = calculate.getMove(storage, color);
                Point point;
                if (playerSet.getMovingPlayer().getColor() == Cell.BLACK) {
                    point = chess5Black.aiMove();
                } else {
                    point = chess5White.aiMove();
                }
                String s = playerSet.getMovingPlayer().getColorString();
//                printer.setMessage(s + " AI moved. Think time = "
//                        + (new Date().getTime() - start) + " ms.  "
//                        + point, Printer.READY);
                move(point);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void showWinMessage(Player winner) {
//            System.out.println(winner.getName() + " win with " + winner.getColorString());
            Dialogs.messageDialog((winner.getName() + " win with " + winner.getColorString()));
        }

        public void updateConfig() {
            if (AI.usingForbiddenMove) {
                drawForbiddenMark();
            } else {
                display.clearForbiddenMark();
                display.commit();
            }
        }

        public void findVCF() {
            printer.setMessage("Finding VCF......", Printer.LOADING);
            Date begin = new Date();
            GameTree vcf = calculate.findVCF(storage, playerSet.getMovingPlayer().getColor());
            Date end = new Date();

//            System.out.println("----findVCF----");
            String result = "Not found. ";
            if (vcf != null) {
//                printGameTree(vcf);
                result = "Found. ";
                display.drawGameTree(vcf);
                display.commit();
            }

            printer.setMessage(result + "Think time: "
                    + (end.getTime() - begin.getTime()) + " ms", Printer.READY);

//            saveVCFInfo(vcf);
        }

        public void saveVCFInfo(GameTree tree) {
            ObjectOutputStream qS = null;
            ObjectOutputStream aS = null;
            BufferedWriter bw = null;
            try {
                File file = new File(Config.Test.Path.VCF, "vcf_question_" + Config.Test.QuesCount.vcf + ".ques");
                if (!file.exists()) {
                    file.createNewFile();
                }
                qS = new ObjectOutputStream(new FileOutputStream(file));
                qS.writeObject(storage);
                qS.flush();

                file = new File(Config.Test.Path.VCF, "vcf_answer_" + Config.Test.QuesCount.vcf + ".ans");
                if (!file.exists()) {
                    file.createNewFile();
                }
                aS = new ObjectOutputStream(new FileOutputStream(file));
                aS.writeObject(tree);
                aS.flush();

                ++Config.Test.QuesCount.vcf;
                bw = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(new File(Config.Test.Path.VCF, "vcf_count.txt"))));
                bw.write(Config.Test.QuesCount.vcf + "");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (qS != null) {
                    try {
                        qS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (aS != null) {
                    try {
                        aS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void findVCT() {
            printer.setMessage("Finding VCT......", Printer.LOADING);
            Date begin = new Date();
            GameTree vct = calculate.findVCT(storage, playerSet.getMovingPlayer().getColor());
            Date end = new Date();

//            System.out.println("----findVCT----");
            String result = "Not found. ";
            if (vct != null) {
//                printGameTree(vct);
                result = "Found. ";
                display.drawGameTree(vct);
                display.commit();
            }

            printer.setMessage(result + "Think time: "
                    + (end.getTime() - begin.getTime()) + " ms", Printer.READY);

//            saveVCFInfo(vct);
        }

        public void hideFindingResult() {
            display.clearGameTree();
            display.commit();
        }

    }

    private void printGameTree(GameTree tree) {
        GameTree node = tree;
        int count = 0;
        do {
            node = node.getChild(0);
            if (node.getColor() == tree.getColor()) {
                count++;
                System.out.println(count + ": "
                        + CellUtils.toString(node.getColor()) + ": "
                        + PointUtils.toString(node.getPoint()));
            }
        } while (node.getColor() == tree.getColor() ? node.size() == 1 : node.size() > 0);
    }

    private void drawRecords() {
        display.removeRecord();
        List<Cell> records = storage.getRecords();
        for (Cell c : records) {
            display.drawRecord(c.getPoint(), c.getType());
        }
    }

}
