package com.haochen.renju.control.ai;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.storage.Point;
import com.haochen.renju.util.PointUtils;

import java.util.Date;
import java.util.Scanner;

/**
 * Created by Haochen on 2016/11/21.
 */
public class TTT {

    Scanner scanner = new Scanner(System.in);

    private static final int win = 6;              //连五
    private static final int flex4 = 5;              //活四
    private static final int block4 = 4;           //冲四
    private static final int flex3 = 3;          //活三
    private static final int block3 = 2;         //眠三
    private static final int flex2 = 1;          //活二
    private static final int Ntype = 7;      //棋型个数
    //棋子坐标结构
    private class xy {
        int x;
        int y;
        public int toInt() {
            return PointUtils.parse(x + 1, y + 1);
        }
    }

    private class point {
        xy p = new xy();
        int val;
    }

    int[][] chessboard = new int[15][15];//棋盘数组
    int step = 0; //当前步数
    int cpt, hum;//双方棋子颜色
    int MaxDepth;//每次搜索最大深度
    int SearchDepth = 8; //搜索最大深度
    int BestVal; //最佳走法分值
    int total; //叶节点数
    int ABcut;//剪枝数
    int MaxLen[] = {0, 4, 5, 4, 5, 10, 10, 15, 20};
    xy BestMove = new xy(); //最佳走法
    xy[] chessxy = new xy[225];//已落棋子数组
    //记录当前深度选点
    point[][] s = new point[10][225];
    double ThinkTime; //电脑思考时间
    int[][] score = new int[15][15];//空格分值
    int[][] move = new int[15][15];

