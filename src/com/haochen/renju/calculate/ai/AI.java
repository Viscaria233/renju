package com.haochen.renju.calculate.ai;

import com.haochen.renju.main.Config;
import com.haochen.renju.storage.Cell;
import com.haochen.renju.calculate.Res;
import com.haochen.renju.calculate.ContinueAttribute;
import com.haochen.renju.calculate.ContinueType;
import com.haochen.renju.calculate.Resources;
import com.haochen.renju.calculate.SingleContinue;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.calculate.ai.GameTreeFinder.MoveSetGetter;
import com.haochen.renju.calculate.ai.GameTreeFinder.FinishCondition;
import com.haochen.renju.storage.BitPieceMap;
import com.haochen.renju.storage.Direction;
import com.haochen.renju.storage.Point;
import com.haochen.renju.util.CellUtils;
import com.haochen.renju.util.PointUtils;

import java.util.*;

public class AI implements Mediator.Calculate {

    private Mediator mediator;

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public Point getMove(Mediator.Storage storage, int color) {
        BitPieceMap map = bitPieceMap(storage);
        Map<Integer, Integer> scores = getAllScore(map, color);
        Set<Map.Entry<Integer, Integer>> set = scores.entrySet();

        Map.Entry<Integer, Integer>[] entries = set.toArray(new Map.Entry[1]);
        int[] highScore = getHighScorePoints(entries, 1);

        return PointUtils.build(highScore[0]);
    }

    @Override
    public GameTree findVCF(Mediator.Storage storage, int color) {
        return findVCF(bitPieceMap(storage), color);
    }

    @Override
    public GameTree findVCT(Mediator.Storage storage, int color) {
        return findVCT(bitPieceMap(storage), color);
    }

    @Override
    public void stopAndReturn() {
        Config.shouldStopFinding = true;
    }

    @Override
    public int findWinner(Mediator.Storage storage, Point lastMove, int color) {
        return findWinner(bitPieceMap(storage), PointUtils.parse(lastMove), color);
    }

    @Override
    public boolean isForbiddenMove(Mediator.Storage storage, Point point, Direction direction) {
        BitPieceMap map = bitPieceMap(storage);
        int p = PointUtils.parse(point);
        if (map.available(p)) {
            map.addPiece(p, BitPieceMap.CELL_TYPE_BLACK);
            ContinueAttribute attribute = getContinueAttribute(map, p, Direction.all);
            map.removeCell(p);
            return isForbiddenMove(map, attribute, direction);
        } else {
            return false;
        }
    }

    private int findWinner(BitPieceMap map, int point, int color) {
        //插入假想棋子
        map.addPiece(point, color);

        ContinueAttribute attribute = getContinueAttribute(map, point, Direction.all);
        if (usingForbiddenMove()
                && color == Cell.BLACK
                && isForbiddenMove(map, attribute, Direction.all)) {
            //删除假想棋子
            map.removeCell(point);
            return Cell.WHITE;
        }

        int winner = 0;
        if (findFive(attribute, Direction.all).getQuantity() > 0) {
            winner = color;
        }

        //删除假想棋子
        map.removeCell(point);
        return winner;
    }

    private boolean usingForbiddenMove() {
        return Config.usingForbiddenMove;
    }

