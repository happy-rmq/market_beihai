package com.lenovo.market.util;

import java.io.File;
import java.util.HashMap;

import com.lenovo.market.activity.circle.friends.FriendSelectActivity;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;

import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.vo.server.FileVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * Created by zhouyang on 14-1-16.
 */

public class FileUploadTask extends AsyncTask<String, Integer, String> {

    public static final int FROM_CHAT = 0;
    public static final int FROM_GROUP_CHAT = 1;
    public static final int FROM_PUBLIC_CHAT = 2;
    public static final int FROM_HOME_CHAT = 3;
    public static final int FROM_FRIEND_SELECT_ACTIVITY = 4;
    public static final int FROM_GroupChatSetting_ACTIVITY = 5;

    public static final String FILE_TYPE_VOICE = "voice";
    public static final String FILE_TYPE_VIDEO = "video";
    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_GROUP_IMAGE = "groupImage";
    private final int from;
    private final HashMap<String, Object> paramsMap;
    private String filePath;

    public FileUploadTask(String filePath, int from, HashMap<String,Object> paramsMap) {
        this.filePath = filePath;
        this.from = from;
        this.paramsMap = paramsMap;
    }

    /**
     * Override this method to perform a computation on a background thread. The specified parameters are the parameters passed to {@link #execute} by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String source = params[1];
        String uid = params[2];
        String resultStr = null;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        PostMethod filePost = null;
        FileVo fileVo = null;
        try {
            Part[] parts = {new FilePart("file", file), new StringPart("source", source), new StringPart("uid", uid)};
            filePost = new PostMethod(url);
            filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(filePost);
            if (status == 200) {
                byte[] body = filePost.getResponseBody();
                resultStr = new String(body);
                if (TextUtils.isEmpty(resultStr)) {
                    return resultStr;
                }
                MyLogger.commLog().i(resultStr);
                ResultVo rVo = ResultParser.parseJSON(resultStr, ResultVo.class);
                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        fileVo = ResultParser.parseJSON(rVo.getMsg().toString(), FileVo.class);
                    } else {
                        Utils.showToast(MarketApp.app, rVo.getErrmsg());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            int type = -1;
            if (source.equals(FILE_TYPE_VOICE)) {
                type = 1;
            } else if (source.equals(FILE_TYPE_VIDEO)) {
                type = 2;
            } else if (source.equals(FILE_TYPE_IMAGE)) {
                type = 3;
            }
            Message message = null;
            switch (from) {
                case FROM_CHAT:
                    message = ChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_NINE);
                    break;
                case FROM_HOME_CHAT:
                    message = HomePageFragment.handler.obtainMessage(MarketApp.HANDLERMESS_NINE);
                    break;
                case FROM_PUBLIC_CHAT:
                    message = PublicChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_NINE);
                    break;
                case FROM_GROUP_CHAT:
                    message = GroupChatActivity.handler.obtainMessage(MarketApp.HANDLERMESS_NINE);
                    break;
                case FROM_FRIEND_SELECT_ACTIVITY:
                    message = FriendSelectActivity.handler.obtainMessage(MarketApp.HANDLERMESS_NINE);
                    if (paramsMap!=null && paramsMap.get("gid")!=null) {
                        HashMap map = new HashMap();
                        map.put("gid",paramsMap.get("gid"));
                        map.put("file",fileVo);
                        message.obj = map;
                        message.sendToTarget();
                    }
                    filePost.releaseConnection();
                    return resultStr;
            }
            if (message != null) {
                message.arg1 = type;
                if (paramsMap!=null && paramsMap.get("id")!=null) {
                    message.arg2 = Integer.parseInt(paramsMap.get("id").toString());
                }
                message.obj = fileVo;
                message.sendToTarget();
            }
            filePost.releaseConnection();
        }
        return resultStr;
    }
}