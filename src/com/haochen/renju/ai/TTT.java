package com.haochen.renju.ai;

import java.util.Date;
import java.util.Scanner;

import com.haochen.renju.common.Mediator;
import com.haochen.renju.form.Point;

public class TTT {
    private int win = 6;              //连五
    private int flex4 = 5;              //活四
    private int block4 = 4;           //冲四
    private int flex3 = 3;          //活三
    private int block3 = 2;         //眠三
    private int flex2 = 1;          //活二
    private int Ntype = 7;      //棋型数
    
    private Mediator mediator;
    
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    private class point
    {
        Point p;
        int val;
    };
    private int[][] chessboard = new int[15][15]; //棋盘数组
    private int step=0; //当前步数
    private int cpt,hum;//双方棋子颜色
    private int MaxDepth;//每次搜索最大深度
    private int SearchDepth=8; //搜索最大深度
    private int BestVal; //最佳走法分值
    private int total; //叶节点数
    private int ABcut;//剪枝数
    private int MaxLen[]={0,4,5,4,5,8,10,15,20};
    private Point BestMove; //最佳走法
    private Point[] chessxy = new Point[255];//已落棋子数组
    //记录当前深度选点
    private point[][] s = new point[10][100];
    private double ThinkTime; //电脑思考时间
    private int[][] score = new int[15][15];//空格分值
    
    public TTT() {
        BestMove = new Point();
        for (int i = 0; i < chessxy.length; ++i) {
            chessxy[i] = new Point();
        }
        for (int i = 0; i < s.length; ++i) {
            for (int j = 0; j < s[i].length; ++j) {
                s[i][j] = new point();
            }
        }
    }
    
    //判断每一步棋子颜色
    //1是黑棋，2是白棋，0是空格
    private int color(int step)
    {
        if(step % 2 == 1) {
            return 1;
        }
        return 2;
    }
    //检查周围四个方向有无该颜色棋子
    //一格以内
    private int CheckRound(int x,int y,int role)
    {
        int i,j;
        for(i=-1;i<2;i++){
            for(j=-1;j<2;j++){
                if(chessboard[x+i][y+j]==role)
                return 1;
            }
        }
        return 0;
    }

    //检查坐标越界
    private int CheckXy(int x,int y)
    {
        if(x<0||x>14||y<0||y>14)
        return 0;
        else
        return 1;
    }
    //添一子
    private void MakeMove(Point next)
    {
        int x=next.x;
        int y=next.y;
        step++;
        chessboard[x][y]=color(step);
        chessxy[step].x=x;
        chessxy[step].y=y;
    }
    //删一子
    private void DelMove()
    {
        int x=chessxy[step].x;
        int y=chessxy[step].y;
        chessboard[x][y]=0;
        step--;
    }
    public void display()//显示电脑思考结果
    {
        System.out.println("ThinkTime=" + ThinkTime);
        System.out.println("total=" + total + " ABcut=" + ABcut);
        System.out.print("bestx=" + BestMove.x+1 + " ");
        System.out.print("besty=" + BestMove.y+1 +  " ");
        System.out.print("step=" + step + " ");
        System.out.println("val=" + BestVal);
    }

