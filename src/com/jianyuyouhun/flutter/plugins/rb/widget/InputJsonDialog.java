package com.jianyuyouhun.flutter.plugins.rb.widget;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InputJsonDialog extends DialogWrapper implements DocumentListener {

    private JTextField titleField;
    private JTextArea jsonField;

    private CustomOKAction okAction;
    private DialogWrapperExitAction exitAction;
    private final OnGetJsonListener onGetJsonListener;

    private String title;
    private JsonElement element;

    public InputJsonDialog(@Nullable Project project, OnGetJsonListener listener, String title, JsonElement jsonElement) {
        super(project, true);
        this.title = title;
        this.element = jsonElement;
        this.onGetJsonListener = listener;
        init();
        setTitle("Input Standard JSON String");
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel titleLabel = new JLabel("InputName");
        constraints.gridx = 0;
        panel.add(titleLabel, constraints);
        titleField = new JTextField();
        titleField.setMinimumSize(new Dimension(500, 40));
        titleField.setPreferredSize(new Dimension(1000, 40));
        titleField.getDocument().addDocumentListener(this);
        titleField.setMargin(JBUI.insetsBottom(40));
        constraints.gridx = 1;
        panel.add(titleField, constraints);

        JLabel jsonLabel = new JLabel("JsonData");
        constraints.gridx = 0;
        panel.add(jsonLabel, constraints);
        jsonField = new JTextArea();
        jsonField.setMinimumSize(new Dimension(500, 200));
        jsonField.setPreferredSize(new Dimension(1000, 200));
        jsonField.getDocument().addDocumentListener(this);
        constraints.gridx = 1;
        panel.add(jsonField, constraints);
        return panel;
    }

    @Override
    protected void dispose() {
        jsonField.getDocument().removeDocumentListener(this);
        titleField.getDocument().removeDocumentListener(this);
        super.dispose();
    }

    /**
     * 覆盖默认的ok/cancel按钮
     *
     * @return
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        exitAction = new DialogWrapperExitAction("cancel", CANCEL_EXIT_CODE);
        okAction = new CustomOKAction();
        okAction.setEnabled(false);
        // 设置默认的焦点按钮
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{okAction, exitAction};
    }


    @Override
    public void insertUpdate(DocumentEvent e) {
        validateJson();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        validateJson();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        validateJson();
    }

    protected void validateJson() {
        title = titleField.getText();
        boolean titleExist = !StringUtils.isEmpty(title);
        if (titleExist) {
            String text = jsonField.getText();
            try {
                JsonElement element = JsonParser.parseString(text);
                if (element.isJsonObject() || element.isJsonArray()) {
                    this.element = element;
                } else {
                    this.element = null;
                }
            } catch (JsonSyntaxException e) {
                element = null;
            }
        }
        okAction.setEnabled(titleExist && element != null);
    }

    /**
     * 自定义 ok Action
     */
    protected class CustomOKAction extends DialogWrapperAction {

        protected CustomOKAction() {
            super("ok");
        }

        @Override
        protected void doAction(ActionEvent e) {
            onGetJsonListener.result(new JsonInfo(title, element));
            close(CANCEL_EXIT_CODE);
        }
    }

    public interface OnGetJsonListener {
        void result(JsonInfo jsonInfo);
    }

    public static class JsonInfo {
        private String name;
        private JsonElement jsonElement;

        public JsonInfo(String name, JsonElement jsonElement) {
            this.name = name;
            this.jsonElement = jsonElement;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public JsonElement getJsonElement() {
            return jsonElement;
        }

        public void setJsonElement(JsonElement jsonElement) {
            this.jsonElement = jsonElement;
        }
    }
}
