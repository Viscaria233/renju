package com.haochen.renju.ui.util;

import javax.swing.JOptionPane;

/**  
 * @ClassName: Dialogs  
 * @Description: TODO   �̳�JOptionPane�࣬��װ�˸��ֵ�������
 * @author HaoChen  
 * @date 2016��3��12�� ����10:55:11  
 *    
 */
public class Dialogs extends JOptionPane {

    /**  
     * @Fields serialVersionUID :  
     */ 
    private static final long serialVersionUID = 1L;
    
    public static int confirmDialog(String message) {
        return showConfirmDialog(null, message, "ȷ��", YES_NO_OPTION, INFORMATION_MESSAGE);
    }
    
    public static void messageDialog(String message) {
        Object[] objects = {"ȷ��"};
        showOptionDialog(null, message, "��ʾ", YES_NO_OPTION, INFORMATION_MESSAGE,
                null, objects, objects[0]);
    }
    
    public static void errorDialog(String message) {
        Object[] objects = {"ȷ��"};
        showOptionDialog(null, message, "����", YES_NO_OPTION, ERROR_MESSAGE,
                null, objects, objects[0]);
    }
    
    public static int fileNotFoundDialog(String message) {
        Object[] objects = {"���", "ȡ��"};
        return showOptionDialog(null, message, "�ļ�ȱʧ",
                DEFAULT_OPTION, ERROR_MESSAGE, null, objects, objects[0]);
    }
}
