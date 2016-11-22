package com.haochen.renju.storage;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.util.CellUtils;
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

    /**
     * 表示棋盘的一维数组
     */
    private int[] map;

    public BitPieceMap() {
        map = new int[15];
    }

    public void setMap(int[] map) {
        this.map = map;
    }

    /**
     * 判断棋盘上一个交叉点是否为空
     * @param point 交叉点坐标
     * @return true: 空  false: 不空
     */
    public boolean available(int point) {
        int x = PointUtils.getX(point);
        int y = PointUtils.getY(point);

        //最后3的意义是二进制数11
        int cell = (map[15 - y] >> (x * 2)) & 3;
        return cell == 0 || cell == 3;
    }

    /**
     * 更新一个交叉点的状态
     * @param point
     * @param type
     */
    private void setCell(int point, int type) {
        int y = PointUtils.getY(point);
        int move = PointUtils.getX(point) * 2;
        map[15 - y] &= ~(3 << move);
        map[15 - y] |= type << move;
    }

    public void addCell(Cell cell) {
        setCell(PointUtils.parse(cell.getPoint()), cell.getType());
    }

    public void addPiece(int point, int type) {
        setCell(point, type);
    }

    public void addForbiddenMark(int point) {
        setCell(point, 3);
    }

    public void removeCell(int point) {
        setCell(point, 0);
    }

    public void removeCell(int x, int y) {
        setCell(PointUtils.parse(x, y), 0);
    }

    public void clear() {
        for (int i = 0; i < 15; ++i) {
            map[i] = 0;
        }
    }

    public void display() {
        for (int i = 0; i < 15; ++i) {
            for (int j = 2; j <= 30; j += 2) {
                int cell = (map[i] >> j) & 3;
                switch (cell) {
                    case 0:
                        System.out.print("┼");
                        break;
                    case 1:
                        System.out.print("●");
                        break;
                    case 2:
                        System.out.print("○");
                        break;
                    case 3:
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
        return (map[15 - y] >> (x * 2)) & 3;
    }

    public int getCell(int x, int y) {
        return getCell(PointUtils.parse(x, y));
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            int value = 0x10;

            @Override
            public boolean hasNext() {
                return value < 0xff;
            }

            @Override
            public Integer next() {
                ++value;
                if (value >> 4 == 0) {
                    value |= 0x10;
                }
                if ((value & 0xf) == 0) {
                    value |= 1;
                }
                return value;
            }

            @Override
            public void remove() {
            }
        };
    }

    public BitPieceMap clone() {
        BitPieceMap o = null;
        try {
            o = (BitPieceMap) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
