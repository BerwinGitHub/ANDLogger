/**
 *
 */
package com.berwin.logger.views.dialogs;

import com.berwin.logger.utility.UserDefault;
import com.berwin.logger.utility.Utility;
import com.berwin.logger.views.MainView;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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

    public ConfigDialog() {
        super(MainView.self, "ADB设置", 0.9f);

        this.addWindowListener(this);
        this.setLayout(new BorderLayout());


        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
//        Border border = BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(Color.GRAY, 1), "项目根目录",
//                TitledBorder.LEFT, TitledBorder.TOP);
//        top.setBorder(border);

        JButton btnOpenFolder = new JButton("打开");
        top.add(btnOpenFolder, BorderLayout.WEST);
        btnOpenFolder.addActionListener(e -> Utility.openFolder(this.tfPRJRoot.getText()));

        this.tfPRJRoot = new JTextField();
        this.tfPRJRoot.setEditable(false);
        this.tfPRJRoot.setText(UserDefault.getInstance().getValueForKey("adb_path", ""));
        top.add(this.tfPRJRoot, BorderLayout.CENTER);

        JButton btnsSelectFolder = new JButton("浏览");
        top.add(btnsSelectFolder, BorderLayout.EAST);
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
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {

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
