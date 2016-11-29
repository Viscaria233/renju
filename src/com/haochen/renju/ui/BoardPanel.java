package com.haochen.renju.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.haochen.renju.bean.Cell;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.control.player.HumanPlayer;
import com.haochen.renju.main.Config;
import com.haochen.renju.resources.Resource;
import com.haochen.renju.storage.Point;
import com.haochen.renju.ui.draw.Layer;
import com.haochen.renju.ui.draw.LayerManager;
import com.haochen.renju.util.CellUtils;

public class BoardPanel extends JPanel implements Mediator.Display {

    private final int cellWidth = 30;
    private final int lineNumber = 15;
    private final int pieceFieldWidth = cellWidth * lineNumber;
    private Image backgroundImage;
    private Image blackPiece;
    private Image whitePiece;
    private final Color axesColor = new Color(0xbbbbbb);
    private final Color forbiddenMarkColor = Color.red;

    private final Color highlightColor = Color.red;
    private GridBagLayout gridBag = new GridBagLayout();
    
    private JPanel background;
    private JPanel net;
    private JPanel hAxes;
    private JPanel vAxes;
    
    private LayerManager lm;

    private ForbiddenMarkLayer forbiddenMarkLayer;
    private RecordLayer recordLayer;
    private PieceLayer pieceLayer;
    private HighlightLayer highlightLayer;
    private IndexLayer indexLayer;
    
    private Mediator mediator;
    
    BoardPanel() {
        super();
        setLayout(gridBag);
        initial();
        eventPerform();
    }

