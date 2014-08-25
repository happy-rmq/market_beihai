package com.lenovo.market.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

/**
 * Created by zhouyang on 14-1-22.
 */

public class FileDownloadTask extends AsyncTask<String, Integer, Boolean> {
    private String filePath;

    public FileDownloadTask(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Boolean doInBackground(String... params) {
        boolean isSuccess = false;
        if (params[0] == null) {
            return isSuccess;
        }
        URL url = null;
        FileOutputStream output = null;
        try {
            url = new URL(params[0]);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            File file = new File(filePath);
            if(file.exists()){
                return false;
            }else{
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 4];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer,0,bytesRead);
            }
            System.out.println("file size=========>" + file.length()/1024.0 + "kb");
            output.flush();
            output.close();
            inputStream.close();
            isSuccess = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}