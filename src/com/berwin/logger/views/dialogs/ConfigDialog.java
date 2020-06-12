/**
 *
 */
package com.berwin.logger.views.dialogs;

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


        JPanel pnlFirst = new JPanel();
        pnlFirst.setLayout(new BorderLayout());
        this.add(pnlFirst);
//        Border border = BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(Color.GRAY, 1), "项目根目录",
//                TitledBorder.LEFT, TitledBorder.TOP);
//        top.setBorder(border);

        JButton btnOpenFolder = new JButton("打开ADB文件夹");
        pnlFirst.add(btnOpenFolder, BorderLayout.WEST);
        btnOpenFolder.addActionListener(e -> Utility.openFolder(this.tfPRJRoot.getText()));

        this.tfPRJRoot = new JTextField();
        this.tfPRJRoot.setEditable(false);
        this.tfPRJRoot.setText(UserDefault.getInstance().getValueForKey("adb_path", ""));
        pnlFirst.add(this.tfPRJRoot, BorderLayout.CENTER);

        JButton btnsSelectFolder = new JButton("浏览");
        pnlFirst.add(btnsSelectFolder, BorderLayout.EAST);
        btnsSelectFolder.addActionListener(e -> {
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

        JPanel pnlSecond = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.add(pnlSecond);

        JLabel lblCacheNum = new JLabel("日志缓存(行):");
        pnlSecond.add(lblCacheNum);

        tfCacheNum = new JTextField(30);
        pnlSecond.add(tfCacheNum);
        tfCacheNum.setText(UserDefault.getInstance().getValueForKey("cache_num", 10000) + "");

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
