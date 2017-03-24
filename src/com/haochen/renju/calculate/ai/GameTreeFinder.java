package com.haochen.renju.calculate.ai;

import com.haochen.renju.main.Config;
import com.haochen.renju.storage.BitPieceMap;
import com.haochen.renju.util.CellUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;

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

    GameTree getGameTree(final BitPieceMap map, final int lastFoeMove, int color) {
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
        final CountDownLatch latch = new CountDownLatch(tree.size());
        Thread[] threads = growByMultiThread(map, tree, foeColor, latch);
        waitForComplete(latch, threads);

        if (shouldStopFinding()) {
            return null;
        }

        Iterator<GameTree> meIterator = tree.iterator();
        while (meIterator.hasNext()) {
            GameTree me = meIterator.next();
            if (foeFinish(map, foeColor, me)) {
                meIterator.remove();
                continue;
            }
            map.addPiece(me.getPoint(), me.getColor());

            // 此处间接递归
            boolean meFinish = growGameTreeIfMeFinish(map, color, me);

            map.removeCell(me.getPoint());
            if (meFinish) {
                tree.clearChildren();
                tree.add(me);
                return tree;
            } else {
                meIterator.remove();
            }
        }
        return tree.isEmpty() ? null : tree;
    }

    private void waitForComplete(CountDownLatch latch, Thread[] threads) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            for (Thread t : threads) {
                if (t != null && !t.isInterrupted()) {
                    t.interrupt();
                }
            }
        }
    }

    private Thread[] growByMultiThread(BitPieceMap map, GameTree tree, final int foeColor, final CountDownLatch latch) {
        Thread[] threads = new Thread[tree.size()];
        for (int i = 0; i < tree.size(); ++i) {
            final GameTree me = tree.getChild(i);
            final BitPieceMap newMap = map.another();
            newMap.addPiece(me.getPoint(), me.getColor());
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    List<Integer> foe = getMoveSet(newMap, me.getPoint(), foeColor);
                    me.add(foe, foeColor);
                    latch.countDown();
                }
            });
            threads[i].start();
        }
        return threads;
    }

    private boolean shouldStopFinding() {
        return Config.shouldStopFinding;
    }

    private boolean growGameTreeIfMeFinish(BitPieceMap map, int color, GameTree me) {
        for (GameTree foe : me) {
            map.addPiece(foe.getPoint(), foe.getColor());
            // 间接递归
            GameTree result = getGameTree(map, foe.getPoint(), color);
            map.removeCell(foe.getPoint());
            if (result == null) {
                return false;
            } else {
                // grow game tree
                foe.addAllChildren(result);
            }
        }
        return true;
    }

    private boolean foeFinish(BitPieceMap map, int foeColor, GameTree me) {
        for (GameTree foe : me) {
            if (isFinish(map, foe.getPoint(), foeColor)) {
                return true;
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
