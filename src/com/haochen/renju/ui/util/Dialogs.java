package com.haochen.renju.ui.util;

import javax.swing.JOptionPane;

/**  
 * @ClassName: Dialogs  
 * @Description: TODO   继承JOptionPane类，封装了各种弹出窗口
 * @author HaoChen  
 * @date 2016年3月12日 下午10:55:11  
 *    
 */
public class Dialogs extends JOptionPane {

    /**  
     * @Fields serialVersionUID :  
     */ 
    private static final long serialVersionUID = 1L;
    
    public static int confirmDialog(String message) {
        return showConfirmDialog(null, message, "确认", YES_NO_OPTION, INFORMATION_MESSAGE);
    }
    
    public static void messageDialog(String message) {
        Object[] objects = {"确定"};
        showOptionDialog(null, message, "提示", YES_NO_OPTION, INFORMATION_MESSAGE,
                null, objects, objects[0]);
    }
    
    public static void errorDialog(String message) {
        Object[] objects = {"确定"};
        showOptionDialog(null, message, "错误", YES_NO_OPTION, ERROR_MESSAGE,
                null, objects, objects[0]);
    }
    
    public static int fileNotFoundDialog(String message) {
        Object[] objects = {"浏览", "取消"};
        return showOptionDialog(null, message, "文件缺失",
                DEFAULT_OPTION, ERROR_MESSAGE, null, objects, objects[0]);
    }
}
