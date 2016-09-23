package com.haochen.renju.exception;

import java.io.File;
import java.io.IOException;

public class ReadFileException extends IOException {
    /**  
     * @Fields serialVersionUID :  
     */ 
    private static final long serialVersionUID = 1L;
    private File file;
    public ReadFileException(String massage, File file) {
        super(massage);
        this.file = file;
    }
    public File getFile() {
        return file;
    }
}