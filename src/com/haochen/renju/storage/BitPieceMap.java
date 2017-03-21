package com.haochen.renju.storage;

import com.haochen.renju.util.PointUtils;

import java.io.Serializable;
import java.util.Iterator;

/**
 * int型占32位，用2位表示一个交叉点，1个int足以表示棋盘上的一行
 * 用15个int表示棋盘15行，每个int的高30位表示15个交叉点
 *
 * 交叉点状态：
 * 00   空
 * 01   黑棋
 * 10   白棋
 * 11   禁手
 *
 * 坐标用2位16进制数（实际也是int）表示
 * 0x00 非法
 * 0x01 非法
 * 0x10 非法
 *
 * 0x11 坐标A1（棋盘左下角）
 * 0x1F 坐标A15（左上角）
 * 0x88 坐标H8（正中央）
 * 0xFF 坐标O15（右上角）
 * 0xF1 坐标O1（右下角）
 *
 */
public class BitPieceMap implements Cloneable, Iterable<Integer>, Serializable {

    public static final int CELL_TYPE_EMPTY = 0;
    public static final int CELL_TYPE_BLACK = 1;
    public static final int CELL_TYPE_WHITE = 2;
    public static final int CELL_TYPE_FORBIDDEN_MOVE = 3;

    private static final int BINARY_11 = 3;

    /**
     * 表示棋盘的一维数组
     */
    private MyInteger[] map;

    public BitPieceMap() {
        map = new MyInteger[15];
        for (int i = 0; i < map.length; ++i) {
            map[i] = new MyInteger();
        }
    }

    /**
     * 判断棋盘上一个交叉点是否为空
     * @param point 交叉点坐标
     * @return true: 空  false: 不空
     */
    public boolean available(int point) {
        int cell = getCell(point);
        return cell == CELL_TYPE_EMPTY || cell == CELL_TYPE_FORBIDDEN_MOVE;
    }

    /**
     * 更新一个交叉点的状态
     * @param point
     * @param type
     */
    private void setCell(int point, int type) {
        int y = PointUtils.getY(point);
        int move = PointUtils.getX(point) * 2;
        getRow(y).integer &= ~(BINARY_11 << move);
        getRow(y).integer |= type << move;
    }

    private MyInteger getRow(int rowNum) {
        return map[15 - rowNum];
    }

    public void addCell(Cell cell) {
        setCell(PointUtils.parse(cell.getPoint()), cell.getType());
    }

    public void addPiece(int point, int type) {
        setCell(point, type);
    }

    public void addForbiddenMark(int point) {
        setCell(point, CELL_TYPE_FORBIDDEN_MOVE);
    }

    public void removeCell(int point) {
        setCell(point, CELL_TYPE_EMPTY);
    }

    public void removeCell(int x, int y) {
        setCell(PointUtils.parse(x, y), CELL_TYPE_EMPTY);
    }

    public void clear() {
        for (int i = 0; i < 15; ++i) {
            map[i].integer = CELL_TYPE_EMPTY;
        }
    }

    public void display() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 2; j <= 30; j += 2) {
                int cell = (map[i].integer >> j) & BINARY_11;
                switch (cell) {
                    case CELL_TYPE_EMPTY:
                        System.out.print("┼");
                        break;
                    case CELL_TYPE_BLACK:
                        System.out.print("●");
                        break;
                    case CELL_TYPE_WHITE:
                        System.out.print("○");
                        break;
                    case CELL_TYPE_FORBIDDEN_MOVE:
                        System.out.print("×");
                        break;
                }
            }
            System.out.println();
        }
    }

    /**
     * 获取一个交叉点的状态
     * @param point
     * @return
     */
    public int getCell(int point) {
        int x = PointUtils.getX(point);
        int y = PointUtils.getY(point);
        return (getRow(y).integer >> (x * 2)) & BINARY_11;
    }

    public int getCell(int x, int y) {
        return getCell(PointUtils.parse(x, y));
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            int value = 0x11;

            @Override
            public boolean hasNext() {
                return value < 0xff;
            }

            @Override
            public Integer next() {
                if (value >> 4 == 0) {
                    value |= 0x10;
                }
                if ((value & 0xf) == 0) {
                    value |= 1;
                }
                return value++;
            }

            @Override
            public void remove() {
            }
        };
    }

    public BitPieceMap another() {
        try {
            return (BitPieceMap) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        BitPieceMap clone = (BitPieceMap) super.clone();
        clone.map = map.clone();
        for (int i = 0; i < map.length; ++i) {
            clone.map[i] = (MyInteger) map[i].clone();
        }
        return clone;
    }

    private static class MyInteger implements Cloneable {
        Integer integer = 0;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= 15; ++i) {
                int cell = integer >> (i * 2) & BINARY_11;
                switch (cell) {
                    case CELL_TYPE_EMPTY:
                        builder.append("┼");
                        break;
                    case CELL_TYPE_BLACK:
                        builder.append("●");
                        break;
                    case CELL_TYPE_WHITE:
                        builder.append("○");
                        break;
                    case CELL_TYPE_FORBIDDEN_MOVE:
                        builder.append("×");
                        break;
                }
            }
            return builder.toString();
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
