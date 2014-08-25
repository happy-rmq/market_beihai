package com.lenovo.market.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lenovo.market.R;

/**
 * 自定义底部聊天栏
 * 
 * @author muqiang
 */
public class CustomChatControls extends LinearLayout {

    private Button chatcontrols_send_bt;
    private Button chatcontrols_select_bt;
    private EditText chatcontrols_inputbox_et;

    public CustomChatControls(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.chatcontrols, this);
    }

    public CustomChatControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.chatcontrols, this);
        chatcontrols_send_bt = (Button) findViewById(R.id.chatcontrols_send_bt);
        chatcontrols_select_bt = (Button) findViewById(R.id.chatcontrols_select_bt);
        chatcontrols_inputbox_et=(EditText) findViewById(R.id.chatcontrols_inputbox_et);
    }
    
    public Button getChatcontrols_send_bt() {
        return chatcontrols_send_bt;
    }

    public Button getChatcontrols_select_bt() {
        return chatcontrols_select_bt;
    }

    public EditText getChatcontrols_inputbox_et() {
        return chatcontrols_inputbox_et;
    }
}