//package com.lenovo.xjpsd.appinterface;
//
//import android.content.Context;
//import android.webkit.JavascriptInterface;
//
//import com.google.gson.reflect.TypeToken;
//import com.lenovo.xjpsd.model.ResultModel;
//import com.lenovo.xjpsd.model.UserModel;
//import com.lenovo.xjpsd.net.ResultParser;
//
//public class JavaScriptInterface {
//
//    private Context context;
//
//    public JavaScriptInterface(Context context) {
//        super();
//        this.context = context;
//    }
//
//    @JavascriptInterface
//    public void login(String s) {
//        System.out.println("sLoginJSON=" + s);
//        if (s.contains("\"data\":\"\"")) {
//            s = s.replace("\"data\":\"\"", "\"data\":[]");
//        } else {
//            s = s.replace("\"data\":", "\"data\":[");
//            s = s.replace("},", "}],");
//        }
//        TypeToken<ResultModel<UserModel>> typeToken = new TypeToken<ResultModel<UserModel>>() {
//        };
//        ResultModel<UserModel> rm = ResultParser.parseJSON(s, typeToken);
//        if (rm.getStatus().equals("success")) {
//            if (rm.getData().size() > 0) {
//                CommonUtils.UMODEL = rm.getData().get(0);
//            }
//        }
//    }
// }
