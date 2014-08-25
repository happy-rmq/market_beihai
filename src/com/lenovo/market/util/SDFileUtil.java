package com.lenovo.market.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

/**
 * SD卡辅助类
 */
public class SDFileUtil {
    private static SDFileUtil sdFile = null;

    public static SDFileUtil getInstance() {
        if (sdFile == null) {
            sdFile = new SDFileUtil();
        }
        return sdFile;
    }

    /**
     * 检查SD卡是否插好,并返回SD卡路径
     */
    public boolean SDCardIsOk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SD路径
     * 
     * @return /sdcard
     */
    public String getSDPath() {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.getPath();
        }
        return "/mnt/sdcard";
    }

    /**
     * 创建文件夹
     * 
     * @param dirName
     */
    public void createDir(String dirName) {
        File destDir = new File(dirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

    /**
     * 获取SD卡路径
     * 
     * @return
     */
    public String getSDCardPath() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    /**
     * 在SD卡上创建文件
     * 
     * @param fileName
     * @return
     */
    public File creatSDFile(String fileName) {
        File file = new File(getSDCardPath() + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 在SD卡上创建目录
     * 
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName) {
        File dir = new File(getSDCardPath() + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 检查SD卡上的文件夹是否存在
     * 
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(getSDCardPath() + fileName);
        return file.exists();
    }

    /**
     * 判断文件是否存在
     * 
     * @param name
     *            文件名
     * @return
     */
    public boolean fileExist(String name) {
        File f = new File(getSDCardPath() + name);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将InputStream里面的数据写入到SD卡中
     * 
     * @param path
     *            文件夹路径
     * @param fileName
     *            文件名
     * @param input
     *            输入流
     * @return
     */
    public File writeFileToSDCard(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream ops = null;

        try {
            createSDDir(path);
            file = creatSDFile(path + fileName);
            ops = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            while ((input.read(buffer) != -1)) {
                ops.write(buffer);
            }
            ops.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ops) {
                    ops.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 输入流转化成图片
     * 
     * @param is
     *            输入流
     * @param imgPathTemp
     *            文件夹路径
     * @param fileName
     *            文件名
     * @return
     */
    public File inputToFile(InputStream is, String imgPathTemp, String fileName) {
        // String imgPathTemp = SDFileUtils.getSDPath()
        // + SlookConstant.userAvatarUrl;
        createDir(imgPathTemp);
        File file = new File(imgPathTemp, fileName);// 保存文件
        // Logs.v(SDFileUtils.class, true, imgPathTemp + "  |  " + fileName);
        try {
            if (!file.exists() && !file.isDirectory()) {
                // 可以在这里通过文件名来判断，是否本地有此图片
                FileOutputStream fos = new FileOutputStream(file);
                int data = is.read();
                while (data != -1) {
                    fos.write(data);
                    data = is.read();
                }
                fos.close();
                is.close();
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据byte数组，生成文件
     */
    public void getFile(byte[] bfile, String imgPathTemp, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        createDir(imgPathTemp);
        File file = new File(imgPathTemp, fileName);// 保存文件
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 数据转化成文件
     * 
     * @param datas
     *            数据源
     * @param imgPathTemp
     *            文件夹路径
     * @param fileName
     *            文件名
     * @return
     */
    public File ByteToFile(byte[] datas, String imgPathTemp, String fileName) {
        createDir(imgPathTemp);
        File file = new File(imgPathTemp, fileName);// 保存文件
        try {
            if (!file.exists() && !file.isDirectory()) {
                // 可以在这里通过文件名来判断，是否本地有此图片
                FileOutputStream fos = new FileOutputStream(file);
                ByteArrayInputStream bais = new ByteArrayInputStream(datas);
                int data = bais.read();
                while (data != -1) {
                    fos.write(data);
                    data = bais.read();
                }
                fos.close();
                bais.close();
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 写文件到SD卡
     * 
     * @param fileName
     *            文件名
     * @param message
     *            文件内容
     * @author ck
     * @date 2013-1-10 下午04:35:32
     */
    public void writeFileSdcard(String fileName, String message) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * 
     * @param path
     */
    public void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    // 获得系统可用内存信息
    // private String getSystemAvaialbeMemorySize(Context ct) {
    // // 获得ActivityManager服务的对象
    // ActivityManager mActivityManager = (ActivityManager) ct
    // .getSystemService(Context.ACTIVITY_SERVICE);
    // // 获得MemoryInfo对象
    // MemoryInfo memoryInfo = new MemoryInfo();
    // // 获得系统可用内存，保存在MemoryInfo对象上
    // mActivityManager.getMemoryInfo(memoryInfo);
    // long memSize = memoryInfo.availMem;
    //
    // // 字符类型转换
    // String availMemStr = formateFileSize(memSize, ct);
    // return availMemStr;
    // }

    // 调用系统函数，字符串转换 long -String KB/MB
    // private String formateFileSize(long size, Context ct) {
    // return Formatter.formatFileSize(ct, size);
    // }

    /**
     * 获取内存卡容量大小
     * 
     * @param path
     * @return
     */
    public long getRoomSize(String path) {
        File file = new File(path);
        return file.length();
    }

    /**
     * 删除文件内容及文件夹
     * 
     * @param folderPath
     * @author wsc
     */
    public final static void deleteDirectory(String folderPath) {
        try {
            // 删除文件夹里所有的文件及文件夹
            deleteAllFile(folderPath);
            File lastFile = new File(folderPath);
            if (lastFile.exists()) {
                // 最后删除空文件夹
                lastFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹里面的所有文件
     */
    public final static void deleteAllFile(String path) {

        // 在内存开辟一个文件空间，但是没有创建
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            String[] tempList = file.list();
            File temp = null;
            for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    // 先删除文件夹里面的文件
                    deleteAllFile(path + "/" + tempList[i]);
                    // 再删除空文件夹
                    deleteDirectory(path + "/" + tempList[i]);
                }
            }
        }
    }
}