    //判断连五
    private int CheckFive(int x,int y,int role)
    {
        int a,b,count=1;
        //横
        for(a=x+1;a<=x+4;a++)
        {
            if(a>14) break;
            if(chessboard[a][y]==role){
                count++;
            }
            else break;
        }
        for(a=x-1;a>=x-4;a--)
        {
            if(a<0) break;
            if(chessboard[a][y]==role){
                count++;
            }
            else break;
        }
        if(count>=5) return win;
        //竖
        count=1;
        for(b=y+1;b<=y+4;b++)
        {
            if(b>14) break;
            if(chessboard[x][b]==role){
                count++;
            }
            else break;
        }
        for(b=y-1;b>=y-4;b--)
        {
            if(b<0) break;
            if(chessboard[x][b]==role){
                count++;
            }
            else break;
        }
        if(count>=5) return win;
        //左斜
        count=1;
        for(a=x+1,b=y+1;a<=x+4;a++,b++)
        {
            if(a>14||b>14) break;
            if(chessboard[a][b]==role){
                count++;
            }
            else break;
        }
        for(a=x-1,b=y-1;a>=x-4;a--,b--)
        {
            if(a<0||b<0) break;
            if(chessboard[a][b]==role){
                count++;
            }
            else break;
        }
        if(count>=5) return win;
        //右斜
        count=1;
        for(a=x+1,b=y-1;a<=x+4;a++,b--)
        {
            if(a>14||b<0) break;
            if(chessboard[a][b]==role){
                count++;
            }
            else break;
        }
        for(a=x-1,b=y+1;a>=x-4;a--,b++)
        {
            if(a<0||b>14) break;
            if(chessboard[a][b]==role){
                count++;
            }
            else break;
        }
        if(count>=5) return win;
        else return 0;
    }
    //检查是否有人胜利
    private int CheckWin()
    {
        int x=chessxy[step].x;
        int y=chessxy[step].y;
        int role=chessboard[x][y];
        if(CheckFive(x,y,role)==win){
            //以电脑角度返回分值
            if(role==cpt) return 10000;
            else return -10000;
        }
        else return 0;
    }
    //判断棋型
    private int CheckType(int len,int len2,int block,int count)
    {
        //len成五空间大于等于5，才能成五
        if(len>=5&&count>1){
            if(count==5) return win;
            //成五空间大于5
            //棋型长度小于5
            //没有被阻挡
            //否则下一步只有一个成五点
            if(len>5&&len2<5&&block==0){
                switch (count){
                    case 2: return flex2;
                    case 3: return flex3;
                    case 4: return flex4;
                }
            }
            else {
                //只有一个成五点，是眠棋
                switch (count){
                    case 3: return block3;
                    case 4: return block4;
                }
            }
        }
        return 0;
    }
    //查询该角色在某空格四个方向棋型
    private void GetType(int x,int y,int role,int[] d)
    {
        int a,b;//坐标
        int kong=0;//空格数
        int block=0; //被阻挡次数(包括边界线)
        int len=1; //成五空间
        int len2=1;  //棋型长度
        int count=1; //棋子数
        //role己方棋子颜色，d记录四方向棋型
        //横
        for(a=x+1;a<=x+4;a++)
        {
            if(a>14){
                //被边界线阻挡
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[a][y]==role){
                //中间空格大于2
                //棋型长度大于4
                //不再增加棋子
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[a][y]==0){
                len++;
                kong++;
            }
            else {
                //对方棋子阻挡
                if(len2==kong+count)
                block++;
                break;
            }
        }
        //计算中间空格
        kong=len2-count;
        //另一方向
        for(a=x-1;a>=x-4;a--)
        {
            if(a<0){
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[a][y]==role){
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[a][y]==0){
                len++;
                kong++;
            }
            else {
                if(len2==kong+count)
                block++;
                break;
            }
        }
        d[0]=CheckType(len,len2,block,count);
        //竖
        len=1;len2=1;count=1;
        block=0;kong=0;
        for(b=y+1;b<=y+4;b++)
        {
            if(b>14){
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[x][b]==role){
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[x][b]==0){
                len++;
                kong++;
            }
            else {
                if(len2==kong+count)
                block++;
                break;
            }
        }
        kong=len2-count;
        for(b=y-1;b>=y-4;b--)
        {
            if(b<0){
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[x][b]==role){
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[x][b]==0){
                len++;
                kong++;
            }
            else {
                if(len2==kong+count)
                block++;
                break;
            }
        }
        d[1]=CheckType(len,len2,block,count);
        //左斜
        len=1;len2=1;count=1;
        block=0;kong=0;
        for(a=x+1,b=y+1;a<=x+4;a++,b++)
        {
            if(a>14||b>14){
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[a][b]==role){
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[a][b]==0){
                len++;
                kong++;
            }
            else {
                if(len2==kong+count)
                block++;
                break;
            }
        }
        kong=len2-count;
        for(a=x-1,b=y-1;a>=x-4;a--,b--)
        {
            if(a<0||b<0){
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[a][b]==role){
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[a][b]==0){
                len++;
                kong++;
            }
            else {
                if(len2==kong+count)
                block++;
                break;
            }
        }
        d[2]=CheckType(len,len2,block,count);
        //右斜
        len=1;len2=1;count=1;
        block=0;kong=0;
        for(a=x+1,b=y-1;a<=x+4;a++,b--)
        {
            if(a>14||b<0){
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[a][b]==role){
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[a][b]==0){
                len++;
                kong++;
            }
            else {
                if(len2==kong+count)
                block++;
                break;
            }
        }
        kong=len2-count;
        for(a=x-1,b=y+1;a>=x-4;a--,b++)
        {
            if(a<0||b>14){
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[a][b]==role){
                if(kong>2||kong+count>4) break;
                count++;
                len++;
                len2=kong+count;
            }
            else if(chessboard[a][b]==0){
                len++;
                kong++;
            }
            else {
                if(len2==kong+count)
                block++;
                break;
            }
        }
        d[3]=CheckType(len,len2,block,count);
    }
    //必杀点分值
    private int GetKill(int[] Ctype,int[] Htype)
    {
        int temp,i;
        if(color(step+1)==hum){
            for(i=1;i<7;i++){
                temp=Ctype[i];
                Ctype[i]=Htype[i];
                Htype[i]=temp;
            }
        }
        if(Ctype[win]>0)
        return 10000;
        else if(Htype[win]>0)
        return 5000;
        else if(Ctype[flex4]>0)
        return 2500;
        else if(Ctype[block4]>1)
        return 2500;
        else if(Ctype[block4]>0&&Ctype[flex3]>0)
        return 2000;
        else if(Htype[flex4]>0)
        return 1000;
        else if(Htype[block4]>1)
        return 1000;
        else if(Htype[block4]>0&&Htype[flex3]>0)
        return 800;
        else if(Ctype[flex3]>1)
        return 400;
        else if(Htype[flex3]>1)
        return 200;
        else 
        return 0;
    }
    //棋型计数
    /**  
     * @Title: TypeCount  
     * @Description: TODO   
     * @param x
     * @param y
     * @param role
     * @param score
     * @param type      
     * 
     * score原本是int* score，传指针
     */
    private void TypeCount(int x, int y, int role, int[] score, int[] type)
    {
        int[] d = new int[4];
        int i;

        GetType(x, y, role, d);
        for (i = 0; i < 4; i++) {
            type[d[i]]++;
        }
        int[] val = { 0, 2, 2, 5, 5 };
        for (i = 1; i < 5; i++) {
            score[0] += val[i] * type[i];
        }
        
    }   
    /**  
     * @Title: ScoreMove  
     * @Description: TODO   给空格打分
     * @param x
     * @param y
     * @return      
     * 
     * Cscore，Hscore要指针传递，用数组代替指针
     */
    private int ScoreMove(int x,int y)
    {
        int[] Cscore={0};
        int[] Hscore={0};
        //棋型个数
        int[] Htype={0, 0, 0, 0, 0, 0, 0};
        int[] Ctype={0, 0, 0, 0, 0, 0, 0};
        //获取单棋型
        TypeCount(x,y,cpt,Cscore,Ctype);
        TypeCount(x,y,hum,Hscore,Htype);
        int kill=GetKill(Ctype,Htype);
        return Cscore[0]+Hscore[0]+kill;
    }
    //检查周围n格内有无棋子
    private int round(int n,int x,int y)
    {
        int i,j;
        for(i=x-n;i<=x+n;i++){
            for(j=y-n;j<=y+n;j++){
                /**
                 * CheckXy返回int，应该改成boolean
                 */
                if(CheckXy(i,j) == 0) continue;
                if(chessboard[i][j]!=0)
                return 1;
            }
        }
        return 0;
    }   
    //更新所有空格分值      
    private void UpdateScore()
    {
        int i,j;
        for(i=0;i<15;i++){
            for(j=0;j<15;j++){
                score[i][j]=0;
                if(chessboard[i][j]==0){
                    /**
                     * round返回int，应该改成boolean
                     */
                    if(round(2,i,j) != 0)
                    score[i][j]=ScoreMove(i,j);
                }
            }
        }
    }
    /**  
     * @Title: ChessScore  
     * @Description: TODO   
     * @param role      
     * 
     * score指针传递，用数组代替
     */
    private void ChessScore(int role,int[] score,int[] type)
    {
        int i,j;
        int x,y;
        int[] d = new int[4];
        score[0]=0;
        for(i=role;i<=step;i+=2){
            x=chessxy[i].x;
            y=chessxy[i].y;
            //棋型计数
            GetType(x,y,role,d);
            for(j=0;j<4;j++){
                type[d[j]]++;
            }
        }
        int[] count={0,2,3,3,4,4,5};
            //除以棋子数
        for(i=1;i<Ntype;i++){
            type[i]/=count[i];
        }
        int[] val={0,2,2,5,5};
        for(i=1;i<5;i++){
            score[0]+=val[i]*type[i];
        }
    }
    /**  
     * @Title: evaluate  
     * @Description: TODO   局面评估
     * @return      
     * 
     * Cscore，Hscore指针传递，用数组代替
     */
    private int evaluate()
    {
        int i,j;
        int[] Cscore = {0};
        int[] Hscore = {0};
        int[] Ctype={0, 0, 0, 0, 0, 0, 0};
        int[] Htype={0, 0, 0, 0, 0, 0, 0};
        total++;
        ChessScore(cpt,Cscore,Ctype);
        ChessScore(hum,Hscore,Htype);
        if(Ctype[win]>0)
        return 10000;
        else if(Htype[win]>0)
        return -10000;
        else if(Ctype[flex4]>0)
        Cscore[0]+=250;
        else if(Ctype[block4]>0)
        Cscore[0]+=250;
        else if(Htype[flex4]>0)
        Hscore[0]+=250;
        else if(Htype[block4]>1)
        Hscore[0]+=250;
        else if(Htype[block4]>0&&Htype[flex3]>0)
        Hscore[0]+=200;
        else if(Ctype[flex3]>0)
        Cscore[0]+=150;
        else if(Htype[flex3]>1)
        Hscore[0]+=80;
        return Cscore[0]-Hscore[0];
    }
    private void sort(point[] a,int n)
    {
        int i,j;
        point key;
        for(i=2;i<=n;i++){
            key=a[i];
            for(j=i;j>1&&a[j-1].val<key.val;j--){
                a[j]=a[j-1];
            }
            a[j]=key;
        }
    }
    //检查空位是否可下
    private int CheckMove(Point p,int role)
    {
        int x=p.x;
        int y=p.y;
        int[] d = new int[4];
        int i;
        //自己成活二以上棋型
        GetType(x,y,role,d);
        for(i=0;i<4;i++){
            if(d[i]>=flex2)
            return 1;
        }
        //必须贴身防守
        if(CheckRound(x,y,3-role)==0) return 0;
        //对方能成活二以上棋型
        GetType(x,y,3-role,d);
        for(i=0;i<4;i++){
            if(d[i]>=flex2)
            return 1;
        }
        return 0;
    }
    //生成下一步所有走法，返回总个数
    private int GetMove(int depth)
    {
        int i,j,val;
        int t=0;//可选点个数
        int role=color(step+1);//下一步棋子颜色
        Point p = new Point();
        UpdateScore();
        for(i=0;i<15;i++)
        {
            for(j=0;j<15;j++)
            {
                p.x=i;
                p.y=j;
                /**
                 * round返回int，应该改成boolean
                 */
                if(chessboard[i][j]==0&&round(2,i,j) != 0){
                    t++;
                    s[depth][t].p=p;
                    s[depth][t].val=score[i][j];
                }
            }
        }
        sort(s[depth],t);
        return t;
    }   
    private int AlphaBeta(int depth,int alpha,int beta)
    {
        int val,iswin;
        iswin=CheckWin();
        /**
         * CheckWin返回int，应该改成boolean
         * iswin类型应该改成boolean
         */
        if(iswin != 0)//有人胜利，直接返回分值
        { 
            //电脑方胜利
            if(depth%2==0) return iswin;
            else //对手胜利
            return -iswin;
        }
        //叶节点，返回局面分
        if(depth==0) return  evaluate();
        //生成下一步走法
        int t=GetMove(depth);
        //遍历所有走法
        for(int i=1;i<=MaxLen[depth];i++)
        {
            //在棋盘上下棋
            MakeMove(s[depth][i].p);
            //递归求分值
            val=-AlphaBeta(depth-1,-beta,-alpha);
            //删除落子
            DelMove();
            //AB剪枝
            if(val>=beta){
                if(depth==MaxDepth)
                BestMove=s[depth][i].p;
                ABcut++;
                return val;
            }
            //找最大分的点
            if(val>alpha)
            {
                alpha=val;
                //记录最佳点
                if(depth==MaxDepth)
                BestMove=s[depth][i].p;
            }
        }
        return alpha;
    }
    public Point gobang()
    {
        Date start,finish;
        start=new Date();
        //初始化节点数，剪枝数
        total=0;ABcut=0;
        //第一步直接下天元
        if(step==0){
            BestMove.x=7; 
            BestMove.y=7;
            return BestMove;
        }
        //从深度2开始，不断加大深度搜索
        //确保以最短路径胜利
        for(MaxDepth=2;MaxDepth<=SearchDepth;MaxDepth+=2){
            BestVal=AlphaBeta(MaxDepth,-10001,10000);
            if(BestVal==10000)
            break;
        }
        finish=new Date();
        ThinkTime=(double)(finish.getTime()-start.getTime());
        display();  
        return BestMove;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //绘制棋盘
//    private void drawchess()
//    {
//        int i,j;
//        for(i=0;i<15;i++)
//        {
//            if(i+1<10) {
//                System.out.print(" " + i+1);
//            } else {
//                System.out.print(i+1);
//            }
//            for(j=0;j<15;j++)
//            {
//                switch(chessboard[j][i])
//                {
//                    case 0: System.out.print("+ ");break;
//                    case 1: System.out.print("o ");break;
//                    case 2: System.out.print("@ ");break;
//                }
//            }
//            System.out.println();
//        }
//        System.out.println("0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5");
//    }
    private void ChessStart()//棋局开始
    {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                chessboard[i][j] = 0;
            }
        }
        step=0;
        System.out.println("电脑先下请输入1，否则输入2");
        Scanner s = new Scanner(System.in);
        cpt = s.nextInt();//电脑棋子颜色
        hum=3-cpt;//对手棋子颜色
    }
//    com.haochen.renju.main()
//    {
//        struct xy next;//下一步
//        drawchess(); //画棋盘
//        ChessStart();//棋局开始
//        while(1)
//        {
//            //上一步是电脑，人下棋
//            if(color(step)==cpt)
//            {
//                printf("请输入下一步棋坐标\n");
//                scanf("%d%d",&next.x,&next.y);
//                next.x--;
//                next.y--;
//                while(chessboard[next.x][next.y]!=0){
//                printf("该坐标已有子 ");
//                printf("请重新输入\n");
//                scanf("%d%d",&next.x,&next.y);
//                next.x--;
//                next.y--;
//                }
//                MakeMove(next);
//                drawchess();
//            }
//            else //电脑下棋
//            {
//                next=gobang();
//                MakeMove(next);
//                drawchess();
//            }
//            //有人胜利
//            if(CheckWin())
//            {
//                if(color(step)==cpt)
//                printf("computer win\n");
//                else
//                printf("man win\n");
//                ChessStart();
//            }
//        }
//    }

        
}
