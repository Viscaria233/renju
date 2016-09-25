package com.haochen.renju.ui.draw;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;

/**  
 * @ClassName: LayerManager  
 * @Description: TODO   图层管理器
 * @author HaoChen  
 * @date 2016年4月30日 下午6:50:46  
 *    
 *    先加入的图层位于下层
 */
public class LayerManager extends ArrayList<Layer> {

    /**  
     * @Fields serialVersionUID :  
     */ 
    private static final long serialVersionUID = 1L;
    
    private JComponent container;
    
    public LayerManager(JComponent container) {
        super();
        this.container = container;
    }

    /**  
     * @Title: commit  
     * @Description: TODO         把所有图层按顺序绘制到container上
     */
    public void commit() {
        restore(container.getGraphics());
    }
    
    /**  
     * @Title: restore  
     * @Description: TODO   container重绘时恢复图层内容
     * @param g      
     * 
     * 在container.paint(g)中调用这个方法
     */
    public void restore(Graphics g) {
        for (Layer layer : this) {
            g.drawImage(layer, 0, 0, null);
        }
    }
    
    public void erase() {
        for (Layer layer : this) {
            layer.erase();
        }
    }
    
    public void erase(Rectangle r) {
        for (Layer layer : this) {
            layer.erase(r);
        }
    }
    
    public void erase(int x, int y, int width, int height) {
        for (Layer layer : this) {
            layer.erase(x, y, width, height);
        }
    }
    
}
