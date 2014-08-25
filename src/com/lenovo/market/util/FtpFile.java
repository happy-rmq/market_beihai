package com.lenovo.market.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 从ftp上下载数据和上传数据
 *
 * @author admin
 */
public class FtpFile {

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param url
     * FTP服务器hostname
     * @param port
     * FTP服务器端口
     * @param username
     * FTP登录账号
     * @param password
     * FTP登录密码
     * @param remotePath
     * FTP服务器上的相对路径
     * @param fileName
     * 要下载的文件名
     * @param localPath
     * 下载后保存到本地的路径
     * @return
     */
    private static String hostName = "58.215.56.97";
    /**
     * 登录名
     */
    private static String userName = "lenovo";

    /**
     * 登录密码
     */
    private static String password = "lenovo2014";

    /**
     * 需要访问的远程目录
     */
    public static boolean downFile(String fileName, String localPath, String remotePath) {
        // 初始表示下载失败
        boolean success = false;
        // 创建FTPClient对象
        FTPClient ftp = new FTPClient();
        try {
            // 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            // ftp.connect(url, port);
            ftp.connect(hostName);

            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.out.println("ftp 服务器连接失败");
                return success;
            }

            System.out.println("连接到ftp服务器：" + hostName + " 成功..开始登录");
            // 登录ftp
            boolean loginSuccess = ftp.login(userName, password);
            if (!loginSuccess) {
                System.out.println("==========用户：" + userName + "登录失败");
                return success;
            }
            //设置传输模式
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();


            ftp.setBufferSize(4 * 1048);
            ftp.setControlEncoding("UTF-8");

            // 转到指定下载目录
            boolean isChanged = ftp.changeWorkingDirectory(remotePath);
            if (!isChanged) {
                System.out.println("==========" + remotePath + "切换失败");
                return success;
            }
            FileOutputStream output = new FileOutputStream(localPath);
            success = ftp.retrieveFile(fileName, output);

            output.close();

            // 退出ftp
            ftp.logout();
            // 下载成功
//            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    /**
     * 上传文件.
     *
     * @param path     FTP服务器保存目录
     * @param fileName 上传到FTP服务器上的文件名
     * @param input    要上传的文件输入流
     * @return true, if successful
     * @author LiuYH
     * @date Jun 21, 2013 11:49:43 AM
     * @version V1.0
     */
    public static boolean uploadFile(String path, String fileName, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();

        try {
            int reply;
            ftp.connect(hostName);// 连接FTP服务器
            System.out.println("连接到ftp服务器：" + hostName + " 成功..开始登录");
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(userName, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(path);
            ftp.storeFile(fileName, input);

            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
}