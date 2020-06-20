package com.berwin.logger.views;

import com.berwin.logger.controller.Command;
import com.berwin.logger.entity.*;
import com.berwin.logger.utility.FileUtility;
import com.berwin.logger.utility.UserDefault;
import com.berwin.logger.utility.Utility;
import com.berwin.logger.views.components.FindPanel;
import com.berwin.logger.views.components.StyleTable;
import com.berwin.logger.views.components.VerticalFlowLayout;
import com.berwin.logger.views.dialogs.ConfigDialog;
import com.berwin.logger.views.dialogs.SelectedDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainView extends JFrame implements WindowListener {
    public static final String PKG_ANY = "*"; // 选择过滤任意包
    public static final boolean IS_WINDOWS = (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1);
    public static MainView self = null;
    // 设备选择框
//    private JComboBox<String> cbDevices = null;
    private JMenu devicesMenu = null;
    private List<String> deviceList = null;
    // package 选择
//    private JComboBox<String> cbPackages = null;
    private JMenu packagesMenu = null;
    private List<String> packageList = null;
    // log level
    private JComboBox<String> cbLogLevels = null;
    // 搜索框
    private JTextField tfFilter = null;
    private JCheckBox cbWords = null;
    private JCheckBox cbMatchCase = null;
    // 正则表达式
    private JCheckBox cbRegex = null;
    // 中间日志
    private JScrollPane spLoggerContainor = null;
    private Filter filter = null;
    private Find finder = null;
    private FindPanel findPanel = null;
    //    private JTextPane tpLoggerContainor = null;
    //
    private StyleTable table = null;

    private JLabel lblLogLines = null;

    private boolean isScrollBottom = false;

    private Command command = null;
    private boolean isADBRunning = false;


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
                UIManager.put("Table.gridColor", new ColorUIResource(new Color(230, 230, 230)));
            } else {
//                UIManager
//                        .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Dimension dimension = Utility.getScreenSize(0.85f, 0.65f);
        this.setSize(dimension);
        this.setTitle("Android Logcat");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addWindowListener(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            KeyEvent e = (KeyEvent) event;
            if (event.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    this.findPanel.setVisible(false);
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);

        this.setLayout(new BorderLayout());
        this.initMenuBar();
        this.initNorth();
        this.initCenter();

        this.updateFilter();

        String adbPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        if (adbPath.equals("")) {
            this.table.addLog(Log.buildLogForText("请在 [设置]-[常规配置] 中选择adb.exe路径", Log.LEVEL_E));
//            new ConfigDialog().setVisible(true);
        } else {
            this.requestDevices();
            this.requestPackages();
//            this.requestLogcat();
        }
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu editMenu = new JMenu("编辑");
        menuBar.add(editMenu);

        JMenuItem filterItem = new JMenuItem("过滤");
        filterItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.META_MASK));
        editMenu.add(filterItem);
        filterItem.addActionListener(e -> this.tfFilter.requestFocus());

        editMenu.addSeparator();

        JMenuItem findItem = new JMenuItem("搜索");
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.META_MASK));
        editMenu.add(findItem);
        findItem.addActionListener(e -> this.findPanel.setVisible(!this.findPanel.isVisible()));

        // 设备
        devicesMenu = new JMenu("设备(0)");
        menuBar.add(devicesMenu);
        deviceList = new ArrayList<>();

        JRadioButtonMenuItem deviceSelectedItem = new JRadioButtonMenuItem("无设备", true);
        deviceSelectedItem.setEnabled(false);
        devicesMenu.add(deviceSelectedItem);

        devicesMenu.addSeparator();

        JMenuItem deviceSelectItem = new JMenuItem("选择设备...");
        devicesMenu.add(deviceSelectItem);
        deviceSelectItem.addActionListener(e -> {
            new SelectedDialog(this.deviceList, deviceSelectedItem.getText(), e1 -> {
                if (e1.getStateChange() == ItemEvent.SELECTED) {
                    String text = (String) e1.getItem();
                    deviceSelectedItem.setText(text);
                    MainView.this.table.addLog(Log.buildLogForText("选择设备:" + text, Log.LEVEL_I));
                }
            }).setVisible(true);
        });

        // 包名
        packagesMenu = new JMenu("包名(0)");
        menuBar.add(packagesMenu);
        packageList = new ArrayList<>();

        JRadioButtonMenuItem packageSelectedItem = new JRadioButtonMenuItem("无包名", true);
        packageSelectedItem.setEnabled(false);
        packagesMenu.add(packageSelectedItem);

        packagesMenu.addSeparator();

        JMenuItem packageSelectItem = new JMenuItem("选择包名...");
        packagesMenu.add(packageSelectItem);
        packageSelectItem.addActionListener(e -> {
            new SelectedDialog(this.packageList, packageSelectedItem.getText(), e1 -> {
                if (e1.getStateChange() == ItemEvent.SELECTED) {
                    String text = (String) e1.getItem();
                    packageSelectedItem.setText(text);
                    MainView.this.table.addLog(Log.buildLogForText("选择包名:" + text, Log.LEVEL_I));
                }
            }).setVisible(true);
        });

        // 设置
        JMenu settingMenu = new JMenu("设置");
        menuBar.add(settingMenu);

        JMenuItem compileItem = new JMenuItem("常规配置");
        compileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.META_MASK));
        settingMenu.add(compileItem);
        compileItem.addActionListener(e -> new ConfigDialog().setVisible(true));

        settingMenu.addSeparator();

        JMenuItem aboutItem = new JMenuItem("关于软件");
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.META_MASK));
        settingMenu.add(aboutItem);
        aboutItem.addActionListener(e -> {
            String name = "关于软件";
            String version = "1.0.0";
            String content = "安卓日志查看程序软件\r\n@版本: 1.0.0\r\n@作者: 唐博文";
            String txt = name + "(" + version + ")" + "\r\n\r\n" + content;
            JOptionPane.showMessageDialog(MainView.this, txt, "",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem gitItem = new JMenuItem("开源地址");
        gitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.META_MASK));
        settingMenu.add(gitItem);
        gitItem.addActionListener(e -> {
            String url = "https://github.com/BerwinGitHub/ANDLogger";
            this.table.addLog(Log.buildLogForText("开源地址:" + url, Log.LEVEL_I));
            try {
                URI u = URI.create(url);
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(u);
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void initNorth() {
        JPanel north = new JPanel();
        north.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        this.add(north, BorderLayout.NORTH);

//        JPanel northFirst = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        north.add(northFirst);
//        this.cbDevices = new JComboBox<String>();
//        northFirst.add(cbDevices);
//        this.cbPackages = new JComboBox<String>();
//        northFirst.add(cbPackages);
//        cbPackages.addItem(MainView.PKG_ANY);
//        cbPackages.addActionListener(e -> {
//            this.requestLogcat();
//        });
        JPanel northSecond = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(northSecond);
        // 刷新设备
        JButton btnRefresh = new JButton("刷新设备");
        northSecond.add(btnRefresh);
        btnRefresh.addActionListener(e -> {
            this.requestDevices();
            this.requestPackages();
        });
        northSecond.add(new JLabel("日志过滤:"));
        // log level
        this.cbLogLevels = new JComboBox(LogType.getLogNames());
        this.cbLogLevels.setSelectedIndex(UserDefault.getInstance().getValueForKey("log_level", 0));
        northSecond.add(cbLogLevels);
        this.cbLogLevels.addActionListener(e -> {
            UserDefault.getInstance().setValueForKey("log_level", this.cbLogLevels.getSelectedIndex());
//            this.requestLogcat();
            this.updateFilter();
        });
        // 过滤框
        this.tfFilter = new JTextField(40);
        northSecond.add(tfFilter);
        tfFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                MainView.this.updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                MainView.this.updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                MainView.this.updateFilter();
            }
        });
        JButton btnClear = new JButton("清除");
        northSecond.add(btnClear);
        btnClear.addActionListener(e -> {
            tfFilter.setText("");
        });
        // 大小写匹配
        this.cbMatchCase = new JCheckBox("Match Case");
        this.cbMatchCase.setSelected(UserDefault.getInstance().getValueForKey("MatchCase", false));
        northSecond.add(cbMatchCase);
        this.cbMatchCase.addActionListener(e -> {
            UserDefault.getInstance().setValueForKey("MatchCase", ((JCheckBox) e.getSource()).isSelected());
            this.updateFilter();
        });
        // 单词匹配
        this.cbWords = new JCheckBox("Words");
        this.cbWords.setSelected(UserDefault.getInstance().getValueForKey("Words", false));
        northSecond.add(cbWords);
        this.cbWords.addActionListener(e -> {
            UserDefault.getInstance().setValueForKey("Words", ((JCheckBox) e.getSource()).isSelected());
            this.updateFilter();
        });
        // 正则表达式
        this.cbRegex = new JCheckBox("Regex");
        this.cbRegex.setSelected(UserDefault.getInstance().getValueForKey("Regex", false));
        northSecond.add(cbRegex);
        this.cbRegex.addActionListener(e -> {
            UserDefault.getInstance().setValueForKey("Regex", ((JCheckBox) e.getSource()).isSelected());
            this.updateFilter();
        });
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
            if (!this.isADBRunning) {
                String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
                new Command(cmdPath + " start-server").start();
                if (this.requestLogcat()) {
                    btnStart.setIcon(new ImageIcon("res/images/stop.png"));
                    btnStart.setToolTipText("停止ADB");
                    this.isADBRunning = !this.isADBRunning;
                }
            } else {
                if (this.command != null)
                    this.command.stop();
                btnStart.setIcon(new ImageIcon("res/images/start.png"));
                btnStart.setToolTipText("启动ADB");
                this.isADBRunning = !this.isADBRunning;
            }
        });
        // 關閉按钮
