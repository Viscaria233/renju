package com.haochen.renju.storage;

import com.haochen.renju.util.PointUtils;

import java.io.Serializable;
import java.util.Iterator;

/**
 * int��ռ32λ����2λ��ʾһ������㣬1��int���Ա�ʾ�����ϵ�һ��
 * ��15��int��ʾ����15�У�ÿ��int�ĸ�30λ��ʾ15�������
 *
 * �����״̬��
 * 00   ��
 * 01   ����
 * 10   ����
 * 11   ����
 *
 * ������2λ16��������ʵ��Ҳ��int����ʾ
 * 0x00 �Ƿ�
 * 0x01 �Ƿ�
 * 0x10 �Ƿ�
 *
 * 0x11 ����A1���������½ǣ�
 * 0x1F ����A15�����Ͻǣ�
 * 0x88 ����H8�������룩
 * 0xFF ����O15�����Ͻǣ�
 * 0xF1 ����O1�����½ǣ�
 *
 */
public class BitPieceMap implements Cloneable, Iterable<Integer>, Serializable {

    public static final int CELL_TYPE_EMPTY = 0;
    public static final int CELL_TYPE_BLACK = 1;
    public static final int CELL_TYPE_WHITE = 2;
    public static final int CELL_TYPE_FORBIDDEN_MOVE = 3;

    private static final int BINARY_11 = 3;

    /**
     * ��ʾ���̵�һά����
     */
    private MyInteger[] map;

    public BitPieceMap() {
        map = new MyInteger[15];
        for (int i = 0; i < map.length; ++i) {
            map[i] = new MyInteger();
        }
    }

    /**
     * �ж�������һ��������Ƿ�Ϊ��
     * @param point ���������
     * @return true: ��  false: ����
     */
    public boolean available(int point) {
        int cell = getCell(point);
        return cell == CELL_TYPE_EMPTY || cell == CELL_TYPE_FORBIDDEN_MOVE;
    }

    /**
     * ����һ��������״̬
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
                        System.out.print("��");
                        break;
                    case CELL_TYPE_BLACK:
                        System.out.print("��");
                        break;
                    case CELL_TYPE_WHITE:
                        System.out.print("��");
                        break;
                    case CELL_TYPE_FORBIDDEN_MOVE:
                        System.out.print("��");
                        break;
                }
            }
            System.out.println();
        }
    }

    /**
     * ��ȡһ��������״̬
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
                        builder.append("��");
                        break;
                    case CELL_TYPE_BLACK:
                        builder.append("��");
                        break;
                    case CELL_TYPE_WHITE:
                        builder.append("��");
                        break;
                    case CELL_TYPE_FORBIDDEN_MOVE:
                        builder.append("��");
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