    /**  
     * @Title: initial  
     * @Description: TODO
     */
    private void initial() {
        try {
            backgroundImage = ImageIO.read(Resource.get("marble.png"));
            blackPiece = ImageIO.read(Resource.get("black.png"));
            whitePiece = ImageIO.read(Resource.get("white.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        background = new NetPanel();
        net = new GlassPanel();

        hAxes = new HorizontalAxesPanel();
        vAxes = new VerticalAxesPanel();
        add(net);
        add(background);
        add(hAxes);
        add(vAxes);
        
        int n = cellWidth / 2;
        gridBag.setConstraints(net, new GBC(1, 0).setInsets(new Insets(n, n, n, n)));
        gridBag.setConstraints(background, new GBC(1, 0));
        gridBag.setConstraints(hAxes, new GBC(1, 1));
        gridBag.setConstraints(vAxes, new GBC(0, 0, 1, 2));

        lm = new LayerManager(net);
        forbiddenMarkLayer = new ForbiddenMarkLayer(pieceFieldWidth, pieceFieldWidth);
        recordLayer = new RecordLayer(pieceFieldWidth, pieceFieldWidth);
        pieceLayer = new PieceLayer(pieceFieldWidth, pieceFieldWidth);
        highlightLayer = new HighlightLayer(pieceFieldWidth, pieceFieldWidth);
        indexLayer = new IndexLayer(pieceFieldWidth, pieceFieldWidth);
        lm.add(forbiddenMarkLayer);
        lm.add(recordLayer);
        lm.add(pieceLayer);
        lm.add(highlightLayer);
        lm.add(indexLayer);
    }
    
    private void eventPerform() {
        net.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (Config.GAME_OVER) {
                    return;
                }
                switch (e.getButton()) {
                case MouseEvent.BUTTON1: {
                    if (mediator.getPlayerSet().getMovingPlayer() instanceof HumanPlayer) {
                        Point point = boardLocation(new Point(e.getX(), e.getY()));
                        mediator.getOperator().move(point);
                    }
                }
                    break;
                case MouseEvent.BUTTON2: {
                }
                    break;
                case MouseEvent.BUTTON3: {
                    if (mediator.getPlayerSet().getMovingPlayer() instanceof HumanPlayer) {
                        mediator.getOperator().withdraw();
                    }
                }
                    break;
                }
            }
        });
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void drawPiece(Cell cell) {
        int index = cell.getIndex();
        Point point = cell.getPoint();
        int color = cell.getType();

        pieceLayer.draw(point, color);
        highlightLayer.draw(point);
        color = CellUtils.foeColor(color);
        indexLayer.draw(index, point, color);
    }

    @Override
    public void removePiece(Point current, Point last) {
        fixBoard(current);
        if (last != null) {
            drawHighlight(last);
        }
    }

    @Override
    public void drawRecord(Point onBoard, int color) {
        recordLayer.draw(onBoard, color);
    }

    @Override
    public void removeRecord(Point onBoard) {
        Point absolutely = absolutelyLocation(onBoard);
        recordLayer.erase(absolutely.x, absolutely.y, cellWidth, cellWidth);
    }

    @Override
    public void removeRecord() {
        recordLayer.erase();
    }

    @Override
    public void drawForbiddenMark(Point onBoard) {
        forbiddenMarkLayer.draw(onBoard);
    }

    @Override
    public void clearForbiddenMark() {
        forbiddenMarkLayer.erase();
    }

    @Override
    public void clear(Point onBoard) {
        Point absolutely = absolutelyLocation(onBoard);
        lm.erase(absolutely.x, absolutely.y, cellWidth, cellWidth);
    }

    @Override
    public void clear() {
        lm.erase();
    }

    @Override
    public void commit() {
        repaint();
        lm.commit();
    }

    private void drawHighlight(Point onBoard) {
        highlightLayer.draw(onBoard);
    }
    
    private void fixBoard(Point onBoard) {
        Point absolutelyLocation = absolutelyLocation(onBoard);
        int x = absolutelyLocation.x;
        int y = absolutelyLocation.y;
        x -= cellWidth / 2;
        y -= cellWidth / 2;
        lm.erase(x, y, cellWidth, cellWidth);
    }
    
    private Point boardLocation(Point absolutely) {
        int x = absolutely.x;
        int y = absolutely.y;
        Point point = new Point(
                x / cellWidth + 1,
                15 - y  / cellWidth);
        return point;
    }

    /**
     *
     * @param onBoard
     * @return  这个格子左上角的绝对坐标
     */
     private Point absolutelyLocation(Point onBoard) {
        int x = onBoard.x;
        int y = onBoard.y;
        Point point = new Point(
                (int) ((x - 0.5) * cellWidth),
                (int) ((16 - y - 0.5) * cellWidth));
        return point;
    }
    
    /**  
     * @ClassName: GlassPanel  
     * @Description: TODO   
     * @author HaoChen  
     * @date 2016年4月30日 下午4:22:47  
     *    
     *    透明面板，用来接收鼠标事件
     */
    private class GlassPanel extends JPanel {
        /**  
         * @Fields serialVersionUID :  
         */ 
        private static final long serialVersionUID = 1L;
        GlassPanel() {
            super();
            this.setPreferredSize(new Dimension(pieceFieldWidth, pieceFieldWidth));
            this.setBackground(new Color(0, 0, 0, 0));
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            lm.restore(g);
        }
    }
    
    /**  
     * @ClassName: NetPanel  
     * @Description: TODO   棋盘网格，落子区域
     * @author HaoChen  
     * @date 2016年4月17日 下午3:36:29  
     *    
     */
    private class NetPanel extends JPanel {
        /**
         * @Fields serialVersionUID :
         */
        private static final long serialVersionUID = 1L;
        private double starRatio = 0.08;
        NetPanel() {
            super();
            int w = cellWidth * (lineNumber + 1);
            setPreferredSize(new Dimension(w, w));
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            drawBoardNet(g);
        }
        private void drawBoardNet(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.black);
            
            //绘制外边框
            g2d.setStroke(new BasicStroke(4.0f));
            int w = cellWidth * (lineNumber - 1);
            int edgeInset = 5;
            g2d.drawRect(cellWidth - edgeInset, cellWidth - edgeInset, 
                    w + 2 * edgeInset + 1, w + 2 * edgeInset + 1);

            // 绘制横纵线
            g2d.setStroke(new BasicStroke(1.0f));
            for (int i = 1; i <= lineNumber; i++) {
                int startX = cellWidth;
                int endX = cellWidth * lineNumber;
                int y = startX * i;
                g2d.drawLine(startX, y, endX, y);
                g2d.drawLine(y, startX, y, endX);
            }

            //绘制星点
            drawStarPoint(new Point(4, 4), g);
            drawStarPoint(new Point(4, 12), g);
            drawStarPoint(new Point(8, 8), g);
            drawStarPoint(new Point(12, 4), g);
            drawStarPoint(new Point(12, 12), g);
        }

        private void drawStarPoint(Point p, Graphics g) {
            int starWidth = (int) (cellWidth * starRatio);
            g.setColor(Color.black);
            g.fillRect(p.x * cellWidth - starWidth, p.y * cellWidth - starWidth,
                    2 * starWidth + 1, 2 * starWidth + 1);
        }
    }
    
