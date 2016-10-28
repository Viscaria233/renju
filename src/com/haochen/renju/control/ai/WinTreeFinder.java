package com.haochen.renju.control.ai;

import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;
import java.util.concurrent.RunnableFuture;

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


    public WinTree getWinTree(final PieceMap map, Point lastFoeMove, final PieceColor color) {
        WinTree tree = new WinTree();
        List<Point> moveSet = getMoveSet(map, lastFoeMove, color);
        for (Point p : moveSet) {
            if (isWin(map, p, color)) {
                tree.add(Arrays.asList(p), color);
                return tree;
            }
        }
        tree.add(moveSet, color);
        PieceColor foeColor = color.foeColor();
        for (int i = 0; i < tree.size(); ++i) {
            final WinTree t = tree.getChild(i);
            map.addPiece(-1, t.getPoint(), t.getColor());
            List<Point> foeMoves = getMoveSet(map, t.getPoint(), foeColor);
            t.add(foeMoves, foeColor);

            final List<Runnable> runnables = new ArrayList<>();
            final Map<WinTree, Boolean> win = new HashMap<>();
            for (final WinTree foe : t) {
                if (isWin(map, foe.getPoint(), color.foeColor())) {
                    tree.remove(t);
                    --i;
                    runnables.clear();
                    win.clear();
                    break;
                } else {
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PieceMap newMap = map.clone();
                                newMap.addPiece(-1, foe.getPoint(), foe.getColor());
                                WinTree result = getWinTree(map, foe.getPoint(), color);
                                synchronized (win) {
                                    if (result.isEmpty()) {
                                        win.put(t, false);
                                    } else {
                                        win.put(t, true);
                                        foe.addAllChildren(result);
                                    }
                                    win.notifyAll();
                                }
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    runnables.add(r);
                }
            }
            map.removeCell(t.getPoint());

            if (runnables.size() > 0) {
                for (Runnable r : runnables) {
                    new Thread(r).start();
                }
                try {
                    synchronized (win) {
                        while (win.size() != runnables.size()) {
                            win.wait();
                        }
                        for (Map.Entry<WinTree, Boolean> entry : win.entrySet()) {
                            if (!entry.getValue()) {
                                tree.remove(t);
                                --i;
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
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
