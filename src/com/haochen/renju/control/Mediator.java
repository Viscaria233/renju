package com.haochen.renju.control;

import java.io.*;
import java.util.*;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.calculate.ContinueType;
import com.haochen.renju.control.ai.AI;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.control.ai.TTT;
import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.main.Config;
import com.haochen.renju.storage.*;
import com.haochen.renju.calculate.ContinueAttribute;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.control.player.Player;
import com.haochen.renju.control.player.PlayerSet;
import com.haochen.renju.util.PointUtils;

public class Mediator {
    private AI ai;
    private Display display;
    private Board board;
//    protected TestMenuBar menuBar;

    private TTT ttt = new TTT();

    private PlayerSet playerSet = new PlayerSet();

    private Operator operator = new Operator();

    public Mediator(Display display) {
        ai = new AI();
        board = new Board();
        this.display = display;
        ai.setMediator(this);
        board.setMediator(this);
        display.setMediator(this);

        Player player = new HumanPlayer("Human_01", Cell.BLACK);
//        Player player = new AIPlayer("Computer_01", Cell.BLACK);
        player.setMediator(this);
        playerSet.addPlayer(player);

        player = new HumanPlayer("Human_02", Cell.WHITE);
//        player = new AIPlayer("Computer_02", Cell.WHITE);
        player.setMediator(this);
        playerSet.addPlayer(player);
    }

    public AI getAi() {
        return ai;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerSet getPlayerSet() {
        return playerSet;
    }

    public Operator getOperator() {
        return operator;
    }

    public interface Display {
        void setMediator(Mediator mediator);

        void drawPiece(Piece piece);

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

    public class Operator {
        public void move(Point point) {
            if (!point.isValid() || !board.available(point)) {
                return;
            }
            int index = board.getNumber() + 1;
            int color = playerSet.getMovingPlayer().getColor();
            Piece piece = new Piece(index, point, color);
            //先判断这个棋子是否能使某一方胜利
            int winner = ai.findWinner(board, PointUtils.parse(point), color);
            //然后落子


            /**
             *
             */
            if (playerSet.getMovingPlayer().getColor() == Cell.WHITE) {
                ttt.humanMove(piece.getPoint());
            }


            board.addPiece(piece);
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
            } else {
                if (board.getNumber() < 225) {
                    if (AI.usingForbiddenMove) {
                        drawForbiddenMark();
                    }
                    playerSet.exchangePlayer();
                    playerSet.move();
                } else {
                    System.out.println("Draw game");
                }
            }
        }

        public void withdraw() {
            Piece current = board.getCurrentPiece();
            if (current == null) {
                return;
            }
            board.removeCurrentPiece();
            Piece last = board.getCurrentPiece();
            display.removePiece(current.getPoint(), last == null ? null : last.getPoint());
            drawRecords();
            display.commit();
            if (AI.usingForbiddenMove) {
                drawForbiddenMark();
            }

            playerSet.exchangePlayer();
            playerSet.move();
        }

        public void showBreakPoint() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }


            /**
             *
             */
            ttt.withdraw();


            ContinueAttribute c = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
            Direction[] d = Direction.createDirectionArray();
            for (int i = 0; i < 4; ++i) {
                int[] temp = c.getContinue(d[i]).getBreakPoint();
                for (int j = 0; j < temp.length; ++j) {
                    System.out.print(" " + PointUtils.build(temp[j]).toString());
                }
                System.out.println(" : " + d[i]);
            }
            System.out.println();
        }

