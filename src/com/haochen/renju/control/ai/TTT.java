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

    private static final int win = 6;              //����
    private static final int flex4 = 5;              //����
    private static final int block4 = 4;           //����
    private static final int flex3 = 3;          //����
    private static final int block3 = 2;         //����
    private static final int flex2 = 1;          //���
    private static final int Ntype = 7;      //���͸���
    //��������ṹ
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

    int[][] chessboard = new int[15][15];//��������
    int step = 0; //��ǰ����
    int cpt, hum;//˫��������ɫ
    int MaxDepth;//ÿ������������
    int SearchDepth = 8; //����������
    int BestVal; //����߷���ֵ
    int total; //Ҷ�ڵ���
    int ABcut;//��֦��
    int MaxLen[] = {0, 4, 5, 4, 5, 10, 10, 15, 20};
    xy BestMove = new xy(); //����߷�
    xy[] chessxy = new xy[225];//������������
    //��¼��ǰ���ѡ��
    point[][] s = new point[10][225];
    double ThinkTime; //����˼��ʱ��
    int[][] score = new int[15][15];//�ո��ֵ
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


    //�ж�ÿһ��������ɫ
    //1�Ǻ��壬2�ǰ��壬0�ǿո�
    int color(int step) {
        if (step % 2 != 0) return 1;
        return 2;
    }

    //�������Խ��
    int CheckXy(int x, int y) {
        if (x < 0 || x > 14 || y < 0 || y > 14) return 0;
        return 1;
    }

    //��һ��
    void MakeMove(xy next) {
        int x = next.x;
        int y = next.y;
        step++;
        chessboard[x][y] = color(step);
        chessxy[step].x = x;
        chessxy[step].y = y;
    }

    //��һ��
    public void move(int point) {
        int x = PointUtils.getX(point) - 1;
        int y = PointUtils.getY(point) - 1;
        step++;
        chessboard[x][y] = color(step);
        chessxy[step].x = x;
        chessxy[step].y = y;
    }

    //ɾһ��
    void DelMove() {
        int x = chessxy[step].x;
        int y = chessxy[step].y;
        chessboard[x][y] = 0;
        step--;
    }

    public void withdraw() {
        DelMove();
    }

    public void display() {//��ʾ����˼�����
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

    //�ж�����
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

    //����Ƿ�����ʤ��
    int CheckWin() {
        int x = chessxy[step].x;
        int y = chessxy[step].y;
        int role = chessboard[x][y];
        if (CheckFive(x, y, role) == win) {
            //�Ե��ԽǶȷ��ط�ֵ
            if (role == cpt) return 10000;
            else return -10000;
        }
        return 0;
    }

    //�ж�����
    int CheckType(int len, int len2, int block, int count) {
        //len����ռ���ڵ���5�����ܳ���
        if (len >= 5 && count > 1) {
            if (count == 5) return win;
            //����ռ����5
            //���ͳ���С��5
            //û�б��赲
            //������һ��ֻ��һ�������
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
                //ֻ��һ������㣬������
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
        int a, b;//����
        int kong = 0;//�ո���
        int block = 0; //���赲����(�����߽���)
        int len = 1; //����ռ�
        int len2 = 1;  //���ͳ���
        int count = 1; //������
        //role��������
        for (k = 1; k <= 4; k++) {
            a = x + k * i;
            b = y + k * j;
            if (CheckXy(a, b) == 0) {
                //���߽����赲
                if (len2 == kong + count)
                    block++;
                break;
            }
            if (chessboard[a][b] == role) {
                //�м�ո����2
                //���ͳ��ȴ���4
                //������������
                if (kong > 2 || kong + count > 4) break;
                count++;
                len++;
                len2 = kong + count;
            } else if (chessboard[a][b] == 0) {
                len++;
                kong++;
            } else {
                //�Է������赲
                if (len2 == kong + count)
                    block++;
                break;
            }
        }
        //�����м�ո�
        kong = len2 - count;
        //��һ����
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

    //���ͼ���
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

    //��ɱ���ֵ
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

    //���ո���
    int ScoreMove(int x, int y) {
        int i;
        int Cscore = 0, Hscore = 0;
        //���͸���
        int[] Htype = new int[7], Ctype = new int[7];
        //��ȡ������
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

    //�����Χn������������
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

    //�������пո��ֵ
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

    //���Ӵ��
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
        //����������
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

    //������һ�������߷��������ܸ���
    int GetMove(int depth) {
        for (int i = 0; i < 225; ++i) {
            s[depth][i].p.x = 0;
            s[depth][i].p.y = 0;
            s[depth][i].val = 0;
        }
        int i, j, val;
        int t = 0;//��ѡ�����
        int role = color(step + 1);//��һ��������ɫ
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
        if (iswin != 0)//����ʤ����ֱ�ӷ��ط�ֵ
        {
            //���Է�ʤ��
            if (depth % 2 == 0) return iswin;
            else //����ʤ��
                return -iswin;
        }
        //Ҷ�ڵ㣬���ؾ����
        if (depth == 0) return evaluate();
        //������һ���߷�
        int t = GetMove(depth);
        //���������߷�
        for (int i = 1; i <= MaxLen[depth]; i++) {
            MakeMove(s[depth][i].p);
            val = -AlphaBeta(depth - 1, -beta, -alpha);
            DelMove();
            //AB��֦
            if (val >= beta) {
                if (depth == MaxDepth) {
                    BestMove.x = s[depth][i].p.x;
                    BestMove.y = s[depth][i].p.y;
                }
                ABcut++;
                return val;
            }
            //�����ֵĵ�
            if (val > alpha) {
                alpha = val;
                //��¼��ѵ�
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
        //��ʼ���ڵ�������֦��
        total = 0;
        ABcut = 0;
        //��һ��ֱ������Ԫ
        if (step == 0) {
            BestMove.x = 7;
            BestMove.y = 7;
            return BestMove;
        }
        //�����2��ʼ�����ϼӴ��������
        //ȷ�������·��ʤ��
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

    //��������
    public void drawchess() {
        int i, j;
        display();
        for (i = 14; i >= 0; i--) {
            if (i + 1 < 10) System.out.printf(" %d", i + 1);
            else System.out.printf("%d", i + 1);
            for (j = 0; j < 15; j++) {
                switch (chessboard[j][i]) {
                    case 0:
                        System.out.printf("��");
                        break;
                    case 1:
                        System.out.printf("��");
                        break;
                    case 2:
                        System.out.printf("��");
                        break;
                    case 3:
                        System.out.printf("��");
                        break;
                }
            }
            System.out.printf("\n");
        }
        System.out.printf("  1 2 3 4 5 6 7 8 9 0 1 2 3 4 5\n");
    }

    void ChessStart() {//��ֿ�ʼ
        step = 0;
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                chessboard[i][j] = 0;
            }
        }
        System.out.printf("��������������1����������2\n");
        cpt = scanner.nextInt();//����������ɫ
        hum = 3 - cpt;//����������ɫ
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
        System.out.printf("��ѡ��һ������\n");
        System.out.printf("��wasd�ƶ���yȷ��\n");
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
        xy next = gobang();//��һ��
        MakeMove(next);
        return PointUtils.build(next.toInt());
    }

    public void launch() {
        drawchess(); //������
        ChessStart();//��ֿ�ʼ
        while (true) {
            //��һ���ǵ��ԣ�������
            if (color(step) == cpt) {
                xy next = new xy();
                GetXy(next);
                MakeMove(next);
                drawchess();
            }
            //��������
            else {
                xy next = gobang();
                MakeMove(next);
                drawchess();
            }
            //����ʤ��
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
