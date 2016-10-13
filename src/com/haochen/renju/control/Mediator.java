package com.haochen.renju.control;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.calculate.ContinueType;
import com.haochen.renju.control.ai.AI;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.bean.RealPiece;
import com.haochen.renju.main.Config;
import com.haochen.renju.storage.*;
import com.haochen.renju.calculate.ContinueAttribute;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.control.player.Player;
import com.haochen.renju.control.player.PlayerSet;

public class Mediator {
    private AI ai;
    private Display display;
    private Board board;
//    protected TestMenuBar menuBar;

    private PlayerSet playerSet = new PlayerSet();

    private  Operator operator = new Operator();

    public Mediator(Display display) {
        ai = new AI();
        board = new Board();
        this.display = display;
        ai.setMediator(this);
        board.setMediator(this);
        display.setMediator(this);

        Player player = new HumanPlayer("Human_01", PieceColor.BLACK);
//        Player player = new AIPlayer("Computer_01", PieceColor.BLACK);
        player.setMediator(this);
        playerSet.addPlayer(player);

        player = new HumanPlayer("Human_02", PieceColor.WHITE);
//        player = new AIPlayer("Computer_02", PieceColor.WHITE);
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

        void drawRecord(Point location, PieceColor color);

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
            PieceColor color = playerSet.getMovingPlayer().getColor();
            Piece piece = new RealPiece(index, point, color);
            //先判断这个棋子是否能使某一方胜利
            PieceMap map = null;
            try {
                map = board.createPieceMap();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            PieceColor winner = ai.findWinner(map, piece);
            //然后落子
            board.addPiece(piece);
            display.drawPiece(piece);
            drawRecords();
            display.commit();


            if (winner != null) {
                Player player;
                if (winner.equals(playerSet.getMovingPlayer().getColor())) {
                    player = playerSet.getMovingPlayer();
                    playerSet.exchangePlayer();
                } else {
                    playerSet.exchangePlayer();
                    player = playerSet.getMovingPlayer();
                }
                showWinMessage(player);
            } else {
                if (board.getNumber() < 225) {
                    if (Config.usingForbiddenMove) {
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
            display.removePiece(current.getLocation(), last == null ? null : last.getLocation());
            drawRecords();
            display.commit();
            if (Config.usingForbiddenMove) {
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
            try {
                ContinueAttribute c = ai.getContinueAttribute(
                        board.createPieceMap(), piece.getColor(), piece.getLocation(), Direction.all);
                Point[][] p = new Point[4][];
                Direction[] d = Direction.createDirectionArray();
                for (int i = 0; i < 4; ++i) {
                    p[i] = c.getContinue(d[i]).getBreakPoint();
                    System.out.println("" + p[i][0] + "  " + p[i][1] + " : " + d[i]);
                }
                System.out.println();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void showFive() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                ContinueAttribute attribute = ai.getContinueAttribute(
                        board.createPieceMap(), piece.getColor(), piece.getLocation(), Direction.all);
                Direction d = ai.findFive(attribute, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void showAliveFour() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                PieceMap map = board.createPieceMap();
                ContinueAttribute attribute = ai.getContinueAttribute(
                        map, piece.getColor(), piece.getLocation(), Direction.all);
                Direction d = ai.findAliveFour(map, attribute, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void showAsleepFour() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                PieceMap map = board.createPieceMap();
                ContinueAttribute attribute = ai.getContinueAttribute(
                        map, piece.getColor(), piece.getLocation(), Direction.all);
                Direction d = ai.findAsleepFour(map, attribute, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void showAliveThree() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                PieceMap map = board.createPieceMap();
                ContinueAttribute attribute = ai.getContinueAttribute(
                        map, piece.getColor(), piece.getLocation(), Direction.all);
                Direction d = ai.findAliveThree(map, attribute, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void showAsleepThree() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                PieceMap map = board.createPieceMap();
                ContinueAttribute attribute = ai.getContinueAttribute(
                        map, piece.getColor(), piece.getLocation(), Direction.all);
                Direction d = ai.findAsleepThree(map, attribute, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void showLongContinue() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                Direction d = ai.findLongContinue(board.createPieceMap(), piece.getLocation(), Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void isItDoubleFour() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                boolean n = ai.isDoubleFour(board.createPieceMap(), piece.getLocation());
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void isItDoubleThree() {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                return;
            }
            try {
                boolean n = ai.isDoubleThree(board.createPieceMap(), piece.getLocation());
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void isItForbiddenMove(Point point) {
            if (!point.isValid()) {
                return;
            }
            if (!board.available(point)) {
                return;
            }
            try {
                boolean n = ai.isForbiddenMove(board.createPieceMap(), point, Direction.all);
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void drawForbiddenMark() {
            try {
                board.clearForbiddenMark();
                display.clearForbiddenMark();
                PieceMap map = board.createPieceMap();
//                Point point;

                for (Point point : map) {
//                    point = cell.getLocation();
                    boolean n = ai.isForbiddenMove(map, point, Direction.all);
                    if (n) {
                        board.addForbiddenMark(point);
                        display.drawForbiddenMark(point);
                    }
                }

//                for (int i = 1; i <= 15; ++i) {
//                    for (int j = 1; j <= 15; ++j) {
//                        point = new Point(i, j);
//                        boolean n = ai.isForbiddenMove(map, point, Direction.all);
//                        if (n) {
//                            board.addForbiddenMark(point);
//                            display.drawForbiddenMark(point);
//                        }
//                    }
//                }
                display.commit();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
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
                PieceMap map = board.createPieceMap();
                PieceColor color = playerSet.getMovingPlayer().getColor();
//                Point point = ai.getCloseMove(map, color, board.getCurrentPiece());
                Point point = ai.getMove(map, color);
                String s = playerSet.getMovingPlayer().getColorString();
                System.out.println(s + " AI moved. Think time = "
                        + (new Date().getTime() - start) + " ms.  "
                        + point);
                move(point);

            } catch (CloneNotSupportedException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void showWinMessage(Player winner) {
            System.out.println(winner.getName() + " win with " + winner.getColorString());
        }

        public void updateConfig() {
            if (Config.usingForbiddenMove) {
                drawForbiddenMark();
            } else {
                board.clearForbiddenMark();
                display.clearForbiddenMark();
                display.commit();
            }
        }

        public void getContinueTypes() {
            try {
                PieceMap pieceMap = board.createPieceMap();
                Piece piece = board.getCurrentPiece();
                if (piece != null) {
                    ContinueAttribute attribute = ai.getContinueAttribute(
                            pieceMap, piece.getColor(), piece.getLocation(), Direction.all);
                    Map<Direction, ContinueType> map = ai.getContinueTypes(pieceMap, attribute);
                    Set<Map.Entry<Direction, ContinueType>> set = map.entrySet();
                    for (Map.Entry<Direction, ContinueType> entry : set) {
                        System.out.println(entry.getKey() + "    " + entry.getValue());
                    }
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void findAllFourPoints() {
            try {
//                List<Point> fourPoints = ai.findAllFourPoints(board.createPieceMap(), playerSet.getMovingPlayer().getColor());
                List<Point> fourPoints = ai.findAllFourPoints(board.createPieceMap(), Config.Test.color);

                System.out.println("----findAllFourPoints----");
                for (Point point : fourPoints) {
                    System.out.println(point);
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void findAllFivePoints() {
            try {
//                List<Point> fivePoints = ai.findAllFivePoints(board.createPieceMap(), playerSet.getMovingPlayer().getColor());
                List<Point> fivePoints = ai.findAllFivePoints(board.createPieceMap(), Config.Test.color);

                System.out.println("----findAllFivePoints----");
                for (Point point : fivePoints) {
                    System.out.println(point);
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        public void findVCF() {
            try {
                Date begin = new Date();

                PieceMap map = board.createPieceMap();
                List<Point> vcf = ai.findVCF(map, playerSet.getMovingPlayer().getColor());

                Date end = new Date();

                System.out.println("----findVCF----");
                File file = new File("F:/renju_test/vcf_result_2.pts");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(vcf);
                oos.flush();
                oos.close();
                for (Point point : vcf) {
                    System.out.println(point);
                }
                System.out.println((end.getTime() - begin.getTime()) + " ms");
            } catch (CloneNotSupportedException | IOException e) {
                e.printStackTrace();
            }
        }

        public void savePieceMap() {
            try {
                File file = new File("F:/renju_test/vcf_piecemap_2.pm");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(board.createPieceMap());
                oos.flush();
                oos.close();
            } catch (IOException | CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    private void drawRecords() {
        display.removeRecord();
        List<Piece> records = board.getRecords();
        for (Piece p : records) {
            display.drawRecord(p.getLocation(), p.getColor());
        }
    }

}
