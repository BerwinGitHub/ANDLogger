package com.berwin.logger.views;

import com.berwin.logger.controller.Command;
import com.berwin.logger.entity.Log;
import com.berwin.logger.entity.Logger;
import com.berwin.logger.utility.UserDefault;
import com.berwin.logger.utility.Utility;
import com.berwin.logger.views.components.StyleTable;
import com.berwin.logger.views.components.VerticalFlowLayout;
import com.berwin.logger.views.dialogs.ConfigDialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView extends JFrame implements WindowListener {
    public static final boolean IS_WINDOWS = (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1);
    public static final String[] LOG_LEVELS = {Logger.LOG_VERBOSE, Logger.LOG_DEBUG, Logger.LOG_INFO, Logger.LOG_WARN, Logger.LOG_ERROR, Logger.LOG_ASSERT};
    public static MainView self = null;
    // 设备选择框
    private JComboBox<String> cbDevices = null;
    // package 选择
    private JComboBox<String> cbPackages = null;
    // log level
    private JComboBox<String> cbLogLevels = null;
    // 搜索框
    private JTextField tfSearch = null;
    // 正则表达式
    private JCheckBox cbRegex = null;
    // 中间日志
    private JScrollPane spLoggerContainor = null;
    //    private JTextPane tpLoggerContainor = null;
    //
    private StyleTable table = null;

    public static boolean isScrollBottom = false;

    private Command commond = null;

    private int isNeedBottom = 0;

    public MainView() {
        MainView.self = this;
        try {
            /**
             * 设置图形界面外观 java的图形界面外观有3种,默认是java的金属外观,还有就是windows系统,motif系统外观.
             * 1、Metal风格 (默认) UIManager.setLookAndFeel(
             * "javax.swing.plaf.metal.MetalLookAndFeel"); 2、Windows风格
             * UIManager.setLookAndFeel(
             * "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); 3、Windows
             * Classic风格 UIManager.setLookAndFeel(
             * "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
             * 4、Motif风格 UIManager.setLookAndFeel(
             * "com.sun.java.swing.plaf.motif.MotifLookAndFeel"); 5、Mac风格
             * (需要在相关的操作系统上方可实现) String lookAndFeel =
             * "com.sun.java.swing.plaf.mac.MacLookAndFeel"
             * ;UIManager.setLookAndFeel(lookAndFeel); 6、GTK风格 (需要在相关的操作系统上方可实现)
             * String lookAndFeel =
             * "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
             * UIManager.setLookAndFeel(lookAndFeel); 7、可跨平台的默认风格 String
             * lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
             * UIManager.setLookAndFeel(lookAndFeel); 8、当前系统的风格 String
             * lookAndFeel = UIManager.getSystemLookAndFeelClassName();
             * UIManager.setLookAndFeel(lookAndFeel);
             */
            if ("Mac OS X".equals(System.getProperties().getProperty("os.name"))) {
                UIManager
                        .setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                UIManager
                        .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Dimension dimension = Utility.getScreenSize(0.85f, 0.65f);
        this.setSize(dimension);
        this.setTitle("Android Logcat");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addWindowListener(this);
        this.setLayout(new BorderLayout());
        this.initMenuBar();
        this.initNorth();
        this.initCenter();

        String adbPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        if (adbPath.equals("")) {
            this.table.addLog(Log.buildLogForText("请在 [设置]-[ADB设置] 中选择adb.exe路径", Log.LEVEL_E));
        } else {
            this.requestDevices();
            this.requestPackages();
//            this.requestLogcat();
        }
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu settingMenu = new JMenu("设置");
        menuBar.add(settingMenu);

        JMenuItem compileItem = new JMenuItem("ADB设置");
        settingMenu.add(compileItem);
        compileItem.addActionListener(e -> new ConfigDialog().setVisible(true));

        JMenuItem aboutItem = new JMenuItem("关于软件");
        settingMenu.add(aboutItem);
        aboutItem.addActionListener(e -> {
            String name = "关于软件";
            String version = "1.0.0";
            String content = "安卓日志查看程序软件\r\n@版本: 1.0.0\r\n@作者: 唐博文";
            String txt = name + "(" + version + ")" + "\r\n\r\n" + content;
            JOptionPane.showMessageDialog(MainView.this, txt, "",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void initNorth() {
        JPanel north = new JPanel();
        north.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(north, BorderLayout.NORTH);
        // 设备选择框 new String[]{"Xiaomi MI 4LTE Android 6.0.1, API 23", "Meizu NOTE 3 Android 7.0.1, API 25"}
        this.cbDevices = new JComboBox<String>();
        north.add(cbDevices);
        // package 选择 new String[]{"com.google.googleplay", "com.berwin.lessmore"}
        this.cbPackages = new JComboBox<String>();
        north.add(cbPackages);
        cbPackages.addItem("*");
        cbPackages.addActionListener(e -> {
            this.requestLogcat();
        });
        // log level
        this.cbLogLevels = new JComboBox<String>(LOG_LEVELS);
        this.cbLogLevels.setSelectedIndex(UserDefault.getInstance().getValueForKey("log_level", 0));
        north.add(cbLogLevels);
        this.cbLogLevels.addActionListener(e -> {
            UserDefault.getInstance().setValueForKey("log_level", this.cbLogLevels.getSelectedIndex());
            this.requestLogcat();
        });
        // 搜索框
        this.tfSearch = new JTextField(40);
        north.add(tfSearch);
        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (tfSearch.getText().trim().equals(""))
                    return;
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    try {
////                        tpLoggerContainor.getDocument().remove(0, tpLoggerContainor.getDocument().getLength());
//                        requestLogcat();
//                    } catch (BadLocationException e1) {
//                        e1.printStackTrace();
//                    }
                }
            }
        });
        // 正则表达式
        this.cbRegex = new JCheckBox("Regex");
        this.cbRegex.setSelected(UserDefault.getInstance().getValueForKey("regex", false));
//        north.add(cbRegex);
        this.cbRegex.addActionListener(e -> requestLogcat());

    }

    private void initCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);

        // 左边
        JPanel nEast = new JPanel();
        nEast.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        center.add(nEast, BorderLayout.WEST);

        // 開始按钮
        ImageIcon iconStart = new ImageIcon("res/images/start.png");
        JButton btnStart = new JButton(iconStart);
        btnStart.setToolTipText("启动ADB");
        btnStart.setPreferredSize(new Dimension(iconStart.getIconWidth() + 10, iconStart.getIconHeight() + 10));
        nEast.add(btnStart);
        btnStart.addActionListener(e -> {
            String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
            new Command(cmdPath + " start-server").start();
            this.requestLogcat();
        });
        // 關閉按钮
        ImageIcon iconStop = new ImageIcon("res/images/stop.png");
        JButton btnStop = new JButton(iconStop);
        btnStop.setToolTipText("停止ADB");
        btnStop.setPreferredSize(new Dimension(iconStart.getIconWidth() + 10, iconStart.getIconHeight() + 10));
        nEast.add(btnStop);
        btnStop.addActionListener(e -> {
            if (this.commond != null)
                this.commond.stop();
        });
        // 刷新按钮
        ImageIcon iconRefresh = new ImageIcon("res/images/refresh.png");
        JButton btnRefresh = new JButton(iconRefresh);
        btnRefresh.setToolTipText("刷新ADB");
        btnRefresh.setPreferredSize(new Dimension(iconRefresh.getIconWidth() + 10, iconRefresh.getIconHeight() + 10));
        nEast.add(btnRefresh);
        btnRefresh.addActionListener(e -> {
            String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
            new Command(cmdPath + " kill-server").start();
            new Command(cmdPath + " start-server").startWithSynchronize();
            this.requestDevices();
            this.requestPackages();
            this.table.addLog(Log.buildLogForText("刷新成功", Log.LEVEL_V));
//            this.requestLogcat();
        });
        // 安装按钮
        ImageIcon iconInstall = new ImageIcon("res/images/install.png");
        JButton btnInstall = new JButton(iconInstall);
        btnInstall.setToolTipText("安装APK");
        btnInstall.setPreferredSize(new Dimension(iconInstall.getIconWidth() + 10, iconInstall.getIconHeight() + 10));
        nEast.add(btnInstall);
        btnInstall.addActionListener(e -> {
            String lastAPKPath = UserDefault.getInstance().getValueForKey("last_apk_path", "");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("选择APK文件");
            fileChooser.setCurrentDirectory(new File(lastAPKPath));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".apk");
                }

                @Override
                public String getDescription() {
                    return "*.apk";
                }
            });
            int result = fileChooser.showDialog(this, "确认");
            if (JFileChooser.APPROVE_OPTION == result) {
                String path = fileChooser.getSelectedFile().getPath();
                UserDefault.getInstance().setValueForKey("last_apk_path", path);
                String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
                new Command(cmdPath + " install " + path, new Command.CommandListenerAdapter() {
                    @Override
                    public void onStart(String cmd) {
                        super.onStart(cmd);
                        table.addLog(Log.buildLogForText("开始安装", Log.LEVEL_I));
                        table.addLog(Log.buildLogForText(cmd, Log.LEVEL_I));
                    }

                    @Override
                    public void onMessage(String content) {
                        super.onMessage(content);
                        table.addLog(Log.buildLogForText(content, Log.LEVEL_I));
                    }

                    @Override
                    public void onFinished() {
                        super.onFinished();
                        table.addLog(Log.buildLogForText("安装完成", Log.LEVEL_I));
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        table.addLog(Log.buildLogForText("安装出错", Log.LEVEL_E));
                        table.addLog(Log.buildLogForText(error, Log.LEVEL_E));
                    }
                }).start();
            }
        });
        // 删除按钮
        ImageIcon iconDelete = new ImageIcon("res/images/delete.png");
        JButton btnDelete = new JButton(iconDelete);
        btnDelete.setToolTipText("清除日志");
        btnDelete.setPreferredSize(new Dimension(iconDelete.getIconWidth() + 10, iconDelete.getIconHeight() + 10));
        nEast.add(btnDelete);
        btnDelete.addActionListener(e -> {
            this.clearLogcat();
            this.clearView();
        });

        // 底部按钮
        MainView.isScrollBottom = UserDefault.getInstance().getValueForKey("isScrollBottom", true);
        ImageIcon iconBottom = new ImageIcon(isScrollBottom ? "res/images/bottom_selected.png" : "res/images/bottom.png");
        JButton btnBottom = new JButton(iconBottom);
        btnBottom.setToolTipText("始终滚动到底部");
        btnBottom.setPreferredSize(new Dimension(iconDelete.getIconWidth() + 10, iconDelete.getIconHeight() + 10));
        nEast.add(btnBottom);
        btnBottom.addActionListener(e -> {
            this.isScrollBottom = !this.isScrollBottom;
            UserDefault.getInstance().setValueForKey("isScrollBottom", isScrollBottom);
            btnBottom.setIcon(new ImageIcon(isScrollBottom ? "res/images/bottom_selected.png" : "res/images/bottom.png"));
        });


