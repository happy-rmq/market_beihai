package com.lenovo.market.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.lenovo.market.R;

/**
 * Created by zhouyang on 14-3-18.
 */
public class CustomProgressDialog extends Dialog {

    // private Button button_cancel;
    // private TextView mContent;
    // private TextView percent;

    public CustomProgressDialog(Context context) {
        super(context, R.style.custom_dialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progress, null);
        // mContent = (TextView) view.findViewById(R.id.tv_content);
        // percent = (TextView) view.findViewById(R.id.percent);

        setContentView(view);
    }
}
