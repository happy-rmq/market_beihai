package com.lenovo.market.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lenovo.market.R;
import com.lenovo.market.adapter.FaceAdapter;
import com.lenovo.market.adapter.GridViewAdapter;
import com.lenovo.market.adapter.ViewPagerAdapter;
import com.lenovo.market.util.FaceConversionUtil;
import com.lenovo.market.vo.local.ChatEmoticonsVo;

/**
 * 自定义选择相应的功能
 * 
 * @author muqiang
 */
public class CustomViewPage extends FrameLayout {

    private Context context;
    private ViewPager viewpager_vp;
    private ViewPager vp_emoticons;
    private LinearLayout layout_points;;
    // private LinearLayout viewpage_guide_ll;
    private LinearLayout viewpage_content;
    private RelativeLayout rl_facechoose;
    private GestureDetector gestureDetector;
    private ArrayList<View> pageViews;
    private ArrayList<ImageView> pointViews;
    private ArrayList<FaceAdapter> faceAdapters;
    private ArrayList<ImageView> dots_ = new ArrayList<ImageView>();// 底部点
    private ArrayList<ArrayList<ChatEmoticonsVo>> emojis;
    private int current = 0;
    /** 当前表情页 */
    public static EditText et_input;

    public CustomViewPage(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_viewpage, this);
    }

    public CustomViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_viewpage, this);
        gestureDetector = new GestureDetector(context, new GuideViewTouch());
        viewpage_content = (LinearLayout) findViewById(R.id.viewpage_content);
        rl_facechoose = (RelativeLayout) findViewById(R.id.rl_facechoose);
        viewpager_vp = (ViewPager) findViewById(R.id.viewpager_vp);
        viewpager_vp.setOnPageChangeListener(new MyOnPageChangeListener());
        // viewpage_guide_ll = (LinearLayout) findViewById(R.id.viewpage_guide_ll);
        // initDots();
        initEmoticonsChooseLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    public void setItems(ArrayList<CustomViewPageItem> items) {
        ArrayList<View> views = new ArrayList<View>();
        LayoutInflater mLi = LayoutInflater.from(context);
        View view = mLi.inflate(R.layout.layout_viewpage_item, null);
        GridView gv = (GridView) view.findViewById(R.id.viewpage_item_gv);
        gv.setAdapter(new GridViewAdapter(context, items, viewpage_content, rl_facechoose));
        views.add(view);
        // 填充ViewPager的数据适配器
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(views);
        viewpager_vp.setAdapter(mPagerAdapter);
    }

    // private void initDots() {
    // ImageView img = null;
    // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    // for (int i = 0; i < 2; i++) {
    // img = new ImageView(context);
    // if (i > 0) {
    // params.leftMargin = Utils.dip2px(context, 10);
    // img.setEnabled(false);
    // }
    // img.setLayoutParams(params);
    // img.setScaleType(ScaleType.MATRIX);
    // img.setImageResource(R.drawable.sl_guide_dot);
    // viewpage_guide_ll.addView(img);
    // dots_.add(img);
    // }
    // }

    class GuideViewTouch extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    class MyOnPageChangeListener implements OnPageChangeListener {

        public void onPageSelected(int page) {
            int size = dots_.size();
            ImageView img = null;
            for (int i = 0; i < size; i++) {
                img = dots_.get(i);
                if (i == page) {
                    img.setEnabled(true);
                } else {
                    img.setEnabled(false);
                }
            }
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 初始化表情选择布局
     */
    private void initEmoticonsChooseLayout() {
        vp_emoticons = (ViewPager) findViewById(R.id.vp_contains);
        layout_points = (LinearLayout) findViewById(R.id.layout_points);

        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(context);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        emojis = FaceConversionUtil.getInstace().emojiLists;
        if (emojis == null || emojis.size() == 0) {
            FaceConversionUtil.getInstace().getFileText(context);
            emojis = FaceConversionUtil.getInstace().emojiLists;
        }

        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(context);
            FaceAdapter adapter = new FaceAdapter(emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ChatEmoticonsVo emoji = (ChatEmoticonsVo) faceAdapters.get(current).getItem(position);
                    int selection = et_input.getSelectionStart();
                    if (emoji.getId() == R.drawable.sl_btn_remove_expression) {
                        // 删除光标钱的表情或者字符
                        String text = et_input.getText().toString();
                        if (selection > 0) {
                            String text2 = text.substring(selection - 1, selection);
                            if ("]".equals(text2)) {
                                String text3 = text.substring(0, selection);
                                int start = text3.lastIndexOf("[");
                                int end = selection;
                                et_input.getText().delete(start, end);
                                return;
                            }
                            et_input.getText().delete(selection - 1, selection);
                        }
                    }
                    if (!TextUtils.isEmpty(emoji.getCharacter())) {
                        // 插入表情
                        SpannableString spannableString = FaceConversionUtil.getInstace().addFace(view.getContext(), emoji.getId(), emoji.getCharacter());
                        et_input.getText().insert(selection, spannableString);
                    }
                }
            });
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(15);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(10, 25, 10, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(context);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
        initPoints();
        initData();
    }

    /**
     * 填充数据
     */
    private void initData() {
        vp_emoticons.setAdapter(new ViewPagerAdapter(pageViews));

        vp_emoticons.setCurrentItem(1);
        current = 0;
        vp_emoticons.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vp_emoticons.setCurrentItem(arg0 + 1);// 第二屏
                                                              // 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.white_dot);
                    } else {
                        vp_emoticons.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.white_dot);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 表情选择布局上的小点
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.white_dot);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.dark_dot);
            }
        }
    }

    private void initPoints() {
        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.dark_dot);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            layout_points.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.white_dot);
            }
            pointViews.add(imageView);
        }
    }

    public RelativeLayout getRl_facechoose() {
        return rl_facechoose;
    }

    public LinearLayout getViewpage_content() {
        return viewpage_content;
    }
}