//        ImageIcon iconStop = new ImageIcon("res/images/stop.png");
//        JButton btnStop = new JButton(iconStop);
//        btnStop.setToolTipText("停止ADB");
//        btnStop.setPreferredSize(new Dimension(iconStart.getIconWidth() + 10, iconStart.getIconHeight() + 10));
//        nEast.add(btnStop);
//        btnStop.addActionListener(e -> {
//            if (this.commond != null)
//                this.commond.stop();
//        });
        // 删除按钮
        ImageIcon iconDelete = new ImageIcon("res/images/delete.png");
        JButton btnDelete = new JButton(iconDelete);
        btnDelete.setToolTipText("清除日志");
        btnDelete.setPreferredSize(new Dimension(iconDelete.getIconWidth() + 10, iconDelete.getIconHeight() + 10));
        nEast.add(btnDelete);
        btnDelete.addActionListener(e -> {
            this.clearView();
            this.clearLogcat();
            this.updateLogLines();
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
                    public void onFinished(List<String> result) {
                        super.onFinished(result);
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

        ImageIcon iconSave = new ImageIcon("res/images/save.png");
        JButton btnSave = new JButton(iconSave);
        btnSave.setToolTipText("保存日志");
        btnSave.setPreferredSize(new Dimension(iconDelete.getIconWidth() + 10, iconDelete.getIconHeight() + 10));
        nEast.add(btnSave);
        btnSave.addActionListener(e -> {
            String path = UserDefault.getInstance().getValueForKey("save_path", "");
            if (path.equals("")) {
                this.table.addLog(Log.buildLogForText("请在 [设置]-[常规配置] 中选择导出文件夹", Log.LEVEL_E));
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
            String filename = String.format("%s.log", sdf.format(new Date()));
            // 保存日志
            new Thread(() -> {
                String file = String.format("%s/%s", path, filename);
                boolean result = FileUtility.saveToFile(file, this.table.toLogString());
                if (!result) {
                    this.table.addLog(Log.buildLogForText("导出失败:" + file, Log.LEVEL_E));
                } else {
                    this.table.addLog(Log.buildLogForText("导出成功:" + file, Log.LEVEL_I));
                    try {
                        Desktop.getDesktop().open(new File(path));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        });

        // 底部按钮
        this.isScrollBottom = UserDefault.getInstance().getValueForKey("isScrollBottom", true);
        ImageIcon iconBottom = new ImageIcon(isScrollBottom ? "res/images/bottom_selected.png" : "res/images/bottom.png");
        JButton btnBottom = new JButton(iconBottom);
        btnBottom.setToolTipText("始终滚动到底部");
        btnBottom.setPreferredSize(new Dimension(iconDelete.getIconWidth() + 10, iconDelete.getIconHeight() + 10));
        nEast.add(btnBottom);
        btnBottom.addActionListener(e -> {
            this.isScrollBottom = !this.isScrollBottom;
            UserDefault.getInstance().setValueForKey("isScrollBottom", isScrollBottom);
            btnBottom.setIcon(new ImageIcon(this.isScrollBottom ? "res/images/bottom_selected.png" : "res/images/bottom.png"));
        });


//        this.tpLoggerContainor = new JTextPane();
//        this.spLoggerContainor.setViewportView(this.tpLoggerContainor);
//        this.tpLoggerContainor.setEditable(false);

        String[] titles = new String[]{"Level", "Time", "PID", "TID", "Tag", "Text"};
        JPanel centerCenter = new JPanel();
        centerCenter.setLayout(new BorderLayout());
        centerCenter.setBorder(BorderFactory.createTitledBorder(""));
        center.add(centerCenter, BorderLayout.CENTER);

        // 过滤
        this.filter = new Filter();
        this.filter.setSearchFilter(new FilterSearch());
        // 搜索
        this.finder = new Find();
        this.finder.setSearchFilter(new FilterSearch());

        this.table = new StyleTable(this, this.filter, this.finder, titles);
        // 搜索界面
        this.findPanel = new FindPanel(this.finder);
        this.findPanel.setVisible(false);
        centerCenter.add(findPanel, BorderLayout.NORTH);
        // 中间
        this.spLoggerContainor = new JScrollPane(this.table);
        centerCenter.add(spLoggerContainor, BorderLayout.CENTER);

        JPanel centerSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        centerCenter.add(centerSouth, BorderLayout.SOUTH);

        lblLogLines = new JLabel(this.table.getShowLines() + "/" + this.table.getAllLines() + "/" + this.table.getMaxLines());
        lblLogLines.setFont(new Font("", Font.ITALIC, 8));
        lblLogLines.setForeground(Color.GRAY);
        centerSouth.add(lblLogLines);
    }


    // 請求連接的設備
    private void requestDevices() {
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        if (cmdPath.equals("")) {
            this.table.addLog(Log.buildLogForText("请配置ADB路径", Log.LEVEL_E));
            new ConfigDialog().setVisible(true);
            return;
        }
        String[] commands = {
                cmdPath + " -d shell getprop ro.product.brand",
                cmdPath + " shell getprop ro.product.model",
                cmdPath + " shell getprop ro.build.version.release",
                cmdPath + " shell getprop ro.build.version.sdk",
        };
        new Command(commands, new Command.CommandListenerAdapter() {

            @Override
            public void onMessage(String content) {
                if ("".equals(content))
                    return;
            }

            @Override
            public void onError(String error) {
                table.addLog(Log.buildLogForText(error, Log.LEVEL_E));
            }

            @Override
            public void onFinished(List<String> result) {
                super.onFinished(result);
                if (result.size() <= 0) {
                    table.addLog(Log.buildLogForText("无设备数据", Log.LEVEL_E));
                    return;
                }
                deviceList.clear();
                for (int i = 0; i < result.size(); i += commands.length) {
                    String text = String.format("%s %s %s %s", result.get(i), result.get(i + 1), result.get(i + 2), result.get(i + 3)).trim();
                    if (!text.equals(""))
                        deviceList.add(text);
                }
                devicesMenu.setText(String.format("设备(%d)", deviceList.size()));
                if (deviceList.size() > 0)
                    devicesMenu.getItem(0).setText(deviceList.get(0));
                table.addLog(Log.buildLogForText("设备列表加载完成:" + deviceList.size(), Log.LEVEL_I));
            }
        }).startWithSynchronize();
    }

    // 請求連接的設備
    private void requestPackages() {
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        if (cmdPath.equals("")) {
            this.table.addLog(Log.buildLogForText("请配置ADB路径", Log.LEVEL_E));
            new ConfigDialog().setVisible(true);
            return;
        }
        packageList.clear();
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
            public void onFinished(List<String> result) {
                super.onFinished(result);
                Collections.sort(packageList);
                packageList.add(0, MainView.PKG_ANY);
                packagesMenu.setText(String.format("包名(%d)", packageList.size() - 1));
                packagesMenu.getItem(0).setText(packageList.get(0));
                table.addLog(Log.buildLogForText("应用列表加载完成:" + (packageList.size() - 1), Log.LEVEL_I));
            }
        }).startWithSynchronize();
    }

    private boolean requestLogcat() {
        if (deviceList.size() <= 0) {
            this.table.addLog(Log.buildLogForText("没有设备信息", Log.LEVEL_E));
            return false;
        }
        String selectedDevice = this.getMenuSelected(devicesMenu);
        if (selectedDevice == null) {
            this.table.addLog(Log.buildLogForText("请在菜单栏选择一个设备", Log.LEVEL_E));
            return false;
        }
        this.clearLogcat();
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        if (cmdPath.equals("")) {
            this.table.addLog(Log.buildLogForText("请配置ADB路径", Log.LEVEL_E));
            new ConfigDialog().setVisible(true);
            return false;
        }
//        String logLevelChar = Logger.LOG_CMD_MARK.get(this.cbLogLevels.getSelectedIndex());
        String packageName = this.getMenuSelected(packagesMenu);
        if (packageName == null) {
            this.table.addLog(Log.buildLogForText("请在菜单栏选择一个应用包名", Log.LEVEL_E));
            return false;
        }
//        String search = tfFilter.getText().trim();
        boolean isRegex = this.cbRegex.isSelected();
        String cmd;
        if (!packageName.equals(MainView.PKG_ANY)) {
            cmd = String.format("%s logcat | grep \"^%s\"", cmdPath, selectedDevice);
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
//            cmd = String.format("%s logcat -v time *:%s | grep \"%s\" & find \"%s\"", cmdPath, logLevelChar, cbPackages.getSelectedItem(), tfFilter.getText());
        if (this.command != null)
            this.command.stop();
        this.command = new Command(cmd, new Command.CommandListenerAdapter() {
            @Override
            public void onStart(String cmd) {
                super.onStart(cmd);
                table.addLog(Log.buildLogForText(cmd, Log.LEVEL_I));
            }

            @Override
            public void onMessage(String content) {
                String target = tfFilter.getText();
                if (!content.equals(""))
                    table.addLog(Log.buildLogFromText(content));
            }

            @Override
            public void onError(String error) {
                table.addLog(Log.buildLogForText(error, Log.LEVEL_E));
            }
        });
        this.command.start();
        return true;
    }

    private boolean clearLogcat() {
        if (deviceList.size() <= 0) {
            this.table.addLog(Log.buildLogForText("没有设备信息", Log.LEVEL_E));
            return false;
        }
        String cmdPath = UserDefault.getInstance().getValueForKey("adb_path", "");
        if (cmdPath.equals("")) {
            this.table.addLog(Log.buildLogForText("请配置ADB路径", Log.LEVEL_E));
            new ConfigDialog().setVisible(true);
            return false;
        }
        new Command(cmdPath + " logcat -c", null).startWithSynchronize();
        return true;
    }

    private void clearView() {
        this.table.removeAllItems();
    }

    private void updateFilter() {
        filter.setLogType(this.cbLogLevels.getSelectedIndex());
        filter.getSearchFilter().setContent(this.tfFilter.getText());
        filter.getSearchFilter().setMatchCase(this.cbMatchCase.isSelected());
        filter.getSearchFilter().setRegex(this.cbRegex.isSelected());
        filter.getSearchFilter().setWords(this.cbWords.isSelected());
        this.table.updatedFilter();
    }


    public List<FindSelect> updateFinder() {
        return this.table.updateFinder();
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
        if (this.command != null)
            this.command.stop();
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

    public void tryScrollBottom() {
        if (this.isScrollBottom)
            this.spLoggerContainor.getVerticalScrollBar().setValue(this.spLoggerContainor.getVerticalScrollBar().getMaximum());
    }

    public void updateConfig(String adbPath, int cacheNum) {
        this.table.updateConfig(cacheNum);
        this.table.addLog(Log.buildLogForText("保存配置，ADB:" + adbPath, Log.LEVEL_V));
        this.table.addLog(Log.buildLogForText("保存配置，缓存:" + cacheNum, Log.LEVEL_V));
        if (this.deviceList.size() <= 0 && !adbPath.equals("")) {
            this.requestDevices();
            this.requestPackages();
        }
    }

    public void updateLogLines() {
        this.lblLogLines.setText(this.table.getShowLines() + "/" + this.table.getAllLines() + "/" + this.table.getMaxLines());
    }

    private String getMenuSelected(JMenu menu) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JRadioButtonMenuItem item = (JRadioButtonMenuItem) menu.getItem(i);
            if (item.isSelected()) {
                return item.getText();
            }
        }
        return null;
    }

    public void updateFindSelected() {
        this.table.repaint();
    }
}
