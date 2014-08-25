package com.lenovo.market.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.QueryMapActivity;
import com.lenovo.market.activity.VideoPlayerActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.activity.circle.friends.PublicAccountDetailsActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.contacts.FriendDetailsActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.activity.home.PictureViewActivity;
import com.lenovo.market.activity.home.WebViewActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.MessageDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.DateUtil;
import com.lenovo.market.util.FaceConversionUtil;
import com.lenovo.market.util.FileDownloadTask;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.GraphicVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

@SuppressWarnings("deprecation")
@SuppressLint("CutPasteId")
public class ActiveListAdapter extends BaseAdapter {

    public ArrayList<MsgChatVo> msgChatVos;
    private FriendMesVo friendVo;// 公众账号
    private MessageDBHelper megHelper;
    private ChatRecordDBHelper chatRDb;
    private ListView listView;
    private String title;
    private Context context;
    private UserVo userInfo;
    private MediaPlayer mPlayer;
    private ImageView playingVoice_;
    private String audioDir;// 音频文件夹
    private String videoDir;// 视频文件夹

    public ActiveListAdapter(Context context, ArrayList<MsgChatVo> msgChatVos, ListView listView, String title, FriendMesVo friendVo) {
        super();
        this.msgChatVos = msgChatVos;
        this.context = context;
        this.megHelper = new MessageDBHelper();
        this.chatRDb = new ChatRecordDBHelper();
        this.listView = listView;
        this.title = title;
        this.friendVo = friendVo;
        this.userInfo = AdminUtils.getUserInfo(MarketApp.app);
        this.audioDir = Utils.getCacheDir(context, "audio");
        this.videoDir = Utils.getCacheDir(context, "video");
    }

    @Override
    public int getCount() {
        return msgChatVos.size();
    }

