package com.jianyuyouhun.flutter.plugins.rb.generator.res;

import java.util.List;

/**
 * 资源文件对象
 */
class ImageCodeInfo {
    private String className;
    private List<ResField> fieldList;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ResField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<ResField> fieldList) {
        this.fieldList = fieldList;
    }

    static class ResField {
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
