package com.haochen.renju.control.ai;

import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;

import java.util.Arrays;
import java.util.List;

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


    public WinTree getWinTree(PieceMap map, Point lastFoeMove, PieceColor color) {
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
            WinTree t = tree.getChild(i);
            map.addPiece(-1, t.getPoint(), t.getColor());
            List<Point> foeMoves = getMoveSet(map, t.getPoint(), foeColor);
            t.add(foeMoves, foeColor);
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
//                for (int j = i + 1; j < tree.size(); ++j) {
//                    tree.remove(tree.getChild(j));
//                    tree.addAllChildren(t);
//                }
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