//        this.tpLoggerContainor = new JTextPane();
//        this.spLoggerContainor.setViewportView(this.tpLoggerContainor);
//        this.tpLoggerContainor.setEditable(false);

        String[] titles = new String[]{"Level", "Time", "PID", "TID", "Tag", "Text"};
        this.table = new StyleTable(titles);
        // 中间
        this.spLoggerContainor = new JScrollPane(this.table);
        center.add(spLoggerContainor, BorderLayout.CENTER);

        // 滚动到底部
        this.spLoggerContainor.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent evt) {
                if (evt.getAdjustmentType() == AdjustmentEvent.TRACK && isNeedBottom <= 3) {
                    spLoggerContainor.getVerticalScrollBar().setValue(spLoggerContainor.getVerticalScrollBar().getModel().getMaximum() - spLoggerContainor.getVerticalScrollBar().getModel().getExtent());
                    isNeedBottom++;
                }
            }
        });
    }


    // 請求連接的設備
    private void requestDevices() {
        cbDevices.removeAllItems();
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        new Command(new String[]{
//                cmdPath + " devices -l",
                cmdPath + " -d shell getprop ro.product.brand",
                cmdPath + " shell getprop ro.product.model",
                cmdPath + " shell getprop ro.build.version.release",
                cmdPath + " shell getprop ro.build.version.sdk",
        }, new Command.CommandListenerAdapter() {

            @Override
            public void onMessage(String content) {
                if ("".equals(content))
                    return;
                String item = cbDevices.getItemAt(0);
                item = item == null ? "" : item;
                item += " " + content;
                cbDevices.removeAllItems();
                cbDevices.addItem(item);
//                logger(content).info();
            }

            @Override
            public void onError(String error) {
                table.addLog(Log.buildLogForText(error, Log.LEVEL_E));
            }
        }).startWithSynchronize();
    }

    // 請求連接的設備
    private void requestPackages() {
        java.util.List<String> packageList = new ArrayList<String>();
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        new Command(cmdPath + " shell pm list packages", new Command.CommandListenerAdapter() {

            @Override
            public void onMessage(String content) {
                if ("".equals(content))
                    return;
                content = content.replaceAll("package:", "");
                packageList.add(content);
            }

            @Override
            public void onError(String error) {
                table.addLog(Log.buildLogForText(error, Log.LEVEL_E));
            }

            @Override
            public void onFinished() {
                super.onFinished();
                Collections.sort(packageList);
                for (String pkg : packageList)
                    cbPackages.addItem(pkg);
            }
        }).startWithSynchronize();
    }

    private void clearLogcat() {
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        new Command(cmdPath + " logcat -c", null).startWithSynchronize();
    }

    private void clearView() {
//        Document doc = this.tpLoggerContainor.getDocument();
//        try {
//            doc.remove(0, doc.getLength());
//        } catch (BadLocationException e1) {
//            e1.printStackTrace();
//        }
        this.table.removeAllItems();
    }

    private void requestLogcat() {
        this.clearLogcat();
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
//        String logLevelChar = Logger.LOG_CMD_MARK.get(this.cbLogLevels.getSelectedIndex());
        String packageName = (String) cbPackages.getSelectedItem();
//        String search = tfSearch.getText().trim();
        boolean isRegex = this.cbRegex.isSelected();
        String cmd;
        if (!packageName.equals("*")) {
            cmd = String.format("%s logcat -v time | grep \"^%s\"", cmdPath, cbPackages.getSelectedItem());
//            if (search.equals("")) {
//                cmd = String.format("%s logcat -v time | grep \"^%s.%s\"", cmdPath, logLevelChar, cbPackages.getSelectedItem());
//            } else {
//                cmd = String.format("%s logcat -v time | grep \"^%s.%s|^..%s\"", cmdPath, logLevelChar, cbPackages.getSelectedItem(), search);
//            }
        } else {
            cmd = String.format("%s logcat", cmdPath);
//            if (search.equals("")) {
//                cmd = String.format("%s logcat -v time | grep \"^%s\"", cmdPath, logLevelChar);
//            } else {
//                cmd = String.format("%s logcat -v time | grep \"^%s|..%s\"", cmdPath, logLevelChar, search);
//            }
        }
//            cmd = String.format("%s logcat -v time *:%s | grep \"%s\" & find \"%s\"", cmdPath, logLevelChar, cbPackages.getSelectedItem(), tfSearch.getText());
        if (this.commond != null)
            this.commond.stop();
        this.commond = new Command(cmd, new Command.CommandListenerAdapter() {
            @Override
            public void onStart(String cmd) {
                super.onStart(cmd);
                table.addLog(Log.buildLogForText(cmd, Log.LEVEL_I));
            }

            @Override
            public void onMessage(String content) {
                String target = tfSearch.getText();
                if (!content.equals(""))
                    table.addLog(Log.buildLogFromText(content));
            }

            @Override
            public void onError(String error) {
                table.addLog(Log.buildLogForText(error, Log.LEVEL_E));
            }
        });
        this.commond.start();
    }

    public void find(String string) {
//        try {
//            Document doc = tpLoggerContainor.getDocument();
//            String content = doc.getText(0, doc.getLength());
//            Pattern pattern = Pattern.compile(string);
//            Matcher matcher = pattern.matcher(content);
//            boolean b = matcher.find();
//            System.out.println(b);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    public Logger logger(String content) {
//        return new Logger(this.spLoggerContainor, null, content);
//    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (this.commond != null)
            this.commond.stop();
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
}
