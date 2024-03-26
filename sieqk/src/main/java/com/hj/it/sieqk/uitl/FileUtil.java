package com.hj.it.sieqk.uitl;

import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static  void writeFile(String path, String content){
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
