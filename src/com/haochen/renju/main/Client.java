package com.haochen.renju.main;

import com.haochen.renju.storage.Cell;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.storage.Point;
import com.haochen.renju.ui.TestFrame;
import test.TestConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Client {

    private static TestFrame frame;

    private static void launch() {
        frame = new TestFrame();
        frame.setVisible(true);
        frame.launch();
    }

    private static void drawPieceMap(Mediator mediator, List<Cell> pieces) {
        for (Cell c : pieces) {
            mediator.getOperator().move(c.getPoint());
        }
    }

    private static void saveList(Mediator.Storage storage) {
        ObjectOutputStream oos = null;
        try {
            int count = TestConfig.Test.QuesCount.vcf;
            oos = new ObjectOutputStream(
                    new FileOutputStream(
                            new File(TestConfig.Test.Path.VCF, "list_" + count + ".list")));
            List<Cell> cells = new ArrayList<>();
            for (Point p : storage) {
                if (!storage.available(p)) {
                    cells.add(storage.getCell(p));
                }
            }

            Cell[] sorted = cells.toArray(new Cell[1]);
            Arrays.sort(sorted, new Comparator<Cell>() {
                @Override
                public int compare(Cell o1, Cell o2) {
                    return o1.getIndex() - o2.getIndex();
                }
            });

            List<Point> points = new ArrayList<>();
            for (Cell p : sorted) {
                points.add(p.getPoint());
            }

            oos.writeObject(points);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void showList() {
        List<List<Point>> ques = new ArrayList<>();

        ObjectInputStream ois = null;
        try {
            for (int i = 0; i < 6; ++i) {
                ois = TestConfig.Test.createVCFStream("list_" + i + ".list");
                ques.add((List<Point>) ois.readObject());
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Mediator mediator = frame.getMediator();

        drawList(mediator, ques.get(0));
    }

    private static void drawList(Mediator mediator, List<Point> points) {
        for (Point p : points) {
            mediator.getOperator().move(p);
        }
    }

    public static void main(String[] args) {
        TestConfig.init();
        launch();
//        showList();
    }

}
