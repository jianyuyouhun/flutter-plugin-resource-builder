package com.gallops.flutter.plugins.rb.util.monitor;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * 文件夹变化监听
 */
public abstract class FileListener implements FileAlterationListener {
    private String dirPath;

    public FileListener(String dirPath) {
        this.dirPath = dirPath;
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {
        onChanged(dirPath);
    }

    @Override
    public void onDirectoryChange(File directory) {
        onChanged(dirPath);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        onChanged(dirPath);
    }

    @Override
    public void onFileCreate(File file) {
        onChanged(dirPath);
    }

    @Override
    public void onFileChange(File file) {
        onChanged(dirPath);
    }

    @Override
    public void onFileDelete(File file) {
        onChanged(dirPath);
    }

    public abstract void onChanged(String dirPath);
}
