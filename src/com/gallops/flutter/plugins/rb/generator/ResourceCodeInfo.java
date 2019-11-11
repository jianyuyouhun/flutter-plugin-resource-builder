package com.gallops.flutter.plugins.rb.generator;

import java.util.List;

/**
 * 资源文件对象
 */
public class ResourceCodeInfo {
    private String className;
    private List<ResField> resName;
    private List<ResourceCodeInfo> childCodeInfo;
    private int deep = 0;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ResField> getResName() {
        return resName;
    }

    public void setResName(List<ResField> resName) {
        this.resName = resName;
    }

    public List<ResourceCodeInfo> getChildCodeInfo() {
        return childCodeInfo;
    }

    public void setChildCodeInfo(List<ResourceCodeInfo> childCodeInfo) {
        this.childCodeInfo = childCodeInfo;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public static class ResField {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
