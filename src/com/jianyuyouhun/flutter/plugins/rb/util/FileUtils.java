package com.jianyuyouhun.flutter.plugins.rb.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件操作类
 */
public class FileUtils {

    public static void putInFile(File file, String codeString) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(codeString);//将字符串写入到指定的路径下的文件中
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
