/**
 *
 */
package com.berwin.logger.views.dialogs;

import com.berwin.logger.entity.LogType;
import com.berwin.logger.utility.ColorUtility;
import com.berwin.logger.utility.UserDefault;
import com.berwin.logger.utility.Utility;
import com.berwin.logger.views.MainView;
import com.berwin.logger.views.components.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * @author Administrator
 */
public class ConfigDialog extends BaseDialog implements WindowListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JTextField tfPRJRoot = new JTextField();
    private JTextField tfCacheNum = null;

    public ConfigDialog() {
        super(MainView.self, "常规配置", 0.75f);

        this.addWindowListener(this);
        this.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));

        this.add(this.initADBBrowser());
        this.add(this.initExportBrowser());

        JPanel pnlSecond = new JPanel(new BorderLayout());
        pnlSecond.setBorder(BorderFactory.createTitledBorder("日志缓存(行)"));
        this.add(pnlSecond);

        tfCacheNum = new JTextField();
        pnlSecond.add(tfCacheNum, BorderLayout.CENTER);
        tfCacheNum.setText(UserDefault.getInstance().getValueForKey("cache_num", 10000) + "");

        JPanel pnlThird = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlThird.setBorder(BorderFactory.createTitledBorder("日志颜色"));
        this.add(pnlThird);

        String[] names = LogType.getLogNames();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];

            JPanel pnlColor = new JPanel();
            pnlColor.setPreferredSize(new Dimension(30, 30));
            Color color = LogType.getColorByName(name);
            pnlColor.setBackground(color);
            pnlThird.add(pnlColor);
            pnlColor.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        Color newColor = JColorChooser.showDialog(ConfigDialog.this, name + " 颜色", color);
                        if (newColor != null) {
                            pnlColor.setBackground(newColor);
                            UserDefault.getInstance().setValueForKey("color_" + name, ColorUtility.colorToHex(newColor));
                            LogType.setLogColor(name, newColor);
                        }
                    }
                }
            });

            pnlThird.add(new JLabel(name + "      "));
        }

    }

    private JPanel initADBBrowser() {
        JPanel pnlFirst = new JPanel();
        pnlFirst.setBorder(BorderFactory.createTitledBorder("ADB文件路径"));
        pnlFirst.setLayout(new BorderLayout());

        JButton btnOpenFolder = new JButton("打开");
        pnlFirst.add(btnOpenFolder, BorderLayout.WEST);
        btnOpenFolder.addActionListener(e -> Utility.openFolder(this.tfPRJRoot.getText()));

        this.tfPRJRoot = new JTextField();
        this.tfPRJRoot.setEditable(false);
        this.tfPRJRoot.setText(UserDefault.getInstance().getValueForKey("adb_path", ""));
        pnlFirst.add(this.tfPRJRoot, BorderLayout.CENTER);

        JButton btnSelectFolder = new JButton("浏览");
        pnlFirst.add(btnSelectFolder, BorderLayout.EAST);
        btnSelectFolder.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("ADB路径");
            fileChooser.setCurrentDirectory(new File(
                    tfPRJRoot.getText()));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showDialog(this, "确认");
            if (JFileChooser.APPROVE_OPTION == result) {
                String path = fileChooser.getSelectedFile().getPath();
                tfPRJRoot.setText(path);
                UserDefault.getInstance().setValueForKey("adb_path", path);
            }
        });
        return pnlFirst;
    }

    private JPanel initExportBrowser() {
        JPanel pnlFirst = new JPanel();
        pnlFirst.setBorder(BorderFactory.createTitledBorder("日志导出路径"));
        pnlFirst.setLayout(new BorderLayout());

        JTextField tfBrowser = new JTextField();
        tfBrowser.setEditable(false);
        tfBrowser.setText(UserDefault.getInstance().getValueForKey("save_path", ""));
        pnlFirst.add(tfBrowser, BorderLayout.CENTER);

        JButton btnOpenFolder = new JButton("打开");
        pnlFirst.add(btnOpenFolder, BorderLayout.WEST);
        btnOpenFolder.addActionListener(e -> Utility.openFolder(tfBrowser.getText()));

        JButton btnSelectFolder = new JButton("浏览");
        pnlFirst.add(btnSelectFolder, BorderLayout.EAST);
        btnSelectFolder.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("日志保存路径");
            fileChooser.setCurrentDirectory(new File(
                    tfBrowser.getText()));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showDialog(this, "确认");
            if (JFileChooser.APPROVE_OPTION == result) {
                String path = fileChooser.getSelectedFile().getPath();
                tfBrowser.setText(path);
                UserDefault.getInstance().setValueForKey("save_path", path);
            }
        });
        return pnlFirst;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        try {
            int cacheNum = Integer.parseInt(tfCacheNum.getText());
            UserDefault.getInstance().setValueForKey("cache_num", cacheNum);
            String adbPath = tfPRJRoot.getText();
            MainView.self.updateConfig(adbPath, cacheNum);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

//    private void copyToolTipText(JComponent component) {
//        component.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                String txt = ((JComponent) e.getSource()).getToolTipText();
//                Utility.copyToClipboard(txt);
//                Toast.makeText(MainView.self, "已复制到剪切板:" + txt, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
