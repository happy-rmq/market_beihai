package com.lenovo.market.activity.setting.friendscircle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.FriendSquareAdapter;
import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.FriendSquareDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.view.PullDownView;
import com.lenovo.market.view.PullDownView.OnPullDownListener;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.MFriendZoneCommentVo;
import com.lenovo.market.vo.server.MFriendZoneImageVo;
import com.lenovo.market.vo.server.MFriendZoneTopicVo;
import com.lenovo.market.vo.server.ResultFriendVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * 朋友圈
 * 
 * @author muqiang
 * 
 */
@SuppressWarnings("unchecked")
public class FriendsCircleActivity extends BaseActivity implements OnPullDownListener, OnClickListener {

    public static Handler handler;
    public static ArrayList<MFriendZoneTopicVo> friendSquareList;
    public static FriendSquareAdapter friendSquareAdapter;
    public static PullDownView mPullDownView;
    public static int DBindex;// 上拉刷新时记录从数据库什么位置查询
    public static Integer listIndex;

    private static LinearLayout friend_square_bottom_ll;
    private static EditText friend_square_bottom_active;
    private ListView friend_square_lv;
    private FriendSquareDBHelper friendSquareDb;
    private FriendInfoDBHelper fiDB;
    private SharedPreferences sp;
    private String lastTime;
    private Button friend_square_bottom_key;
    private int totalCount;// 记录数据库有多少信息
    private boolean blean;
    private String account;
    private ProgressDialog pd;

    @Override
    protected void setContentView() {
        // TODO Auto-generated method stub
        setContentView(R.layout.layout_friend_square);
        setTitleBarText(R.string.circle_Friends);
        setTitleBarRightBtnText("发消息");
        setTitleBarLeftBtnText();
        pd = Utils.createProgressDialog(this, "正在加载数据中......");
        pd.show();
        sp = getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
        friendSquareDb = new FriendSquareDBHelper();
        fiDB = new FriendInfoDBHelper();
        handler = new FriendSquareHandler(this);
    }

