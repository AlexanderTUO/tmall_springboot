package com.how2java.tmall.util;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * @Author: tyk
 * @Date: 2019/7/17 09:27
 * @Description:
 */
public class PortUtil {
    public static boolean testPort(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.close();
            return false;
        } catch (java.net.BindException e1) {
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void checkPort(int port, String server,boolean shutdown) {
        if (!testPort(port)) {
            if (shutdown) {
                String message = String.format("在端口%d未检测到%s启动%n",port,server);
                JOptionPane.showMessageDialog(null, message);
                System.exit(1);
            } else {
                String message = String.format("在端口%d未检测到%s启动%n，是否继续？",port,server);
                if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null, message)) {
                    System.exit(1);
                }
            }
        }
    }
}
