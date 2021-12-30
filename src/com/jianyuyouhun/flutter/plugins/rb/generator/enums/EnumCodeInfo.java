package com.jianyuyouhun.flutter.plugins.rb.generator.enums;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * 枚举代码对象
 * enum Status {//className
 * WAITING, FINISHED, //enumName
 * UNDEFINED,//任何枚举都会额外添加此类型表示未定义字符串
 * }
 * <p>
 * class StatusUtils {
 * static Status build(dynamic value) {
 * switch(value?.toString()) {
 * case 'WAITING':
 * return Status.WAITING;
 * case 'FINISHED':
 * return Status.FINISHED;
 * default:
 * return Status.UNDEFINED;
 * }
 * }
 * }
 * <p>
 * extension StatusEx on Status {//扩展属性
 * <p>
 * }
 */
class EnumCodeInfo {
    private String className;
    private List<EnumItem> enumItemList;
    private List<String> attrKeySet;

    EnumCodeInfo(@NotNull String className, @NotNull List<EnumItem> enumItemList, @NotNull List<String> attrKeySet) {
        this.className = className;
        this.enumItemList = enumItemList;
        this.attrKeySet = attrKeySet;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<EnumItem> getEnumItemList() {
        return enumItemList;
    }

    public void setEnumItemList(List<EnumItem> enumItemList) {
        this.enumItemList = enumItemList;
    }

    public List<String> getAttrKeySet() {
        return attrKeySet;
    }

    public void setAttrKeySet(List<String> attrKeySet) {
        this.attrKeySet = attrKeySet;
    }


    /**
     * 枚举item
     */
    static class EnumItem {
        /**
         * 枚举名称
         */
        private String enumName;
        private Map<String, Object> attrMap;

        public String getEnumName() {
            return enumName;
        }

        public void setEnumName(String enumName) {
            this.enumName = enumName;
        }

        public Map<String, Object> getAttrMap() {
            return attrMap;
        }

        public void setAttrMap(Map<String, Object> attrMap) {
            this.attrMap = attrMap;
        }

    }
}
