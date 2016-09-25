package com.haochen.renju.control;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import com.haochen.renju.control.ai.AI;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.bean.RealPiece;
import com.haochen.renju.storage.Board;
import com.haochen.renju.calculate.ContinueAttribute;
import com.haochen.renju.storage.Direction;
import com.haochen.renju.storage.Point;
import com.haochen.renju.control.player.AIPlayer;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.control.player.Player;
import com.haochen.renju.control.player.PlayerSet;
import com.haochen.renju.storage.PieceMap;

public class Mediator {
    private AI ai;
    private Display display;
    private Board board;
//    protected TestMenuBar menuBar;

    private PlayerSet playerSet = new PlayerSet();

    public Mediator(Display display) {
        ai = new AI();
        board = new Board();
        this.display = display;
        ai.setMediator(this);
        board.setMediator(this);
        display.setMediator(this);

        Player player = new HumanPlayer("You", Color.black);
        player.setMediator(this);
        playerSet.addPlayer(player);

        player = new AIPlayer("Computer", Color.white);
        player.setMediator(this);
        playerSet.addPlayer(player);
    }

    public AI getAi() {
        return ai;
    }

    public Board getBoard() {
        return board;
    }

    public interface Display {
        void setMediator(Mediator mediator);
        void drawPiece(Piece piece);
        void removePiece(Point currentLocation, Point lastLocation);
        void drawRecord(Point location, Color color);
        void removeRecord(Point location);
        void removeRecord();
        void drawForbiddenMark(Point location);
        void clearForbiddenMark();
        void clear(Point location);
        void clear();
        void commit();
    }

    public void response(String key, Object value) {
        switch (key) {
        case "move": {
            Point point = (Point) value;
            if (!point.isValid() || !board.available(point)) {
                break;
            }
            int index = board.getNumber() + 1;
            Color color = playerSet.getMovingPlayer().getColor();
            Piece piece = new RealPiece(index, point, color);
            //先判断这个棋子是否能使某一方胜利
            PieceMap map = null;
            try {
                map = board.createPieceMap();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            Color winner = ai.findWinner(map, piece);
            //然后落子
            board.addPiece(piece);
            display.drawPiece(piece);
            drawRecords();
            display.commit();

            if (winner != null) {
                Player player;
                if (winner.equals(playerSet.getMovingPlayer().getColor())) {
                    player = playerSet.getMovingPlayer();
                } else {
                    playerSet.exchangePlayer();
                    player = playerSet.getMovingPlayer();
                }
                response("show win message", player);
            } else {
                if (board.getNumber() < 225) {
                    playerSet.exchangePlayer();
                    playerSet.move();
                } else {
                    System.out.println("Draw game");
                }
            }
        }
            break;
        case "withdraw": {
            Piece current = board.getCurrentPiece();
            if (current == null) {
                break;
            }
            board.removeCurrentPiece();
            Piece last = board.getCurrentPiece();
            display.removePiece(current.getLocation(), last == null ? null : last.getLocation());
            drawRecords();
            display.commit();
        }
            break;
        case "MIDDLE BUTTON": {
            Point point = (Point) value;
            if (!point.isValid()) {
                break;
            }
            board.display();
        }
            break;
        case "show break point": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                ContinueAttribute c = ai.getContinueAttribute(
                        board.createPieceMap(), piece.getColor(), piece.getLocation(), Direction.all);
                Point[][] p = new Point[4][];
                Direction[] d = Direction.createDirectionArray();
                for (int i = 0; i < 4; ++i) {
                    p[i] = c.getContinue(d[i]).getBreakPoint();
                    System.out.println("" + p[i][0] + "  " +  p[i][1] + " : " + d[i]);
                }
                System.out.println();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show five": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findFive(board.createPieceMap(), piece.getColor(), piece.getLocation(), Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show alive four": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findAliveFour(board.createPieceMap(), piece.getColor(), piece.getLocation(), Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show asleep four": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findAsleepFour(board.createPieceMap(), piece.getColor(), piece.getLocation(), Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show alive three": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findAliveThree(board.createPieceMap(), piece.getColor(), piece.getLocation(), Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show long continue": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findLongContinue(board.createPieceMap(), piece.getLocation(), Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "is it double four": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                boolean n = ai.isDoubleFour(board.createPieceMap(), piece.getLocation());
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "is it double three": {
            Piece piece = board.getCurrentPiece();
            if (piece == null) {
                break;
            }
            try {
                boolean n = ai.isDoubleThree(board.createPieceMap(), piece.getLocation());
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "is it hand cut": {
            Point point = (Point) value;
            if (!point.isValid()) {
                break;
            }
            if (!board.available(point)) {
                break;
            }
            try {
                boolean n = ai.isHandCut(board.createPieceMap(), point, Direction.all);
//                boolean n = com.haochen.renju.control.ai.isForbiddenMove(board.createPieceMap(), point, Direction.all.remove(Direction.horizontal));
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "draw hand cut": {
            try {
                board.clearForbiddenMark();
                display.clearForbiddenMark();
                PieceMap map = board.createPieceMap();
                Point point;
                for (int i = 1; i <= 15; ++i) {
                    for (int j = 1; j <= 15; ++j) {
                        point = new Point(i, j);
                        boolean n = ai.isHandCut(map, point, Direction.all);
                        if (n) {
                            board.addForbiddenMark(point);
                            display.drawForbiddenMark(point);
                        }
                    }
                }
//                display.output();
                display.commit();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "launch": {
            playerSet.move();
        }
            break;
        case "human moving": {
            System.out.println("Please Move");
        }
            break;
        case "ai moving": {
            System.out.println("AI is thinking......");
            long start = new Date().getTime();
            try {
                PieceMap map = board.createPieceMap();
                Color color = playerSet.getMovingPlayer().getColor();
//                Point point = ai.getRandomMove(map, color);
                Point point = ai.getCloseMove(map, color, board.getCurrentPiece());
//                int index = board.getNumber() + 1;
//                board.addPiece(index, point, color);
//                display.drawPiece(index, point, color);
//                display.commit();
                String s = playerSet.getMovingPlayer().getColorString();
                System.out.println(s + " AI moved. Think time = "
                        + (new Date().getTime() - start) + " ms.  "
                        + point);
                response("move", point);
                
//                playerSet.exchangePlayer();
//                playerSet.move();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show win message": {
            Player winner = (Player) value;
            System.out.println(winner.getName() + " win with " + winner.getColorString());
        }
            break;
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
