package com.berwin.logger.views.components;

import com.berwin.logger.entity.Find;
import com.berwin.logger.entity.FindSelect;
import com.berwin.logger.views.MainView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FindPanel extends JPanel {

    // 搜索框
    private JTextField tfFind;
    private JCheckBox cbWords;
    private JCheckBox cbMatchCase;
    private JLabel lblMatches;
    // 正则表达式
    private JCheckBox cbRegex;
    private Find finder;

    private String cacheContent;
    private java.util.List<FindSelect> findLog;
    private int selectedIdx = 0;

    public FindPanel(Find finder) {
        this.finder = finder;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(new JLabel("日志搜索:"));
        // 搜索框
        this.tfFind = new JTextField(40);
        this.add(tfFind);
        tfFind.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                FindPanel.this.updateFinder();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                FindPanel.this.updateFinder();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                FindPanel.this.updateFinder();
            }
        });
        tfFind.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_ENTER)
                    return;
                int lastIndex = FindPanel.this.selectedIdx;
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (FindPanel.this.findLog.size() <= 0)
                        return;
                    if (--lastIndex < 0)
                        lastIndex = FindPanel.this.findLog.size() - 1;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (FindPanel.this.findLog.size() <= 0)
                        return;
                    if (++lastIndex >= FindPanel.this.findLog.size())
                        lastIndex = 0;
                }
                FindPanel.this.updateSelected(lastIndex);
            }

        });
        JButton btnClear = new JButton("清除");
        this.add(btnClear);
        btnClear.addActionListener(e -> tfFind.setText(""));
        // 大小写匹配
        this.cbMatchCase = new JCheckBox("Match Case");
        this.add(cbMatchCase);
        this.cbMatchCase.addActionListener(e -> this.updateFinder());
        // 单词匹配
        this.cbWords = new JCheckBox("Words");
        this.add(cbWords);
        this.cbWords.addActionListener(e -> this.updateFinder());
        // 正则表达式
        this.cbRegex = new JCheckBox("Regex");
        this.add(cbRegex);
        this.cbRegex.addActionListener(e -> this.updateFinder());
        //
        this.lblMatches = new JLabel("no matches");
        this.lblMatches.setForeground(Color.gray);
        this.add(this.lblMatches);

        JButton btnClose = new JButton("关闭");
        this.add(btnClose);
        btnClose.addActionListener(e -> this.setVisible(false));
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            this.tfFind.setText(this.cacheContent);
            this.tfFind.requestFocus();
            this.lblMatches.setText("no matches");
        } else {
            this.cacheContent = this.tfFind.getText();
            this.tfFind.setText("");
        }
    }

    private void updateFinder() {
        finder.getSearchFilter().setContent(this.tfFind.getText());
        finder.getSearchFilter().setMatchCase(this.cbMatchCase.isSelected());
        finder.getSearchFilter().setRegex(this.cbRegex.isSelected());
        finder.getSearchFilter().setWords(this.cbWords.isSelected());
        findLog = MainView.self.updateFinder();
        if (findLog.size() > 0)
            this.updateSelected(0);
    }

    private void updateSelected(int selectedIdx) {
        if (selectedIdx < 0 || selectedIdx >= this.findLog.size()) {
            System.out.println("数组越界错误");
            return;
        }
        if (this.selectedIdx >= 0 && this.selectedIdx < this.findLog.size())
            this.findLog.get(this.selectedIdx).unselect();
        this.findLog.get(selectedIdx).select();
        this.selectedIdx = selectedIdx;
        this.lblMatches.setText(String.format("%d/%d matches", selectedIdx + 1, this.findLog.size()));
        MainView.self.updateFindSelected();
        MainView.self.getTable().scrollToRow(this.findLog.get(selectedIdx).getLog().getRow());
    }
}
