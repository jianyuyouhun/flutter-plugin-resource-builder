package com.gallops.flutter.plugins.rb.util.parser;

import com.intellij.openapi.project.Project;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * yaml文件加载器
 * 主要用于读取flutter的assets文件目录。
 */
public class YamlLoader {
    public static Map<String, Object> getYaml(Project project, String yamlName) {
        File yamlFile = new File(project.getBasePath() + yamlName);
        Yaml yaml = new Yaml();
        try {
            return (Map<String, Object>) yaml.load(new FileInputStream(yamlFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



}