    @Override
    protected void findViewById() {
        friend_square_bottom_ll = (LinearLayout) findViewById(R.id.friend_square_bottom_ll);
        friend_square_bottom_active = (EditText) findViewById(R.id.friend_square_bottom_active);
        friend_square_bottom_key = (Button) findViewById(R.id.friend_square_bottom_key);
        mPullDownView = (PullDownView) findViewById(R.id.friend_square_lv);
        friend_square_lv = mPullDownView.getListView();
        friend_square_lv.setVerticalScrollBarEnabled(false);
        totalCount = DBindex;
        if (totalCount <= 0) {
            totalCount = MarketApp.COUNT;
        }
        if (TextUtils.isEmpty(AdminUtils.getUserInfo(this).getAccount())) {
            SharedPreferences sp = getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
            account = sp.getString(MarketApp.LOGIN_ACCOUNT, "");
        } else {
            account = AdminUtils.getUserInfo(this).getAccount();
        }

        friendSquareList = friendSquareDb.getFriendSquareList(totalCount, account);
        if (friendSquareList == null || friendSquareList.size() == 0) {
            friend_square_lv.setDividerHeight(0);
            Editor editor = sp.edit();
            editor.putString(AdminUtils.getUserInfo(this).getAccount(), "all");
            editor.commit();
        } else {
            for (int i = 0; i < friendSquareList.size(); i++) {
                ArrayList<MFriendZoneCommentVo> friendSquareCommenList = friendSquareDb.getFriendSquareCommenList(friendSquareList.get(i).getId(), account);
                if (friendSquareCommenList != null && friendSquareCommenList.size() > 0) {
                    friendSquareList.get(i).getComments().addAll(friendSquareCommenList);
                }
                ArrayList<MFriendZoneImageVo> friendSquareImg = friendSquareDb.getFriendSquareImg(friendSquareList.get(i).getId(), account);
                if (friendSquareImg != null && friendSquareImg.size() > 0) {
                    friendSquareList.get(i).getImages().addAll(friendSquareImg);
                }
            }
        }

        // 隐藏 并禁用尾部
        mPullDownView.setHideFooter();
        mPullDownView.setOnPullDownListener(this);
        friend_square_lv.setCacheColorHint(getResources().getColor(R.color.transparent));
        friend_square_lv.setSelector(R.color.transparent);

        friendSquareAdapter = new FriendSquareAdapter(this, friendSquareList, friend_square_lv, account);
        friend_square_lv.setAdapter(friendSquareAdapter);
        blean = true;

        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            getData();
        } else {
            if (null != pd && pd.isShowing()) {
                pd.cancel();
                pd = null;
            }
        }
        setListener();
    }

    @Override
    protected void setListener() {
        friend_square_bottom_key.setOnClickListener(this);
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);
        btn_right_.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, SendFriendSquareActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_right:
            Intent intent = new Intent(this, DialogOneActivity.class);
            startActivity(intent);
            break;
        case R.id.btn_left:// 返回
            finish();
            break;
        case R.id.friend_square_bottom_key:
            if (!MarketApp.network_available) {
                Utils.showToast(this, "网络不可用,请连接网络！");
                return;
            }
            final String content = friend_square_bottom_active.getText().toString().trim();
            final long currentTimeMillis = System.currentTimeMillis();
            final MFriendZoneCommentVo mVO = new MFriendZoneCommentVo(friendSquareList.get(listIndex).getId(), content, MarketApp.PID, "2", AdminUtils.getUserInfo(this).getAccount(), currentTimeMillis + "");
            final String id = Utils.getDeviceUUID();
            mVO.setId(id);
            mVO.setLoginUser(AdminUtils.getUserInfo(this).getAccount());
            friendSquareDb.insertCommentMessage(mVO);
            LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
            maps.put("id", id);
            maps.put("topicId", friendSquareList.get(listIndex).getId());
            maps.put("content", content);
            maps.put("pid", MarketApp.PID);
            maps.put("type", "2");
            maps.put("createUser", AdminUtils.getUserInfo(this).getAccount());
            NetUtils.startTask(new TaskListener() {

                @Override
                public void onError(int errorCode, String message) {
                }

                @Override
                public void onComplete(String resulte) {
                    ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                    if (rVo != null) {
                        String result = rVo.getResult();
                        if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                            Utils.showToast(context, result);
                            friendSquareList.get(listIndex).getComments().add(mVO);
                            ArrayList<FriendMesVo> friendAll = fiDB.getFriendAll("1");
                            for (int i = 0; i < friendAll.size(); i++) {
                                org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message(Utils.getJidFromUsername(friendAll.get(i).getFriendAccount()), org.jivesoftware.smack.packet.Message.Type.chat);
                                MFriendZoneCommentVo mVO = new MFriendZoneCommentVo(friendSquareList.get(listIndex).getId(), content, MarketApp.PID, "2", AdminUtils.getUserInfo(context).getAccount(), currentTimeMillis + "");
                                mVO.setId(id);
                                Gson gson = new Gson();
                                String json = gson.toJson(mVO);
                                MsgXmlVo mxVo = new MsgXmlVo();
                                mxVo.setMsgType(MarketApp.SEND_TEXT);
                                mxVo.setContent(json);
                                mxVo.setTargetType("2");
                                mxVo.setCreateTime(mVO.getCreateTime());
                                String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_TEXT);
                                message.setBody(sendxml);
                                if (XmppUtils.getInstance().getConnection() != null) {
                                    try {
                                        XmppUtils.getInstance().getConnection().sendPacket(message);
                                    } catch (Exception e) {
                                        Utils.showToast(context, "连接已经断开,正在重连,请稍后再试...");
                                        CommonUtil.ConnectionXmpp(context);
                                        e.printStackTrace();
                                    }
                                    friend_square_bottom_active.getEditableText().clear();
                                } else {
                                    Utils.showToast(context, "连接已经断开,正在重连,请稍后再试...");
                                    CommonUtil.ConnectionXmpp(context);
                                }
                            }
                        } else {
                            Utils.showToast(context, result);
                        }
                        friendSquareAdapter.notifyDataSetChanged();
                        // 诉它数据加载完毕;
                        mPullDownView.notifyDidMore();
                        // 告诉它更新完毕
                        mPullDownView.RefreshComplete();
                    }
                }

                @Override
                public void onCancel() {

                }
            }, maps, MarketApp.FRIENDSQUARE_SENDCOMMENT, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_15);
            friend_square_bottom_ll.setVisibility(View.GONE);
            break;
        }
    }

    static class FriendSquareHandler extends Handler {
        WeakReference<FriendsCircleActivity> mActivity;

        public FriendSquareHandler(FriendsCircleActivity activity) {
            mActivity = new WeakReference<FriendsCircleActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FriendsCircleActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
            case MarketApp.HANDLERMESS_ZERO:
                ArrayList<MFriendZoneTopicVo> mTvs = (ArrayList<MFriendZoneTopicVo>) msg.obj;
                if (mTvs != null && mTvs.size() > 0) {
                    activity.totalCount = activity.totalCount + mTvs.size();
                    for (int i = 0; i < mTvs.size(); i++) {
                        boolean b = false;
                        if (friendSquareList.size() > 0) {
                            for (int j = 0; j < friendSquareList.size(); j++) {
                                for (int z = 0; z < mTvs.get(i).getComments().size(); z++) {
                                    if (friendSquareList.get(j).getId().contains(mTvs.get(i).getComments().get(z).getTopicId())) {
                                        b = true;
                                        break;
                                    }
                                }
                            }
                            if (!b) {
                                friendSquareList.add(i, mTvs.get(i));
                            }
                        } else {
                            friendSquareList.add(i, mTvs.get(i));
                        }
                    }
                }
                if (null != activity.pd && activity.pd.isShowing()) {
                    activity.pd.cancel();
                    activity.pd = null;
                }
                break;
            case MarketApp.HANDLERMESS_ONE:
                MFriendZoneTopicVo mv = (MFriendZoneTopicVo) msg.obj;
                friendSquareList.add(0, mv);
                break;
            case MarketApp.HANDLERMESS_TWO:// openfire
                String json = (String) msg.obj;
                MFriendZoneCommentVo mVo = (MFriendZoneCommentVo) ResultParser.parseJSON(json, MFriendZoneCommentVo.class);
                // 查询数据库是否有这条主消息
                boolean friendSquare = activity.friendSquareDb.getFriendSquareIS(mVo.getTopicId());
                boolean b = false;
                if (friendSquare && friendSquareList.size() > 0) {
                    for (int i = 0; i < friendSquareList.size(); i++) {
                        if (friendSquareList.get(i).getComments().size() > 0) {
                            for (int j = 0; j < friendSquareList.get(i).getComments().size(); j++) {
                                // 代码重复 需要改进
                                if (friendSquareList.get(i).getComments().get(j).getId().equals(mVo.getId())) {
                                    b = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!b) {
                        mVo.setLoginUser(AdminUtils.getUserInfo(activity).getAccount());
                        long id = activity.friendSquareDb.insertCommentMessage(mVo);
                        if (id > 0) {
                            for (int i = 0; i < friendSquareList.size(); i++) {
                                if (friendSquareList.get(i).getId().equals(mVo.getTopicId())) {
                                    boolean bn = false;
                                    for (int j = 0; j < friendSquareList.get(i).getComments().size(); j++) {
                                        if (friendSquareList.get(i).getComments().get(j).getType().equals("1") && friendSquareList.get(i).getComments().get(j).getCreateUser().equals(mVo.getCreateUser())) {
                                            bn = true;
                                        }
                                    }
                                    if (!bn) {
                                        friendSquareList.get(i).getComments().add(mVo);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case MarketApp.HANDLERMESS_THREE:
                activity.totalCount = activity.totalCount + MarketApp.COUNT;
                if (TextUtils.isEmpty(activity.account)) {
                    if (TextUtils.isEmpty(AdminUtils.getUserInfo(activity).getAccount())) {
                        SharedPreferences sp = activity.getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, Context.MODE_PRIVATE);
                        activity.account = sp.getString(MarketApp.LOGIN_ACCOUNT, "");
                    } else {
                        activity.account = AdminUtils.getUserInfo(activity).getAccount();
                    }
                }
                ArrayList<MFriendZoneTopicVo> friendSquareLists = activity.friendSquareDb.getFriendSquareList(activity.totalCount, activity.account);
                if (friendSquareLists != null && friendSquareLists.size() > 0) {
                    // 显示并启用自动获取更多
                    mPullDownView.setShowFooter();
                    activity.friend_square_lv.setDividerHeight(1);
                    for (int i = 0; i < friendSquareLists.size(); i++) {
                        ArrayList<MFriendZoneCommentVo> friendSquareCommenList = activity.friendSquareDb.getFriendSquareCommenList(friendSquareLists.get(i).getId(), activity.account);
                        if (friendSquareCommenList != null && friendSquareCommenList.size() > 0) {
                            friendSquareLists.get(i).getComments().addAll(friendSquareCommenList);
                        }
                        ArrayList<MFriendZoneImageVo> friendSquareImg = activity.friendSquareDb.getFriendSquareImg(friendSquareLists.get(i).getId(), activity.account);
                        if (friendSquareImg != null && friendSquareImg.size() > 0) {
                            friendSquareLists.get(i).getImages().addAll(friendSquareImg);
                        }
                    }
                    friendSquareList.addAll(friendSquareLists);
                } else {
                    // 隐藏 并禁用尾部
                    mPullDownView.setHideFooter();
                }
                break;
            case MarketApp.HANDLERMESS_FOUR:
                break;
            }

            int count = activity.friendSquareDb.getFriendSquareCount();
            if (friendSquareList.size() < count) {
                // 显示并启用自动获取更多
                mPullDownView.setShowFooter();
            } else {
                // 隐藏 并禁用尾部
                mPullDownView.setHideFooter();
            }
            if (friendSquareList.size() > 0) {
                activity.friend_square_lv.setDividerHeight(1);
            } else {
                activity.friend_square_lv.setDividerHeight(0);
            }

            friendSquareAdapter.notifyDataSetChanged();
            // 诉它数据加载完毕;
            mPullDownView.notifyDidMore();
            // 告诉它更新完毕
            mPullDownView.RefreshComplete();
        }
    }

    public static void getVisibility(int visible) {
        friend_square_bottom_ll.setVisibility(visible);
    }

    public static void getContent(String content) {
        friend_square_bottom_active.setHint(content);
    }

    @Override
    public void onRefresh() {
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            getData();
        } else {
            // 告诉它更新完毕
            mPullDownView.RefreshComplete();
        }
    }

    @Override
    public void onMore() {
        handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
    }

    private void getData() {
        lastTime = sp.getString(AdminUtils.getUserInfo(context).getAccount(), "");
        if (!blean) {
            if (lastTime.equals("all")) {
                lastTime = "noself";
            }
        } else {
            if (lastTime.equals("all") && friendSquareList.size() > 0) {
                lastTime = "noself";
            }
        }

        if (!lastTime.equals("all")) {
            if (!lastTime.equals("noself")) {
                // 更新数据
                LinkedHashMap<String, Object> mapUpd = new LinkedHashMap<String, Object>();
                mapUpd.put("createUser", AdminUtils.getUserInfo(context).getAccount());
                mapUpd.put("dateTime", lastTime);
                NetUtils.startTask(new TaskListener() {

                    @Override
                    public void onError(int errorCode, String message) {
                        // if (pd != null)
                        // pd.dismiss();
                    }

                    @Override
                    public void onComplete(String resulte) {
                        // if (pd != null)
                        // pd.dismiss();
                        ResultFriendVo rVo = (ResultFriendVo) ResultParser.parseJSON(resulte, ResultFriendVo.class);
                        ArrayList<MFriendZoneTopicVo> result = null;
                        if (rVo != null && rVo.getResult().equals("succ")) {
                            result = rVo.getDatas();
                            for (MFriendZoneTopicVo mVO : result) {
                                mVO.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                                if (!TextUtils.isEmpty(mVO.getId())) {
                                    friendSquareDb.delCMessage(mVO.getId());
                                    friendSquareDb.delCommentMessage(mVO.getId());
                                    for (int i = 0; i < friendSquareList.size(); i++) {
                                        if (friendSquareList.get(i).getId().equals(mVO.getId())) {
                                            friendSquareList.remove(i);
                                            break;
                                        }
                                    }
                                } else if (mVO.getComments() != null && mVO.getComments().size() > 0) {
                                    for (int i = 0; i < mVO.getComments().size(); i++) {
                                        if (null == mVO.getComments().get(i)) {
                                            continue;
                                        }
                                        if (null != mVO.getComments().get(i).getPid() && !mVO.getComments().get(i).getPid().equals("delete")) {
                                            // 添加评论
                                            mVO.getComments().get(i).setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                                            long id = friendSquareDb.insertCommentMessage(mVO.getComments().get(i));
                                            if (id > 0) {
                                                for (int j = 0; j < friendSquareList.size(); j++) {
                                                    if (friendSquareList.get(j).getId().equals(mVO.getComments().get(i).getTopicId())) {
                                                        friendSquareList.get(j).getComments().add(mVO.getComments().get(i));
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            for (int j = 0; j < friendSquareList.size(); j++) {
                                                if (friendSquareList.get(j).getId().equals(mVO.getComments().get(i).getTopicId())) {
                                                    boolean b = friendSquareDb.delCommentMessage(mVO.getComments().get(i).getId());
                                                    if (b) {
                                                        for (int k = 0; k < friendSquareList.get(j).getComments().size(); k++) {
                                                            if (friendSquareList.get(j).getComments().get(k).getId().equals(mVO.getComments().get(i).getId())) {
                                                                friendSquareList.get(j).getComments().remove(k);
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Message updateMsg = new Message();
                        updateMsg.what = MarketApp.HANDLERMESS_FOUR;
                        updateMsg.obj = result;
                        handler.sendMessage(updateMsg);
                    }

                    @Override
                    public void onCancel() {
                        // if (pd != null)
                        // pd.dismiss();
                    }
                }, mapUpd, MarketApp.FRIENDSQUARE_UPDMESSAGE, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_18);
            }
        }

        // 添加新的数据
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("dateTime", lastTime);
        maps.put("createUser", AdminUtils.getUserInfo(context).getAccount());
        maps.put("currentPage", 1);
        maps.put("pageSize", "10000");
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                if (null != pd && pd.isShowing()) {
                    pd.cancel();
                    pd = null;
                }
            }

            @Override
            public void onComplete(String resulte) {
                ResultFriendVo rVo = (ResultFriendVo) ResultParser.parseJSON(resulte, ResultFriendVo.class);
                ArrayList<MFriendZoneTopicVo> result = null;
                if (rVo != null && rVo.getResult().equals("succ")) {
                    if (rVo != null) {
                        result = rVo.getDatas();
                        for (MFriendZoneTopicVo mVO : result) {
                            mVO.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                            friendSquareDb.insertNewMessage(mVO);
                            if (mVO.getComments() != null && mVO.getComments().size() > 0) {
                                for (int i = 0; i < mVO.getComments().size(); i++) {
                                    MFriendZoneCommentVo vo = mVO.getComments().get(i);
                                    vo.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                                    friendSquareDb.insertCommentMessage(vo);
                                }
                            }
                            if (mVO.getImages() != null && mVO.getImages().size() > 0) {
                                for (int i = 0; i < mVO.getImages().size(); i++) {
                                    MFriendZoneImageVo iv = mVO.getImages().get(i);
                                    iv.setLoginUser(AdminUtils.getUserInfo(context).getAccount());
                                    friendSquareDb.insertFriendSquareImg(iv);
                                }
                            }
                        }
                    }
                    // 是否记录账号
                    if (result != null && result.size() > 0) {
                        Editor editor = sp.edit();
                        editor.putString(AdminUtils.getUserInfo(context).getAccount(), result.get(0).getCreateTime());
                        editor.commit();
                    }
                } else {
                    if (friendSquareList.size() == 0) {
                        result = new ArrayList<MFriendZoneTopicVo>();
                        MFriendZoneTopicVo mVO = new MFriendZoneTopicVo("", "", "", "", "", "", "", "");
                        result.add(mVO);
                    }
                }
                blean = false;
                Message updateMsg = new Message();
                updateMsg.what = MarketApp.HANDLERMESS_ZERO;
                updateMsg.obj = result;
                handler.sendMessage(updateMsg);
            }

            @Override
            public void onCancel() {
                if (null != pd && pd.isShowing()) {
                    pd.cancel();
                    pd = null;
                }
            }
        }, maps, MarketApp.FRIENDSQUARE_GETMESSAGE, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_14);
    }
}
