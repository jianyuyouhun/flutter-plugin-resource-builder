package com.gallops.flutter.plugins.rb.util.monitor;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 文件夹监听器，
 * 监听到assets配置的文件目录下的一切变化，然后通知代码生成器
 */
public class DirMonitor {
    private static DirMonitor instance;

    private Map<String, OnDirChangeListener> listenerMap = new HashMap<>();
    private Map<String, FileAlterationMonitor> monitorMap = new HashMap<>();

    public static DirMonitor getInstance() {
        if (instance == null) {
            instance = new DirMonitor();
        }
        return instance;
    }

    private DirMonitor() {
    }

    public void startMonitor(String dirPath, OnDirChangeListener listener) {
        if (listenerMap.containsKey(dirPath)) {
            System.out.println("已经为此文件夹注册过监听器");
            return;
        }
        listenerMap.put(dirPath, listener);
        long interval = TimeUnit.MILLISECONDS.toMillis(500);
        File dir = new File(dirPath);
        FileAlterationObserver observer = new FileAlterationObserver(dir);
        observer.addListener(new FileListener(dirPath) {
            @Override
            public void onChanged(String dirPath) {
                listenerMap.get(dirPath).onChanged(dirPath);
            }
        });
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        monitorMap.put(dirPath, monitor);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelMonitor(String dirPath) {
        FileAlterationMonitor monitor = monitorMap.remove(dirPath);
        if (monitor != null) {
            try {
                monitor.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelAll() {
        for (FileAlterationMonitor monitor : monitorMap.values()) {
            try {
                monitor.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        monitorMap.clear();
    }

    public interface OnDirChangeListener {
        void onChanged(String dir);
    }
}
