package com.lenovo.market.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BasicMapActivity;
import com.lenovo.market.activity.BusinessCardActivity;
import com.lenovo.market.activity.CameraActivity;
import com.lenovo.market.activity.DialogThreeActivity;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.view.CustomViewPageItem;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CustomViewPageItem> items;
    private LinearLayout viewpage_content;
    private RelativeLayout rl_facechoose;

    public GridViewAdapter(Context context, ArrayList<CustomViewPageItem> items, LinearLayout viewpage_content, RelativeLayout rl_facechoose) {
        this.context = context;
        this.items = items;
        this.viewpage_content = viewpage_content;
        this.rl_facechoose = rl_facechoose;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CustomViewPageItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View inflate = View.inflate(context, R.layout.layout_gridview_item, null);
        TextView gridView_name_tv = (TextView) inflate.findViewById(R.id.gridview_name_tv);
        ImageButton gridView_img_tv = (ImageButton) inflate.findViewById(R.id.gridview_img_tv);
        CustomViewPageItem item = getItem(position);
        gridView_name_tv.setText(item.getName());
        gridView_img_tv.setBackgroundResource(item.getImgSrc());
        gridView_img_tv.setTag(item.getImgSrc());
        gridView_img_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Integer id = (Integer) v.getTag();
                switch (id) {
                case R.drawable.app_panel_expression_icon:// 表情
                    viewpage_content.setVisibility(View.GONE);
                    rl_facechoose.setVisibility(View.VISIBLE);
                    break;
                case R.drawable.app_panel_pic_icon:// 图片
                    Intent intent = new Intent(context, DialogThreeActivity.class);
                    context.startActivity(intent);
                    break;
                case R.drawable.app_panel_video_icon:// 视频
                    int from = 0;
                    if (context instanceof ChatActivity) {
                        from = 1;
                    } else if (context instanceof GroupChatActivity) {
                        from = 2;
                    } else if (context instanceof ViewPaperMenuActivity) {
                        from = 3;
                    } else if (context instanceof PublicChatActivity) {
                        from = 4;
                    }
                    Intent camera = new Intent(context, CameraActivity.class);
                    camera.putExtra("from", from);
                    context.startActivity(camera);
                    break;
                case R.drawable.app_panel_friendcard_icon:// 名片
                    intent = new Intent(context, BusinessCardActivity.class);
                    context.startActivity(intent);
                    break;
                case R.drawable.app_panel_location_icon:// 地理位置
                    from = 0;
                    if (context instanceof ChatActivity) {
                        from = 1;
                    } else if (context instanceof GroupChatActivity) {
                        from = 2;
                    } else if (context instanceof ViewPaperMenuActivity) {
                        from = 3;
                    } else if (context instanceof PublicChatActivity) {
                        from = 4;
                    }
                    intent = new Intent(context, BasicMapActivity.class);
                    intent.putExtra("from", from);
                    context.startActivity(intent);
                    break;
                }
            }
        });
        return inflate;
    }
}
