package com.haochen.renju.main;

import com.haochen.renju.bean.Piece;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.exception.ReadFileException;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;
import com.haochen.renju.ui.Dialogs;
import com.haochen.renju.ui.TestFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Client {

    private static TestFrame frame;
    private static File path = new File("renju_test");

    public static void launch() throws ReadFileException {
        frame = new TestFrame();
        frame.setVisible(true);
        frame.launch();
    }

    private static ObjectInputStream createStream(String fileName) {
        File file = new File(path, fileName);
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static void showPieceMap() {
        List<PieceMap> ques = new ArrayList<>();

        ObjectInputStream ois = null;
        try {
            for (int i = 1; i <= 6; ++i) {
                ois = createStream("vcf_question_" + i + ".pm");
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

        drawPieceMap(mediator, pieces.get(2));
    }

    private static void drawPieceMap(Mediator mediator, List<Piece> pieces) {
        for (Piece p : pieces) {
            mediator.getOperator().move(p.getLocation());
        }
    }
    
    public static void main(String[] args) {
        try {
            launch();
            showPieceMap();
        } catch (ReadFileException e) {
            e.printStackTrace();
            Dialogs.errorDialog(e.getMessage() + '\n' + e.getFile().getAbsolutePath());
        }
    }

}
