package com.haochen.renju.storage;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.util.CellUtils;
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

    /**
     * ��ʾ���̵�һά����
     */
    private int[] map;

    public BitPieceMap() {
        map = new int[15];
    }

    public void setMap(int[] map) {
        this.map = map;
    }

    /**
     * �ж�������һ��������Ƿ�Ϊ��
     * @param point ���������
     * @return true: ��  false: ����
     */
    public boolean available(int point) {
        int x = PointUtils.getX(point);
        int y = PointUtils.getY(point);

        //���3�������Ƕ�������11
        int cell = (map[15 - y] >> (x * 2)) & 3;
        return cell == 0 || cell == 3;
    }

    /**
     * ����һ��������״̬
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
                        System.out.print("��");
                        break;
                    case 1:
                        System.out.print("��");
                        break;
                    case 2:
                        System.out.print("��");
                        break;
                    case 3:
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
