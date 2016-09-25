package com.haochen.renju.ui.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageCutter {
    
//    public static BufferedImage hyalinizeRect(BufferedImage image,
//            int x, int y, int width,int height) {
//        BufferedImage cutter = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
//        Graphics g = cutter.createGraphics();
//        g.setColor(Color.white);
//        g.fillRect(0, 0, cutter.getWidth(), cutter.getHeight());
//        g.setColor(Color.black);
//        g.fillRect(x, y, width, height);
//        
//        try {
//            ImageIO.write(image, "png", new File("E:\\original.png"));
//            ImageIO.write(cutter, "png", new File("E:\\cutter.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
//        g = img.getGraphics();
//        int rgb, R, G, B, A;
//        
//        for (int i = x; i < width; ++i) {
//            for (int j = y; j < height; ++j) {
////        for (int i = 0; i < img.getWidth(); ++i) {
////            for (int j = 0; j < img.getHeight(); ++j) {
//                rgb = image.getRGB(i, j);
//                //取出的值是16进制数，格式为ARGB
//                
//                if ((rgb & 0xff000000) == 0) {
//                    //如果这个像素的alpha为0（即原本就是透明像素）
//                    //则不做修改
//                    continue;
//                }
//                
//                R = (rgb & 0xff0000) >> 16;
//                G = (rgb & 0xff00) >> 8;
//                B = (rgb & 0xff);
//                A = cutter.getRGB(i, j) & 0xff;
//                g.setColor(new Color(R, G, B, A));
//                g.fillRect(i, j, 1, 1);
//            }
//        }
//        return img;
//    }
    
    /**  
     * @Title: hyalinizeRect  
     * @Description: TODO   将指定BufferedImage中的特定矩形区域透明化
     * @param image     被透明化的BufferedImage
     * @param x             透明化区域的左上角X坐标
     * @param y             透明化区域的左上角Y坐标
     * @param width         透明化区域的宽度
     * @param height        透明化区域的高度
     */
    public static void hyalinizeRect(BufferedImage image, int x, int y, int width,int height) {
        BufferedImage cutter = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = cutter.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, cutter.getWidth(), cutter.getHeight());
        g.setColor(Color.black);
        g.fillRect(x, y, width, height);
        
//        try {
//            ImageIO.write(image, "png", new File("E:\\original.png"));
//            ImageIO.write(cutter, "png", new File("E:\\cutter.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        int rgb, R, G, B, A;
        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                rgb = image.getRGB(i, j);
                //取出的值是16进制数，格式为ARGB
                
                if ((rgb & 0xff000000) == 0) {
                    //如果这个像素的alpha为0（即原本就是透明像素）
                    //则不做修改
                    continue;
                }
                
                R = (rgb & 0xff0000) >> 16;
                G = (rgb & 0xff00) >> 8;
                B = (rgb & 0xff);
                A = cutter.getRGB(i, j) & 0xff;
                image.setRGB(i, j, new Color(R, G, B, A).getRGB());
            }
        }
    }
    
}