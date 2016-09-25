package com.haochen.renju.ai;

import java.util.Date;
import java.util.Scanner;

import com.haochen.renju.common.Mediator;
import com.haochen.renju.form.Point;

public class TTT {
    private int win = 6;              //����
    private int flex4 = 5;              //����
    private int block4 = 4;           //����
    private int flex3 = 3;          //����
    private int block3 = 2;         //����
    private int flex2 = 1;          //���
    private int Ntype = 7;      //������
    
    private Mediator mediator;
    
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    private class point
    {
        Point p;
        int val;
    };
    private int[][] chessboard = new int[15][15]; //��������
    private int step=0; //��ǰ����
    private int cpt,hum;//˫��������ɫ
    private int MaxDepth;//ÿ������������
    private int SearchDepth=8; //����������
    private int BestVal; //����߷���ֵ
    private int total; //Ҷ�ڵ���
    private int ABcut;//��֦��
    private int MaxLen[]={0,4,5,4,5,8,10,15,20};
    private Point BestMove; //����߷�
    private Point[] chessxy = new Point[255];//������������
    //��¼��ǰ���ѡ��
    private point[][] s = new point[10][100];
    private double ThinkTime; //����˼��ʱ��
    private int[][] score = new int[15][15];//�ո��ֵ
    
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
    
    //�ж�ÿһ��������ɫ
    //1�Ǻ��壬2�ǰ��壬0�ǿո�
    private int color(int step)
    {
        if(step % 2 == 1) {
            return 1;
        }
        return 2;
    }
    //�����Χ�ĸ��������޸���ɫ����
    //һ������
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

    //�������Խ��
    private int CheckXy(int x,int y)
    {
        if(x<0||x>14||y<0||y>14)
        return 0;
        else
        return 1;
    }
    //��һ��
    private void MakeMove(Point next)
    {
        int x=next.x;
        int y=next.y;
        step++;
        chessboard[x][y]=color(step);
        chessxy[step].x=x;
        chessxy[step].y=y;
    }
    //ɾһ��
    private void DelMove()
    {
        int x=chessxy[step].x;
        int y=chessxy[step].y;
        chessboard[x][y]=0;
        step--;
    }
    public void display()//��ʾ����˼�����
    {
        System.out.println("ThinkTime=" + ThinkTime);
        System.out.println("total=" + total + " ABcut=" + ABcut);
        System.out.print("bestx=" + BestMove.x+1 + " ");
        System.out.print("besty=" + BestMove.y+1 +  " ");
        System.out.print("step=" + step + " ");
        System.out.println("val=" + BestVal);
    }

