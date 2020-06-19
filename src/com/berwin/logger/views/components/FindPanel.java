package com.berwin.logger.views.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class FindPanel extends JPanel {

    // 搜索框
    private JTextField tfSearch = null;
    private JCheckBox cbWords = null;
    private JCheckBox cbMatchCase = null;
    // 正则表达式
    private JCheckBox cbRegex = null;

    public FindPanel() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        // 搜索框
        this.tfSearch = new JTextField(40);
        this.add(tfSearch);
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        JButton btnClear = new JButton("清除");
        this.add(btnClear);
        btnClear.addActionListener(e -> {
            tfSearch.setText("");
        });
        // 大小写匹配
        this.cbMatchCase = new JCheckBox("Match Case");
        this.add(cbMatchCase);
        // 单词匹配
        this.cbWords = new JCheckBox("Words");
        this.add(cbWords);
        // 正则表达式
        this.cbRegex = new JCheckBox("Regex");
        this.add(cbRegex);
    }
}
