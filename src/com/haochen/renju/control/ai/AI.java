package com.haochen.renju.control.ai;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.bean.Piece;
import com.haochen.renju.calculate.ContinueAttribute;
import com.haochen.renju.calculate.ContinueType;
import com.haochen.renju.calculate.SingleContinue;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.main.Config;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.Direction;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;

import java.util.*;

public class  AI {

    private boolean forbiddenMove = true;
    private Mediator mediator;
    
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    
    public PieceColor findWinner(PieceMap map, Piece piece) {
        Point location = piece.getLocation();
        PieceColor color = piece.getColor();
        if (color.equals(PieceColor.BLACK)) {
            if (forbiddenMove && map.getCell(location).isForbiddenMove()) {
                return PieceColor.WHITE;
            }
        }
        
        PieceColor winner = null;
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, color);
            isImaginary = true;
        }

        ContinueAttribute attribute = getContinueAttribute(map, color, location, Direction.all);
        if (findFive(attribute, Direction.all).getQuantity() > 0) {
            winner = color;
        }
        
        //删除假想棋子
        if (isImaginary) {
            map.removeCell(location);
        }
        return winner;
    }
    
    public ContinueAttribute getContinueAttribute(PieceMap map, PieceColor color, Point location, Direction direction) {
        //坐标不合法，或坐标处没有棋子
        if (!location.isValid() || map.available(location)) {
            return null;
        }
        if (!map.getCell(location).getColor().equals(color)) {
            return new ContinueAttribute(color, location, direction);
        }

        ContinueAttribute attribute = new ContinueAttribute(color, location, Direction.empty);

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    attribute.append(getContinueAttribute(map, color, location, d));
                }
            }
            return attribute;
        }
        
        boolean[] isValid = new boolean[2];
        for (int i = 0; i < isValid.length; i++) {
            isValid[i] = true;
        }
        Point[] backup = new Point[2];
        for (int i = 0; i < backup.length; i++) {
            backup[i] = new Point();
        }
        Point[] temp = new Point[2];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = new Point();
        }
        Point[] continueEnd = new Point[2];
        Point[] breakPoint = new Point[2];
        
        for (int i = 1; i < 15; i++) {
            if (!isValid[0] && !isValid[1]) {
                break;
            }
            if (isValid[0]) {
                if (direction == Direction.horizontal) {
                    backup[0].setLocation(location.x - (i - 1), location.y);
                    temp[0].setLocation(location.x - i, location.y);
                } else if (direction == Direction.vertical) {
                    backup[0].setLocation(location.x, location.y - (i - 1));
                    temp[0].setLocation(location.x, location.y - i);
                } else if (direction == Direction.mainDiagonal) {
                    backup[0].setLocation(location.x - (i - 1), location.y + (i - 1));
                    temp[0].setLocation(location.x - i, location.y + i);
                } else if (direction == Direction.counterDiagonal) {
                    backup[0].setLocation(location.x - (i - 1), location.y - (i - 1));
                    temp[0].setLocation(location.x - i, location.y - i);
                }
                isValid[0] = temp[0].isValid();
            }
            if (isValid[1]) {
                if (direction == Direction.horizontal) {
                    backup[1].setLocation(location.x + (i - 1), location.y);
                    temp[1].setLocation(location.x + i, location.y);
                } else if (direction == Direction.vertical) {
                    backup[1].setLocation(location.x, location.y + (i - 1));
                    temp[1].setLocation(location.x, location.y + i);
                } else if (direction == Direction.mainDiagonal) {
                    backup[1].setLocation(location.x + (i - 1), location.y - (i - 1));
                    temp[1].setLocation(location.x + i, location.y - i);
                } else if (direction == Direction.counterDiagonal) {
                    backup[1].setLocation(location.x + (i - 1), location.y + (i - 1));
                    temp[1].setLocation(location.x + i, location.y + i);
                }
                isValid[1] = temp[1].isValid();
            }
            for (int j = 0; j < isValid.length; j++) {
                if (isValid[j]) {
                    Cell cell = map.getCell(temp[j]);
                    if (!cell.isPiece() || !cell.getColor().equals(color)) {
                        continueEnd[j] = backup[j];
                        breakPoint[j] = temp[j];
                        isValid[j] = false;
                    }
                } else {
                    continueEnd[j] = backup[j];
                    breakPoint[j] = temp[j];
                }
                if (!temp[j].isValid()) {
                    breakPoint[j] = null;
                }
            }
            SingleContinue single = new SingleContinue();
            single.setDirection(direction);
            single.setContinueEnd(continueEnd);
            single.setBreakPoint(breakPoint);
            single.generateLength();
            attribute.setContinue(single);
        }
        return attribute;
    }

    public Direction findFive(ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findFive(attribute, d));
                }
            }
            return result;
        }

        SingleContinue single = attribute.getContinue(direction);
        if (forbiddenMove) {
            if (attribute.getColor().equals(PieceColor.BLACK)) {
                if (single.getLength() == 5) {
                    result.add(direction);
                }
            } else {
                if (single.getLength() >= 5) {
                    result.add(direction);
                }
            }
        } else {
            if (single.getLength() >= 5) {
                result.add(direction);
            } 
        }
        return result;
    }

    public Direction findAliveFour(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAliveFour(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() != 4) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        for (Point p : breakPoint) {
            if (p != null) {
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findFive(breakAttr, direction).getQuantity() > 0) {
                    fivePoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (fivePoint == 2) {
            result.add(direction);
        }
        return result;
    }

    public Direction findAsleepFour(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAsleepFour(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 4) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        for (Point p :breakPoint) {
            if (p != null) {
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findFive(breakAttr, direction).getQuantity() > 0) {
                    fivePoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (fivePoint == 1) {
            result.add(direction);
        } else if (fivePoint == 2 && single.getLength() != 4) {
            result.doubleAdd(direction);
        }
        return result;
    }
    
    public Direction findAliveThree(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAliveThree(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 3) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int aliveFourPoint = 0;
        for (Point p : breakPoint) {
            if (p != null) {
                //先判断此处是不是禁手
                if (map.getCell(p).isForbiddenMove()) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findAliveFour(map, breakAttr, direction).getQuantity() > 0) {
                    aliveFourPoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (aliveFourPoint > 0) {
            result.add(direction);
        }
        return result;
    }
    
    public Direction findAsleepThree(PieceMap map, ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findAsleepThree(map, attribute, d));
                }
            }
            return result;
        }

        PieceColor color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 3) {
            return result;
        }
        Point[] breakPoint = single.getBreakPoint();
        int asleepFourPoint = 0;
        for (Point p : breakPoint) {
            if (p != null) {
                //先判断此处是不是禁手
                if (map.getCell(p).isForbiddenMove()) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(-1, p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, color, p, direction);
                if (findAsleepFour(map, breakAttr, direction).getQuantity() > 0) {
                    asleepFourPoint++;
                }
                if (findAliveFour(map, breakAttr, direction).getQuantity() > 0) {
                    asleepFourPoint = 0;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        if (asleepFourPoint > 0) {
            result.add(direction);
        }
        return result;
    }

    public Direction findLongContinue(PieceMap map, Point location, Direction direction) {
        Direction result = new Direction();
        if (!location.isValid() || map.available(location)) {
            return result;
        }
        
        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findLongContinue(map, location, d));
                }
            }
            return result;
        }

        SingleContinue single = getContinueAttribute(map, PieceColor.BLACK, location, direction).getContinue(direction);
        if (single.getLength() > 5) {
            result.add(direction);
        }
        return result;
    }

    public boolean isDoubleFour(PieceMap map, Point location) {
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, PieceColor.BLACK);
            isImaginary = true;
        }

        ContinueAttribute attribute = getContinueAttribute(map, PieceColor.BLACK, location, Direction.all);
        int four = findAliveFour(map, attribute, Direction.all).getQuantity()
                + findAsleepFour(map, attribute, Direction.all).getQuantity();

        //删除假想棋子
        if(isImaginary) {
            map.removeCell(location);
        }
        return four >= 2;
    }
    
    public boolean isDoubleThree(PieceMap map, Point location) {
        boolean isImaginary = false;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, PieceColor.BLACK);
            isImaginary = true;
        }

        ContinueAttribute attribute = getContinueAttribute(map, PieceColor.BLACK, location, Direction.all);
        int three = findAliveThree(map, attribute, Direction.all).getQuantity();
        
        //删除假想棋子
        if(isImaginary) {
            map.removeCell(location);
        }
        return three >= 2;
    }
    
    public boolean isForbiddenMove(PieceMap map, Point location, Direction direction) {
        boolean forbidden = false;
        boolean isImaginary = false;
        PieceColor color = PieceColor.BLACK;
        //插入假想棋子
        if (map.available(location)) {
            map.addPiece(-1, location, color);
            isImaginary = true;
        }

        ContinueAttribute allDirection = getContinueAttribute(map, color, location, direction);
        if (findFive(allDirection, direction).getQuantity() > 0) {
            forbidden = false;
        } else if (findAliveFour(map, allDirection, direction).getQuantity()
                + findAsleepFour(map, allDirection, direction).getQuantity() >= 2
                || findLongContinue(map, location, direction).getQuantity() > 0) {
            forbidden = true;
        } else {
            Direction aliveThree = findAliveThree(map, allDirection, direction);
            if (aliveThree.getQuantity() >= 2) {
                ContinueAttribute aliveThreeAttr = getContinueAttribute(map, color, location, aliveThree);
                Direction[] directionArray = Direction.createDirectionArray();
                int valid = 0;
                for (Direction d : directionArray) {
                    if (aliveThree.contains(d)) {
                        SingleContinue single = aliveThreeAttr.getContinue(d);
                        Point[] breakPoint = single.getBreakPoint();
                        Point[] aliveFourPoint = new Point[2];
                        for (int j = 0; j < breakPoint.length; j++) {
                            if (breakPoint[j] != null) {
                                boolean isImaginary1 = false;
                                //插入假想棋子
                                if (map.available(breakPoint[j])) {
                                    map.addPiece(-1, breakPoint[j], color);
                                    isImaginary1 = true;
                                }
                                ContinueAttribute breakAttr = getContinueAttribute(map, color, breakPoint[j], d);
                                if (findAliveFour(map, breakAttr, d).getQuantity() > 0) {
                                    aliveFourPoint[j] = breakPoint[j];
                                }
                                //删除假想棋子
                                if (isImaginary1) {
                                    map.removeCell(breakPoint[j]);
                                }
                            }
                        }
                        for (Point p : aliveFourPoint) {
                            if (p != null) {
                                boolean isImaginary1 = false;
                                //插入假想棋子
                                if (map.available(p)) {
                                    map.addPiece(-1, p, color);
                                    isImaginary1 = true;
                                }

                                Direction dire = direction.remove(d);
                                ContinueAttribute aliveFour = getContinueAttribute(map, color, p, dire);
                                if (findFive(aliveFour, dire).getQuantity() > 0) {
                                    //删除假想棋子
                                    if (isImaginary1) {
                                        map.removeCell(p);
                                    }
                                    continue;
                                }
                                if (isForbiddenMove(map, p, Direction.all)) {
                                    //删除假想棋子
                                    if (isImaginary1) {
                                        map.removeCell(p);
                                    }
                                    continue;
                                }
                                //删除假想棋子
                                if (isImaginary1) {
                                    map.removeCell(p);
                                }
                                valid++;
                                break;
                            }
                        }
                    }
                }
                if (valid >= 2) {
                    forbidden = true;
                }
            }
        }
        //删除假想棋子
        if (isImaginary) {
            map.removeCell(location);
        }
        return forbidden;
    }




    public Map<Direction, ContinueType> getContinueTypes(PieceMap map, ContinueAttribute attribute) {
        Map<Direction, ContinueType> result = new HashMap<>();
        Direction[] directions = Direction.createDirectionArray();
        for (Direction direction : directions) {
            ContinueType type = getContinueType(map, attribute, direction);
            if (type != null) {
                result.put(direction, type);
            }
        }
        return result;
    }

    private ContinueType getContinueType(PieceMap map, ContinueAttribute attribute, Direction direction) {
        SingleContinue single = attribute.getContinue(direction);
        if (single == null) {
            return null;
        }
        if (Config.usingForbiddenMove
                && attribute.getColor().equals(PieceColor.BLACK)
                && isForbiddenMove(map, attribute.getLocation(), direction)) {
            return ContinueType.FORBIDDEN_MOVE;
        }
        if (single.getLength() >= 5) {
            return ContinueType.FIVE;
        }
        if (findAliveFour(map, attribute, direction).contains(direction)) {
            return ContinueType.ALIVE_FOUR;
        } else if (findAsleepFour(map, attribute, direction).contains(direction)) {
            return ContinueType.ASLEEP_FOUR;
        } else if (findAliveThree(map, attribute, direction).contains(direction)) {
            return ContinueType.ALIVE_THREE;
        } else if (findAsleepThree(map, attribute, direction).contains(direction)) {
            return ContinueType.ASLEEP_THREE;
        } else {
            return ContinueType.NONE;
        }
    }

    public Point getCloseMove(PieceMap map, PieceColor color, Piece lastPiece) {
        if (lastPiece == null) {
            return new Point(8, 8);
        }
        int x = lastPiece.getLocation().x;
        int y = lastPiece.getLocation().y;
        Point point;
        do {
            point = new Point((int) (Math.random() * 3) - 1 + x, (int) (Math.random() * 3) - 1 + y);
        } while (!map.available(point));
        return point;
    }
    
    public Point getMove(PieceMap map, PieceColor color) {
        Map<Point, Integer> scores = getAllScore(map, color);
        Set<Map.Entry<Point, Integer>> set = scores.entrySet();

        Map.Entry<Point, Integer>[] entries = set.toArray(new Map.Entry[1]);
        Point[] highScore = getHighScorePoints(entries, 1);

        return highScore[0];
    }

    private Point[] getHighScorePoints(Map.Entry<Point, Integer>[] entries, int size) {
        if (size > entries.length) {
            size = entries.length;
        }

        Point[] highScore = new Point[size];
        for (int i = 0; i < size; ++i) {
            int max = i;
            for (int j = i; j < entries.length; ++j){
                if (entries[j].getValue() > entries[i].getValue()) {
                    max = j;
                }
            }
            if (max != i) {
                Map.Entry<Point, Integer> t = entries[max];
                entries[max] = entries[i];
                entries[i] = t;
                highScore[i] = t.getKey();
            }
        }
        return highScore;
    }

    private Map<Point, Integer> getAllScore(PieceMap map, PieceColor color) {
        Map<Point, Integer> result = new HashMap<>();
        ContinueAttribute current;
        ContinueAttribute other;

        for (Point point : map) {
            if (map.available(point)) {
                current = getContinueAttribute(map, color, point, Direction.all);
                other = getContinueAttribute(map, color.foeColor(), point, Direction.all);
                result.put(point, getScore(map, current, other));
            }
        }
//        for (int i = 1; i <= 15; ++i) {
//            for (int j = 1; j<= 15; ++j) {
//                point = new Point(i, j);
//                if (map.available(point)) {
//                    current = getContinueAttribute(map, color, point, Direction.all);
//                    other = getContinueAttribute(map, color.foeColor(), point, Direction.all);
//                    result.put(point, getScore(map, current, other));
//                }
//            }
//        }
        return result;
    }

    private int getScore(PieceMap map, ContinueAttribute currentPlayer, ContinueAttribute otherPlayer) {
        return new Random().nextInt();
    }

    public List<Point> findVCF(PieceMap map, PieceColor color) {
        Stack<Point> result = new Stack<>();
        Stack<List<Point>> allFour = new Stack<>();
        Stack<List<Point>> imaginary = new Stack<>();
        Stack<Integer> cursor = new Stack<>();

//        if (findAllFourPoints(map, color).size() < 1) {
//            return result;
//        }

        while (true) {
            List<Point> five = findAllFivePoints(map, color);
            if (five.size() > 0) {
                result.push(five.get(0));
                break;
            } else {
                allFour.push(findAllFourPoints(map, color));
                cursor.push(0);
                imaginary.push(new ArrayList<Point>());
                while (true) {
                    if (cursor.peek() >= allFour.peek().size()) {
                        allFour.pop();
                        if (allFour.isEmpty()) {
                            return result;
                        }
                        cursor.pop();
                        imaginary.pop();
                        result.pop();
                        List<Point> ima = imaginary.peek();
                        for (Point p : ima) {
                            map.removeCell(p);
                        }
                        imaginary.peek().clear();
                    } else {
                        int peek = cursor.peek();
                        Point p = allFour.peek().get(peek);
                        cursor.pop();
                        cursor.push(peek + 1);
                        if (Config.usingForbiddenMove
                                && color.equals(PieceColor.BLACK)
                                && isForbiddenMove(map, p, Direction.all)) {
                            continue;
                        }
                        result.push(p);
                        map.addPiece(-1, p, color);
                        imaginary.peek().add(p);
                        if (findAllFivePoints(map, color.foeColor()).size() > 0) {
                            map.removeCell(p);
                            imaginary.peek().remove(p);
                            result.pop();
                        } else {
                            List<Point> defend = findAllFivePoints(map, color);
                            Point def = defend.get(0);
                            map.addPiece(-1, def, color.foeColor());
                            imaginary.peek().add(def);
                            break;
                        }
                    }
                }
            }
        }
        if (result.size() > 1) {
            result.pop();
        }
        return result;
    }

    public List<Point> findAllFourPoints(PieceMap map, PieceColor color) {
        List<Point> result = new ArrayList<>();
        ContinueAttribute attribute;
        ContinueType type;
        Map<Direction, ContinueType> types;
        for (Point point : map) {
            if (map.available(point)) {

                map.addPiece(-1, point, color);

                attribute = getContinueAttribute(map, color, point, Direction.all);
                types = getContinueTypes(map, attribute);

                if (!types.containsValue(ContinueType.FIVE)
                        && (types.containsValue(ContinueType.ALIVE_FOUR)
                        || types.containsValue(ContinueType.ASLEEP_FOUR))) {
                    result.add(point);
                }

                map.removeCell(point);
            }
        }
        return result;
    }

    public List<Point> findAllFivePoints(PieceMap map, PieceColor color) {
        List<Point> result = new ArrayList<>();
        for (Point point : map) {
            if (map.available(point)) {

                map.addPiece(-1, point, color);

                ContinueAttribute attribute = getContinueAttribute(map, color, point, Direction.all);
                Map<Direction, ContinueType> types = getContinueTypes(map, attribute);

                map.removeCell(point);

                if (types.containsValue(ContinueType.FIVE)) {
                    result.add(point);
                }
            }
        }
        return result;
    }
}