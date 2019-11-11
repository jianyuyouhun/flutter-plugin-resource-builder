package com.gallops.flutter.plugins.rb.generator;

import com.gallops.flutter.plugins.rb.generator.ResourceCodeInfo;
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
        measureDeep(codeInfo, 0);
        FileUtils.putInFile(new File(outPath), generateCode(codeInfo));
    }

    private void measureDeep(ResourceCodeInfo codeInfo, int deep) {
        codeInfo.setDeep(deep);
        for (ResourceCodeInfo info : codeInfo.getChildCodeInfo()) {
            measureDeep(info, deep + 1);
        }
    }

    /**
     * 生成代码
     *
     * @param codeInfo
     */
    private String generateCode(ResourceCodeInfo codeInfo) {
        StringBuilder codeBuilder = new StringBuilder();
        if (codeInfo.getDeep() == 0) {
            appendBlank(codeBuilder, codeInfo.getDeep());
            codeBuilder.append("///auto generate code, please do not modify;\n");
        }
        appendBlank(codeBuilder, codeInfo.getDeep());
        codeBuilder.append("class ").append(codeInfo.getClassName()).append(" {\n");
        if (codeInfo.getResName().size() != 0) {
            generateFieldCode(codeInfo.getResName(), codeBuilder, codeInfo.getDeep());
        }
        if (codeInfo.getChildCodeInfo().size() != 0) {
            for (ResourceCodeInfo info : codeInfo.getChildCodeInfo()) {
                codeBuilder.append(generateCode(info));
            }
        }
        appendBlank(codeBuilder, codeInfo.getDeep());
        codeBuilder.append("}\n\n");
        return codeBuilder.toString();
    }

    private void appendBlank(StringBuilder builder, int deep) {
        for (int i = 0; i < deep; i++) {
            builder.append("   ");
        }
    }

    private void generateFieldCode(List<ResourceCodeInfo.ResField> fields, StringBuilder builder, int deep) {
        for (ResourceCodeInfo.ResField field : fields) {
            builder.append("   ");
            appendBlank(builder, deep);
            builder.append("static const String ").append(field.getKey()).append(" = '").append(field.getValue()).append("';\n");
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
        File[] files = file.listFiles();
        if (files == null) files = new File[]{};
        List<ResourceCodeInfo.ResField> fieldList = new ArrayList<>();
        List<ResourceCodeInfo> childCodeInfo = new ArrayList<>();
        for (File child : files) {
            if (child.isDirectory()) {
                childCodeInfo.add(create(child));
            } else {
                ResourceCodeInfo.ResField resField = new ResourceCodeInfo.ResField();
                resField.setKey(child.getName().substring(0, child.getName().lastIndexOf(".")));
                resField.setValue(child.getAbsolutePath().replaceAll("\\\\", "/").replace(dirPath, virtualPath));
                fieldList.add(resField);
            }
        }
        codeInfo.setChildCodeInfo(childCodeInfo);
        codeInfo.setResName(fieldList);
        return codeInfo;
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
