package com.haochen.renju.ui.draw;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;

/**  
 * @ClassName: LayerManager  
 * @Description: TODO   ͼ�������
 * @author HaoChen  
 * @date 2016��4��30�� ����6:50:46  
 *    
 *    �ȼ����ͼ��λ���²�
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
     * @Description: TODO         ������ͼ�㰴˳����Ƶ�container��
     */
    public void commit() {
        restore(container.getGraphics());
    }
    
    /**  
     * @Title: restore  
     * @Description: TODO   container�ػ�ʱ�ָ�ͼ������
     * @param g      
     * 
     * ��container.paint(g)�е����������
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
