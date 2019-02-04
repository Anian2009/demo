package com.example.demo.controller.profile;

import java.io.FileInputStream;
import java.io.IOException;

public class ReadFromFile {

    public String readFromFile(String file) throws IOException {
        FileInputStream inFile = new FileInputStream("src\\test\\resources\\"+file);
        byte[] str = new byte[inFile.available()];
        inFile.read(str);
        return new String(str);

    }
}