    public TTT() {
        for (int i = 0; i < chessxy.length; ++i) {
            chessxy[i] = new xy();
        }
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 225; ++j) {
                s[i][j] = new point();
            }
        }
    }

    public TTT(int color) {
        if (color == Cell.BLACK) {
            cpt = Cell.BLACK;
            hum = Cell.WHITE;
        } else {
            cpt = Cell.WHITE;
            hum = Cell.BLACK;
        }
        for (int i = 0; i < chessxy.length; ++i) {
            chessxy[i] = new xy();
        }
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 225; ++j) {
                s[i][j] = new point();
            }
        }
    }


    //判断每一步棋子颜色
    //1是黑棋，2是白棋，0是空格
    int color(int step) {
        if (step % 2 != 0) return 1;
        return 2;
    }

    //检查坐标越界
    int CheckXy(int x, int y) {
        if (x < 0 || x > 14 || y < 0 || y > 14) return 0;
        return 1;
    }

    //添一子
    void MakeMove(xy next) {
        int x = next.x;
        int y = next.y;
        step++;
        chessboard[x][y] = color(step);
        chessxy[step].x = x;
        chessxy[step].y = y;
    }

    //添一子
    public void move(int point) {
        int x = PointUtils.getX(point) - 1;
        int y = PointUtils.getY(point) - 1;
        step++;
        chessboard[x][y] = color(step);
        chessxy[step].x = x;
        chessxy[step].y = y;
    }

    //删一子
    void DelMove() {
        int x = chessxy[step].x;
        int y = chessxy[step].y;
        chessboard[x][y] = 0;
        step--;
    }

    public void withdraw() {
        DelMove();
    }

    public void display() {//显示电脑思考结果
        System.out.printf("ThinkTime=%f\n", ThinkTime);
        System.out.printf("total=%d ABcut=%d\n", total, ABcut);
        System.out.printf("bestx=%d ", BestMove.x + 1);
        System.out.printf("besty=%d ", BestMove.y + 1);
        System.out.printf("step=%d ", step);
        System.out.printf("val=%d\n", BestVal);

//        for (int i = 0; i < 15; ++i) {
//            for (int j = 0; j < 15; ++j) {
//                System.out.printf("%10d", score[i][j]);
//            }
//            System.out.println();
//        }
    }

    //判断连五
    int FiveLine(int role, int x, int y, int i, int j) {
        int a, b, k;
        int count = 1;
        for (k = 1; k <= 4; k++) {
            a = x + k * i;
            b = y + k * j;
            if (CheckXy(a, b) == 0) break;
            if (chessboard[a][b] != role) break;
            count++;
        }
        for (k = 1; k <= 4; k++) {
            a = x - k * i;
            b = y - k * j;
            if (CheckXy(a, b) == 0) break;
            if (chessboard[a][b] != role) break;
            count++;
        }
        if (count >= 5) return 1;
        return 0;
    }

    int CheckFive(int x, int y, int role) {
        if (FiveLine(role, x, y, 1, 0) != 0)
            return win;
        if (FiveLine(role, x, y, 0, 1) != 0)
            return win;
        if (FiveLine(role, x, y, 1, 1) != 0)
            return win;
        if (FiveLine(role, x, y, 1, -1) != 0)
            return win;
        return 0;
    }

    //检查是否有人胜利
    int CheckWin() {
        int x = chessxy[step].x;
        int y = chessxy[step].y;
        int role = chessboard[x][y];
        if (CheckFive(x, y, role) == win) {
            //以电脑角度返回分值
            if (role == cpt) return 10000;
            else return -10000;
        }
        return 0;
    }

    //判断棋型
    int CheckType(int len, int len2, int block, int count) {
        //len成五空间大于等于5，才能成五
        if (len >= 5 && count > 1) {
            if (count == 5) return win;
            //成五空间大于5
            //棋型长度小于5
            //没有被阻挡
            //否则下一步只有一个成五点
            if (len > 5 && len2 < 5 && block == 0) {
                switch (count) {
                    case 2:
                        return flex2;
                    case 3:
                        return flex3;
                    case 4:
                        return flex4;
                }
            } else {
                //只有一个成五点，是眠棋
                switch (count) {
                    case 3:
                        return block3;
                    case 4:
                        return block4;
                }
            }
        }
        return 0;
    }

    int TypeLine(int role, int x, int y, int i, int j) {
        int k;
        int a, b;//坐标
        int kong = 0;//空格数
        int block = 0; //被阻挡次数(包括边界线)
        int len = 1; //成五空间
        int len2 = 1;  //棋型长度
        int count = 1; //棋子数
        //role己方棋子
        for (k = 1; k <= 4; k++) {
            a = x + k * i;
            b = y + k * j;
            if (CheckXy(a, b) == 0) {
                //被边界线阻挡
                if (len2 == kong + count)
                    block++;
                break;
            }
            if (chessboard[a][b] == role) {
                //中间空格大于2
                //棋型长度大于4
                //不再增加棋子
                if (kong > 2 || kong + count > 4) break;
                count++;
                len++;
                len2 = kong + count;
            } else if (chessboard[a][b] == 0) {
                len++;
                kong++;
            } else {
                //对方棋子阻挡
                if (len2 == kong + count)
                    block++;
                break;
            }
        }
        //计算中间空格
        kong = len2 - count;
        //另一方向
        for (k = 1; k <= 4; k++) {
            a = x - k * i;
            b = y - k * j;
            if (CheckXy(a, b) == 0) {
                if (len2 == kong + count)
                    block++;
                break;
            }
            if (chessboard[a][b] == role) {
                if (kong > 2 || kong + count > 4) break;
                count++;
                len++;
                len2 = kong + count;
            } else if (chessboard[a][b] == 0) {
                len++;
                kong++;
            } else {
                if (len2 == kong + count)
                    block++;
                break;
            }
        }
        return CheckType(len, len2, block, count);
    }

    //棋型计数
    void TypeCount(int x, int y, int role, int[] type) {
        int[] d = new int[4];
        d[0] = TypeLine(role, x, y, 1, 0);
        d[1] = TypeLine(role, x, y, 0, 1);
        d[2] = TypeLine(role, x, y, 1, 1);
        d[3] = TypeLine(role, x, y, 1, -1);
        type[d[0]]++;
        type[d[1]]++;
        type[d[2]]++;
        type[d[3]]++;
    }

    //必杀点分值
    int GetKill(int[] Ctype, int[] Htype) {
        int temp, i;
        if (color(step + 1) == hum) {
            for (i = 1; i < 7; i++) {
                temp = Ctype[i];
                Ctype[i] = Htype[i];
                Htype[i] = temp;
            }
        }
        if (Ctype[win] > 0)
            return 10000;
        else if (Htype[win] > 0)
            return 5000;
        else if (Ctype[flex4] > 0)
            return 2500;
        else if (Ctype[block4] > 1)
            return 2500;
        else if (Ctype[block4] > 0 && Ctype[flex3] > 0)
            return 2000;
        else if (Htype[flex4] > 0)
            return 1200;
        else if (Htype[block4] > 1)
            return 1000;
        else if (Htype[block4] > 0 && Htype[flex3] > 0)
            return 800;
        else if (Ctype[flex3] > 1)
            return 400;
        else if (Htype[flex3] > 1)
            return 200;
        else
            return 0;
    }

    //给空格打分
    int ScoreMove(int x, int y) {
        int i;
        int Cscore = 0, Hscore = 0;
        //棋型个数
        int[] Htype = new int[7], Ctype = new int[7];
        //获取单棋型
        TypeCount(x, y, cpt, Ctype);
        TypeCount(x, y, hum, Htype);
        int kill = GetKill(Ctype, Htype);
        int[] Cval = {0, 5, 5, 30, 30};
        int[] Hval = {0, 5, 5, 30, 30};
        for (i = 1; i < 5; i++) {
            if (color(step + 1) == cpt)
                Cval[i] += 5;
            else
                Hval[i] += 5;
            Cscore += Cval[i] * Ctype[i];
            Hscore += Hval[i] * Htype[i];
        }
        return Cscore + Hscore + kill;
    }

    //检查周围n格内有无棋子
    int round(int n, int x, int y) {
        int i, j;
        for (i = x - n; i <= x + n; i++) {
            for (j = y - n; j <= y + n; j++) {
                if (CheckXy(i, j) != 0 && chessboard[i][j] != 0)
                    return 1;
            }
        }
        return 0;
    }

    //更新所有空格分值
    void UpdateScore() {
        int i, j;
        for (i = 0; i < 15; i++) {
            for (j = 0; j < 15; j++) {
                score[i][j] = 0;
                if (chessboard[i][j] == 0) {
                    if (round(2, i, j) != 0)
                        score[i][j] = ScoreMove(i, j);
                }
            }
        }
    }

    //棋子打分
    void ChessScore(int role, int[] score, int[] type) {
        int i, j;
        int x, y;
        int[] d = new int[4];
        score[0] = 0;
        for (i = role; i <= step; i += 2) {
            x = chessxy[i].x;
            y = chessxy[i].y;
            TypeCount(x, y, role, type);
        }
        int[] count = {0, 2, 3, 3, 4, 4, 5};
        //除以棋子数
        for (i = 1; i < Ntype; i++) {
            type[i] /= count[i];
        }
        int[] val = {0, 2, 2, 5, 5};
        for (i = 1; i < 5; i++) {
            score[0] += val[i] * type[i];
        }
    }

    int evaluate2() {
        int i, j;
        int[] Cscore = new int[1], Hscore = new int[1];
        int[] Ctype = new int[7], Htype = new int[7];
        total++;
        ChessScore(cpt, Cscore, Ctype);
        ChessScore(hum, Hscore, Htype);

        if (Ctype[win] > 0)
            return 10000;
        else if (Htype[win] > 0)
            return -10000;
        else if (Htype[flex4] > 0)
            Hscore[0] += 250;
        else if (Htype[block4] > 0)
            Hscore[0] += 250;
        else if (Ctype[flex4] > 0)
            Cscore[0] += 250;
        else if (Ctype[block4] > 1)
            Cscore[0] += 250;
        else if (Ctype[block4] > 0 && Ctype[flex3] > 0)
            Cscore[0] += 200;
        else if (Htype[flex3] > 0)
            Hscore[0] += 150;
        else if (Ctype[flex3] > 1)
            Hscore[0] += 80;
        return Cscore[0] - Hscore[0];
    }

    int evaluate() {
        int i, j;
        int val;
        int cs = 0;
        xy p = new xy();
        for (i = 0; i < 15; i++) {
            for (j = 0; j < 15; j++) {
                if (chessboard[i][j] == 0) {
                    if (round(2, i, j) != 0) {
                        val = ScoreMove(i, j);
                        if (val > cs) {
                            cs = val;
                            p.x = i;
                            p.y = j;
                        }
                    }
                }
            }
        }
        MakeMove(p);
        val = evaluate2();
        DelMove();
        return val;
    }

    void sort(point[] a, int n) {
        int i, j;
        for (i = 2; i <= n; i++) {
            point key = new point();
            key.p.x = a[i].p.x;
            key.p.y = a[i].p.y;
            key.val = a[i].val;
            for (j = i; j > 1 && a[j - 1].val < key.val; j--) {
                a[j].p.x = a[j - 1].p.x;
                a[j].p.y = a[j - 1].p.y;
                a[j].val = a[j - 1].val;
            }
            a[j].p.x = key.p.x;
            a[j].p.y = key.p.y;
            a[j].val = key.val;
        }
    }

    //生成下一步所有走法，返回总个数
    int GetMove(int depth) {
        for (int i = 0; i < 225; ++i) {
            s[depth][i].p.x = 0;
            s[depth][i].p.y = 0;
            s[depth][i].val = 0;
        }
        int i, j, val;
        int t = 0;//可选点个数
        int role = color(step + 1);//下一步棋子颜色
        xy p = new xy();
        UpdateScore();
        for (i = 0; i < 15; i++) {
            for (j = 0; j < 15; j++) {
                p.x = i;
                p.y = j;
                if (chessboard[i][j] == 0) {
                    if (round(2, i, j) != 0) {
                        t++;
                        s[depth][t].p.x = p.x;
                        s[depth][t].p.y = p.y;
                        s[depth][t].val = score[i][j];
                    }
                }
            }
        }
        sort(s[depth], t);
        return t;
    }

    int AlphaBeta(int depth, int alpha, int beta) {
        int val, iswin;
        iswin = CheckWin();
        if (iswin != 0)//有人胜利，直接返回分值
        {
            //电脑方胜利
            if (depth % 2 == 0) return iswin;
            else //对手胜利
                return -iswin;
        }
        //叶节点，返回局面分
        if (depth == 0) return evaluate();
        //生成下一步走法
        int t = GetMove(depth);
        //遍历所有走法
        for (int i = 1; i <= MaxLen[depth]; i++) {
            MakeMove(s[depth][i].p);
            val = -AlphaBeta(depth - 1, -beta, -alpha);
            DelMove();
            //AB剪枝
            if (val >= beta) {
                if (depth == MaxDepth) {
                    BestMove.x = s[depth][i].p.x;
                    BestMove.y = s[depth][i].p.y;
                }
                ABcut++;
                return val;
            }
            //找最大分的点
            if (val > alpha) {
                alpha = val;
                //记录最佳点
                if (depth == MaxDepth) {
                    BestMove.x = s[depth][i].p.x;
                    BestMove.y = s[depth][i].p.y;
                }
            }
        }
        return alpha;
    }

    xy gobang() {
        int i;
        Date start = new Date();
        //初始化节点数，剪枝数
        total = 0;
        ABcut = 0;
        //第一步直接下天元
        if (step == 0) {
            BestMove.x = 7;
            BestMove.y = 7;
            return BestMove;
        }
        //从深度2开始，不断加大深度搜索
        //确保以最短路径胜利
        for (i = 2; i <= SearchDepth; i += 2) {
            MaxDepth = i;
            BestVal = AlphaBeta(i, -10001, 10000);
            if (BestVal >= 10000)
                break;
        }
        Date finish = new Date();
        ThinkTime = (double) (finish.getTime() - start.getTime());
        System.out.println(BestMove.x + ", " + BestMove.y + ": " + BestVal + "/ " + score[BestMove.x][BestMove.y]);
        return BestMove;
    }

    //绘制棋盘
    public void drawchess() {
        int i, j;
        display();
        for (i = 14; i >= 0; i--) {
            if (i + 1 < 10) System.out.printf(" %d", i + 1);
            else System.out.printf("%d", i + 1);
            for (j = 0; j < 15; j++) {
                switch (chessboard[j][i]) {
                    case 0:
                        System.out.printf("┼");
                        break;
                    case 1:
                        System.out.printf("●");
                        break;
                    case 2:
                        System.out.printf("○");
                        break;
                    case 3:
                        System.out.printf("□");
                        break;
                }
            }
            System.out.printf("\n");
        }
        System.out.printf("  1 2 3 4 5 6 7 8 9 0 1 2 3 4 5\n");
    }

    void ChessStart() {//棋局开始
        step = 0;
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                chessboard[i][j] = 0;
            }
        }
        System.out.printf("电脑先下请输入1，否则输入2\n");
        cpt = scanner.nextInt();//电脑棋子颜色
        hum = 3 - cpt;//对手棋子颜色
    }

    int GoToP(xy p) {
        int dx = p.x, dy = p.y;
        int c = scanner.nextInt();
        switch (c) {
            case 8:
                dy++;
                break;
            case 2:
                dy--;
                break;
            case 4:
                dx--;
                break;
            case 6:
                dx++;
                break;
            case 5:
                return 0;
            default:
                return 1;
        }
        if (chessboard[dx][dy] != 0) {
            drawchess();
            p.x = dx;
            p.y = dy;
        } else {
            chessboard[dx][dy] = 3;
            drawchess();
            chessboard[dx][dy] = 0;
            p.x = dx;
            p.y = dy;
        }
        return 1;
    }

    void GetXy(xy p) {
        int i, j;
        int flag;
        int dx = chessxy[step].x;
        int dy = chessxy[step].y;
        for (i = -2; i <= 2; i++) {
            for (j = -2; j <= 2; j++) {
                if (CheckXy(dx + i, dy + j) == 1 && chessboard[dx + i][dy + j] == 0) {
                    dx += i;
                    dy += j;
                    i = j = 3;
                }
            }
        }
        chessboard[dx][dy] = 3;
        drawchess();
        chessboard[dx][dy] = 0;
        p.x = dx;
        p.y = dy;
        System.out.printf("请选择一点下棋\n");
        System.out.printf("按wasd移动，y确认\n");
        do {
            flag = (GoToP(p));
            if (chessboard[p.x][p.y]!=0)
                flag = 1;
        } while (flag != 0);
    }

    public void humanMove(Point point) {
        move(PointUtils.parse(point));
    }

    public Point aiMove() {
        xy next = gobang();//下一步
        MakeMove(next);
        return PointUtils.build(next.toInt());
    }

    public void launch() {
        drawchess(); //画棋盘
        ChessStart();//棋局开始
        while (true) {
            //上一步是电脑，人下棋
            if (color(step) == cpt) {
                xy next = new xy();
                GetXy(next);
                MakeMove(next);
                drawchess();
            }
            //电脑下棋
            else {
                xy next = gobang();
                MakeMove(next);
                drawchess();
            }
            //有人胜利
            if (CheckWin() != 0) {
                if (color(step) == cpt)
                    System.out.printf("computer win\n");
                else
                    System.out.printf("man win\n");
                ChessStart();
            }
        }
    }
}
