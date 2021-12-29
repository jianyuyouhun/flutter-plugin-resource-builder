package com.jianyuyouhun.flutter.plugins.rb.actions;

import com.google.gson.JsonElement;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.jianyuyouhun.flutter.plugins.rb.generator.CodeGenerator;
import com.jianyuyouhun.flutter.plugins.rb.generator.EnumGenerator;
import com.jianyuyouhun.flutter.plugins.rb.util.CommonUtils;
import com.jianyuyouhun.flutter.plugins.rb.widget.InputJsonDialog;

public class GenerateEnumAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile data = e.getData(DataKey.create("virtualFile"));
        if (data == null) {
            Messages.showMessageDialog("Please choice right directory", "Exception", Messages.getInformationIcon());
            return;
        }
        final VirtualFile directory = data.isDirectory() ? data : data.getParent();
        new InputJsonDialog(project, jsonInfo -> {
            if (!jsonInfo.getJsonElement().isJsonObject()) {
                Messages.showMessageDialog("Wrong json type in generate enum code, please typed your code in jsonObject or not use jsonArray etc.", "Exception", Messages.getInformationIcon());
            } else {
                doGenerate(directory, jsonInfo);
            }
        }, null, null).show();
    }

    private void doGenerate(VirtualFile directory, InputJsonDialog.JsonInfo jsonInfo) {
        String fileName = jsonInfo.getName();
        String className;
        if (fileName.contains("_")) {//包含下划线则统一先以下划线转驼峰
            className = CommonUtils.lineToBigCamel(fileName);
        } else {
            className = CommonUtils.lineToBigCamel(CommonUtils.camelToLine(fileName));
        }
        fileName = CommonUtils.camelToLine(className);//再以class名称驼峰转下划线成文件名
        CodeGenerator generator = new EnumGenerator(fileName + ".dart", className, jsonInfo.getJsonElement(), directory);
        generator.generate();
    }
}
