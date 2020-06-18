package com.berwin.logger.utility;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtility {

    /**
     * 拷贝文件夹/文件
     * 做排除文件夹的操作
     *
     * @param src      目标文件/夹
     * @param dst      目的文件/夹
     * @param excludes 需要排除的文件
     */
    public static boolean copy(File src, File dst, List<String> excludes) {
        boolean isExclude = false;
        if (excludes != null) {
            for (String exclude : excludes) {
                if (!"".equals(exclude) && src.getAbsolutePath().indexOf(exclude) != -1) {
                    isExclude = true;
                    break;
                }
            }
        }
        if (isExclude)
            return true;
        if (src.isDirectory()) {
            if (!dst.exists())
                dst.mkdirs();
            try {
                if (src.isHidden())
                    Runtime.getRuntime().exec("attrib +H \"" + dst.getAbsolutePath() + "\"");
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] files = src.list();
            for (String file : files) {
                boolean success = copy(new File(src, file), new File(dst, file), excludes);
                if (!success)
                    return false;
            }
        } else {
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                if (!dst.exists())
                    dst.createNewFile();
                inChannel = new FileInputStream(src).getChannel();
                outChannel = new FileOutputStream(dst).getChannel();
                outChannel.transferFrom(inChannel, 0, inChannel.size());
                if (src.isHidden())
                    Runtime.getRuntime().exec("attrib +H \"" + dst.getAbsolutePath() + "\"");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (null != inChannel)
                        inChannel.close();
                    if (null != outChannel) {
                        outChannel.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 递归获取指定文件的路径
     *
     * @param src       指定文件/夹
     * @param listFiles 找到的文件保存到List
     * @param suffix    需要找的文件的类型
     */
    public static void recursiveFiles(File src, List<String> listFiles, String suffix) {
        if (src.isDirectory()) {
            String[] files = src.list();
            for (String file : files) {
                recursiveFiles(new File(src, file), listFiles, suffix);
            }
        } else if (suffix == null || src.getAbsolutePath().endsWith(suffix)) {
            listFiles.add(src.getAbsolutePath());
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param file 将要删除的文件/夹
     * @return 删除是否成功
     */
    public static boolean delete(File file) {
        return delete(file, null);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param file 将要删除的文件/夹
     * @return 删除是否成功
     */
    public static boolean delete(File file, String suffix) {
        if (file.isDirectory()) {
            String[] files = file.list();
            for (String f : files) {
                boolean success = delete(new File(file, f), suffix);
                if (!success)
                    return false;
            }
        }
        if (suffix == null || file.getAbsolutePath().endsWith(suffix))
            return file.delete();
        return true;
    }

    /**
     * 文件/夹 MD5 值
     *
     * @param file 将要取到MD5的文件/夹
     * @param md5s md5 存储的容器
     */
    public static void md5(File file, Map<String, String> md5s) {
        if (file.isDirectory()) {
            String[] files = file.list();
            for (String f : files)
                md5(new File(file, f), md5s);
        } else {
            try {
                md5s.put(file.getCanonicalPath(), DigestUtils.md5Hex(new FileInputStream(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存到文件
     *
     * @param path
     * @param content
     * @return
     */
    public static boolean saveToFile(String path, String content) {
        File file = new File(path);
        FileWriter writer = null;
        try {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if (!file.exists())
                file.createNewFile();
            writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件中读取为String
     *
     * @param path
     * @return
     */
    public static String getStringFromFile(String path) throws IOException {
        File file = new File(path);
        if (!file.isFile() || !file.exists())
            return null;

        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String s = "";
        while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
//            System.out.println(s);
        }
        bReader.close();
        String str = sb.toString();
        return str;
    }

    /**
     * 将一个目录拷贝到另外个目录
     *
     * @param src
     * @param dst
     */
    public static void relocate(File src, File dst, boolean delete) {
        List<String> relocateFiles = new ArrayList<>();
        recursiveFiles(new File(src.getAbsolutePath()), relocateFiles, null);
        for (String p : relocateFiles) {
            File newFile = new File(p);
            String relative = p.replace(src.getAbsolutePath(), "");
            File dstFile = new File(dst, relative);
            if (!dstFile.getParentFile().exists())
                dstFile.getParentFile().mkdirs();
//            newFile.renameTo(dstFile);
            copy(newFile, dstFile, null);
        }
        if (delete)
            FileUtility.delete(src);
    }


    /**
     * 文件夹下面所有文件的长度
     *
     * @param src
     * @return
     */
    public static long listSize(File src) {
        long size = 0;
        if (src.isDirectory()) {
            String[] files = src.list();
            for (String file : files) {
                size += listSize(new File(src, file));
            }
        } else {
            size += src.length();
        }
        return size;
    }

    /**
     * 文件/路径 是否存在
     *
     * @param path 文件/路径 名
     * @return
     */
    public static boolean exist(String path) {
        File file = new File(path);
        return file.exists();
    }
}
