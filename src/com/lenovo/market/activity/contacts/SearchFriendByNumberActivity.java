package com.lenovo.market.activity.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.FriendVoAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.PageDateVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 搜好友
 * 
 * @author zhouyang
 * 
 */
public class SearchFriendByNumberActivity extends BaseActivity implements OnClickListener {

    public static Handler mHandler;

    private EditText editText;
    private ListView listview_;
    private ArrayList<FriendMesVo> friends;
    private FriendInfoDBHelper friendDB;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_search_friend_by_number);
        setTitleBarText(R.string.search_number_title);
        setTitleBarRightBtnText(R.string.square_number_find_btn);
        setTitleBarLeftBtnText();
        friendDB = new FriendInfoDBHelper();
    }

    @Override
    protected void findViewById() {
        editText = (EditText) findViewById(R.id.search_input_et);
        listview_ = (ListView) findViewById(R.id.search_listview);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);

        listview_.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), FriendDetailsActivity.class);
                FriendMesVo friend = (FriendMesVo) adapterView.getAdapter().getItem(position);
                FriendMesVo friend2 = friendDB.getFriend(friend.getFriendAccount());
                if (null != friend2) {
                    intent.putExtra(FriendDetailsActivity.DETAILED, 2);
                } else {
                    intent.putExtra(FriendDetailsActivity.DETAILED, 0);
                }
                intent.putExtra(MarketApp.FRIEND, friend);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.btn_right:
            findFriendList();
            break;
        }
    }

    public void findFriendList() {
        String num = editText.getText().toString().trim();
        if (null != friends && friends.size() > 0) {
            friends.clear();
            FriendVoAdapter adapter = (FriendVoAdapter) listview_.getAdapter();
            adapter.notifyDataSetChanged();
        }
        if (TextUtils.isEmpty(num)) {
            Utils.showToast(this, "输入不能为空！");
            return;
        }

        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("keystr", num);
        maps.put("currentPageNO", 1);
        maps.put("pageSize", 10000);
        boolean startTask = NetUtils.startTask(new TaskListener() {

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
                        friends = pageDataVo.getDateList();
                        if (null != friends && friends.size() > 0) {
                            Collections.sort(friends);
                            FriendVoAdapter adapter = new FriendVoAdapter(SearchFriendByNumberActivity.this,R.drawable.ic_single_chat,friends, listview_);
                            listview_.setAdapter(adapter);
                        } else {
                            Utils.showToast(context, "无法查找到此号码");
                        }

                    } else {
                        Utils.showToast(context, "无法查找到此号码");
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null)
                    pd.dismiss();
            }
        }, maps, MarketApp.FIND_FRIEND_LIST_METHODNAME, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_24);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在查找……");
            pd.show();
        }
    }
}
