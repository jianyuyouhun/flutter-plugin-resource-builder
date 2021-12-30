package com.jianyuyouhun.flutter.plugins.rb.util.monitor;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * 文件夹变化监听
 * 弃用，不需要传入文件夹，并且文件夹和文件变化最好不要统一成onChange事件，此实现极容易导致无限的循环调用
 * 仅可用于ResourceManager的业务逻辑。
 * 建议还是使用FileAlterationListener接口结合实际流程处理
 */
@Deprecated
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
