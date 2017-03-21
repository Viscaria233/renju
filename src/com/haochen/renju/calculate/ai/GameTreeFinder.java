package com.haochen.renju.calculate.ai;

import com.haochen.renju.storage.BitPieceMap;
import com.haochen.renju.util.CellUtils;

import java.util.*;

/**
 * Created by Haochen on 2016/10/24.
 */
class GameTreeFinder {

    private FinishCondition finishCondition;
    private MoveSetGetter moveSetGetter;

    GameTreeFinder(FinishCondition finishCondition, MoveSetGetter moveSetGetter) {
        this.finishCondition = finishCondition;
        this.moveSetGetter = moveSetGetter;
    }


    GameTree getGameTree(final BitPieceMap map, int lastFoeMove, int color) {
        List<Integer> moveSet = getMoveSet(map, lastFoeMove, color);
        if (moveSet.size() == 0) {
            return null;
        }
        GameTree tree = new GameTree();
        tree.setColor(color);
        for (int p : moveSet) {
            if (isFinish(map, p, color)) {
                tree.add(Collections.singletonList(p), color);
                return tree;
            }
        }
        tree.add(moveSet, color);
        final int foeColor = CellUtils.foeColor(color);

//        final Map<GameTree, List<Integer>> foeMoveSets = new HashMap<>();
        final int[] complete = new int[1];
//        for (final GameTree me : tree) {
        for (int i = 0; i < tree.size(); ++i) {
            final GameTree me = tree.getChild(i);
            final BitPieceMap newMap = map.another();
            newMap.addPiece(me.getPoint(), me.getColor());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Integer> foe = getMoveSet(newMap, me.getPoint(), foeColor);
//                    map.addPiece(me.getPoint(), me.getColor());
//                    List<Integer> foe = getMoveSet(map, me.getPoint(), foeColor);
//                    map.removeCell(me.getPoint());
                    me.add(foe, foeColor);
                    synchronized (complete) {
                        complete[0]++;
                        complete.notifyAll();
                    }
//                    synchronized (foeMoveSets) {
//                        foeMoveSets.put(me, foe);
//                        foeMoveSets.notifyAll();
//                    }
                }
            }).start();
        }
        try {
            while (complete[0] < tree.size()) {
                synchronized (complete) {
                    if (complete[0] < tree.size()) {
                        complete.wait();
                    }
                }
            }
//            synchronized (foeMoveSets) {
//                while (foeMoveSets.size() < tree.size()) {
//                    foeMoveSets.wait();
//                }
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Iterator<GameTree> iterator = tree.iterator();
        while (iterator.hasNext()) {
            GameTree me = iterator.next();
            if (foeFinish(map, foeColor, me)) {
                iterator.remove();
                continue;
            }
            map.addPiece(me.getPoint(), me.getColor());
            boolean meFinish = growGameTreeIfMeFinish(map, color, me);
            map.removeCell(me.getPoint());
            if (meFinish) {
                tree.clearChildren();
                tree.add(me);
                return tree;
            } else {
                iterator.remove();
            }
        }
//            if (win) {
//                tree.clearChildren();
//                tree.add(t);
//                return tree;
//            }
//            if (!win) {
//                tree.remove(t);
//            }
        return tree.isEmpty() ? null : tree;
    }

    private boolean growGameTreeIfMeFinish(BitPieceMap map, int color, GameTree me) {
        for (GameTree foe : me) {
            map.addPiece(foe.getPoint(), foe.getColor());
            GameTree result = getGameTree(map, foe.getPoint(), color);
            map.removeCell(foe.getPoint());
            if (result == null) {
                return false;
            } else {
                //grow game tree
                foe.addAllChildren(result);
            }
        }
        return true;
    }

    private boolean foeFinish(BitPieceMap map, int foeColor, GameTree me) {
        for (GameTree foe : me) {
            if (isFinish(map, foe.getPoint(), foeColor)) {
//                    tree.remove(me);
//                    --i;
                return true;
//                    return null;
            }
        }
        return false;
    }


    private boolean isFinish(BitPieceMap map, int point, int color) {
        return finishCondition.isFinish(map, point, color);
    }

    private List<Integer> getMoveSet(BitPieceMap map, int lastFoeMove, int color) {
        return moveSetGetter.getMoveSet(map, lastFoeMove, color);
    }

    public interface FinishCondition {
        boolean isFinish(BitPieceMap map, int point, int color);
    }
    public interface MoveSetGetter {
        List<Integer> getMoveSet(BitPieceMap map, int lastFoeMove, int color);
    }
}
