package com.gallops.flutter.plugins.rb.manager;

import com.gallops.flutter.plugins.rb.generator.ResourceGenerator;
import com.gallops.flutter.plugins.rb.monitor.DirMonitor;
import com.gallops.flutter.plugins.rb.parser.YamlLoader;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResourceManager extends BaseManager {
    private Project project;
    private FileAlterationMonitor monitor;
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Project project) {
        this.project = project;
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
        File targetFile = new File(parentFile, file.getName() + "-res.dart");
        new ResourceGenerator(path, targetFile.getAbsolutePath(), virtualPath).generator();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (monitor!=null) {
            try {
                monitor.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DirMonitor.getInstance().cancelAll();
    }
}
