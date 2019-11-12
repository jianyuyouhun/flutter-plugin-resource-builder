package com.gallops.flutter.plugins.rb.generator;

import com.gallops.flutter.plugins.rb.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源文件代码生成器
 * 1、向monitor注册监听器，监听到文件夹变化
 * 2、生成资源文件代码，放在assets定义的同级目录下，命名为res.dart
 */
public class ResourceGenerator {
    private String dirPath;
    private String outPath;
    private String virtualPath;

    public ResourceGenerator(String dirPath, String outPath, String virtualPath) {
        this.dirPath = dirPath;
        this.outPath = outPath;
        this.virtualPath = virtualPath;
    }

    public void generator() {
        deleteOldFile();
        File file = new File(dirPath);
        if (!file.isDirectory()) {
            return;
        }
        ResourceCodeInfo codeInfo = create(file);
        FileUtils.putInFile(new File(outPath), generateCode(codeInfo));
    }

    /**
     * 生成代码
     *
     * @param codeInfo
     */
    private String generateCode(ResourceCodeInfo codeInfo) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("///auto generate code, please do not modify;\n");
        codeBuilder.append("class ").append(codeInfo.getClassName()).append(" {\n");
        generateFieldCode(codeInfo.getFieldList(), codeBuilder);
        codeBuilder.append("}\n");
        return codeBuilder.toString();
    }

    private void generateFieldCode(List<ResourceCodeInfo.ResField> fields, StringBuilder builder) {
        for (ResourceCodeInfo.ResField field : fields) {
            builder.append("  static const String ").append(field.getKey()).append(" = '").append(field.getValue()).append("';\n");
        }
    }


    /**
     * 递归整理res文件夹
     *
     * @param file
     * @return
     */
    private ResourceCodeInfo create(File file) {
        ResourceCodeInfo codeInfo = new ResourceCodeInfo();
        codeInfo.setClassName(file.getName());
        List<ResourceCodeInfo.ResField> fieldList = new ArrayList<>();
        getResField(fieldList, file);
        codeInfo.setFieldList(fieldList);
        return codeInfo;
    }

    private void getResField(List<ResourceCodeInfo.ResField> fieldList, File directory) {
        File[] files = directory.listFiles();
        if (files == null) files = new File[]{};
        for (File child : files) {
            if (child.isDirectory()) {
                getResField(fieldList, child);
            } else {
                String name = child.getName();
                if (name.endsWith("png") || name.endsWith("jpg") || name.endsWith("jpeg") || name.endsWith("gif")) {
                    ResourceCodeInfo.ResField resField = new ResourceCodeInfo.ResField();
                    resField.setKey(name.substring(0, name.lastIndexOf(".")));
                    resField.setValue(child.getAbsolutePath().replaceAll("\\\\", "/").replace(dirPath, virtualPath));
                    fieldList.add(resField);
                }
            }
        }
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
