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

            List<Thread> threads = new ArrayList<>();
            List<Boolean> win = new ArrayList<>();
            for (final WinTree foe : t) {
                if (isWin(map, foe.getPoint(), color.foeColor())) {
                    threads.clear();
                    win.clear();
                    break;
                } else {
                    try {
                        PieceMap newMap = map.clone();
                        Thread thread = new WinTreeThread(newMap, this, foe, color, threads, win);
                        threads.add(thread);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
            map.removeCell(t.getPoint());

            if (threads.size() > 0) {
                for (Thread thread : threads) {
                    thread.start();
                }
                try {
                    synchronized (win) {
                        while (win.size() < threads.size()) {
                            win.wait();
                        }
                        boolean allWin = true;
                        for (boolean b : win) {
                            allWin &=  b;
                        }
                        if (allWin) {
                            tree.clearChildren();
                            tree.add(t);
                            return tree;
                        } else {
                            tree.remove(t);
                            --i;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                tree.remove(t);
                --i;
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
