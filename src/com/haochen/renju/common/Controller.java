package com.haochen.renju.common;

import java.awt.Color;
import java.util.Date;

import com.haochen.renju.ai.AI;
import com.haochen.renju.form.Board;
import com.haochen.renju.form.ContinueAttribute;
import com.haochen.renju.form.Direction;
import com.haochen.renju.form.Point;
import com.haochen.renju.main.TestMenuBar;
import com.haochen.renju.player.AIPlayer;
import com.haochen.renju.player.HumanPlayer;
import com.haochen.renju.player.Player;
import com.haochen.renju.player.PlayerSet;
import com.haochen.renju.ui.BoardPanel;
import com.haochen.renju.util.PieceMap;

public class Controller {
    private AI ai;
    private BoardPanel display;
    private Board situation;
    private TestMenuBar menuBar;
    
    private PlayerSet playerSet = new PlayerSet();
    
    public Controller(AI ai, BoardPanel display, Board situation, TestMenuBar menuBar) {
        ai.setController(this);
        display.setController(this);
        situation.setController(this);
        menuBar.setController(this);
        this.ai = ai;
        this.display = display;
        this.situation = situation;
        this.menuBar = menuBar;
        
        Player player = new AIPlayer("You", Color.black);
        player.setController(this);
        playerSet.addPlayer(player);
        
        player = new AIPlayer("Computer", Color.white);
        player.setController(this);
        playerSet.addPlayer(player);
    }
    
    public void response(String key, Object value) {
        switch (key) {
        case "move": {
            Point point = (Point) value;
            if (!point.isValid() || !situation.avaliable(point)) {
                break;
            }
            int index = situation.getNumber() + 1;
            Color color = playerSet.getMovingPlayer().getColor();
            Piece piece = new RealPiece(index, point, color);
            //先判断这个棋子是否能使某一方胜利
            PieceMap map = null;
            try {
                map = situation.createPieceMap();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            Color winner = ai.findWinner(map, piece);
            //然后落子
            situation.addPiece(piece);
            display.drawPiece(piece);
            display.commit();
            if (winner != null) {
                Player player = null;
                if (winner.equals(playerSet.getMovingPlayer().getColor())) {
                    player = playerSet.getMovingPlayer();
                } else {
                    playerSet.exchangePlayer();
                    player = playerSet.getMovingPlayer();
                }
                response("show win message", player);
            } else {
                if (situation.getNumber() < 225) {
                    playerSet.exchangePlayer();
                    playerSet.move();
                } else {
                    System.out.println("Draw game");
                }
            }
        }
            break;
        case "withdraw": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            situation.removeLastPiece();
            display.fixBoard(piece.getLocation());
            piece = situation.getLastPiece();
            if (piece != null) {
                display.drawHighlight(piece.getLocation());
            }
            display.commit();
        }
            break;
        case "MIDDLE BUTTON": {
            Point point = (Point) value;
            if (!point.isValid()) {
                break;
            }
            situation.display();
        }
            break;
        case "show break point": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                ContinueAttribute c = ai.getContinueAttribute(
                        situation.createPieceMap(), piece.color, piece.location, Direction.all);
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
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findFive(situation.createPieceMap(), piece.color, piece.location, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show alive four": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findAliveFour(situation.createPieceMap(), piece.color, piece.location, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show asleep four": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findAsleepFour(situation.createPieceMap(), piece.color, piece.location, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show alive three": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findAliveThree(situation.createPieceMap(), piece.color, piece.location, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "show long continue": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                Direction d = ai.findLongContinue(situation.createPieceMap(), piece.location, Direction.all);
                System.out.println(d);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "is it double four": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                boolean n = ai.isDoubleFour(situation.createPieceMap(), piece.location);
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "is it double three": {
            Piece piece = situation.getLastPiece();
            if (piece == null) {
                break;
            }
            try {
                boolean n = ai.isDoubleThree(situation.createPieceMap(), piece.location);
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
            if (!situation.avaliable(point)) {
                break;
            }
            try {
                boolean n = ai.isHandCut(situation.createPieceMap(), point, Direction.all);
//                boolean n = com.haochen.renju.ai.isHandCut(situation.createPieceMap(), point, Direction.all.remove(Direction.horizontal));
                System.out.println(n);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
            break;
        case "draw hand cut": {
            try {
                situation.clearHandCut();
                display.clearHandCut();
                PieceMap map = situation.createPieceMap();
                Point point;
                for (int i = 1; i <= 15; ++i) {
                    for (int j = 1; j <= 15; ++j) {
                        point = new Point(i, j);
                        boolean n = ai.isHandCut(map, point, Direction.all);
                        if (n) {
                            situation.addHandCut(point);
                            display.drawHandCut(point);
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
                PieceMap map = situation.createPieceMap();
                Color color = playerSet.getMovingPlayer().getColor();
//                Point point = ai.getRandomMove(map, color);
                Point point = ai.getCloseMove(map, color, situation.getLastPiece());
//                int index = situation.getNumber() + 1;
//                situation.addPiece(index, point, color);
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
    
}
