package com.haochen.renju.control.ai;

import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.PieceMap;

import java.util.List;

/**
 * Created by Haochen on 2016/10/29.
 */
public class WinTreeThread extends Thread {

    private PieceMap map;
    private WinTreeFinder finder;
    private WinTree tree;
    private PieceColor color;
    private List<Thread> threads;
    private List<Boolean> win;

    public WinTreeThread(PieceMap map, WinTreeFinder finder, WinTree tree, PieceColor color, List<Thread> threads, List<Boolean> win) {
        this.map = map;
        this.finder = finder;
        this.tree = tree;
        this.color = color;
        this.threads = threads;
        this.win = win;
    }

    @Override
    public void run() {
        try {
            PieceMap newMap = map.clone();
            newMap.addPiece(-1, tree.getPoint(), tree.getColor());
            WinTree result = finder.getWinTree(map, tree.getPoint(), color);
            synchronized (win) {
                if (result.isEmpty()) {
                    synchronized (threads) {
                        for (Thread thread : threads) {
                            if (this != thread) {
                                thread.interrupt();
                            }
                        }
                        threads.clear();
                    }
                    win.clear();
                    win.add(false);
                } else {
                    win.add(true);
                    tree.addAllChildren(result);
                }
                win.notifyAll();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
