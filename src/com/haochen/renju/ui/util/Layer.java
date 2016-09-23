package com.haochen.renju.ui.util;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Layer extends BufferedImage {
    
    protected Graphics2D g2d;
    
    public Layer(int width, int height) {
        super(width, height, Layer.TYPE_4BYTE_ABGR);
        g2d = createGraphics();
    }
    
    public void erase() {
        erase(0, 0, getWidth(), getHeight());
    }
    
    public void erase(Rectangle r) {
        erase(r.x, r.y, r.width, r.height);
    }
    
    public void erase(int x, int y, int width, int height) {
        ImageCutter.hyalinizeRect(this, x, y, width, height);
    }
    
}
