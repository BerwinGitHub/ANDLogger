package com.berwin.logger.controller;

import com.berwin.logger.views.MainView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Command implements Runnable {

    private Thread thread = null;
    private Process process = null;
    private boolean isRunning = false;

    public interface CommandListener {
        void onStart(String cmd);

        void onMessage(String content);

        void onFinished(List<String> result);

        void onError(String error);
    }

    public abstract static class CommandListenerAdapter implements CommandListener {
        @Override
        public void onStart(String cmd) {

        }

        @Override
        public void onMessage(String content) {

        }

        @Override
        public void onFinished(List<String> result) {

        }

        @Override
        public void onError(String error) {

        }
    }

    private String[] commands;
    private CommandListener listener = null;

    public Command(String command) {
        this.commands = new String[]{command};
    }

    public Command(String command, CommandListener listener) {
        this.commands = new String[]{command};
        this.listener = listener;
    }

    public Command(String[] commands, CommandListener listener) {
        this.commands = commands;
        this.listener = listener;
    }

    public void startWithSynchronize() {
        this.isRunning = true;
        this.run();
    }

    public void start() {
        this.isRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        this.isRunning = false;
    }

    @Override
    public void run() {
//        try {
//            //接收正常结果流
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            //接收异常结果流
//            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
//            System.out.println(this.commond);
//            CommandLine commandline = CommandLine.parse(this.commond);
//            DefaultExecutor exec = new DefaultExecutor();
//            exec.setExitValues(null);
//            //设置一分钟超时
//            ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
//            exec.setWatchdog(watchdog);
//            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
//            exec.setStreamHandler(streamHandler);
//            exec.execute(commandline);
//            //不同操作系统注意编码，否则结果乱码
//            String out = outputStream.toString("GBK");
//            String error = errorStream.toString("GBK");
//            System.out.println("out:" + out);
//            System.out.println("error:" + error);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        }
        List<String> result = new ArrayList<>();
        for (String command : this.commands) {
            BufferedReader reader = null;
            String buffer = "";
            try {
                while (true) {
                    if (this.listener != null)
                        this.listener.onStart(command);
                    process = Runtime.getRuntime().exec(command);
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream(), MainView.IS_WINDOWS ? "GBK" : "UTF-8"));
                    String line;
                    while (this.isRunning && (line = reader.readLine()) != null)
                        if (this.listener != null) {
                            this.listener.onMessage(line);
                            buffer = line == null ? "" : line;
                        }
                    if (!this.isRunning)
                        break;
                    process.waitFor();
                    // 阻塞等待
                    break;
                }
                result.add(buffer);
            } catch (Exception e) {
                this.isRunning = false;
                e.printStackTrace();
                if (this.listener != null)
                    this.listener.onError(e.getMessage());
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (Exception e) {
                }
            }
        }
        if (this.listener != null)
            this.listener.onFinished(result);
    }

    public static void main(String[] args) {
//        Model.getInstance();
//        new Command(new String[]{"pngquant --quality=70 /Users/Berwin/Desktop/1/ -o /Users/Berwin/Desktop/2/ --skip-if-larger --speed 3 --nofs --floyd=0.5 --strip", "cocos -h"}, new CommandListener() {
//            @Override
//            public void onStart(String cmd) {
//                System.out.println("onStart:" + cmd);
//            }
//
//            @Override
//            public void onMessage(String content) {
//                System.out.println(">>>:" + content);
//            }
//
//            @Override
//            public void onFinished() {
//                System.out.println("onFinished");
//            }
//
//            @Override
//            public void onError(String error) {
//                System.out.println("error:" + error);
//            }
//        }).start();
        String s = new SimpleDateFormat("yy.M.d.H.m").format(new Date().getTime());
        System.out.println(s);
    }

}
