package com.jianyuyouhun.flutter.plugins.rb.generator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.intellij.openapi.vfs.VirtualFile;
import com.jianyuyouhun.flutter.plugins.rb.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 枚举代码生成器
 */
public class EnumGenerator implements CodeGenerator {

    private String fileName;
    private String className;
    private JsonElement jsonElement;
    private VirtualFile directory;

    /**
     * 构造器
     *
     * @param fileName    文件名
     * @param className   枚举类名
     * @param jsonElement json配置，这里只解析两层结构，即
     *                    {
     *                    'a':{
     *                    'title':'A'
     *                    },
     *                    'b':{
     *                    'color':'F5F5F5'
     *                    }
     *                    }
     *                    第一层结构为枚举类型，
     *                    第二层结构为扩展属性，取并集，缺省或者为json的都取null，
     * @param directory   输出目录
     */
    public EnumGenerator(String fileName, String className, JsonElement jsonElement, VirtualFile directory) {
        this.fileName = fileName;
        this.className = className;
        this.jsonElement = jsonElement;
        this.directory = directory;
    }

    @NotNull
    private EnumCodeInfo create() {
        assert jsonElement.isJsonObject();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        List<EnumCodeInfo.EnumItem> itemList = new ArrayList<>();
        List<String> attrKeySet = new ArrayList<>();
        for (Map.Entry<String, JsonElement> elementEntry : jsonObject.entrySet()) {
            EnumCodeInfo.EnumItem item = new EnumCodeInfo.EnumItem();
            item.setEnumName(elementEntry.getKey());
            Map<String, Object> attrMap = new HashMap<>();
            JsonElement jsonElement = elementEntry.getValue();
            if (jsonElement.isJsonObject()) {//只解析jsonObject，其他类型认为是null
                JsonObject attrJsonObject = jsonElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> attrEntry : attrJsonObject.entrySet()) {
                    Object value = toDartBasicType(attrEntry.getValue());
                    String key = attrEntry.getKey();
                    if (value != null) {
                        attrMap.put(key, value);
                    }
                    if (!attrKeySet.contains(key)) {
                        attrKeySet.add(key);
                    }
                }
                //如果没有value，则默认添加value，值为enumName
                if(!attrKeySet.contains("value")) {
                    attrKeySet.add("value");
                }
                if(!attrMap.containsKey("value")) {
                    attrMap.put("value", elementEntry.getKey());
                }
            }
            item.setAttrMap(attrMap);
            itemList.add(item);
        }
        return new EnumCodeInfo(className, itemList, attrKeySet);
    }

    @Nullable
    private Object toDartBasicType(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else {
                return null;
            }
        }
        return null;
    }


    @Override
    public void generate() {
        deleteOldFile();
        File file = new File(directory.getPath(), fileName);
        EnumCodeInfo codeInfo = create();
        System.out.println(new Gson().toJson(codeInfo));
        FileUtils.putInFile(file, generateCode(codeInfo));
    }

    private String generateCode(EnumCodeInfo codeInfo) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("///auto generate code, please do not modify;\n");
        generateEnumClass(codeBuilder, codeInfo);
        generateEnumUtilsClass(codeBuilder, codeInfo);
        generateEnumAttrClass(codeBuilder, codeInfo);
        return codeBuilder.toString();
    }

    /**
     * 生成枚举代码
     */
    private void generateEnumClass(StringBuilder codeBuilder, EnumCodeInfo codeInfo) {
        codeBuilder.append("enum ").append(codeInfo.getClassName()).append(" {\n");
        for (EnumCodeInfo.EnumItem enumItem : codeInfo.getEnumItemList()) {
            codeBuilder.append("  ").append(enumItem.getEnumName()).append(",\n");
        }
        codeBuilder.append("  ").append("UNDEFINED").append(",\n");
        codeBuilder.append("}\n\n");
    }

    /**
     * 生成枚举构建代码
     */
    private void generateEnumUtilsClass(StringBuilder codeBuilder, EnumCodeInfo codeInfo) {
        codeBuilder.append("class ").append(codeInfo.getClassName()).append("Utils {\n");
        codeBuilder.append("  static ").append(codeInfo.getClassName()).append(" build(String? value) {\n");
        codeBuilder.append("    switch (value) {\n");
        for (EnumCodeInfo.EnumItem item : codeInfo.getEnumItemList()) {
            codeBuilder.append("      case '").append(item.getEnumName()).append("':\n")
                    .append("        return ").append(codeInfo.getClassName()).append(".")
                    .append(item.getEnumName()).append(";\n");
        }
        codeBuilder.append("      default:\n").append("        return ")
                .append(codeInfo.getClassName()).append(".UNDEFINED;\n");
        codeBuilder.append("    }\n");
        codeBuilder.append("  }\n");
        codeBuilder.append("}\n\n");
    }

    /**
     * 生成属性扩展代码
     */
    private void generateEnumAttrClass(StringBuilder codeBuilder, EnumCodeInfo codeInfo) {
        codeBuilder.append("extension ").append(codeInfo.getClassName()).append("Ex on ").append(codeInfo.getClassName()).append(" {\n");
        for (int i = 0; i < codeInfo.getAttrKeySet().size(); i++) {
            String attrKey = codeInfo.getAttrKeySet().get(i);
            codeBuilder.append("  ").append(attrKey).append("() {\n");
            codeBuilder.append("    switch (this) {\n");
            for (EnumCodeInfo.EnumItem item : codeInfo.getEnumItemList()) {
                codeBuilder.append("      case ").append(codeInfo.getClassName()).append(".").append(item.getEnumName()).append(":\n")
                        .append("        return ").append(getEnumItemAttr(item, attrKey)).append(";\n");
            }
            codeBuilder.append("      case ").append(codeInfo.getClassName()).append(".UNDEFINED:\n").append("        return null;\n");
            codeBuilder.append("    }\n");
            codeBuilder.append("  }\n");
            if (i < codeInfo.getAttrKeySet().size() - 1) {
                codeBuilder.append("\n");
            }
        }
        codeBuilder.append("}\n\n");
    }

    private String getEnumItemAttr(EnumCodeInfo.EnumItem item, String key) {
        Object value = item.getAttrMap().get(key);
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value.toString();
    }

    /**
     * 移除旧的文件
     *
     * @return
     */
    private void deleteOldFile() {
        File file = new File(directory.getPath(), fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

}
