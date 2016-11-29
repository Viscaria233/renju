package com.haochen.renju.control;

import java.io.*;
import java.util.*;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.control.ai.AI;
import com.haochen.renju.control.ai.Chess5;
import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.main.Config;
import com.haochen.renju.storage.*;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.control.player.Player;
import com.haochen.renju.control.player.PlayerSet;
import com.haochen.renju.ui.Dialogs;

public class Mediator {
    private Calculate calculate;
    private Display display;
    private Storage storage;
//    protected TestMenuBar menuBar;

    private Chess5 chess5Black = new Chess5(Cell.BLACK);
    private Chess5 chess5White = new Chess5(Cell.WHITE);

    private PlayerSet playerSet;

    private Operator operator = new Operator();

    public Mediator(Display display) {
        calculate = new AI();
        storage = new Board();
        this.display = display;
        calculate.setMediator(this);
        storage.setMediator(this);
        display.setMediator(this);

        initPlayerSet();
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

    public Calculate getCalculate() {
        return calculate;
    }

    public Storage getStorage() {
        return storage;
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

        void clear(Point location);

        void clear();

        void commit();
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
        WinTree findVCF(Storage storage, int color);
        WinTree findVCT(Storage storage, int color);
        void stopAndReturn();
        int findWinner(Storage storage, Point lastMove, int color);
        boolean isForbiddenMove(Storage storage, Point point, Direction direction);
    }

    public class Operator {
        public void move(Point point) {
            if (!point.isValid() || !storage.available(point)) {
                return;
            }
            int index = storage.getNumber() + 1;
            int color = playerSet.getMovingPlayer().getColor();
            Cell piece = new Cell(index, point, color);
            //先判断这个棋子是否能使某一方胜利
            int winner = calculate.findWinner(storage, point, color);
            //然后落子


            /**
             *
             */
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
            System.out.println("------------------------------------");
            chess5Black.drawchess();


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
                    System.out.println("Draw game");
                }
            }
        }

        public void withdraw() {
            Cell current = storage.getCurrentCell();
            if (current == null) {
                return;
            }


            /**
             *
             */
            chess5Black.withdraw();
            chess5White.withdraw();
            System.out.println("------------------------------------");
            chess5Black.drawchess();


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
            System.out.println("Please move");
        }

        public void aiMove() {
            System.out.println("AI is thinking......");
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
                System.out.println(s + " AI moved. Think time = "
                        + (new Date().getTime() - start) + " ms.  "
                        + point);
                move(point);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void showWinMessage(Player winner) {
            System.out.println(winner.getName() + " win with " + winner.getColorString());
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
            Date begin = new Date();
            WinTree vcf = calculate.findVCF(storage, playerSet.getMovingPlayer().getColor());
            Date end = new Date();

            System.out.println("----findVCF----");
            for (WinTree t : vcf) {
                System.out.print(t.getColor() + ": ");
                System.out.println(t.getPoint());
            }

            System.out.println((end.getTime() - begin.getTime()) + " ms");

//            saveVCFInfo(vcf);
        }

        public void saveVCFInfo(WinTree tree) {
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

            Date begin = new Date();
            WinTree vct = calculate.findVCT(storage, playerSet.getMovingPlayer().getColor());
            Date end = new Date();

            System.out.println("----findVCT----");
            for (WinTree t : vct) {
                System.out.print(t.getColor() + ": ");
                System.out.println(t.getPoint());
            }

            System.out.println((end.getTime() - begin.getTime()) + " ms");

//            saveVCFInfo(vct);
        }

    }

    private void drawRecords() {
        display.removeRecord();
        List<Cell> records = storage.getRecords();
        for (Cell c : records) {
            display.drawRecord(c.getPoint(), c.getType());
        }
    }

}
