package com.gallops.flutter.plugins.rb.manager;

import com.intellij.openapi.project.Project;

public abstract class BaseManager {
    public abstract void onCreate(Project project);

    public void onAllCreate() {
    }

    public void onDestroy() {
    }
}
