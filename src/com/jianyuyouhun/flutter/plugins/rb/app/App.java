package com.jianyuyouhun.flutter.plugins.rb.app;

import com.jianyuyouhun.flutter.plugins.rb.manager.ResourceManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class App implements AppComponent {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    private final Project rootProject;

    public Project getRootProject() {
        return rootProject;
    }

    private final List<BaseManager> managerList = new ArrayList<>();

    public App(Project rootProject) {
        this.rootProject = rootProject;
        instance = this;
    }

    @Override
    public void projectOpened() {
        if (rootProject == null) return;
        registerManager(managerList);
        for (BaseManager manager : managerList) {
            manager.onCreate();
        }
        for (BaseManager manager : managerList) {
            manager.onAllCreate();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseManager> T getManager(Class<T> managerClass) {
        for (BaseManager manager : managerList) {
            if (manager.getClass() == managerClass) {
                return (T) manager;
            }
        }
        return null;
    }

    private void registerManager(List<BaseManager> managerList) {
        managerList.add(new ResourceManager());
    }

    @Override
    public void projectClosed() {
        for (BaseManager manager : managerList) {
            manager.onDestroy();
        }
    }
}
