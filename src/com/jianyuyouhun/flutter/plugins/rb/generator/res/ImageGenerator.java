package com.jianyuyouhun.flutter.plugins.rb.generator.res;

import com.jianyuyouhun.flutter.plugins.rb.generator.CodeGenerator;
import com.jianyuyouhun.flutter.plugins.rb.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 资源文件代码生成器
 */
public class ImageGenerator implements CodeGenerator {
    private String dirPath;
    private String outPath;
    private String virtualPath;
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String number = "0123456789";

    public ImageGenerator(String dirPath, String outPath, String virtualPath) {
        this.dirPath = dirPath;
        this.outPath = outPath;
        this.virtualPath = virtualPath;
    }

    @Override
    public void generate() {
        deleteOldFile();
        File file = new File(dirPath);
        if (!file.isDirectory()) {
            return;
        }
        ImageCodeInfo codeInfo = create(file);
        sortFieldList(codeInfo);
        FileUtils.putInFile(new File(outPath), generateCode(codeInfo));
    }

    /**
     * 生成代码
     *
     * @param codeInfo
     */
    private String generateCode(ImageCodeInfo codeInfo) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("///auto generate code, please do not modify;\n");
        codeBuilder.append("class ").append(codeInfo.getClassName()).append(" {\n");
        generateFieldCode(codeInfo.getFieldList(), codeBuilder);
        codeBuilder.append("}\n\n");
        return codeBuilder.toString();
    }

    private void generateFieldCode(List<ImageCodeInfo.ResField> fields, StringBuilder builder) {
        for (ImageCodeInfo.ResField field : fields) {
            builder.append("   static const String ").append(field.getKey()).append(" = '").append(field.getValue()).append("';\n");
        }
    }


    /**
     * 获取代码结构，dart不允许内部类，所以这里对多级文件夹做扁平化处理
     *
     * @param file
     * @return
     */
    private ImageCodeInfo create(File file) {
        ImageCodeInfo codeInfo = new ImageCodeInfo();
        String fileName = file.getName();
        String firstChar = String.valueOf(fileName.charAt(0));
        codeInfo.setClassName(fileName.replaceFirst(firstChar, firstChar.toUpperCase()));
        List<ImageCodeInfo.ResField> fieldList = new ArrayList<>();
        getResField(fieldList, file);
        codeInfo.setFieldList(fieldList);
        return codeInfo;
    }

    /**
     * 按照a-Z排序fieldList
     *
     * @param codeInfo
     */
    private void sortFieldList(ImageCodeInfo codeInfo) {
        codeInfo.getFieldList().sort(Comparator.comparing(ImageCodeInfo.ResField::getKey));
    }

    /**
     * 递归整理res文件夹
     *
     * @param fieldList
     * @param directory
     * @return
     */
    private void getResField(List<ImageCodeInfo.ResField> fieldList, File directory) {
        File[] files = directory.listFiles();
        if (files == null) files = new File[]{};
        for (File child : files) {
            if (child.isDirectory()) {
                getResField(fieldList, child);
            } else {
                String name = child.getName().toLowerCase();
                if (name.endsWith("png") || name.endsWith("jpg") || name.endsWith("jpeg") || name.endsWith("gif") || name.endsWith("svg") || name.endsWith("webp") || name.endsWith("heic")) {
                    ImageCodeInfo.ResField resField = new ImageCodeInfo.ResField();
                    resField.setKey(formatName(name.substring(0, name.lastIndexOf("."))));
                    resField.setValue(child.getAbsolutePath().replaceAll("\\\\", "/").replace(dirPath, virtualPath));
                    fieldList.add(resField);
                }
            }
        }
    }

    /**
     * 转成合格字母，非字母的都以下划线代替
     *
     * @param fileName
     * @return
     */
    private String formatName(String fileName) {
        StringBuilder result = new StringBuilder();
        if (fileName.length() < 1) {
            return "";
        }
        Character firstChar = fileName.charAt(0);
        if (number.contains(firstChar.toString())) {
            fileName = "m" + fileName;
        }
        for (int i = 0; i < fileName.length(); i++) {
            Character character = fileName.charAt(i);
            if (alphabet.contains(character.toString())) {
                result.append(character);
            } else {
                result.append("_");
            }
        }
        return result.toString();
    }

    /**
     * 移除旧的文件
     *
     * @return
     */
    private void deleteOldFile() {
        File file = new File(outPath);
        if (file.exists()) {
            file.delete();
        }
    }

}
