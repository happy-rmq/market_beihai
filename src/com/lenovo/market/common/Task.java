package com.lenovo.market.common;

import java.util.HashMap;

import org.apache.http.conn.ClientConnectionManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.service.MainService;
import com.lenovo.market.vo.local.RequestVo;

public class Task {
    
    private int taskID; // 唯一的任务id。
    private int taskType; // 任务类型 默认solomo 类型。
    private boolean isCancel; // 标记任务是否是取消状态
    private ProgressDialog dialog; // 加载进度条默认没有进度条
    private ClientConnectionManager ccm; // 执行网络操作的ConnectionManager对象用于取消任务。
    private TaskListener onTaskCompleteListener; // 任务完成的回调
    private HashMap<String, Object> parameterMap = new HashMap<String, Object>(6); // 参数的json形式。
    private RequestVo reqVo;
    private String mUrl;

    public Task(TaskListener onTaskCompleteListener, String url, int taskID) {
        this.onTaskCompleteListener = onTaskCompleteListener;
        this.taskID = taskID;
        this.mUrl = url;
        this.taskType = TaskConstant.TaskType.TYPE_SOLOMO;
    }

    public RequestVo getReqVo() {
        return reqVo;
    }

    public void setReqVo(RequestVo reqVo) {
        this.reqVo = reqVo;
    }

    public String getmUrl() {
        return mUrl;
    }

    public HashMap<String, Object> getParameterMap() {
        return parameterMap;
    }

    public ProgressDialog getLodingDialog() {
        return dialog;
    }

    public void setLodingDialog(Context context, String msg) {
        this.dialog = creatDialog(context, msg, taskID);
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public void setStringParameter(String key, String value) {

        this.parameterMap.put(key, value);
    }

    public void setIntParameter(String key, int value) {

        this.parameterMap.put(key, value);
    }

    public void setLongParameter(String key, long value) {

        this.parameterMap.put(key, value);
    }

    public void setDoubleParameter(String key, double value) {

        this.parameterMap.put(key, value);
    }

    public void setBooleanParameter(String key, boolean value) {

        this.parameterMap.put(key, value);
    }

    public void setObjectParameter(String key, Object value) {

        this.parameterMap.put(key, value);
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public ClientConnectionManager getCcm() {
        return ccm;
    }

    public void setCcm(ClientConnectionManager ccm) {
        this.ccm = ccm;
    }

    public TaskListener getListener() {
        return onTaskCompleteListener;
    }

    public int getTaskID() {
        return taskID;
    }

    public void executeTask() {

        synchronized (MainService.sTaskMap) {
            MainService.sTaskMap.put(taskID, this);
        }
        System.out.println("任务个数：" + MainService.sTaskMap.size());
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        MainService.addNewTask(this.taskID);
    }

    public ProgressDialog creatDialog(Context context, String msg, final int taskID) {
        ProgressDialog gPDialog = new ProgressDialog(context);
        gPDialog.setMessage(msg);
        gPDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                MainService.cancelTask(taskID);
            }
        });
        return gPDialog;
    }
}
