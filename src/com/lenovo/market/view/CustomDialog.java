package com.lenovo.market.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.market.R;

/**
 * Created by zhouyang on 14-3-18.
 */
public class CustomDialog extends Dialog {

    private Button button_right;
    private Button button_left;
    private TextView mContent;
    private TextView mTitle;

    public CustomDialog(Context context) {
        super(context, R.style.custom_dialog);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_version_notice, null);
        mTitle = (TextView) view.findViewById(R.id.tv_title);
        mContent = (TextView) view.findViewById(R.id.tv_content);
        button_left = (Button)view.findViewById(R.id.button_left);
        button_right = (Button)view.findViewById(R.id.button_right);

        setContentView(view);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitle.setText(titleId);
    }

    public void setContent(CharSequence content){
        mContent.setText(content);
    }

    public void setContent(int contentId){
        mContent.setText(contentId);
    }

    public void setLeftButton(CharSequence text,View.OnClickListener listener){
        button_left.setVisibility(View.VISIBLE);
        button_left.setText(text);
        button_left.setOnClickListener(listener);
    }

    public void setLeftButton(int textId,View.OnClickListener listener){
        button_left.setVisibility(View.VISIBLE);
        button_left.setText(textId);
        button_left.setOnClickListener(listener);
    }

    public void setRightButton(CharSequence text,View.OnClickListener listener){
        button_right.setText(text);
        button_right.setOnClickListener(listener);
    }

    public void setRightButton(int textId,View.OnClickListener listener){
        button_right.setText(textId);
        button_right.setOnClickListener(listener);
    }
}