    /**  
     * @ClassName: HorizontalAxesPanel  
     * @Description: TODO   
     * @author HaoChen  
     * @date 2016年4月30日 下午4:36:21  
     *    
     */
    private class HorizontalAxesPanel extends JPanel {
        /**
         * @Fields serialVersionUID :
         */
        private static final long serialVersionUID = 1L;
        HorizontalAxesPanel() {
            setBackground(axesColor);
            setPreferredSize(new Dimension(cellWidth * (lineNumber + 1), cellWidth));
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            // 绘制横坐标
            String letter[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O" };
            g.setFont(new Font("Arial", Font.BOLD, cellWidth / 2));
            FontMetrics m = g.getFontMetrics();
            int strHeight = m.getHeight();
            int strWidth = 0;
            for (int i = 0; i < 15; i++) {
                strWidth = m.stringWidth(letter[i]);
                g.drawString(letter[i], (i + 1) * cellWidth - strWidth / 2, lineNumber + strHeight / 3);
            }
        }
    }
    /**  
     * @ClassName: VerticalAxesPanel  
     * @Description: TODO   
     * @author HaoChen  
     * @date 2016年4月30日 下午4:36:38  
     *    
     */
    private class VerticalAxesPanel extends JPanel {
        /**
         * @Fields serialVersionUID :
         */
        private static final long serialVersionUID = 1L;
        VerticalAxesPanel() {
            setBackground(axesColor);
            setPreferredSize(new Dimension(cellWidth, cellWidth * (lineNumber + 2)));
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            // 绘制纵坐标
            g.setFont(new Font("Arail", Font.BOLD, cellWidth / 2));
            FontMetrics m = g.getFontMetrics();
            int strHeight = m.getHeight();
            int strWidth = 0;
            int n = 0;
            for (int i = 0; i < lineNumber; i++) {
                n = lineNumber - i;
                strWidth = m.stringWidth(n + "");
                g.drawString(n + "", (cellWidth - strWidth) / 2, cellWidth * (i + 1) + strHeight / 3);
            }
        }
    }
    
    /**  
     * @ClassName: ForbiddenMarkLayer
     * @Description: TODO   显示禁手标记的图层
     * @author HaoChen  
     * @date 2016年4月17日 上午10:02:46  
     *    
     */
    private class ForbiddenMarkLayer extends Layer {
        private final double ratio = 0.5;
        ForbiddenMarkLayer(int width, int height) {
            super(width, height);
        }
        void draw(Point boardLocation) {
            Point point = absolutelyLocation(boardLocation);
            int x = point.x;
            int y = point.y;
            int w = (int) (cellWidth * ratio);
            if (w % 2 == 1) {
                --w;
            }
            x -= w / 2;
            y -= w / 2;
            g2d.setColor(forbiddenMarkColor);
            g2d.setStroke(new BasicStroke(4.0f));
            g2d.drawLine(x, y, x + w, y + w);
            g2d.drawLine(x + w, y, x, y + w);
        }
    }
    
