package com.lenovo.market.activity.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.circle.friends.PublicAccountDetailsActivity;
import com.lenovo.market.adapter.FriendVoAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.SideBarView;
import com.lenovo.market.view.SideBarView.OnTouchingLetterChangedListener;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.PageDateVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 公众账号
 *
 * @author zhouyang
 */
public class MarketingAccountActivity extends BaseActivity implements OnTouchingLetterChangedListener, OnClickListener {

    public ArrayList<FriendMesVo> friends;
    
    private Handler handler;
    private ListView lvShow;
    private TextView overlay;
    private SideBarView myView;
    private FriendVoAdapter adapter;
    private FriendInfoDBHelper friendInfoDB_;
    private OverlayThread overlayThread;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_contacts_market_account);
        setTitleBarText(R.string.marketing_account);
        setTitleBarLeftBtnText();
        friendInfoDB_ = new FriendInfoDBHelper();
        overlayThread = new OverlayThread();
        handler = new Handler();
    }

    @Override
    protected void findViewById() {
        lvShow = (ListView) findViewById(R.id.lvShow);
        myView = (SideBarView) findViewById(R.id.myView);
        overlay = (TextView) findViewById(R.id.tvLetter);

        lvShow.setTextFilterEnabled(true);
        overlay.setVisibility(View.INVISIBLE);

        View headerLayout = getLayoutInflater().inflate(R.layout.layout_contacts_marketing_header, null);
        EditText search = (EditText) headerLayout.findViewById(R.id.et_search);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        lvShow.addHeaderView(headerLayout);

        friends = new ArrayList<FriendMesVo>();
        adapter = new FriendVoAdapter(MarketingAccountActivity.this,R.drawable.ic_publicchat,friends, lvShow);
        lvShow.setAdapter(adapter);
        ArrayList<FriendMesVo> friendAll = friendInfoDB_.getFriendAll(MarketApp.FRIEND_TYPE_PUBLIC);
        String opreaId = AdminUtils.getOperationalUid(this);
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            if (friendAll == null) {
                pd = Utils.createProgressDialog(this, "正在获取公众账号列表");
                pd.show();
            } else {
                friends.addAll(friendAll);
                for (FriendMesVo vo : friends) {
                    if (vo != null && vo.getFriendId() != null && opreaId != null) {
                        if (vo.getFriendId().equals(opreaId)) {
                            friends.remove(vo);
                            break;
                        }
                    }
                }
                Collections.sort(friends);
                adapter.notifyDataSetChanged();
            }
            initFriends();
        } else {
            Collections.sort(friends);
            friends.addAll(friendAll);
            for (FriendMesVo vo : friends) {
                if (vo != null && vo.getFriendId() != null && opreaId != null) {
                    if (vo.getFriendId().equals(opreaId)) {
                        friends.remove(vo);
                        break;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        myView.setOnTouchingLetterChangedListener(this);
        lvShow.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), PublicAccountDetailsActivity.class);
                FriendMesVo friend = (FriendMesVo) adapterView.getAdapter().getItem(position);
                friend.setIsFriend("true");
                intent.putExtra(MarketApp.FRIEND, friend);
                intent.putExtra("IsVisible", 2);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 200) {
            String friendAccount = data.getStringExtra(MarketApp.FRIEND);
            if (!TextUtils.isEmpty(friendAccount)) {
                String tempAccount = null;
                for (FriendMesVo vo : friends) {
                    tempAccount = vo.getFriendAccount();
                    if (null != tempAccount && tempAccount.equals(friendAccount)) {
                        friends.remove(vo);
                        Collections.sort(friends);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void initFriends() {

        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("keystr", null);
        maps.put("currentPageNO", 1);
        maps.put("pageSize", 5000);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                if (pd != null)
                    pd.dismiss();
            }

            @Override
            public void onComplete(String resulte) {
                if (pd != null)
                    pd.dismiss();
                ResultVo rVo = (ResultVo) ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    log.d("result--->" + result);
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        TypeToken<PageDateVo<FriendMesVo>> typeToken = new TypeToken<PageDateVo<FriendMesVo>>() {
                        };
                        PageDateVo<FriendMesVo> pageDataVo = ResultParser.parseJSON(rVo.getMsg().toString(), typeToken);
                        ArrayList<FriendMesVo> dataList = pageDataVo.getDateList();
                        if (null != dataList) {
                            friends.clear();
                            friends.addAll(dataList);
                            String opreaId = AdminUtils.getOperationalUid(context);
                            for (FriendMesVo vo : friends) {
                                if (vo != null && vo.getFriendId() != null && opreaId != null) {
                                    if (vo.getFriendId().equals(opreaId)) {
                                        friends.remove(vo);
                                        break;
                                    }
                                }
                            }
                            Collections.sort(friends);
                            adapter.notifyDataSetChanged();
                            // 保存好友信息到本地数据库
                            for (FriendMesVo vo : friends) {
                                vo.setFriendType(2);// 设置好友类型为普通好友
                            }
                            friendInfoDB_.saveFriendList(friends);
                        }
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.USER_PUB_LIST_METHODNAME, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_27);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        overlay.setText(s);
        overlay.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1000);
        if (alphaIndexer(s) > 0) {
            int position = alphaIndexer(s);
            lvShow.setSelection(position);
        }
    }

    private class OverlayThread implements Runnable {

        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    private int alphaIndexer(String s) {
        int position = -1;
        if (null == friends)
            return position;
        Locale locale = Locale.getDefault();
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getPy().toUpperCase(locale).startsWith(s)) {
                position = i;
                break;
            }
        }
        return position + 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
        }
    }
}