        public void showFive() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            ContinueAttribute attribute = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
            Direction d = ai.findFive(attribute, Direction.all);
            System.out.println(d);
        }

        public void showAliveFour() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            ContinueAttribute attribute = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
            Direction d = ai.findAliveFour(board, attribute, Direction.all);
            System.out.println(d);
        }

        public void showAsleepFour() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            ContinueAttribute attribute = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
            Direction d = ai.findAsleepFour(board, attribute, Direction.all);
            System.out.println(d);
        }

        public void showAliveThree() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            ContinueAttribute attribute = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
            Direction d = ai.findAliveThree(board, attribute, Direction.all);
            System.out.println(d);
        }

        public void showAsleepThree() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            ContinueAttribute attribute = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
            Direction d = ai.findAsleepThree(board, attribute, Direction.all);
            System.out.println(d);
        }

        public void showLongContinue() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            ContinueAttribute attribute = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
            Direction d = ai.findLongContinue(attribute, Direction.all);
            System.out.println(d);
        }

        public void isItDoubleFour() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            boolean n = ai.isDoubleFour(board, piece.getPoint());
            System.out.println(n);
        }

        public void isItDoubleThree() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            boolean n = ai.isDoubleThree(board, piece.getPoint());
            System.out.println(n);
        }

        public void isItForbiddenMove(Point point) {
            if (!point.isValid()) {
                return;
            }
            if (!board.available(point)) {
                return;
            }
            ContinueAttribute attribute = ai.getContinueAttribute(board, point, Direction.all);
            boolean n = ai.isForbiddenMove(board, attribute, Direction.all);
            System.out.println(n);
        }

        public void drawForbiddenMark() {
            board.clearForbiddenMark();
            display.clearForbiddenMark();
            PieceMap map = board.pieceMap();
//                Point point;

//                for (Point point : map) {
////                    point = cell.getPoint();
//                    boolean imaginary = false;
//                    if (map.available(point)) {
//                        map.addPiece(-1, point, Cell.BLACK);
//                        imaginary = true;
//                    }
//                    ContinueAttribute attribute = ai.getContinueAttribute(board, point, Direction.all);
//                    boolean n = ai.isForbiddenMove(board, attribute, Direction.all);
//                    if (imaginary) {
//                        map.removeCell(point);
//                    }
//                    if (n) {
//                        board.addForbiddenMark(point);
//                        display.drawForbiddenMark(point);
//                    }
//                }

            for (Point point : map) {
//                    point = cell.getPoint();
                if (map.available(point)) {
                    map.addPiece(-1, point, Cell.BLACK);
                    ContinueAttribute attribute = ai.getContinueAttribute(board, point, Direction.all);
                    map.removeCell(point);

                    if (ai.isForbiddenMove(board, attribute, Direction.all)) {
                        board.addForbiddenMark(point);
                        display.drawForbiddenMark(point);
                    }
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
                PieceMap map = board.pieceMap();
                int color = playerSet.getMovingPlayer().getColor();
//                Point point = ai.getCloseMove(map, type, board.getCurrentPiece());
//                Point point = ai.getMove(board, color);
                Point point = ttt.aiMove();
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
        }

        public void updateConfig() {
            if (AI.usingForbiddenMove) {
                drawForbiddenMark();
            } else {
                board.clearForbiddenMark();
                display.clearForbiddenMark();
                display.commit();
            }
        }

        public void getContinueTypes() {
            Piece piece = board.getCurrentPiece();
            if (piece != null) {
                ContinueAttribute attribute = ai.getContinueAttribute(board, piece.getPoint(), Direction.all);
                Map<Direction, ContinueType> map = ai.getContinueTypes(board, attribute);
                Set<Map.Entry<Direction, ContinueType>> set = map.entrySet();
                for (Map.Entry<Direction, ContinueType> entry : set) {
                    System.out.println(entry.getKey() + "    " + entry.getValue());
                }
            }
        }

        public void findAllFourPoints() {
            List<Point> fourPoints = ai.findPoints(board, Config.Test.color,
                    Arrays.asList(ContinueType.ASLEEP_FOUR, ContinueType.ALIVE_FOUR),
                    Arrays.asList(ContinueType.FIVE));

            System.out.println("----findAllFourPoints----");
            for (Point point : fourPoints) {
                System.out.println(point);
            }
        }

        public void findAllFivePoints() {
            List<Point> fivePoints = ai.findPoints(board, Config.Test.color,
                    Arrays.asList(ContinueType.FIVE), null);

            System.out.println("----findAllFivePoints----");
            for (Point point : fivePoints) {
                System.out.println(point);
            }
        }

        public void findVCF() {
            Date begin = new Date();
            WinTree vcf = ai.findVCF(board, playerSet.getMovingPlayer().getColor());
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
                qS.writeObject(board.pieceMap());
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
            WinTree vct = ai.findVCT(board, playerSet.getMovingPlayer().getColor());
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
        List<Piece> records = board.getRecords();
        for (Piece p : records) {
            display.drawRecord(p.getPoint(), p.getType());
        }
    }

}