    /**  
     * @ClassName: RecordLayer  
     * @Description: TODO   显示历史落子记录的图层
     * @author HaoChen  
     * @date 2016年4月17日 上午10:23:08  
     *    
     */
    private class RecordLayer extends Layer {
        private final double ratio = 0.5;
        RecordLayer(int width, int height) {
            super(width, height);
        }
        void draw(Point boardLocation, int color) {
            Point point= absolutelyLocation(boardLocation);
            int x = point.x;
            int y = point.y;
            int w = (int) (cellWidth * ratio);
            if (w % 2 == 1) {
                --w;
            }
            x -= w / 2;
            y -= w / 2;
            g2d.setColor(CellUtils.getAwtColor(color));
            g2d.fillOval(x, y, w, w);
        }
    }
    
    /**  
     * @ClassName: PieceLayer  
     * @Description: TODO   显示棋子的图层
     * @author HaoChen  
     * @date 2016年4月16日 下午11:01:49  
     *    
     */
    private class PieceLayer extends Layer {
        private final double ratio = 0.9;
        PieceLayer(int width, int height) {
            super(width, height);
        }
        void draw(Point boardLocation, int color) {
            Point point = absolutelyLocation(boardLocation);
            int x = point.x;
            int y = point.y;
            int w = (int) (cellWidth * ratio);
            if (w % 2 == 1) {
                --w;
            }
            x -= w / 2;
            y -= w / 2;
//            g2d.setColor(type);
//            g2d.fillOval(x, y, w, w);
            Image image = null;
            if (color == Cell.BLACK) {
                image = blackPiece;
            } else {
                image = whitePiece;
            }
            g2d.drawImage(image, x, y, w, w, null);
        }
    }
    
    /**  
     * @ClassName: HighlightLayer  
     * @Description: TODO   显示最后一手高亮的图层
     * @author HaoChen  
     * @date 2016年4月16日 下午11:01:33  
     *    
     */
    private class HighlightLayer extends Layer {
        private final double ratio = 0.9;
        private Rectangle currentHighlight;
        HighlightLayer(int width, int height) {
            super(width, height);
        }
        void draw(Point boardLocation) {
            if (currentHighlight != null) {
                erase(currentHighlight);
            }
            Point point = absolutelyLocation(boardLocation);
            int x = point.x;
            int y = point.y;
            int w = cellWidth;
            currentHighlight = new Rectangle(x - w / 2, y - w / 2, w, w);
            w = (int) (w * ratio);
            if (w % 2 == 1) {
                --w;
            }
            x -= w / 2 - 2;
            y -= w / 2 - 2;
            w -= 4;
            g2d.setColor(highlightColor);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(x, y, w, w);
        }
    }
    
    /**  
     * @ClassName: IndexLayer  
     * @Description: TODO   显示手顺的图层
     * @author HaoChen  
     * @date 2016年4月16日 下午11:01:16  
     *    
     */
    private class IndexLayer extends Layer {
        private final double ratio = 0.5;
        IndexLayer(int width, int height) {
            super(width, height);
        }
        void draw(int index, Point boardLocation, int color) {
            Point point = absolutelyLocation(boardLocation);
            int x = point.x;
            int y = point.y;

            g2d.setFont(new Font("宋体", 0, (int) (cellWidth * ratio)));
            FontMetrics fm = g2d.getFontMetrics();
            String str = "" + index;
            int width = fm.stringWidth(str);
            int height = fm.getHeight();
            x -= width / 2;
            y += height / 4;
            g2d.setColor(CellUtils.getAwtColor(color));
            g2d.drawString(str, x, y);
        }
    }
}
