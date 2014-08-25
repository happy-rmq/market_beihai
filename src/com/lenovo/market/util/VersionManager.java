package com.lenovo.market.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.GuideActivity;
import com.lenovo.market.activity.login.LoginActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.view.CustomDialog;
import com.lenovo.market.view.CustomProgressDialog;
import com.lenovo.market.vo.server.AppInfoVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;

@SuppressLint("HandlerLeak")
public class VersionManager {

    private Context context;
    private int localVersionCode;
    // private String versionName;
    private ProgressBar mProgress;
    /**
     * 取消下载任务标志
     */
    private boolean interceptFlag;
    private int progress;
    /**
     * apk保存路径
     */
    private String savePath;
    /**
     * apk下载地址
     */
    public String apkUrl;
    /**
     * apk文件名
     */
    private String saveFileName = "lenovo-cw.apk";

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /** 更新下载进度 */
                case MarketApp.HANDLERMESS_ZERO:
                    mProgress.setProgress(progress);
                    percent.setText(progress + "%");
                    break;
                /** apk下载结束 */
                case MarketApp.HANDLERMESS_ONE:
                    if (downloadDialog.isShowing()) {
                        downloadDialog.dismiss();
                    }
                    installApk();
                    break;
            }
        }
    };
    //    private AlertDialog downloadDialog;
    private CustomProgressDialog downloadDialog;
    private TextView percent;

    public VersionManager(Context context) {
        this.context = context;
        this.localVersionCode = getLocalVersionCode(context);
        // this.versionName = getLocalVersionName(context);
        savePath = Utils.getUpdateCacheDir(context);
    }

    /**
     * @param context
     * @return 返回客户端版本名称
     */
    public static String getLocalVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            MyLogger.commLog().e(e);
        }
        return versionName;
    }

    /**
     * @param context
     * @return 返回客户端版本号，如果获取不到则返回-1
     */
    public static int getLocalVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            MyLogger.commLog().e(e);
        }
        return versionCode;
    }

    /**
     * 检查版本
     */
    public void checkVersion(final boolean isFromSplash) {
        // boolean hasNetwork = NetUtils.hasNetwork(context);
        // if (!hasNetwork) {
        // Utils.showToast(context, "网络未连接，请检查后重试！");
        // return;
        // }
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("versionCode", localVersionCode);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                intoApplication();
            }

            @Override
            public void onComplete(String resultstr) {
                ResultVo rVo = ResultParser.parseJSON(resultstr, ResultVo.class);
                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        AppInfoVo appInfoVO = ResultParser.parseJSON(rVo.getMsg().toString(), AppInfoVo.class);
                        int vCode = Integer.parseInt(appInfoVO.getVno());
                        if (vCode > localVersionCode) {
                            String url = appInfoVO.getUrl();
                            if (!TextUtils.isEmpty(url)) {
                                apkUrl = url;
                                String content = appInfoVO.getRemark();
                                String isUpdate = appInfoVO.getIsUpdate();
                                boolean isForcedUpdate = false;
                                if (isUpdate != null && isUpdate.equals("1")) {
                                    isForcedUpdate = true;
                                }
                                showCustomNoticeDialog(isFromSplash, content, isForcedUpdate);
                            }
                        } else {
                            if (isFromSplash) {
                                // 如果不需要更新并且在加载界面则走正常进入app的流程
                                intoApplication();
                            } else {
                                // 不在加载界面才会提示
                                Utils.showToast(context, "当前已是最新版本");
                            }
                        }
                    } else {
                        if (isFromSplash) {
                            intoApplication();
                        }
                    }
                }
            }

            @Override
            public void onCancel() {
            }
        }, map, MarketApp.GET_VERSION, MarketApp.APP_INFO_SERVICE, TaskConstant.GET_DATA_30);
    }

    /**
     * 进入应用
     */
    public void intoApplication() {
        // 判断是否进入向导界面
        SharedPreferences sp_guide = context.getSharedPreferences(MarketApp.GUIDESP, Context.MODE_PRIVATE);
        int versionCodeInSP = sp_guide.getInt(MarketApp.VERSION_CODE, -1);
        if (localVersionCode != versionCodeInSP) {
            sp_guide.edit().putBoolean(MarketApp.IS_GUIDED, false).commit();
        }
        boolean guided = sp_guide.getBoolean(MarketApp.IS_GUIDED, false);
        final Intent intent = new Intent();
        if (!guided) {
            intent.setClass(context, GuideActivity.class);
        } else {
            intent.setClass(context, LoginActivity.class);
            SharedPreferences sp_lenovo = context.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
            String account = sp_lenovo.getString(MarketApp.LOGIN_ACCOUNT, "");
            if (!TextUtils.isEmpty(account)) {
                UserDBHelper userDb = new UserDBHelper();
                UserVo userVo = userDb.getUserInfo(account);
                if (userVo != null) {
                    String user_account = userVo.getAccount();
                    String password = userVo.getPassword();
                    LoginUtils.login(context, user_account, password);
                    return;
                }
            }
        }
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * @param isFromSplash   是否在闪屏界面触发
     * @param content        更新内容
     * @param isForcedUpdate 是否为强制更新
     */
    public void showCustomNoticeDialog(final boolean isFromSplash, String content, boolean isForcedUpdate) {
        final CustomDialog dialog = new CustomDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("版本更新");
        dialog.setContent("检测到新版本，立即升级？");
        if (!TextUtils.isEmpty(content)) {
            dialog.setContent(content);
        }
        if (!isForcedUpdate) {
            // 强制更新的话不能有暂不更新按钮
            dialog.setLeftButton("暂不更新", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (isFromSplash) {
                        intoApplication();
                    }
                }
            });
        }
        dialog.setRightButton("立即更新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDownloadDialog(isFromSplash);
            }
        });
        dialog.show();
    }

    /**
     * 弹出下载进度条
     */
    private void showDownloadDialog(final boolean isFromSplash) {
        if (null == savePath) {
            Utils.showToast(context, "未检测到sd卡无法下载安装包到手机！");
            return;
        }

        downloadDialog = new CustomProgressDialog(context);
        mProgress = (ProgressBar) downloadDialog.findViewById(R.id.progress);
        percent = (TextView) downloadDialog.findViewById(R.id.percent);
        Button button_cancel = (Button) downloadDialog.findViewById(R.id.btn_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDialog.dismiss();
                interceptFlag = true;
                if (isFromSplash) {
                    intoApplication();
                }
            }
        });
        downloadDialog.show();

        if (!TextUtils.isEmpty(apkUrl)) {
            downloadApk();
        }
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String apkFile = savePath + File.separator + saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(MarketApp.HANDLERMESS_ONE);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    private Thread downLoadThread;

    private void downloadApk() {
        // 开启线程下载apk
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
        // 启动浏览器下载apk
        // Intent intent = new Intent();
        // intent.setAction("android.intent.action.VIEW");
        // Uri content_url = Uri.parse(apkUrl);
        // intent.setData(content_url);
        // context.startActivity(intent);
    }

    private void installApk() {
        File apkfile = new File(savePath + File.separator + saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
        Activity activity = (Activity) context;
        activity.finish();
    }

}
