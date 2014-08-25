package com.lenovo.market.listener;

public abstract class TaskListener {
    
    public abstract void onComplete(String resultstr);

    public void onComplete(String resultstr, int type) {
        onComplete(resultstr);
    }

    public abstract void onError(int errorCode, String message);

    public abstract void onCancel();
}
