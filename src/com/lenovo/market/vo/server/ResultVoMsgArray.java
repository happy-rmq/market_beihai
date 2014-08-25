package com.lenovo.market.vo.server;

/**
 * 服务器响应结果
 *
 * @author muqiang
 */
public class ResultVoMsgArray<T> {

    private String result;//结果，必选
    private String errcode;//错误代码，可选
    private String errmsg;//错误信息，可选
    private T msg;//提示信息，可选

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }
}
