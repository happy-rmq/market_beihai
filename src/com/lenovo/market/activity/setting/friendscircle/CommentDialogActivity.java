package com.lenovo.market.activity.setting.friendscircle;

import java.util.LinkedHashMap;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendSquareDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.server.ResultVo;

/**
 * 删除评论信息
 * 
 * @author muqiang
 * 
 */
public class CommentDialogActivity extends BaseActivity implements OnClickListener {

    private Button dialog_taking_bt;
    private Button dialog_select_bt;
    private Button btn_cancel;
    private TextView dialog_text;
    private int index;
    private int position;
    private FriendSquareDBHelper fsDb;

    @Override
    protected void setContentView() {
        setContentView(R.layout.alert_dialog_menu_layout);
        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        index = Integer.parseInt(getIntent().getExtras().getString("index"));
        position = Integer.parseInt(getIntent().getExtras().getString("position"));
        fsDb = new FriendSquareDBHelper();
    }

    @Override
    protected void findViewById() {
        dialog_text = (TextView) findViewById(R.id.dialog_text);
        dialog_text.setVisibility(View.VISIBLE);
        dialog_taking_bt = (Button) findViewById(R.id.dialog_taking_bt);
        dialog_taking_bt.setVisibility(View.GONE);
        dialog_select_bt = (Button) findViewById(R.id.dialog_select_bt);
        dialog_select_bt.setText("删除");
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
    }

    @Override
    protected void setListener() {
        dialog_taking_bt.setOnClickListener(this);
        dialog_select_bt.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.dialog_select_bt:
            if (FriendsCircleActivity.friendSquareList != null && FriendsCircleActivity.friendSquareList.size() > 0) {
                String id = FriendsCircleActivity.friendSquareList.get(position).getComments().get(index).getId();
                fsDb.delCommentMessage(FriendsCircleActivity.friendSquareList.get(position).getComments().get(index).getId());
                FriendsCircleActivity.friendSquareList.get(position).getComments().remove(index);
                FriendsCircleActivity.friendSquareAdapter.notifyDataSetChanged();
                // 诉它数据加载完毕;
                FriendsCircleActivity.mPullDownView.notifyDidMore();
                // 告诉它更新完毕
                FriendsCircleActivity.mPullDownView.RefreshComplete();
                finish();
                LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
                maps.put("id", id);
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
                            } else {
                                Utils.showToast(context, result);
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                }, maps, MarketApp.FRIENDSQUARE_CLEARCOMMENT, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_17);
            }
            break;
        case R.id.btn_cancel:
            finish();
            break;
        }
    }
}
