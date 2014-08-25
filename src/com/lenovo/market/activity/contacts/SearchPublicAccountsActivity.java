package com.lenovo.market.activity.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.text.TextUtils;
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
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.PageDateVo;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 查找公共账号
 * 
 * @author zhouyang
 * 
 */
public class SearchPublicAccountsActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private EditText editText;
    private ListView listview_;
    private ArrayList<FriendMesVo> friends;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_search_friend_by_number);
        setTitleBarText("找公众账号");
        setTitleBarRightBtnText(R.string.square_number_find_btn);
        setTitleBarLeftBtnText();
    }

    @Override
    protected void findViewById() {
        TextView text = (TextView) findViewById(R.id.search_explain);
        text.setText(R.string.marketing_account);
        editText = (EditText) findViewById(R.id.search_input_et);
        listview_ = (ListView) findViewById(R.id.search_listview);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);
        listview_.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log.d(resultCode);
        if (resultCode == 200) {
            findPublicAccountList();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.btn_right:
            findPublicAccountList();
            break;
        }
    }

    public void findPublicAccountList() {
        String num = editText.getText().toString().trim();
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
                            FriendVoAdapter adapter = new FriendVoAdapter(SearchPublicAccountsActivity.this,R.drawable.ic_publicchat,friends, listview_);
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
        }, maps, MarketApp.FIND_PUB_LIST_METHODNAME, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_26);
        if (startTask) {
            pd = Utils.createProgressDialog(this, "正在查找……");
            pd.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), PublicAccountDetailsActivity.class);
        FriendMesVo friend = (FriendMesVo) parent.getAdapter().getItem(position);
        intent.putExtra(MarketApp.FRIEND, friend);
        intent.putExtra("IsVisible", 2);
        startActivityForResult(intent, 0);
    }
}