    @Override
    public Object getItem(int index) {
        return msgChatVos.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        MessageTypeHolder0 holder0 = null;
        MessageTypeHolder1 holder1 = null;
        MessageTypeHolder2 holder2 = null;
        MessageTypeHolder3 holder3 = null;
        MessageTypeHolder4 holder4 = null;
        MessageTypeHolder5 holder5 = null;
        MessageTypeHolder6 holder6 = null;
        MessageTypeHolder7 holder7 = null;
        MessageTypeHolder8 holder8 = null;
        MessageTypeHolder9 holder9 = null;
        MessageTypeHolder10 holder10 = null;
        MessageTypeHolder11 holder11 = null;
        MessageTypeHolder12 holder12 = null;
        MessageTypeHolder13 holder13 = null;
        MsgChatVo msgChatVo = msgChatVos.get(index);
        if (msgChatVo != null && !TextUtils.isEmpty(msgChatVo.getType())) {
            int type = getItemViewType(index);
            if (convertView == null) {
                switch (type) {
                case MarketApp.ZERO:// 接收文本信息
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_text_left, null);
                    holder0 = new MessageTypeHolder0();
                    holder0.userImage = (ImageView) convertView.findViewById(R.id.msg_text_left_img);
                    holder0.messageContent = (TextView) convertView.findViewById(R.id.msg_text_left_speak);
                    convertView.setTag(holder0);
                    break;
                case MarketApp.ONE:// 接收图片
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_image, null);
                    holder1 = new MessageTypeHolder1();
                    holder1.userImage = (ImageView) convertView.findViewById(R.id.msg_image_headportrait);
                    holder1.messageImage = (ImageView) convertView.findViewById(R.id.msg_image_speak);
                    convertView.setTag(holder1);
                    break;
                case MarketApp.TWO:// 接收图文信息(多)
                    convertView = View.inflate(MarketApp.app, R.layout.listitem_message_image_text_list, null);
                    holder2 = new MessageTypeHolder2();
                    holder2.table = (LinearLayout) convertView.findViewById(R.id.active_image_text_list_ll);
                    holder2.table.removeAllViews();
                    holder2.table.setBackgroundResource(R.drawable.bg_message_list_or_image_text);
                    LayoutParams layoutParam1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    for (int k = 0; k < msgChatVo.getXmlVo().getnVos().size(); k++) {
                        if (k == 0) {
                            LinearLayout linearlayout = (LinearLayout) LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_image_text_list, null);
                            holder2.table.addView(linearlayout, layoutParam1);
                        } else {
                            LinearLayout linearlayout = (LinearLayout) LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_image_text_item, null);
                            holder2.table.addView(linearlayout, layoutParam1);
                        }
                    }
                    convertView.setTag(holder2);
                    break;
                case MarketApp.THREE:// 接收图文信息(单)
                    convertView = View.inflate(MarketApp.app, R.layout.listitem_message_image_text, null);
                    holder3 = new MessageTypeHolder3();
                    holder3.title = (TextView) convertView.findViewById(R.id.tv_active_message_title);
                    holder3.time = (TextView) convertView.findViewById(R.id.tv_active_message_date);
                    holder3.messageImage = (ImageView) convertView.findViewById(R.id.iv_active_message_image);
                    holder3.messageContent = (TextView) convertView.findViewById(R.id.tv_active_message_content);
                    holder3.explain = (TextView) convertView.findViewById(R.id.tv_active_message_enter_detail);
                    // 替换的 实际是更多按钮
                    holder3.userImage = (ImageView) convertView.findViewById(R.id.bt_active_message_detail);
                    holder3.table = (LinearLayout) convertView.findViewById(R.id.ll_home_itme1);
                    convertView.setTag(holder3);
                    break;
                case MarketApp.FOUR:// 接收音乐信息
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_music, null);
                    holder4 = new MessageTypeHolder4();
                    holder4.userImage = (ImageView) convertView.findViewById(R.id.music_avatar);
                    holder4.title = (TextView) convertView.findViewById(R.id.music_title);
                    holder4.messageContent = (TextView) convertView.findViewById(R.id.music_content);
                    convertView.setTag(holder4);
                    break;
                case MarketApp.FIVE:// 显示时间
                    convertView = View.inflate(MarketApp.app, R.layout.listitem_message_show_time, null);
                    holder5 = new MessageTypeHolder5();
                    holder5.time = (TextView) convertView.findViewById(R.id.message_show_time);
                    convertView.setTag(holder5);
                    break;
                case MarketApp.SIX:// 发送文本
                    convertView = View.inflate(MarketApp.app, R.layout.listitem_message_text_right, null);
                    holder6 = new MessageTypeHolder6();
                    holder6.userImage = (ImageView) convertView.findViewById(R.id.ic_active_text_avatar);
                    holder6.messageContent = (TextView) convertView.findViewById(R.id.tv_home_speak);
                    holder6.tv_home_status = (TextView) convertView.findViewById(R.id.tv_home_status);
                    convertView.setTag(holder6);
                    break;
                case MarketApp.SEVEN:// 发送的图片
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_send_image, null);
                    holder7 = new MessageTypeHolder7();
                    holder7.userImage = (ImageView) convertView.findViewById(R.id.send_image_icon);
                    holder7.messageImage = (ImageView) convertView.findViewById(R.id.send_image_pic);
                    holder7.tv_home_status = (TextView) convertView.findViewById(R.id.send_image_status);
                    convertView.setTag(holder7);
                    break;
                case MarketApp.EIGHT:// 发送的语音
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_voice_right, null);
                    holder8 = new MessageTypeHolder8();
                    holder8.contentLayout = (LinearLayout) convertView.findViewById(R.id.chatinfo_voice_right_layout);
                    holder8.userAvatar = (ImageView) convertView.findViewById(R.id.chatinfo_voice_right_iv);
                    holder8.tv_home_status = (TextView) convertView.findViewById(R.id.chatinfo_voice_right_status);
                    convertView.setTag(holder8);
                    break;
                case MarketApp.NINE:// 文字导航(特殊)
                    convertView = View.inflate(MarketApp.app, R.layout.listitem_message_table_text, null);
                    holder9 = new MessageTypeHolder9();
                    holder9.userImage = (ImageView) convertView.findViewById(R.id.msg_table_text_headportrait);
                    holder9.explain = (TextView) convertView.findViewById(R.id.msg_table_text_keyword1);
                    holder9.time = (TextView) convertView.findViewById(R.id.msg_table_text_function1);
                    holder9.messageContent = (TextView) convertView.findViewById(R.id.msg_table_text_title);
                    holder9.table = (LinearLayout) convertView.findViewById(R.id.msg_table_text_ll);
                    convertView.setTag(holder9);
                    break;
                case MarketApp.TEN:// 接收音频
                    convertView = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_voice_left, null);
                    holder10 = new MessageTypeHolder10();
                    holder10.contentLayout = (LinearLayout) convertView.findViewById(R.id.chatinfo_voice_left_layout);
                    holder10.userAvatar = (ImageView) convertView.findViewById(R.id.chatinfo_voice_left_iv);
                    convertView.setTag(holder10);
                    break;
                case MarketApp.ELEVEN:// send video
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_video_right, null);
                    holder11 = new MessageTypeHolder11();
                    holder11.contentLayout = (LinearLayout) convertView.findViewById(R.id.chatinfo_video_right_layout);
                    holder11.userAvatar = (ImageView) convertView.findViewById(R.id.chatinfo_video_right_iv);
                    holder11.status = (TextView) convertView.findViewById(R.id.chatinfo_video_right_status);
                    convertView.setTag(holder11);
                    break;
                case MarketApp.TWELVE:// receive video
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_video_left, null);
                    holder12 = new MessageTypeHolder12();
                    holder12.contentLayout = (LinearLayout) convertView.findViewById(R.id.chatinfo_video_left_layout);
                    holder12.userAvatar = (ImageView) convertView.findViewById(R.id.chatinfo_video_left_iv);
                    convertView.setTag(holder12);
                    break;
                case MarketApp.THIRTEEN:// send location
                    convertView = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_location_right, null);
                    holder13 = new MessageTypeHolder13();
                    holder13.contentLayout = (RelativeLayout) convertView.findViewById(R.id.chatinfo_location_right_layout);
                    holder13.userAvatar = (ImageView) convertView.findViewById(R.id.chatinfo_location_right_iv);
                    holder13.content = (TextView) convertView.findViewById(R.id.chatinfo_location_right_content);
                    holder13.status = (TextView) convertView.findViewById(R.id.chatinfo_location_right_status);
                    convertView.setTag(holder13);
                    break;
                }
            } else {
                switch (type) {
                case MarketApp.ZERO:
                    holder0 = (MessageTypeHolder0) convertView.getTag();
                    break;
                case MarketApp.ONE:
                    holder1 = (MessageTypeHolder1) convertView.getTag();
                    break;
                case MarketApp.TWO:
                    holder2 = (MessageTypeHolder2) convertView.getTag();
                    break;
                case MarketApp.THREE:
                    holder3 = (MessageTypeHolder3) convertView.getTag();
                    break;
                case MarketApp.FOUR:
                    holder4 = (MessageTypeHolder4) convertView.getTag();
                    break;
                case MarketApp.FIVE:
                    holder5 = (MessageTypeHolder5) convertView.getTag();
                    break;
                case MarketApp.SIX:
                    holder6 = (MessageTypeHolder6) convertView.getTag();
                    break;
                case MarketApp.SEVEN:
                    holder7 = (MessageTypeHolder7) convertView.getTag();
                    break;
                case MarketApp.EIGHT:
                    holder8 = (MessageTypeHolder8) convertView.getTag();
                    break;
                case MarketApp.NINE:
                    holder9 = (MessageTypeHolder9) convertView.getTag();
                    break;
                case MarketApp.TEN:
                    holder10 = (MessageTypeHolder10) convertView.getTag();
                    break;
                case MarketApp.ELEVEN:
                    holder11 = (MessageTypeHolder11) convertView.getTag();
                    break;
                case MarketApp.TWELVE:
                    holder12 = (MessageTypeHolder12) convertView.getTag();
                    break;
                case MarketApp.THIRTEEN:
                    holder13 = (MessageTypeHolder13) convertView.getTag();
                    break;
                }
            }

            switch (type) {
            case MarketApp.ZERO:// 接收文本信息
                if (friendVo != null) {
                    // 设置头像
                    Utils.downloadImg(true, context, holder0.userImage, friendVo.getPicture(), R.drawable.icon, listView);
                }
                String str = msgChatVo.getXmlVo().getContent();
                if (str == null)
                    break;
                if (str.contains("\r\n")) {
                    str = str.replaceAll("\r\n", "<br>");
                }
                if (str.contains("\\n")) {
                    str = str.replaceAll("\\\\n", "<br>");
                }
                holder0.messageContent.setText(Html.fromHtml(str, null, null));
                holder0.messageContent.setMovementMethod(LinkMovementMethod.getInstance());
                CharSequence text = holder0.messageContent.getText();
                if (text instanceof Spannable) {
                    int end = text.length();
                    Spannable sp = (Spannable) holder0.messageContent.getText();
                    URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
                    SpannableStringBuilder style = new SpannableStringBuilder(text);
                    style.clearSpans();// should clear old spans

                    // 循环把链接发过去
                    for (URLSpan url : urls) {
                        MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                        style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    holder0.messageContent.setText(style);
                }
                holder0.messageContent.setTag(msgChatVo);
                holder0.messageContent.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showThreeItemsDialog(mVo, index);
                        return false;
                    }
                });
                holder0.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != friendVo) {
                            Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                            friendVo.setIsFriend("true");
                            intent.putExtra(MarketApp.FRIEND, friendVo);
                            intent.putExtra("IsVisible", 1);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case MarketApp.ONE:// 接收图片信息
                if (friendVo != null) {
                    // 设置头像
                    Utils.downloadImg(true, context, holder1.userImage, friendVo.getPicture(), R.drawable.icon, listView);
                }
                // 下载要展示的图片
                Utils.downloadImg(false, context, holder1.messageImage, msgChatVo.getXmlVo().getPicUrl(), R.drawable.moren, listView);
                holder1.messageImage.setTag(msgChatVo);
                holder1.messageImage.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showTwoItemsDialog(mVo, index);
                        return false;
                    }
                });
                holder1.messageImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        Intent intent = new Intent(MarketApp.app, PictureViewActivity.class);
                        intent.putExtra("filePath", mVo.getXmlVo().getPicUrl());
                        context.startActivity(intent);
                    }
                });
                holder1.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != friendVo) {
                            Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                            friendVo.setIsFriend("true");
                            intent.putExtra(MarketApp.FRIEND, friendVo);
                            intent.putExtra("IsVisible", 1);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case MarketApp.TWO:// 接收图文信息(多)
                LayoutParams layoutP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                holder2.table.removeAllViews();
                for (int i = 0; i < msgChatVo.getXmlVo().getnVos().size(); i++) {
                    if (i == 0) {
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_image_text_list, null);
                        layout.setBackgroundResource(R.drawable.list_item_bg_selected2);
                        holder2.table.addView(layout, layoutP);
                    } else {
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_image_text_item, null);
                        layout.setBackgroundResource(R.drawable.list_item_bg_selected2);
                        holder2.table.addView(layout, layoutP);
                    }
                }
                for (int i = 0; i < holder2.table.getChildCount(); i++) {
                    holder2.table.getChildAt(i).setTag(R.id.shareurl, msgChatVo.getXmlVo().getnVos().get(i).getUrl());
                    if (!TextUtils.isEmpty(msgChatVo.getXmlVo().getnVos().get(i).getTitle())) {
                        holder2.table.getChildAt(i).setTag(R.id.sharetitle, msgChatVo.getXmlVo().getnVos().get(i).getTitle());
                        holder2.table.getChildAt(i).setTag(R.id.sharefilepath, msgChatVo.getXmlVo().getnVos().get(i).getPicUrl());
                    }
                    holder2.table.getChildAt(i).setTag(msgChatVo);
                    holder2.table.getChildAt(i).setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            MsgChatVo mVo = (MsgChatVo) v.getTag();
                            showOneItemDialog(mVo, index);
                            return false;
                        }
                    });
                    holder2.table.getChildAt(i).setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String url = (String) v.getTag(R.id.shareurl);
                            String shareTitle = (String) v.getTag(R.id.sharetitle);
                            String sharefilepath = (String) v.getTag(R.id.sharefilepath);
                            Intent intent = new Intent(MarketApp.app, WebViewActivity.class);
                            intent.putExtra(WebViewActivity.SHAREFILEPATH, sharefilepath);
                            intent.putExtra(WebViewActivity.TITLE, title);
                            intent.putExtra(WebViewActivity.SHARETITLE, shareTitle);
                            intent.putExtra(WebViewActivity.URL, url);
                            context.startActivity(intent);
                        }
                    });
                }
                for (int j = 0; j < holder2.table.getChildCount(); j++) {
                    View imagelist = holder2.table.getChildAt(j);
                    TextView key = null;
                    ImageView function = null;
                    if (j == 0) {
                        key = (TextView) imagelist.findViewById(R.id.active_image_text_item_content);
                        function = (ImageView) imagelist.findViewById(R.id.active_image_text_item_pic);
                    } else {
                        key = (TextView) imagelist.findViewById(R.id.tv_message_image);
                        function = (ImageView) imagelist.findViewById(R.id.iv_message_image);
                    }
                    if (j < msgChatVo.getXmlVo().getnVos().size()) {
                        GraphicVo exhibition = (GraphicVo) msgChatVo.getXmlVo().getnVos().get(j);
                        key.setText(exhibition.getTitle());
                        String imageUrl = exhibition.getPicUrl();
                        Utils.downloadImg(false, context, function, imageUrl, R.drawable.moren, listView);
                    }
                }
                break;
            case MarketApp.THREE:// 接收图文信息(单)
                holder3.table.setTag(msgChatVo);
                holder3.table.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, index);
                        return false;
                    }
                });
                holder3.title.setText(msgChatVo.getXmlVo().getnVos().get(0).getTitle());
                String datestr = DateUtil.getDateFromLong(Long.parseLong(msgChatVo.getCreateTime()));
                datestr = datestr.substring(5, datestr.indexOf(" ")) + "日";
                holder3.time.setText(datestr);
                String imageTextdetial = msgChatVo.getXmlVo().getnVos().get(0).getPicUrl();
                Utils.downloadImg(false, context, holder3.messageImage, imageTextdetial, R.drawable.moren, listView);
                holder3.messageContent.setText(msgChatVo.getXmlVo().getnVos().get(0).getDescription());
                holder3.explain.setText(MarketApp.app.getString(R.string.active_list_explain));
                holder3.userImage.setVisibility(View.VISIBLE);
                holder3.table.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        MsgChatVo mVo = (MsgChatVo) view.getTag();
                        GraphicVo naviVO = mVo.getXmlVo().getnVos().get(0);
                        if (null != naviVO) {
                            if (!TextUtils.isEmpty(naviVO.getUrl())) {
                                Intent intent = new Intent(MarketApp.app, WebViewActivity.class);
                                intent.putExtra(WebViewActivity.URL, naviVO.getUrl());
                                intent.putExtra(WebViewActivity.SHAREFILEPATH, naviVO.getPicUrl());
                                intent.putExtra(WebViewActivity.SHARETITLE, naviVO.getTitle());
                                intent.putExtra(WebViewActivity.TITLE, title);
                                context.startActivity(intent);
                            }
                        }
                    }
                });
                break;
            case MarketApp.FOUR:// 接收音乐信息(没实现播放功能)
                if (friendVo != null) {
                    // 设置头像
                    Utils.downloadImg(true, context, holder4.userImage, friendVo.getPicture(), R.drawable.icon, listView);
                }
                holder4.title.setText(msgChatVo.getXmlVo().getTitle());
                String content = msgChatVo.getXmlVo().getDescription();
                holder4.messageContent.setText(content);
                holder4.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != friendVo) {
                            Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                            friendVo.setIsFriend("true");
                            intent.putExtra(MarketApp.FRIEND, friendVo);
                            intent.putExtra("IsVisible", 1);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case MarketApp.FIVE:// 显示时间信息
                holder5.time.setText(DateUtil.getDateStrFromLong(msgChatVo.getCreateTime()));
                break;
            case MarketApp.SIX:// 发送的文本信息
                // 设置头像
                String picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder6.userImage, picture, R.drawable.icon, listView);
                if (msgChatVo.getXmlVo().getContent() != null && msgChatVo.getXmlVo().getContent().contains("[") && msgChatVo.getXmlVo().getContent().contains("]")) {
                    SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, msgChatVo.getXmlVo().getContent(), 25);
                    holder6.messageContent.setText(spannableString);
                } else {
                    holder6.messageContent.setText(msgChatVo.getXmlVo().getContent());
                }
                holder6.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                holder6.messageContent.setTag(msgChatVo);
                holder6.messageContent.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showThreeItemsDialog(mVo, index);
                        return false;
                    }
                });
                if (msgChatVo.getStatus().equals("0")) {
                    holder6.tv_home_status.setVisibility(View.GONE);
                } else {
                    holder6.tv_home_status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.SEVEN:// 发送的图片
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder7.userImage, picture, R.drawable.icon, listView);
                // 显示发送的图片
                Utils.downloadImg(false, context, holder7.messageImage, msgChatVo.getXmlVo().getPicUrl(), R.drawable.moren, listView);
                holder7.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                holder7.messageImage.setTag(msgChatVo);
                holder7.messageImage.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showTwoItemsDialog(mVo, index);
                        return false;
                    }
                });
                holder7.messageImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        Intent intent = new Intent(MarketApp.app, PictureViewActivity.class);
                        intent.putExtra("filePath", mVo.getXmlVo().getPicUrl());
                        context.startActivity(intent);
                    }
                });
                if (msgChatVo.getStatus().equals("0")) {
                    holder7.tv_home_status.setVisibility(View.GONE);
                } else {
                    holder7.tv_home_status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.EIGHT: // send voice
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder8.userAvatar, picture, R.drawable.icon, listView);
                holder8.contentLayout.setTag(msgChatVo);
                holder8.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, index);
                        return false;
                    }
                });
                holder8.contentLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ImageView img = (ImageView) v.findViewById(R.id.imageView);
                        Drawable background = img.getBackground();
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            // voice is Playing
                            stopPlaying();
                            if (playingVoice_ != null && playingVoice_ == img) {
                                // current view is the playing view
                                playingVoice_ = null;
                                if (background instanceof AnimationDrawable) {
                                    AnimationDrawable anim = (AnimationDrawable) background;
                                    anim.stop();
                                    img.setBackgroundResource(R.drawable.chatto_voice_playing);
                                }
                            } else {
                                // the playing view is not the current view
                                Drawable playingBg = playingVoice_.getBackground();
                                if (playingBg instanceof AnimationDrawable) {
                                    // stop the anim and set background
                                    AnimationDrawable anim = (AnimationDrawable) playingBg;
                                    anim.stop();
                                    int id = (Integer) playingVoice_.getTag();
                                    switch (id) {
                                    case R.drawable.anim_chatfrom_voice_playing:
                                        playingVoice_.setBackgroundResource(R.drawable.chatfrom_voice_playing);
                                        break;
                                    case R.drawable.anim_chatto_voice_playing:
                                        playingVoice_.setBackgroundResource(R.drawable.chatto_voice_playing);
                                        break;
                                    }
                                }
                                // start the anim of the current view and play voice
                                img.setBackgroundResource(R.drawable.anim_chatto_voice_playing);
                                img.setTag(R.drawable.anim_chatto_voice_playing);
                                MsgChatVo mVo = (MsgChatVo) v.getTag();
                                String path = mVo.getXmlVo().getVoiceUrl();
                                startPlaying(path, img);
                                playingVoice_ = img;
                            }
                        } else {
                            // voice is not playing
                            img.setBackgroundResource(R.drawable.anim_chatto_voice_playing);
                            img.setTag(R.drawable.anim_chatto_voice_playing);
                            MsgChatVo mVo = (MsgChatVo) v.getTag();
                            String path = mVo.getXmlVo().getVoiceUrl();
                            startPlaying(path, img);
                            playingVoice_ = img;
                        }
                    }
                });
                holder8.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                if (msgChatVo.getStatus().equals("0")) {
                    holder8.tv_home_status.setVisibility(View.GONE);
                } else {
                    holder8.tv_home_status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.NINE:// 文字导航(特殊)
                if (friendVo != null) {
                    // 设置头像
                    Utils.downloadImg(true, context, holder9.userImage, friendVo.getPicture(), R.drawable.icon, listView);
                }
                if (!TextUtils.isEmpty(msgChatVo.getXmlVo().getContent())) {
                    holder9.messageContent.setText(msgChatVo.getXmlVo().getContent());
                }
                holder9.table.removeAllViews();
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                if (msgChatVo.getXmlVo().getnVos().size() > 0) {
                    holder9.explain.setText(msgChatVo.getXmlVo().getnVos().get(0).getCode());
                    holder9.time.setText(msgChatVo.getXmlVo().getnVos().get(0).getTitle());
                    for (int i = 0; i < msgChatVo.getXmlVo().getnVos().size() - 1; i++) {
                        LinearLayout layout = (LinearLayout) LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_table_item, null);
                        holder9.table.addView(layout, lp);
                    }
                    for (int j = 0; j < holder9.table.getChildCount(); j++) {
                        View view = holder9.table.getChildAt(j);
                        TextView key = (TextView) view.findViewById(R.id.table_keyword_content);
                        TextView function = (TextView) view.findViewById(R.id.table_function_content);
                        if (j + 1 < msgChatVo.getXmlVo().getnVos().size()) {
                            GraphicVo exhibition = (GraphicVo) msgChatVo.getXmlVo().getnVos().get(j + 1);
                            key.setText(exhibition.getCode());
                            function.setText(exhibition.getTitle());
                            if (exhibition.getUrl() != null) {
                                ImageView btn = (ImageView) view.findViewById(R.id.table_item_btn);
                                btn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                holder9.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != friendVo) {
                            Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                            friendVo.setIsFriend("true");
                            intent.putExtra(MarketApp.FRIEND, friendVo);
                            intent.putExtra("IsVisible", 1);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case MarketApp.TEN:// 接收语音
                if (friendVo != null) {
                    // 设置头像
                    Utils.downloadImg(true, context, holder10.userAvatar, friendVo.getPicture(), R.drawable.icon, listView);
                }
                holder10.contentLayout.setTag(R.id.tag_first, msgChatVo);
                holder10.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag(R.id.tag_first);
                        showOneItemDialog(mVo, index);
                        return false;
                    }
                });
                if (msgChatVo.getXmlVo().getVoiceUrl() != null && msgChatVo.getXmlVo().getVoiceUrl().startsWith("http:")) {
                    String fileName = msgChatVo.getXmlVo().getTitle();
                    String filePath = audioDir + "/" + fileName;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        new FileDownloadTask(filePath).execute(msgChatVo.getXmlVo().getVoiceUrl());
                    }
                    holder10.contentLayout.setTag(R.id.tag_second, filePath);
                }
                holder10.contentLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ImageView img = (ImageView) v.findViewById(R.id.imageView);
                        Drawable background = img.getBackground();
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            // voice is Playing
                            stopPlaying();
                            if (playingVoice_ != null && playingVoice_ == img) {
                                // current view is the playing view
                                playingVoice_ = null;
                                if (background instanceof AnimationDrawable) {
                                    AnimationDrawable anim = (AnimationDrawable) background;
                                    anim.stop();
                                    img.setBackgroundResource(R.drawable.chatfrom_voice_playing);
                                }
                            } else {
                                // the playing view is not the current view
                                Drawable playingBg = playingVoice_.getBackground();
                                if (playingBg instanceof AnimationDrawable) {
                                    // stop the anim and set background
                                    AnimationDrawable anim = (AnimationDrawable) playingBg;
                                    anim.stop();
                                    int id = (Integer) playingVoice_.getTag();
                                    switch (id) {
                                    case R.drawable.anim_chatfrom_voice_playing:
                                        playingVoice_.setBackgroundResource(R.drawable.chatfrom_voice_playing);
                                        break;
                                    case R.drawable.anim_chatto_voice_playing:
                                        playingVoice_.setBackgroundResource(R.drawable.chatto_voice_playing);
                                        break;
                                    }
                                }
                                // start the anim of the current view and play voice
                                img.setBackgroundResource(R.drawable.anim_chatfrom_voice_playing);
                                img.setTag(R.drawable.anim_chatfrom_voice_playing);
                                String path = (String) v.getTag(R.id.tag_second);
                                startPlaying(path, img);
                                playingVoice_ = img;
                            }
                        } else {
                            // voice is not playing
                            img.setBackgroundResource(R.drawable.anim_chatfrom_voice_playing);
                            img.setTag(R.drawable.anim_chatfrom_voice_playing);
                            String path = (String) v.getTag(R.id.tag_second);
                            startPlaying(path, img);
                            playingVoice_ = img;
                        }
                    }
                });
                holder10.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != friendVo) {
                            Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                            friendVo.setIsFriend("true");
                            intent.putExtra(MarketApp.FRIEND, friendVo);
                            intent.putExtra("IsVisible", 1);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case MarketApp.ELEVEN:// send video
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder11.userAvatar, picture, R.drawable.icon, listView);
                holder11.contentLayout.setTag(msgChatVo);
                holder11.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, index);
                        return false;
                    }
                });
                holder11.contentLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        intent.putExtra("filePath", mVo.getXmlVo().getVideoUrl());
                        intent.putExtra("needSend", false);
                        context.startActivity(intent);
                    }
                });
                holder11.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                if (msgChatVo.getStatus().equals("0")) {
                    holder11.status.setVisibility(View.GONE);
                } else {
                    holder11.status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.TWELVE:// receive video
                if (friendVo != null) {
                    // 设置头像
                    Utils.downloadImg(true, context, holder12.userAvatar, friendVo.getPicture(), R.drawable.icon, listView);
                }
                holder12.contentLayout.setTag(R.id.tag_first, msgChatVo);
                holder12.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag(R.id.tag_first);
                        showOneItemDialog(mVo, index);
                        return false;
                    }
                });
                if (msgChatVo.getXmlVo().getVideoUrl() != null && msgChatVo.getXmlVo().getVideoUrl().startsWith("http:")) {
                    String fileName = msgChatVo.getXmlVo().getTitle();
                    String filePath = videoDir + "/" + fileName;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        new FileDownloadTask(filePath).execute(msgChatVo.getXmlVo().getVideoUrl());
                    }
                    holder12.contentLayout.setTag(R.id.tag_second, filePath);
                }
                holder12.contentLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra("filePath", (String) v.getTag(R.id.tag_second));
                        intent.putExtra("needSend", false);
                        context.startActivity(intent);
                    }
                });
                holder12.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != friendVo) {
                            Intent intent = new Intent(context, PublicAccountDetailsActivity.class);
                            friendVo.setIsFriend("true");
                            intent.putExtra(MarketApp.FRIEND, friendVo);
                            intent.putExtra("IsVisible", 1);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
            case MarketApp.THIRTEEN:// send location
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder13.userAvatar, picture, R.drawable.icon, listView);
                holder13.content.setText(msgChatVo.getXmlVo().getLabel());
                holder13.contentLayout.setTag(msgChatVo);
                holder13.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, index);
                        return false;
                    }
                });
                holder13.contentLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        MsgXmlVo xmlVo = mVo.getXmlVo();
                        Intent intent = new Intent(context, QueryMapActivity.class);
                        intent.putExtra("Location_X", xmlVo.getLocation_X());
                        intent.putExtra("Location_Y", xmlVo.getLocation_Y());
                        context.startActivity(intent);
                    }
                });
                holder13.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                if (msgChatVo.getStatus().equals("0")) {
                    holder13.status.setVisibility(View.GONE);
                } else {
                    holder13.status.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        String type = msgChatVos.get(position).getType();
        if (type.equals(MarketApp.RECEIVE_TEXT)) {
            return MarketApp.ZERO;
        } else if (type.equals(MarketApp.RECEIVE_PIC)) {
            return MarketApp.ONE;
        } else if (type.equals(MarketApp.RECEIVE_NEWSS)) {
            return MarketApp.TWO;
        } else if (type.equals(MarketApp.RECEIVE_NEWS)) {
            return MarketApp.THREE;
        } else if (type.equals(MarketApp.RECEIVE_MUSIC)) {
            return MarketApp.FOUR;
        } else if (type.equals(MarketApp.MESSAGE_TIME)) {
            return MarketApp.FIVE;
        } else if (type.equals(MarketApp.SEND_TEXT)) {
            return MarketApp.SIX;
        } else if (type.equals(MarketApp.SEND_PIC)) {
            return MarketApp.SEVEN;
        } else if (type.equals(MarketApp.SEND_VOICE)) {
            return MarketApp.EIGHT;
        } else if (type.equals(MarketApp.RECEIVE_NAVI)) {
            return MarketApp.NINE;
        } else if (type.equals(MarketApp.RECEIVE_VOICE)) {
            return MarketApp.TEN;
        } else if (type.equals(MarketApp.SEND_VIDEO)) {
            return MarketApp.ELEVEN;
        } else if (type.equals(MarketApp.RECEIVE_VIDEO)) {
            return MarketApp.TWELVE;
        } else if (type.equals(MarketApp.SEND_LOCATION)) {
            return MarketApp.THIRTEEN;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 14;
    }

    /**
     * 弹出只有删除item的dialog
     * 
     * @param index
     */
    private void showOneItemDialog(final MsgChatVo mVo, final int index) {
        Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("菜单");
        AlertDialog dialog = builder.setItems(R.array.one_item_dialog, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:// 删除消息
                    MsgChatVo msgVo = null;
                    if (msgChatVos.size() > index + 1) {
                        msgVo = msgChatVos.get(index + 1);
                    }
                    megHelper.Isdelete(mVo, msgChatVos.get(index - 1), msgVo);
                    MarketApp.index = index;
                    if (PublicChatActivity.isActive) {
                        PublicChatActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);

                        // 直接修改消息
                        MsgChatVo messageVo = megHelper.getMessageVo(mVo);
                        if (messageVo != null) {
                            ChatRecordVo record = new ChatRecordVo(friendVo.getFriendAccount(), friendVo.getFriendName(), messageVo.getCreateTime(), 0, friendVo.getPicture(), 2, messageVo.getContent(), messageVo.getLoginUser(), messageVo.getStatus(), "", "");
                            chatRDb.insertRecord(record, false);
                        } else {
                            chatRDb.updateContent(friendVo.getFriendAccount());
                        }
                        // 更新消息
                        if (null != FriendListFragment.handler) {
                            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                        }
                    } else {
                        HomePageFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);
                    }
                    break;
                }
            }
        }).create();
        dialog.show();
    }

    /**
     * 弹出拥有删除和转发两个条目的dialog
     * 
     * @param index
     */
    private void showTwoItemsDialog(final MsgChatVo mVo, final int index) {
        Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("菜单");
        AlertDialog dialog = builder.setItems(R.array.two_items_dialog, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:// 删除消息
                    MsgChatVo msgVo = null;
                    if (msgChatVos.size() > index + 1) {
                        msgVo = msgChatVos.get(index + 1);
                    }
                    megHelper.Isdelete(mVo, msgChatVos.get(index - 1), msgVo);
                    MarketApp.index = index;
                    if (PublicChatActivity.isActive) {
                        PublicChatActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);

                        // 直接修改消息
                        MsgChatVo messageVo = megHelper.getMessageVo(mVo);
                        if (messageVo != null) {
                            ChatRecordVo record = new ChatRecordVo(friendVo.getFriendAccount(), friendVo.getFriendName(), messageVo.getCreateTime(), 0, friendVo.getPicture(), 2, messageVo.getContent(), messageVo.getLoginUser(), messageVo.getStatus(), "", "");
                            chatRDb.insertRecord(record, false);
                        } else {
                            chatRDb.updateContent(friendVo.getFriendAccount());
                        }
                        // 更新消息
                        if (null != FriendListFragment.handler) {
                            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                        }
                    } else {
                        HomePageFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);
                    }
                    break;
                case 1:// 转发消息
                    break;
                }
            }
        }).create();
        dialog.show();
    }

    /**
     * 弹出 删除，复制和转发的dialog
     * 
     * @param index
     */
    protected void showThreeItemsDialog(final MsgChatVo mVo, final int index) {
        Builder builder = new android.app.AlertDialog.Builder(context);
        // 设置对话框的图标
        // builder.setIcon(R.drawable.header);
        // 设置对话框的标题
        builder.setTitle("列表对话框");
        // 添加按钮，android.content.DialogInterface.OnClickListener.OnClickListener
        builder.setItems(R.array.three_items_dialog, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:// 删除消息
                    MsgChatVo msgVo = null;
                    if (msgChatVos.size() > index + 1) {
                        msgVo = msgChatVos.get(index + 1);
                    }
                    megHelper.Isdelete(mVo, msgChatVos.get(index - 1), msgVo);
                    MarketApp.index = index;
                    if (PublicChatActivity.isActive) {
                        PublicChatActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);

                        // 直接修改消息
                        MsgChatVo messageVo = megHelper.getMessageVo(mVo);
                        if (messageVo != null) {
                            ChatRecordVo record = new ChatRecordVo(friendVo.getFriendAccount(), friendVo.getFriendName(), messageVo.getCreateTime(), 0, friendVo.getPicture(), 2, messageVo.getContent(), messageVo.getLoginUser(), messageVo.getStatus(), "", "");
                            chatRDb.insertRecord(record, false);
                        } else {
                            chatRDb.updateContent(friendVo.getFriendAccount());
                        }
                        // 更新消息
                        if (null != FriendListFragment.handler) {
                            FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                        }
                    } else {
                        HomePageFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_TWO);
                    }
                    break;
                case 1:// 复制消息
                    ClipboardManager cmb = (ClipboardManager) MarketApp.app.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(mVo.getXmlVo().getContent());
                    break;
                case 2:// 转发消息
                    break;
                }
            }
        });
        // 创建一个列表对话框
        builder.create();
        builder.show();
    }

    private class MyURLSpan extends ClickableSpan {

        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(MarketApp.app, WebViewActivity.class);
            intent.putExtra(WebViewActivity.URL, mUrl);
            context.startActivity(intent);
        }
    }

    private void startPlaying(String fileName, final ImageView img) {
        mPlayer = new MediaPlayer();
        final AnimationDrawable anim = (AnimationDrawable) img.getBackground();
        anim.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                anim.stop();
                int id = (Integer) img.getTag();
                switch (id) {
                case R.drawable.anim_chatfrom_voice_playing:
                    img.setBackgroundResource(R.drawable.chatfrom_voice_playing);
                    break;
                case R.drawable.anim_chatto_voice_playing:
                    img.setBackgroundResource(R.drawable.chatto_voice_playing);
                    break;
                }
                mPlayer.release();
                mPlayer = null;
            }
        });
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            anim.stop();
            int id = (Integer) img.getTag();
            switch (id) {
            case R.drawable.anim_chatfrom_voice_playing:
                img.setBackgroundResource(R.drawable.chatfrom_voice_playing);
                break;
            case R.drawable.anim_chatto_voice_playing:
                img.setBackgroundResource(R.drawable.chatto_voice_playing);
                break;
            }
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    // 接收的文本信息
    private static class MessageTypeHolder0 {
        private ImageView userImage;
        private TextView messageContent;
    }

    // 接收的图片
    private static class MessageTypeHolder1 {
        private ImageView userImage;
        private ImageView messageImage;
    }

    // 接收的图文(多)
    private static class MessageTypeHolder2 {
        private LinearLayout table;
    }

    // 接收的图文(单)
    private static class MessageTypeHolder3 {
        private ImageView userImage;
        private TextView title;
        private TextView time;
        private ImageView messageImage;
        private TextView messageContent;
        private TextView explain;
        private LinearLayout table;
    }

    // 接收音乐信息
    private static class MessageTypeHolder4 {
        private ImageView userImage;
        private TextView title;
        private TextView messageContent;
    }

    // 显示时间
    private static class MessageTypeHolder5 {
        private TextView time;
    }

    // 发送文本
    private static class MessageTypeHolder6 {
        private ImageView userImage;
        private TextView messageContent;
        private TextView tv_home_status;
    }

    // 发送图片
    private static class MessageTypeHolder7 {
        private ImageView userImage;
        private ImageView messageImage;
        private TextView tv_home_status;
    }

    // 发送语音
    private static class MessageTypeHolder8 {
        private LinearLayout contentLayout;
        private ImageView userAvatar;
        private TextView tv_home_status;
    }

    // 文字导航(特殊)
    private static class MessageTypeHolder9 {
        private ImageView userImage;
        private TextView time;
        private TextView explain;
        private TextView messageContent;
        private LinearLayout table;
    }

    // 接收语音
    private static class MessageTypeHolder10 {
        private LinearLayout contentLayout;
        private ImageView userAvatar;
    }

    // 发送视频
    private static class MessageTypeHolder11 {
        private LinearLayout contentLayout;
        private ImageView userAvatar;
        private TextView status;
    }

    // 接收视频
    private static class MessageTypeHolder12 {
        private LinearLayout contentLayout;
        private ImageView userAvatar;
    }

    // 发送地理位置
    private static class MessageTypeHolder13 {
        private RelativeLayout contentLayout;
        private ImageView userAvatar;
        private TextView status;
        private TextView content;
    }
}