    //�ж�����
    private int CheckFive(int x,int y,int role)
    {
        int a,b,count=1;
        //��
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
        //��
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
        //��б
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
        //��б
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
    //����Ƿ�����ʤ��
    private int CheckWin()
    {
        int x=chessxy[step].x;
        int y=chessxy[step].y;
        int role=chessboard[x][y];
        if(CheckFive(x,y,role)==win){
            //�Ե��ԽǶȷ��ط�ֵ
            if(role==cpt) return 10000;
            else return -10000;
        }
        else return 0;
    }
    //�ж�����
    private int CheckType(int len,int len2,int block,int count)
    {
        //len����ռ���ڵ���5�����ܳ���
        if(len>=5&&count>1){
            if(count==5) return win;
            //����ռ����5
            //���ͳ���С��5
            //û�б��赲
            //������һ��ֻ��һ�������
            if(len>5&&len2<5&&block==0){
                switch (count){
                    case 2: return flex2;
                    case 3: return flex3;
                    case 4: return flex4;
                }
            }
            else {
                //ֻ��һ������㣬������
                switch (count){
                    case 3: return block3;
                    case 4: return block4;
                }
            }
        }
        return 0;
    }
    //��ѯ�ý�ɫ��ĳ�ո��ĸ���������
    private void GetType(int x,int y,int role,int[] d)
    {
        int a,b;//����
        int kong=0;//�ո���
        int block=0; //���赲����(�����߽���)
        int len=1; //����ռ�
        int len2=1;  //���ͳ���
        int count=1; //������
        //role����������ɫ��d��¼�ķ�������
        //��
        for(a=x+1;a<=x+4;a++)
        {
            if(a>14){
                //���߽����赲
                if(len2==kong+count)
                block++;
                break;
            }
            if(chessboard[a][y]==role){
                //�м�ո����2
                //���ͳ��ȴ���4
                //������������
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
                //�Է������赲
                if(len2==kong+count)
                block++;
                break;
            }
        }
        //�����м�ո�
        kong=len2-count;
        //��һ����
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
        //��
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
        //��б
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
        //��б
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
    //��ɱ���ֵ
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
    //���ͼ���
    /**  
     * @Title: TypeCount  
     * @Description: TODO   
     * @param x
     * @param y
     * @param role
     * @param score
     * @param type      
     * 
     * scoreԭ����int* score����ָ��
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
     * @Description: TODO   ���ո���
     * @param x
     * @param y
     * @return      
     * 
     * Cscore��HscoreҪָ�봫�ݣ����������ָ��
     */
    private int ScoreMove(int x,int y)
    {
        int[] Cscore={0};
        int[] Hscore={0};
        //���͸���
        int[] Htype={0, 0, 0, 0, 0, 0, 0};
        int[] Ctype={0, 0, 0, 0, 0, 0, 0};
        //��ȡ������
        TypeCount(x,y,cpt,Cscore,Ctype);
        TypeCount(x,y,hum,Hscore,Htype);
        int kill=GetKill(Ctype,Htype);
        return Cscore[0]+Hscore[0]+kill;
    }
    //�����Χn������������
    private int round(int n,int x,int y)
    {
        int i,j;
        for(i=x-n;i<=x+n;i++){
            for(j=y-n;j<=y+n;j++){
                /**
                 * CheckXy����int��Ӧ�øĳ�boolean
                 */
                if(CheckXy(i,j) == 0) continue;
                if(chessboard[i][j]!=0)
                return 1;
            }
        }
        return 0;
    }   
    //�������пո��ֵ      
    private void UpdateScore()
    {
        int i,j;
        for(i=0;i<15;i++){
            for(j=0;j<15;j++){
                score[i][j]=0;
                if(chessboard[i][j]==0){
                    /**
                     * round����int��Ӧ�øĳ�boolean
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
     * scoreָ�봫�ݣ����������
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
            //���ͼ���
            GetType(x,y,role,d);
            for(j=0;j<4;j++){
                type[d[j]]++;
            }
        }
        int[] count={0,2,3,3,4,4,5};
            //����������
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
     * @Description: TODO   ��������
     * @return      
     * 
     * Cscore��Hscoreָ�봫�ݣ����������
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
    //����λ�Ƿ����
    private int CheckMove(Point p,int role)
    {
        int x=p.x;
        int y=p.y;
        int[] d = new int[4];
        int i;
        //�Լ��ɻ����������
        GetType(x,y,role,d);
        for(i=0;i<4;i++){
            if(d[i]>=flex2)
            return 1;
        }
        //�����������
        if(CheckRound(x,y,3-role)==0) return 0;
        //�Է��ܳɻ����������
        GetType(x,y,3-role,d);
        for(i=0;i<4;i++){
            if(d[i]>=flex2)
            return 1;
        }
        return 0;
    }
    //������һ�������߷��������ܸ���
    private int GetMove(int depth)
    {
        int i,j,val;
        int t=0;//��ѡ�����
        int role=color(step+1);//��һ��������ɫ
        Point p = new Point();
        UpdateScore();
        for(i=0;i<15;i++)
        {
            for(j=0;j<15;j++)
            {
                p.x=i;
                p.y=j;
                /**
                 * round����int��Ӧ�øĳ�boolean
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
         * CheckWin����int��Ӧ�øĳ�boolean
         * iswin����Ӧ�øĳ�boolean
         */
        if(iswin != 0)//����ʤ����ֱ�ӷ��ط�ֵ
        { 
            //���Է�ʤ��
            if(depth%2==0) return iswin;
            else //����ʤ��
            return -iswin;
        }
        //Ҷ�ڵ㣬���ؾ����
        if(depth==0) return  evaluate();
        //������һ���߷�
        int t=GetMove(depth);
        //���������߷�
        for(int i=1;i<=MaxLen[depth];i++)
        {
            //������������
            MakeMove(s[depth][i].p);
            //�ݹ����ֵ
            val=-AlphaBeta(depth-1,-beta,-alpha);
            //ɾ������
            DelMove();
            //AB��֦
            if(val>=beta){
                if(depth==MaxDepth)
                BestMove=s[depth][i].p;
                ABcut++;
                return val;
            }
            //�����ֵĵ�
            if(val>alpha)
            {
                alpha=val;
                //��¼��ѵ�
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
        //��ʼ���ڵ�������֦��
        total=0;ABcut=0;
        //��һ��ֱ������Ԫ
        if(step==0){
            BestMove.x=7; 
            BestMove.y=7;
            return BestMove;
        }
        //�����2��ʼ�����ϼӴ��������
        //ȷ�������·��ʤ��
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //��������
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
    private void ChessStart()//��ֿ�ʼ
    {
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                chessboard[i][j] = 0;
            }
        }
        step=0;
        System.out.println("��������������1����������2");
        Scanner s = new Scanner(System.in);
        cpt = s.nextInt();//����������ɫ
        hum=3-cpt;//����������ɫ
    }
//    com.haochen.renju.main()
//    {
//        struct xy next;//��һ��
//        drawchess(); //������
//        ChessStart();//��ֿ�ʼ
//        while(1)
//        {
//            //��һ���ǵ��ԣ�������
//            if(color(step)==cpt)
//            {
//                printf("��������һ��������\n");
//                scanf("%d%d",&next.x,&next.y);
//                next.x--;
//                next.y--;
//                while(chessboard[next.x][next.y]!=0){
//                printf("������������ ");
//                printf("����������\n");
//                scanf("%d%d",&next.x,&next.y);
//                next.x--;
//                next.y--;
//                }
//                MakeMove(next);
//                drawchess();
//            }
//            else //��������
//            {
//                next=gobang();
//                MakeMove(next);
//                drawchess();
//            }
//            //����ʤ��
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
