package com.jianyuyouhun.flutter.plugins.rb.manager;

import com.jianyuyouhun.flutter.plugins.rb.app.App;
import com.jianyuyouhun.flutter.plugins.rb.app.BaseManager;
import com.jianyuyouhun.flutter.plugins.rb.generator.CodeGenerator;
import com.jianyuyouhun.flutter.plugins.rb.generator.res.ImageGenerator;
import com.jianyuyouhun.flutter.plugins.rb.util.monitor.DirMonitor;
import com.jianyuyouhun.flutter.plugins.rb.util.parser.YamlLoader;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 资源管理器
 * 1、向monitor注册监听器，监听到文件夹变化
 * 2、生成资源文件代码，放在assets定义的同级目录下，命名为res.dart
 */
public class ResourceManager extends BaseManager {
    private FileAlterationMonitor monitor;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate() {
        Project project = App.getInstance().getRootProject();
        long interval = TimeUnit.MILLISECONDS.toMillis(500);
        File rootPath = new File(project.getBasePath());
        File pubspec = new File(project.getBasePath() + "/pubspec.yaml");
        FileAlterationObserver observer = new FileAlterationObserver(rootPath);
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                if (file.getAbsolutePath().equals(pubspec.getAbsolutePath())) {
                    System.out.println("配置文件发生变化");
                    DirMonitor.getInstance().cancelAll();
                    start();
                }
            }
        });
        try {
            monitor = new FileAlterationMonitor(interval, observer);
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DirMonitor.getInstance().cancelAll();
        start();
    }

    private void start() {
        Project project = App.getInstance().getRootProject();
        Map<String, Object> yaml = YamlLoader.getYaml(project, "/pubspec.yaml");
        if (yaml == null) return;
        Map<String, Object> flutter = (Map<String, Object>) yaml.get("flutter");
        if (flutter == null) return;
        Object assets = flutter.get("assets");
        if (assets instanceof List) {
            List<String> assetsArray = (List<String>) assets;
            for (String assetDir : assetsArray) {
                generateCode(project.getBasePath() + "/" + assetDir, assetDir);
                DirMonitor.getInstance().startMonitor(project.getBasePath() + "/" + assetDir, dir -> {
                    generateCode(dir, assetDir);
                });
            }
        }
    }

    private void generateCode(String path, String virtualPath) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if(!parentFile.getPath().contains("lib")) {
            parentFile = new File(path.replace(virtualPath, "") + "/lib");
        }
        File targetFile = new File(parentFile, file.getName() + "-res.dart");
        CodeGenerator codeGenerator = new ImageGenerator(path, targetFile.getAbsolutePath(), virtualPath);
        codeGenerator.generate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (monitor != null) {
            try {
                monitor.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DirMonitor.getInstance().cancelAll();
    }
}
