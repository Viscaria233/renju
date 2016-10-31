package com.haochen.renju.main;

import com.haochen.renju.bean.Piece;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.exception.ReadFileException;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;
import com.haochen.renju.ui.Dialogs;
import com.haochen.renju.ui.TestFrame;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Client {

    private static TestFrame frame;

    public static void launch() throws ReadFileException {
        frame = new TestFrame();
        frame.setVisible(true);
        frame.launch();
    }

    public static void showPieceMap() {
        List<PieceMap> ques = new ArrayList<>();

        ObjectInputStream ois = null;
        try {
            for (int i = 1; i <= Config.Test.QuesCount.vcf; ++i) {
                ois = Config.Test.createVCFStream("vcf_question_" + i + ".pm");
                ques.add((PieceMap) ois.readObject());
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<List<Piece>> pieces = new ArrayList<>();
        for (final PieceMap map : ques) {
            List<Piece> pts = new ArrayList<>();
            for (Point p : map) {
                if (!map.available(p)) {
                    Piece piece = (Piece) map.getCell(p);
                    pts.add(piece);
                }
            }
            Piece[] sorted = pts.toArray(new Piece[1]);
            Arrays.sort(sorted, new Comparator<Piece>() {
                @Override
                public int compare(Piece o1, Piece o2) {
                    return o1.getIndex() - o2.getIndex();
                }
            });
            pieces.add(Arrays.asList(sorted));
        }

        Mediator mediator = frame.getMediator();

        drawPieceMap(mediator, pieces.get(5));
    }

    private static void drawPieceMap(Mediator mediator, List<Piece> pieces) {
        for (Piece p : pieces) {
            mediator.getOperator().move(p.getLocation());
        }
    }

    private static void saveList(List<PieceMap> maps) {
        ObjectOutputStream oos = null;
        try {
            for (int i = 0; i < maps.size(); ++i) {
                oos = new ObjectOutputStream(
                        new FileOutputStream(new File(Config.Test.Path.VCF, "list_" + i + ".list")));
                PieceMap map = maps.get(i);
                List<Piece> pieces = new ArrayList<>();
                for (Point p : map) {
                    if (!map.available(p)) {
                        pieces.add((Piece) map.getCell(p));
                    }
                }

                Piece[] sorted = pieces.toArray(new Piece[1]);
                Arrays.sort(sorted, new Comparator<Piece>() {
                    @Override
                    public int compare(Piece o1, Piece o2) {
                        return o1.getIndex() - o2.getIndex();
                    }
                });

                List<Point> points = new ArrayList<>();
                for (Piece p : sorted) {
                    points.add(p.getLocation());
                }

                oos.writeObject(points);
                oos.flush();
                oos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showList() {
        List<List<Point>> ques = new ArrayList<>();

        ObjectInputStream ois = null;
        try {
            for (int i = 0; i < 6; ++i) {
                ois = Config.Test.createVCFStream("list_" + i + ".list");
                ques.add((List<Point>) ois.readObject());
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Mediator mediator = frame.getMediator();

        drawList(mediator, ques.get(5));
    }

    private static void drawList(Mediator mediator, List<Point> points) {
        for (Point p : points) {
            mediator.getOperator().move(p);
        }
    }

    public static void main(String[] args) {
        try {
            Config.init();
            launch();
//            showList();
        } catch (ReadFileException e) {
            e.printStackTrace();
            Dialogs.errorDialog(e.getMessage() + '\n' + e.getFile().getAbsolutePath());
        }
    }

}
