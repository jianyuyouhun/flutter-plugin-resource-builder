package com.gallops.flutter.plugins.rb.app;

import com.intellij.openapi.project.Project;

public abstract class BaseManager {
    private Project project;
    public void onCreate(Project project) {
        this.project = project;
    }

    protected Project getProject() {
        return project;
    }

    public void onAllCreate() {
    }

    public void onDestroy() {
    }
}
