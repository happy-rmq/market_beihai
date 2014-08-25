package com.lenovo.xjpsd.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.xjpsd.adapter.RightCateGoryAdapter;
import com.lenovo.xjpsd.model.ItemPerMsgCenterModel;

public class RightLoginFragment extends Fragment implements OnItemClickListener {

    private View mView;
    private GridView right_permsg_gridview;
    private String[] category_name;
    private Integer[] category_img;
    private List<ItemPerMsgCenterModel> mLists;
    private TextView right_permsg_login_tv_name;
    private ImageView right_permsg_login_img_usericon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.psd_right_login, container, false);
            initView();
            initListener();
        }
        return mView;
    }

    private void initView() {
        right_permsg_login_tv_name = (TextView) mView.findViewById(R.id.right_permsg_login_tv_name);
        right_permsg_login_tv_name.setText(AdminUtils.getUserInfo(getActivity()).getUserName());

        right_permsg_login_img_usericon = (ImageView) mView.findViewById(R.id.right_permsg_login_img_usericon);
        Utils.downloadImg(true, getActivity(), right_permsg_login_img_usericon, AdminUtils.getUserInfo(getActivity()).getPicture(), R.drawable.psd_right_headportrait, right_permsg_login_img_usericon);
        right_permsg_gridview = (GridView) mView.findViewById(R.id.right_permsg_gridview);
        if (!TextUtils.isEmpty(AdminUtils.getUserInfo(getActivity()).getCompanyId())) {
            category_name = getActivity().getResources().getStringArray(R.array.msg_center);
        } else {
            category_name = getActivity().getResources().getStringArray(R.array.msg_center1);
        }
        category_img = new Integer[]{R.drawable.psd_right_ico1, R.drawable.psd_right_ico2, R.drawable.psd_right_ico3, R.drawable.psd_right_ico1, R.drawable.psd_right_ico2, R.drawable.psd_right_ico3};
        mLists = new ArrayList<ItemPerMsgCenterModel>();
        for (int i = 0; i < category_name.length; i++) {
            mLists.add(new ItemPerMsgCenterModel(category_img[i], category_name[i]));
        }
        right_permsg_gridview.setAdapter(new RightCateGoryAdapter(getActivity(), mLists));
    }

    private void initListener() {
        right_permsg_gridview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getActivity().finish();
        switch (position) {
            case 1:
                ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_FIVE);
                break;
            case 2:
                ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_SIX);
                break;
            case 3:
                ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_SEVEN);
                break;
            case 4:
                ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_EIGHT);
                break;
            case 5:
                ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_NINE);
                break;
        }
    }
}
