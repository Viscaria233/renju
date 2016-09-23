package com.haochen.renju.main;

import com.haochen.renju.exception.ReadFileException;
import com.haochen.renju.ui.util.Dialogs;

public class Client {

    public static void launch() throws ReadFileException {
        TestFrame frame = new TestFrame();
        frame.setVisible(true);
//        frame.launch();
    }
    
    public static void main(String[] args) {
        try {
            launch();
        } catch (ReadFileException e) {
            e.printStackTrace();
            Dialogs.errorDialog(e.getMessage() + '\n' + e.getFile().getAbsolutePath());
        }
    }

}
