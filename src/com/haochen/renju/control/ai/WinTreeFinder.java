package com.haochen.renju.control.ai;

import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.storage.BitPieceMap;
import com.haochen.renju.util.CellUtils;

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


    public WinTree getWinTree(final BitPieceMap map, int lastFoeMove, int color) {
        WinTree tree = new WinTree();
        List<Integer> moveSet = getMoveSet(map, lastFoeMove, color);
        for (int p : moveSet) {
            if (isWin(map, p, color)) {
                tree.add(Arrays.asList(p), color);
                return tree;
            }
        }
        tree.add(moveSet, color);
        final int foeColor = CellUtils.foeColor(color);

        final Map<WinTree, List<Integer>> foeMoveSets = new HashMap<>();
        for (final WinTree t : tree) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BitPieceMap newMap = map.clone();
                    newMap.addPiece(t.getPoint(), t.getColor());
                    List<Integer> foe = getMoveSet(newMap, t.getPoint(), foeColor);
                    t.add(foe, foeColor);
                    synchronized (foeMoveSets) {
                        foeMoveSets.put(t, foe);
                        foeMoveSets.notifyAll();
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
            map.addPiece(t.getPoint(), t.getColor());
            boolean win = true;
            for (WinTree foe : t) {
                if (isWin(map, foe.getPoint(), CellUtils.foeColor(color))) {
                    tree.remove(t);
                    --i;
                    win = false;
                    break;
                } else {
                    map.addPiece(foe.getPoint(), foe.getColor());
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


    private boolean isWin(BitPieceMap map, int point, int color) {
        return winMethod.isWin(map, point, color);
    }

    private List<Integer> getMoveSet(BitPieceMap map, int lastFoeMove, int color) {
        return moveSetGetter.getMoveSet(map, lastFoeMove, color);
    }

    public interface WinMethod {
        boolean isWin(BitPieceMap map, int point, int color);
    }
    public interface MoveSetGetter {
        List<Integer> getMoveSet(BitPieceMap map, int lastFoeMove, int color);
    }
}
