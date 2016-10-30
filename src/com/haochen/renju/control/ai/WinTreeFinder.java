package com.haochen.renju.control.ai;

import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Haochen on 2016/10/24.
 */
public class WinTreeFinder {

    private WinMethod winMethod;
    private MoveSetGetter moveSetGetter;

    public WinTreeFinder(WinMethod winMethod, MoveSetGetter moveSetGetter) {
        this.winMethod = winMethod;
        this.moveSetGetter = moveSetGetter;
    }


    public WinTree getWinTree(final PieceMap map, Point lastFoeMove, PieceColor color) {
        WinTree tree = new WinTree();
        List<Point> moveSet = getMoveSet(map, lastFoeMove, color);
        for (Point p : moveSet) {
            if (isWin(map, p, color)) {
                tree.add(Arrays.asList(p), color);
                return tree;
            }
        }
        tree.add(moveSet, color);
        final PieceColor foeColor = color.foeColor();

        final Map<WinTree, List<Point>> foeMoveSets = new HashMap<>();
        for (final WinTree t : tree) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PieceMap newMap = map.clone();
                        newMap.addPiece(-1, t.getPoint(), t.getColor());
                        List<Point> foe = getMoveSet(newMap, t.getPoint(), foeColor);
                        t.add(foe, foeColor);
                        synchronized (foeMoveSets) {
                            foeMoveSets.put(t, foe);
                            foeMoveSets.notifyAll();
                        }
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        try {
            synchronized (foeMoveSets) {
                while (foeMoveSets.size() < tree.size()) {
                    foeMoveSets.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < tree.size(); ++i) {
            WinTree t = tree.getChild(i);
            map.addPiece(-1, t.getPoint(), t.getColor());
            boolean win = true;
            for (WinTree foe : t) {
                if (isWin(map, foe.getPoint(), color.foeColor())) {
                    tree.remove(t);
                    --i;
                    win = false;
                    break;
                } else {
                    map.addPiece(-1, foe.getPoint(), foe.getColor());
                    WinTree result = getWinTree(map, foe.getPoint(), color);
                    if (result.isEmpty()) {
                        tree.remove(t);
                        map.removeCell(foe.getPoint());
                        --i;
                        win = false;
                        break;
                    } else {
                        foe.addAllChildren(result);
                    }
                    map.removeCell(foe.getPoint());
                }
            }
            map.removeCell(t.getPoint());
            if (win) {
                tree.clearChildren();
                tree.add(t);
                return tree;
            }
        }
        return tree;
    }


    private boolean isWin(PieceMap map, Point point, PieceColor color) {
        return winMethod.isWin(map, point, color);
    }

    private List<Point> getMoveSet(PieceMap map, Point lastFoeMove, PieceColor color) {
        return moveSetGetter.getMoveSet(map, lastFoeMove, color);
    }

    public interface WinMethod {
        boolean isWin(PieceMap map, Point point, PieceColor color);
    }
    public interface MoveSetGetter {
        List<Point> getMoveSet(PieceMap map, Point lastFoeMove, PieceColor color);
    }
}
