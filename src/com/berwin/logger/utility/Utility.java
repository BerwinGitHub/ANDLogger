package com.berwin.logger.utility;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class Utility {
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static Dimension getScreenSize(float wRate, float hRate) {
        Dimension dimension = Utility.getScreenSize();
        return new Dimension((int) (dimension.width * wRate),
                (int) (dimension.height * hRate));
    }

    public static JPanel warpBorder(String name, Component component) {
        JPanel panel = Utility.makeBorderPanel(name);
        panel.setLayout(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    public static JPanel makeBorderPanel(String name) {
        JPanel panel = new JPanel();
        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), name,
                TitledBorder.LEFT, TitledBorder.TOP);
        panel.setBorder(border);
        return panel;
    }

    public static boolean openURL(String url) {
        // 打開官方网址
        try {
            java.net.URI uri = java.net.URI.create(url);
            // 获取当前系统桌面扩展
            java.awt.Desktop dp = java.awt.Desktop.getDesktop();
            // 判断系统桌面是否支持要执行的功能
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                dp.browse(uri);// 获取系统默认浏览器打开链接
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void copyToClipboard(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

    /**
     * 对json字符串格式化输出
     *
     * @param jsonStr
     * @return
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++)
            sb.append(' ');
    }

    /**
     * 打开文件夹
     *
     * @param path
     */
    public static void openFolder(String path) {
        if ("".equals(path))
            return;
        try {
            File f = new File(path);
            if (f.isDirectory())
                Desktop.getDesktop().open(f);
            else
                Desktop.getDesktop().open(f.getParentFile());
        } catch (IOException e) {
            System.out.println("路径不存在/合法:" + path);
        }
    }

    public static boolean isWindows() {
        return (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1);
    }

    public static boolean isMacos() {
        return false;
    }

}
