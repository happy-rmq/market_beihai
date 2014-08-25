package com.lenovo.market.activity.circle.group;

import java.util.UUID;

import com.lenovo.market.dbhelper.RoomDBHelper;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.MucUtils;

/**
 * 修改群组名称
 * 
 * @author zhouyang
 */
public class ModifyRoomNameActivity extends BaseActivity implements OnClickListener {

    public static final String ROOM_NAME = "room_name";

    private EditText et_name_;// 名字文本框
    private TextView setting_introduce;

    private String room_id;
    private RoomDBHelper roomDB;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_settings_modifyname);
        setTitleBarText(R.string.title_modifyroomname);
        setTitleBarRightBtnText(R.string.title_save);
        setTitleBarLeftBtnText();
        room_id = getIntent().getStringExtra(GroupChatSettingActivity.ROOM_ID);
        roomDB = new RoomDBHelper();
    }

    @Override
    protected void findViewById() {
        et_name_ = (EditText) findViewById(R.id.et_name);
        setting_introduce = (TextView) findViewById(R.id.setting_introduce);
        setting_introduce.setVisibility(View.GONE);
        String name = getIntent().getStringExtra(ROOM_NAME);
        if (!TextUtils.isEmpty(name)) {
            et_name_.setText(name);
        }
        room_id = getIntent().getStringExtra(GroupChatSettingActivity.ROOM_ID);
    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_left:
            finish();
            break;
        case R.id.btn_right:
            saveName();
            break;
        }
    }

    private void saveName() {
        String name = et_name_.getText().toString().trim();
        // 数据校验
        if (TextUtils.isEmpty(name)) {
            Utils.showToast(this, "名字不能为空");
            return;
        }
        if (room_id == null)
            return;
        
        if (!MarketApp.network_available || !NetUtils.hasNetwork()) {
            Utils.showToast(MarketApp.app, "网络连接不可用，请稍后重试");
            return;
        }

        MultiUserChat muc = MucUtils.getMuc(room_id);
        try {
            muc.changeSubject(name);
            // Form form = muc.getConfigurationForm();
            // Form answerForm = form.createAnswerForm();
            // answerForm.setAnswer("muc#roomconfig_roomname", name);
            // muc.sendConfigurationForm(answerForm);
            Utils.showToast(this, "修改成功");
            roomDB.updateRoomName(room_id,name);

            String username = AdminUtils.getUserInfo(this).getUserName();
            String uuid = UUID.randomUUID().toString();
            String timeMillis = System.currentTimeMillis() + "";
            MsgXmlVo vo = new MsgXmlVo();
            vo.setMsgType(MarketApp.MESSAGE_NOTICE);
            String content = "修改群名为\"" + name + "\"";
            if (username != null) {
                content = username + content;
            }
            vo.setContent(content);
            vo.setCreateTime(timeMillis);
            vo.setMsgId(uuid);
            String xml = XMLUtil.createXML(vo, MarketApp.MESSAGE_NOTICE);

            String room_jid = room_id + "@" + MarketApp.ROOM_SERVER_NAME;
            Message message = new Message(room_jid, org.jivesoftware.smack.packet.Message.Type.groupchat);
            message.setBody(xml);
            muc.sendMessage(message);
            
            Intent data = new Intent();
            data.putExtra(ROOM_NAME, name);
            setResult(MarketApp.HANDLERMESS_ONE, data);
            finish();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