    private ContinueAttribute getContinueAttribute(BitPieceMap map, int point, Direction direction) {
        //坐标不合法，或坐标处没有棋子
        if (!PointUtils.isValid(point) || map.available(point)) {
            return null;
        }
        int color = map.getCell(point);

        ContinueAttribute attribute = new ContinueAttribute(color, point, Direction.empty);

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    attribute.append(getContinueAttribute(map, point, d));
                }
            }
            return attribute;
        }

        boolean[] isValid = new boolean[2];
        for (int i = 0; i < isValid.length; i++) {
            isValid[i] = true;
        }
        int[] backup = new int[2];
        int[] temp = new int[2];
        int[] continueEnd = new int[2];
        int[] breakPoint = new int[2];

        for (int i = 1; i < 15; i++) {
            if (!isValid[0] && !isValid[1]) {
                break;
            }
            if (isValid[0]) {
                if (direction == Direction.horizontal) {
                    backup[0] = PointUtils.move(point, 1 - i, 0);
                    temp[0] = PointUtils.move(point, - i, 0);
                } else if (direction == Direction.vertical) {
                    backup[0] = PointUtils.move(point, 0, 1 - i);
                    temp[0] = PointUtils.move(point, 0, - i);
                } else if (direction == Direction.mainDiagonal) {
                    backup[0] = PointUtils.move(point, 1 - i, i - 1);
                    temp[0] = PointUtils.move(point, - i, i);
                } else if (direction == Direction.counterDiagonal) {
                    backup[0] = PointUtils.move(point, 1 - i, 1 - i);
                    temp[0] = PointUtils.move(point, - i, - i);
                }
                isValid[0] = PointUtils.isValid(temp[0]);
            }
            if (isValid[1]) {
                if (direction == Direction.horizontal) {
                    backup[1] = PointUtils.move(point, i - 1, 0);
                    temp[1] = PointUtils.move(point, i, 0);
                } else if (direction == Direction.vertical) {
                    backup[1] = PointUtils.move(point, 0, i - 1);
                    temp[1] = PointUtils.move(point, 0, i);
                } else if (direction == Direction.mainDiagonal) {
                    backup[1] = PointUtils.move(point, i - 1, 1 - i);
                    temp[1] = PointUtils.move(point, i, - i);
                } else if (direction == Direction.counterDiagonal) {
                    backup[1] = PointUtils.move(point, i - 1, i - 1);
                    temp[1] = PointUtils.move(point, i, i);
                }
                isValid[1] = PointUtils.isValid(temp[1]);
            }
            for (int j = 0; j < isValid.length; j++) {
                if (isValid[j]) {
                    int cell = map.getCell(temp[j]);
                    if (cell != color) {
                        continueEnd[j] = backup[j];
                        breakPoint[j] = temp[j];
                        isValid[j] = false;
                    }
                } else {
                    continueEnd[j] = backup[j];
                    breakPoint[j] = temp[j];
                }
                if (!PointUtils.isValid(temp[j])) {
                    breakPoint[j] = 0;
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

    private Direction findFive(ContinueAttribute attribute, Direction direction) {
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
        if (usingForbiddenMove()) {
            if (attribute.getColor() == Cell.BLACK) {
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

    private Direction findAliveFour(BitPieceMap map, ContinueAttribute attribute, Direction direction) {
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

        int color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() != 4) {
            return result;
        }
        int[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        fivePoint = getFivePoint(map, direction, color, breakPoint, fivePoint);
        if (fivePoint == 2) {
            result.add(direction);
        }
        return result;
    }

    private int getFivePoint(BitPieceMap map, Direction direction, int color, int[] breakPoint, int fivePoint) {
        for (int p : breakPoint) {
            if (p != 0) {
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, p, direction);
                if (findFive(breakAttr, direction).getQuantity() > 0) {
                    fivePoint++;
                }

                //删除假想棋子
                if (isImaginary) {
                    map.removeCell(p);
                }
            }
        }
        return fivePoint;
    }

    private Direction findAsleepFour(BitPieceMap map, ContinueAttribute attribute, Direction direction) {
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

        int color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 4) {
            return result;
        }
        int[] breakPoint = single.getBreakPoint();
        int fivePoint = 0;
        fivePoint = getFivePoint(map, direction, color, breakPoint, fivePoint);
        if (fivePoint == 1) {
            result.add(direction);
        } else if (fivePoint == 2 && single.getLength() != 4) {
            result.doubleAdd(direction);
        }
        return result;
    }

    private Direction findAliveThree(BitPieceMap map, ContinueAttribute attribute, Direction direction) {
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

        int color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 3) {
            return result;
        }
        int[] breakPoint = single.getBreakPoint();
        int aliveFourPoint = 0;
        for (int p : breakPoint) {
            if (p != 0) {
                //先判断此处是不是禁手
                if (map.getCell(p) == 3) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, p, direction);
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

    private Direction findAsleepThree(BitPieceMap map, ContinueAttribute attribute, Direction direction) {
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

        int color = attribute.getColor();
        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 3) {
            return result;
        }
        int[] breakPoint = single.getBreakPoint();
        int asleepFourPoint = 0;
        for (int p : breakPoint) {
            if (p != 0) {
                //先判断此处是不是禁手
                if (map.getCell(p) == 3) {
                    continue;
                }
                boolean isImaginary = false;
                if (map.available(p)) {
                    //插入假想棋子
                    map.addPiece(p, color);
                    isImaginary = true;
                }

                ContinueAttribute breakAttr = getContinueAttribute(map, p, direction);
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

    private Direction findLongContinue(ContinueAttribute attribute, Direction direction) {
        Direction result = new Direction();
        if (attribute == null) {
            return result;
        }

        Direction[] directionArray = Direction.createDirectionArray();
        if (!direction.isSingle()) {
            for (Direction d : directionArray) {
                if (direction.contains(d)) {
                    result.append(findLongContinue(attribute, d));
                }
            }
            return result;
        }

        SingleContinue single = attribute.getContinue(direction);
        if (single.getLength() > 5) {
            result.add(direction);
        }
        return result;
    }

//    private boolean isDoubleFour(BitPieceMap map, int point) {
//        boolean isImaginary = false;
//        //插入假想棋子
//        if (map.available(point)) {
//            map.addPiece(point, Cell.BLACK);
//            isImaginary = true;
//        }
//
//        ContinueAttribute attribute = getContinueAttribute(map, point, Direction.all);
//        int four = findAliveFour(map, attribute, Direction.all).getQuantity()
//                + findAsleepFour(map, attribute, Direction.all).getQuantity();
//
//        //删除假想棋子
//        if (isImaginary) {
//            map.removeCell(point);
//        }
//        return four >= 2;
//    }
//
//    private boolean isDoubleThree(BitPieceMap map, int point) {
//        boolean isImaginary = false;
//        //插入假想棋子
//        if (map.available(point)) {
//            map.addPiece(point, Cell.BLACK);
//            isImaginary = true;
//        }
//
//        ContinueAttribute attribute = getContinueAttribute(map, point, Direction.all);
//        int three = findAliveThree(map, attribute, Direction.all).getQuantity();
//
//        //删除假想棋子
//        if (isImaginary) {
//            map.removeCell(point);
//        }
//        return three >= 2;
//    }

    private boolean isForbiddenMove(BitPieceMap map, int point, Direction direction) {
        return isForbiddenMove(map, getContinueAttribute(map, point, Direction.all), direction);
    }

    private boolean isForbiddenMove(BitPieceMap map, ContinueAttribute attribute, Direction direction) {
        boolean forbidden = false;
        int color = Cell.BLACK;

        if (findFive(attribute, direction).getQuantity() > 0) {
            forbidden = false;
        } else if (findAliveFour(map, attribute, direction).getQuantity()
                + findAsleepFour(map, attribute, direction).getQuantity() >= 2
                || findLongContinue(attribute, direction).getQuantity() > 0) {
            forbidden = true;
        } else {
            Direction aliveThree = findAliveThree(map, attribute, direction);
            if (aliveThree.getQuantity() >= 2) {
                Direction[] directionArray = Direction.createDirectionArray();
                int valid = 0;
                for (Direction d : directionArray) {
                    if (aliveThree.contains(d)) {
                        SingleContinue single = attribute.getContinue(d);
                        int[] breakPoint = single.getBreakPoint();
                        int[] aliveFourPoint = new int[2];
                        for (int j = 0; j < breakPoint.length; j++) {
                            if (breakPoint[j] != 0) {
                                boolean isImaginary1 = false;
                                //插入假想棋子
                                if (map.available(breakPoint[j])) {
                                    map.addPiece(breakPoint[j], color);
                                    isImaginary1 = true;
                                }
                                ContinueAttribute breakAttr = getContinueAttribute(map, breakPoint[j], d);
                                if (findAliveFour(map, breakAttr, d).getQuantity() > 0) {
                                    aliveFourPoint[j] = breakPoint[j];
                                }
                                //删除假想棋子
                                if (isImaginary1) {
                                    map.removeCell(breakPoint[j]);
                                }
                            }
                        }
                        for (int p : aliveFourPoint) {
                            if (p != 0) {
                                boolean isImaginary1 = false;
                                //插入假想棋子
                                if (map.available(p)) {
                                    map.addPiece(p, color);
                                    isImaginary1 = true;
                                }

                                Direction dire = direction.remove(d);
                                ContinueAttribute aliveFour = getContinueAttribute(map, p, Direction.all);
                                if (findFive(aliveFour, dire).getQuantity() > 0) {
                                    //删除假想棋子
                                    if (isImaginary1) {
                                        map.removeCell(p);
                                    }
                                    continue;
                                }
                                if (isForbiddenMove(map, aliveFour, Direction.all)) {
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
        return forbidden;
    }

    private Map<Direction, ContinueType> getContinueTypes(BitPieceMap map, int point) {
        return getContinueTypes(map, getContinueAttribute(map, point, Direction.all));
    }

    private Map<Direction, ContinueType> getContinueTypes(BitPieceMap map, ContinueAttribute attribute) {
        Map<Direction, ContinueType> result = new HashMap<>();
        Direction[] directions = Direction.createDirectionArray();
        for (Direction direction : directions) {
            ContinueType type = getType(map, attribute, direction);
            if (type != null) {
                result.put(direction, type);
            }
        }
        return result;
    }

    private ContinueType getType(BitPieceMap map, ContinueAttribute attribute, Direction direction) {
        SingleContinue single = attribute.getContinue(direction);
        if (single == null) {
            return null;
        }
        if (usingForbiddenMove()
                && attribute.getColor() == Cell.BLACK
                && isForbiddenMove(map, attribute, Direction.all)) {
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

    private boolean allowMovingAt(BitPieceMap map, int point, int color) {
        return color == Cell.WHITE || !isForbiddenMove(map, point, Direction.all);
    }

    private Resources getResource(BitPieceMap map) {
        Resources result = new Resources();
        for (int point : map) {
            int color = map.getCell(point);
            if (color != Cell.EMPTY) {
                List<Res> list = getRes(map, point);
                for (Res res : list) {
                    List<Integer> points = res.getPoints();
                    for (int i : points) {
                        result.add(i, color, res);
                    }
                }
            }
        }
        return result;
    }

    private List<Res> getRes(BitPieceMap map, int point) {
        int color = map.getCell(point);
        int foe = CellUtils.foeColor(color);
        if (color == Cell.EMPTY) {
            return null;
        }
        List<Res> result = new ArrayList<>();
        int[] dx = {1, 0, 1, 1};
        int[] dy = {0, 1, 1, -1};
        for (int i = 0; i < 4; ++i) {   //4个方向
            int[] p = {point, point};
            int[] sides = new int[2];
            int sameColor = 0;
            for (int j = 0; j < 2; ++j) {
                int moves = 0;
                do {
                    moves++;
                    p[j] = PointUtils.move(p[j], (1 - 2 * j) * dx[i], (1 - 2 * j) * dy[i]);
                    //1-2*j: j=0 => 1, j=1 => -1
                    if (map.getCell(p[j]) == foe) {
                        sides[j] = p[j];
                    } else if (map.getCell(p[j]) == color) {
                        sameColor++;
                    }
                } while (map.getCell(p[j]) != foe && moves < 5);
                if (moves == 5) {
                    sides[j] = p[j];
                }
            }
            int space = 0;
            if (i % 2 == 0) {
                space = Math.abs(PointUtils.getX(sides[0]) - PointUtils.getX(sides[1])) - 2;
            }
            if (space == 5) {

            }
        }
        return result;
    }

    private int[] getHighScorePoints(Map.Entry<Integer, Integer>[] entries, int size) {
        if (size > entries.length) {
            size = entries.length;
        }

        int[] highScore = new int[size];
        for (int i = 0; i < size; ++i) {
            int max = i;
            for (int j = i; j < entries.length; ++j) {
                if (entries[j].getValue() > entries[i].getValue()) {
                    max = j;
                }
            }
            if (max != i) {
                Map.Entry<Integer, Integer> t = entries[max];
                entries[max] = entries[i];
                entries[i] = t;
                highScore[i] = t.getKey();
            }
        }
        return highScore;
    }

    private Map<Integer, Integer> getAllScore(BitPieceMap map, int color) {
        Map<Integer, Integer> result = new HashMap<>();
        ContinueAttribute current;
        ContinueAttribute foe;

        for (int p : map) {
            if (map.available(p)) {
                current = getContinueAttribute(map, p, Direction.all);
                foe = getContinueAttribute(map, p, Direction.all);
                result.put(p, getScore(map, current, foe));
            }
        }
        return result;
    }

    private int getScore(BitPieceMap map, ContinueAttribute current, ContinueAttribute foe) {
        return new Random().nextInt();
    }

    private List<Integer> findAllCatchPoint(BitPieceMap map) {
        List<Integer> result = new ArrayList<>();
        if (findPoints(map, Cell.BLACK, Arrays.asList(ContinueType.FIVE), null).size() > 0) {
            return result;
        }
        List<Integer> five = findPoints(map, Cell.WHITE, Arrays.asList(ContinueType.FIVE), null);
        List<Integer> four = findPoints(map, Cell.WHITE,
                Arrays.asList(ContinueType.ASLEEP_FOUR, ContinueType.ALIVE_FOUR),
                Arrays.asList(ContinueType.FIVE));
        for (int p : four) {
            map.addPiece(p, Cell.WHITE);

            List<Integer> def = findPoints(map, Cell.WHITE, Arrays.asList(ContinueType.FIVE), null);
            def.removeAll(five);

            int def0 = def.get(0);
            map.addPiece(def0, Cell.BLACK);
            ContinueAttribute attribute = getContinueAttribute(map, def0, Direction.all);
            map.removeCell(def0);

            if (isForbiddenMove(map, attribute, Direction.all)) {
                result.add(p);
            }

            map.removeCell(p);
        }
        return result;
    }

    private List<Integer> findAllWinPoints(BitPieceMap map, int color) {
        List<Integer> five = findPoints(map, color, Arrays.asList(ContinueType.FIVE), null);
        if (color == Cell.WHITE) {
            five.addAll(findAllCatchPoint(map));
        }
        return five;
    }

    private GameTree findVCF(BitPieceMap map, int color) {
        final FinishCondition finishCondition = new FinishCondition() {
            @Override
            public boolean isFinish(BitPieceMap map, int point, int color) {
                return findWinner(map, point, color) == color;
            }
        };

        final int c = color;
        final MoveSetGetter moveSetGetter = new GameTreeFinder.MoveSetGetter() {
            @Override
            public List<Integer> getMoveSet(BitPieceMap map, int lastFoeMove, int color) {
                List<Integer> result = new ArrayList<>();
                if (color == c) {
                    result = findPoints(map, color, Arrays.asList(ContinueType.FIVE,
                            ContinueType.ALIVE_FOUR, ContinueType.ASLEEP_FOUR), null);
                } else if (color == CellUtils.foeColor(c)) {
                    result = findPoints(map, color, Collections.singletonList(ContinueType.FIVE), null);
                    if (result.isEmpty()) {
                        result = findPoints(map, c,
                                Collections.singletonList(ContinueType.FIVE), null);
                    }
                }
                return result;
            }
        };
        return new GameTreeFinder(finishCondition, moveSetGetter).getGameTree(map, 0, color);
    }

    private GameTree findVCT(BitPieceMap map, int color) {
        FinishCondition finishCondition = new FinishCondition() {
            @Override
            public boolean isFinish(BitPieceMap map, int point, int color) {
                return findWinner(map, point, color) == color;
            }
        };

        final int c = color;
        MoveSetGetter moveSetGetter = new MoveSetGetter() {
            @Override
            public List<Integer> getMoveSet(BitPieceMap map, int lastFoeMove, int color) {
                List<Integer> result = new ArrayList<>();
                if (color == c) {
                    result = findPoints(map, color, Arrays.asList(
                            ContinueType.FIVE, ContinueType.ALIVE_FOUR, ContinueType.ASLEEP_FOUR,
                            ContinueType.ALIVE_THREE), null);
                } else if (color == CellUtils.foeColor(c)) {
                    result = findPoints(map, color, Collections.singletonList(ContinueType.FIVE), null);
                    if (result.isEmpty()) {
                        result = findPoints(map, c, Collections.singletonList(ContinueType.FIVE), null);
                        if (result.isEmpty()) {
                            result = findPoints(map, color, Arrays.asList(ContinueType.ALIVE_FOUR,
                                    ContinueType.ASLEEP_FOUR), null);
                            result.addAll(findPoints(map, c, Arrays.asList(ContinueType.ALIVE_FOUR,
                                    ContinueType.ASLEEP_FOUR), null));
                        }
                    }
                }
                return result;
            }
        };
        return new GameTreeFinder(finishCondition, moveSetGetter).getGameTree(map, 0, color);
    }

    private List<Integer> findPoints(BitPieceMap map, int color,
                                     Collection<ContinueType> contains, Collection<ContinueType> excludes) {
        List<Integer> result = new ArrayList<>();
        ContinueAttribute attribute;
        Map<Direction, ContinueType> types;
        for (int p : map) {
            if (map.available(p)) {
                map.addPiece(p, color);
                attribute = getContinueAttribute(map, p, Direction.all);
                types = getContinueTypes(map, attribute);
                map.removeCell(p);

                if (containsOneType(types, contains) && excludesAllTypes(types, excludes)) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    private boolean containsOneType(Map<Direction, ContinueType> map, Collection<ContinueType> types) {
        if (types == null) {
            return true;
        }
        for (ContinueType t : types) {
            if (map.containsValue(t)) {
                return true;
            }
        }
        return false;
    }

    private boolean excludesAllTypes(Map<Direction, ContinueType> map, Collection<ContinueType> types) {
        if (types == null) {
            return true;
        }
        for (ContinueType t : types) {
            if (map.containsValue(t)) {
                return false;
            }
        }
        return true;
    }

    private BitPieceMap bitPieceMap(Mediator.Storage storage) {
        BitPieceMap result = new BitPieceMap();
        for (Point p : storage) {
            result.addPiece(PointUtils.parse(p), storage.getCell(p).getType());
        }
        return result;
    }
}