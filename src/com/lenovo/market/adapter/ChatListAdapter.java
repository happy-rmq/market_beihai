package com.lenovo.market.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
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
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicAccountDetailsActivity;
import com.lenovo.market.activity.contacts.FriendDetailsActivity;
import com.lenovo.market.activity.home.PictureViewActivity;
import com.lenovo.market.activity.home.WebViewActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.ChatInfoDBHelper;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.DateUtil;
import com.lenovo.market.util.FaceConversionUtil;
import com.lenovo.market.util.FileDownloadTask;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

@SuppressWarnings("deprecation")
public class ChatListAdapter extends BaseAdapter {

    public ArrayList<MsgChatVo> msgChatVos;
    private FriendMesVo friend;
    private String audioDir;// 音频文件夹
    private String videoDir;// 视频文件夹
    private MsgChatVo mVo;
    private FriendInfoDBHelper friendDb;
    private ListView listView;
    private UserVo userInfo;
    private ChatActivity context;
    private ImageView playingVoice_;
    private ChatInfoDBHelper chatInfoDb;
    private ChatRecordDBHelper chatRDb;
    private MediaPlayer mPlayer;

    public ChatListAdapter(ChatActivity context, ArrayList<MsgChatVo> msgChatVos, ListView listView, FriendMesVo friend) {
        super();
        this.msgChatVos = msgChatVos;
        this.friendDb = new FriendInfoDBHelper();
        this.chatInfoDb = new ChatInfoDBHelper();
        this.chatRDb = new ChatRecordDBHelper();
        this.listView = listView;
        this.context = context;
        this.userInfo = AdminUtils.getUserInfo(context);
        this.audioDir = Utils.getCacheDir(context, "audio");
        this.videoDir = Utils.getCacheDir(context, "video");
        this.friend = friend;
    }

    @Override
    public int getCount() {
        return msgChatVos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return msgChatVos.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View viewConvert, ViewGroup arg2) {
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
        MessageTypeHolder14 holder14 = null;
        mVo = msgChatVos.get(position);
        if (mVo != null && mVo.getType() != null) {
            int type = getItemViewType(position);
            if (viewConvert == null) {
                switch (type) {
                case MarketApp.ZERO:// 发送文本消息
                    viewConvert = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_text_right, null);
                    holder0 = new MessageTypeHolder0();
                    holder0.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_right_avatar);
                    holder0.msg = (TextView) viewConvert.findViewById(R.id.chatinfo_right_speak);
                    holder0.tv_home_status = (TextView) viewConvert.findViewById(R.id.chatinfo_right_status);
                    viewConvert.setTag(holder0);
                    break;
                case MarketApp.ONE:// 接收文本消息
                    viewConvert = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_text_left, null);
                    holder1 = new MessageTypeHolder1();
                    holder1.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_left_avatar);
                    holder1.msg = (TextView) viewConvert.findViewById(R.id.chatinfo_left_speak);
                    viewConvert.setTag(holder1);
                    break;
                case MarketApp.TWO:// 时间消息
                    viewConvert = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_show_time, null);
                    holder2 = new MessageTypeHolder2();
                    holder2.msg = (TextView) viewConvert.findViewById(R.id.chatinfo_show_time);
                    viewConvert.setTag(holder2);
                    break;
                case MarketApp.THREE:// 发送分享消息
                    viewConvert = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_share_right, null);
                    holder3 = new MessageTypeHolder3();
                    holder3.msg = (TextView) viewConvert.findViewById(R.id.chatinfo_right_sharetitle);
                    holder3.nick = (TextView) viewConvert.findViewById(R.id.chatinfo_right_sharecontent);
                    holder3.pic = (ImageView) viewConvert.findViewById(R.id.chatinfo_right_shareimg);
                    holder3.chatinfo_share_ll = (LinearLayout) viewConvert.findViewById(R.id.chatinfo_right_share_ll);
                    holder3.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_right_share_avatar);
                    holder3.title = (TextView) viewConvert.findViewById(R.id.tv_chatinfo_share_right_title);
                    holder3.status = (TextView) viewConvert.findViewById(R.id.chatinfo_right_share_status);
                    viewConvert.setTag(holder3);
                    break;
                case MarketApp.FOUR:// 接收分享消息
                    viewConvert = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_share_left, null);
                    holder4 = new MessageTypeHolder4();
                    holder4.msg = (TextView) viewConvert.findViewById(R.id.chatinfo_sharetitle);
                    holder4.chatinfo_share_ll = (LinearLayout) viewConvert.findViewById(R.id.chatinfo_share_ll);
                    holder4.nick = (TextView) viewConvert.findViewById(R.id.chatinfo_sharecontent);
                    holder4.pic = (ImageView) viewConvert.findViewById(R.id.chatinfo_shareimg);
                    holder4.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_left_share_avatar);
                    holder4.title = (TextView) viewConvert.findViewById(R.id.tv_chatinfo_share_left_title);
                    viewConvert.setTag(holder4);
                    break;
                case MarketApp.FIVE:// 发送语音消息
                    viewConvert = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_voice_right, null);
                    holder5 = new MessageTypeHolder5();
                    holder5.contentLayout = (LinearLayout) viewConvert.findViewById(R.id.chatinfo_voice_right_layout);
                    holder5.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_voice_right_iv);
                    holder5.tv_home_status = (TextView) viewConvert.findViewById(R.id.chatinfo_voice_right_status);
                    viewConvert.setTag(holder5);
                    break;
                case MarketApp.SIX:// 接收语音消息
                    viewConvert = LayoutInflater.from(context).inflate(R.layout.listitem_chatinfo_voice_left, null);
                    holder6 = new MessageTypeHolder6();
                    holder6.contentLayout = (LinearLayout) viewConvert.findViewById(R.id.chatinfo_voice_left_layout);
                    holder6.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_voice_left_iv);
                    viewConvert.setTag(holder6);
                    break;
                case MarketApp.SEVEN:// 发送的图片
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_send_image, null);
                    holder7 = new MessageTypeHolder7();
                    holder7.userImage = (ImageView) viewConvert.findViewById(R.id.send_image_icon);
                    holder7.messageImage = (ImageView) viewConvert.findViewById(R.id.send_image_pic);
                    holder7.status = (TextView) viewConvert.findViewById(R.id.send_image_status);
                    viewConvert.setTag(holder7);
                    break;
                case MarketApp.EIGHT:// 接收的图片
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_message_image, null);
                    holder8 = new MessageTypeHolder8();
                    holder8.userImage = (ImageView) viewConvert.findViewById(R.id.msg_image_headportrait);
                    holder8.messageImage = (ImageView) viewConvert.findViewById(R.id.msg_image_speak);
                    viewConvert.setTag(holder8);
                    break;
                case MarketApp.NINE:// 发送名片
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_businesscard_right, null);
                    holder9 = new MessageTypeHolder9();
                    holder9.layout = (LinearLayout) viewConvert.findViewById(R.id.businesscard_layout);
                    holder9.avatar = (ImageView) viewConvert.findViewById(R.id.businesscard_avatar);
                    holder9.img = (ImageView) viewConvert.findViewById(R.id.businesscard_img);
                    holder9.status = (TextView) viewConvert.findViewById(R.id.businesscard_status);
                    holder9.name = (TextView) viewConvert.findViewById(R.id.businesscard_name);
                    holder9.sign = (TextView) viewConvert.findViewById(R.id.businesscard_sign);
                    viewConvert.setTag(holder9);
                    break;
                case MarketApp.TEN:// 接收名片
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_businesscard_left, null);
                    holder10 = new MessageTypeHolder10();
                    holder10.layout = (LinearLayout) viewConvert.findViewById(R.id.businesscard_left_layout);
                    holder10.avatar = (ImageView) viewConvert.findViewById(R.id.businesscard_left_avatar);
                    holder10.img = (ImageView) viewConvert.findViewById(R.id.businesscard_left_img);
                    holder10.name = (TextView) viewConvert.findViewById(R.id.businesscard_left_name);
                    holder10.sign = (TextView) viewConvert.findViewById(R.id.businesscard_left_sign);
                    viewConvert.setTag(holder10);
                    break;
                case MarketApp.ELEVEN:// send video
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_video_right, null);
                    holder11 = new MessageTypeHolder11();
                    holder11.contentLayout = (LinearLayout) viewConvert.findViewById(R.id.chatinfo_video_right_layout);
                    holder11.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_video_right_iv);
                    holder11.status = (TextView) viewConvert.findViewById(R.id.chatinfo_video_right_status);
                    viewConvert.setTag(holder11);
                    break;
                case MarketApp.TWELVE:// receive video
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_video_left, null);
                    holder12 = new MessageTypeHolder12();
                    holder12.contentLayout = (LinearLayout) viewConvert.findViewById(R.id.chatinfo_video_left_layout);
                    holder12.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_video_left_iv);
                    viewConvert.setTag(holder12);
                    break;
                case MarketApp.THIRTEEN:// send location
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_location_right, null);
                    holder13 = new MessageTypeHolder13();
                    holder13.contentLayout = (RelativeLayout) viewConvert.findViewById(R.id.chatinfo_location_right_layout);
                    holder13.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_location_right_iv);
                    holder13.content = (TextView) viewConvert.findViewById(R.id.chatinfo_location_right_content);
                    holder13.status = (TextView) viewConvert.findViewById(R.id.chatinfo_location_right_status);
                    viewConvert.setTag(holder13);
                    break;
                case MarketApp.FOURTEEN:// receive location
                    viewConvert = LayoutInflater.from(MarketApp.app).inflate(R.layout.listitem_chatinfo_location_left, null);
                    holder14 = new MessageTypeHolder14();
                    holder14.contentLayout = (RelativeLayout) viewConvert.findViewById(R.id.chatinfo_location_left_layout);
                    holder14.userAvatar = (ImageView) viewConvert.findViewById(R.id.chatinfo_location_left_iv);
                    holder14.content = (TextView) viewConvert.findViewById(R.id.chatinfo_location_left_content);
                    viewConvert.setTag(holder14);
                    break;
                }
            } else {
                switch (type) {
                case MarketApp.ZERO:
                    holder0 = (MessageTypeHolder0) viewConvert.getTag();
                    break;
                case MarketApp.ONE:
                    holder1 = (MessageTypeHolder1) viewConvert.getTag();
                    break;
                case MarketApp.TWO:
                    holder2 = (MessageTypeHolder2) viewConvert.getTag();
                    break;
                case MarketApp.THREE:
                    holder3 = (MessageTypeHolder3) viewConvert.getTag();
                    holder3.title.setVisibility(View.GONE);
                    break;
                case MarketApp.FOUR:
                    holder4 = (MessageTypeHolder4) viewConvert.getTag();
                    holder4.title.setVisibility(View.GONE);
                    break;
                case MarketApp.FIVE:
                    holder5 = (MessageTypeHolder5) viewConvert.getTag();
                    break;
                case MarketApp.SIX:
                    holder6 = (MessageTypeHolder6) viewConvert.getTag();
                    break;
                case MarketApp.SEVEN:
                    holder7 = (MessageTypeHolder7) viewConvert.getTag();
                    break;
                case MarketApp.EIGHT:
                    holder8 = (MessageTypeHolder8) viewConvert.getTag();
                    break;
                case MarketApp.NINE:
                    holder9 = (MessageTypeHolder9) viewConvert.getTag();
                    break;
                case MarketApp.TEN:
                    holder10 = (MessageTypeHolder10) viewConvert.getTag();
                    break;
                case MarketApp.ELEVEN:
                    holder11 = (MessageTypeHolder11) viewConvert.getTag();
                    break;
                case MarketApp.TWELVE:
                    holder12 = (MessageTypeHolder12) viewConvert.getTag();
                    break;
                case MarketApp.THIRTEEN:
                    holder13 = (MessageTypeHolder13) viewConvert.getTag();
                    break;
                case MarketApp.FOURTEEN:
                    holder14 = (MessageTypeHolder14) viewConvert.getTag();
                    break;
                }
            }
            switch (type) {
            case MarketApp.ZERO:// 发送文本消息
                // 设置头像
                String picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder0.userAvatar, picture, R.drawable.icon, listView);
                if (mVo.getXmlVo().getContent() != null && mVo.getXmlVo().getContent().contains("[") && mVo.getXmlVo().getContent().contains("]")) {
                    SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, mVo.getXmlVo().getContent(), 25);
                    holder0.msg.setText(spannableString);
                } else {
                    holder0.msg.setText(mVo.getXmlVo().getContent());
                }
                holder0.msg.setTag(mVo);
                holder0.msg.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showThreeItemsDialog(mVo, position);
                        return false;
                    }
                });
                holder0.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                if (mVo.getStatus().equals("0")) {
                    holder0.tv_home_status.setVisibility(View.GONE);
                } else {
                    holder0.tv_home_status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.ONE:// 接收文本消息
                // 设置头像
                Utils.downloadImg(true, context, holder1.userAvatar, friend.getPicture(), R.drawable.icon, listView);
                if (mVo.getXmlVo().getContent() != null && mVo.getXmlVo().getContent().contains("[") && mVo.getXmlVo().getContent().contains("]")) {
                    SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, mVo.getXmlVo().getContent(), 25);
                    holder1.msg.setText(spannableString);
                } else {
                    holder1.msg.setText(mVo.getXmlVo().getContent());
                }
                holder1.msg.setTag(mVo);
                holder1.msg.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showThreeItemsDialog(mVo, position);
                        return false;
                    }
                });
                holder1.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                break;
            case MarketApp.TWO:// 时间消息
                holder2.msg.setText(DateUtil.getDateStrFromLong(mVo.getCreateTime()));
                break;
            case MarketApp.THREE:// 发送分享消息
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder3.userAvatar, picture, R.drawable.icon, listView);
                holder3.msg.setText(mVo.getXmlVo().getTitle());
                if (!TextUtils.isEmpty(mVo.getXmlVo().getContent())) {
                    holder3.title.setVisibility(View.VISIBLE);
                    holder3.title.setText(mVo.getXmlVo().getContent());
                }
                holder3.chatinfo_share_ll.setTag(mVo);
                holder3.chatinfo_share_ll.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                holder3.chatinfo_share_ll.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        String url = mVo.getXmlVo().getUrl();
                        String title = mVo.getXmlVo().getTitle();
                        String filepath = mVo.getXmlVo().getPicUrl();
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.URL, url);
                        intent.putExtra(WebViewActivity.TITLE, title);
                        intent.putExtra(WebViewActivity.SHARETITLE, title);
                        intent.putExtra(WebViewActivity.SHAREFILEPATH, filepath);
                        context.startActivity(intent);
                    }
                });
                holder3.nick.setText(mVo.getXmlVo().getUrl());
                Utils.downloadImg(false, context, holder3.pic, mVo.getXmlVo().getPicUrl(), R.drawable.icon, listView);
                holder3.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                if (mVo.getStatus().equals("0")) {
                    holder3.status.setVisibility(View.GONE);
                } else {
                    holder3.status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.FOUR:// 接收分享消息
                // 设置头像
                Utils.downloadImg(true, context, holder4.userAvatar, friend.getPicture(), R.drawable.icon, listView);
                holder4.msg.setText(mVo.getXmlVo().getTitle());
                if (!TextUtils.isEmpty(mVo.getXmlVo().getContent())) {
                    holder4.title.setVisibility(View.VISIBLE);
                    holder4.title.setText(mVo.getXmlVo().getContent());
                }
                holder4.chatinfo_share_ll.setTag(mVo);
                holder4.chatinfo_share_ll.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                holder4.chatinfo_share_ll.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        String url = mVo.getXmlVo().getUrl();
                        String title = mVo.getXmlVo().getTitle();
                        String filepath = mVo.getXmlVo().getPicUrl();
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.URL, url);
                        intent.putExtra(WebViewActivity.TITLE, title);
                        intent.putExtra(WebViewActivity.SHARETITLE, title);
                        intent.putExtra(WebViewActivity.SHAREFILEPATH, filepath);
                        context.startActivity(intent);
                    }
                });
                holder4.nick.setText(mVo.getXmlVo().getUrl());
                Utils.downloadImg(false, context, holder4.pic, mVo.getXmlVo().getPicUrl(), R.drawable.icon, listView);
                holder4.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                break;
            case MarketApp.FIVE:// send voice
                // 设置头像
                Utils.downloadImg(true, context, holder5.userAvatar, userInfo.getPicture(), R.drawable.icon, listView);
                holder5.contentLayout.setTag(mVo);
                holder5.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                holder5.contentLayout.setOnClickListener(new OnClickListener() {

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
                holder5.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                if (mVo.getStatus().equals("0")) {
                    holder5.tv_home_status.setVisibility(View.GONE);
                } else {
                    holder5.tv_home_status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.SIX:// receive voice
                // 设置头像
                Utils.downloadImg(true, context, holder6.userAvatar, friend.getPicture(), R.drawable.icon, listView);
                holder6.contentLayout.setTag(R.id.tag_first, mVo);
                holder6.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag(R.id.tag_first);
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                if (mVo.getXmlVo().getVoiceUrl() != null && mVo.getXmlVo().getVoiceUrl().startsWith("http:")) {
                    String fileName = mVo.getXmlVo().getVoiceUrl().substring(mVo.getXmlVo().getVoiceUrl().lastIndexOf("/") + 1);
                    String filePath = audioDir + "/" + fileName;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        new FileDownloadTask(filePath).execute(mVo.getXmlVo().getVoiceUrl());
                    }
                    holder6.contentLayout.setTag(R.id.tag_second, filePath);
                }
                holder6.contentLayout.setOnClickListener(new OnClickListener() {

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
                holder6.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                break;
            case MarketApp.SEVEN:// 发送的图片
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder7.userImage, picture, R.drawable.icon, listView);
                // 显示发送的图片
                Utils.downloadImg(false, context, holder7.messageImage, mVo.getXmlVo().getPicUrl(), R.drawable.moren, listView);
                holder7.messageImage.setTag(mVo);
                holder7.messageImage.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showTwoItemsDialog(mVo, position);
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
                holder7.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                if (mVo.getStatus().equals("0")) {
                    holder7.status.setVisibility(View.GONE);
                } else {
                    holder7.status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.EIGHT:// 接受的图片
                // 设置头像
                Utils.downloadImg(true, context, holder8.userImage, friend.getPicture(), R.drawable.icon, listView);
                // 显示接受的图片
                Utils.downloadImg(false, context, holder8.messageImage, mVo.getXmlVo().getPicUrl(), R.drawable.moren, listView);
                holder8.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                holder8.messageImage.setTag(mVo);
                holder8.messageImage.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showTwoItemsDialog(mVo, position);
                        return false;
                    }
                });
                holder8.messageImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        Intent intent = new Intent(MarketApp.app, PictureViewActivity.class);
                        intent.putExtra("filePath", mVo.getXmlVo().getPicUrl());
                        context.startActivity(intent);
                    }
                });
                break;
            case MarketApp.NINE:// 发送名片消息
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder9.avatar, picture, R.drawable.icon, listView);
                holder9.layout.setTag(mVo);
                holder9.layout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                holder9.layout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo m = (MsgChatVo) v.getTag();
                        MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(m.getContent());
                        FriendMesVo friend2 = friendDb.getFriend(xmlVo.getFriendId());
                        if (null != friend2) {
                            if (friend2.getFriendType() == 2) {
                                Intent intent = new Intent(MarketApp.app, PublicAccountDetailsActivity.class);
                                intent.putExtra(MarketApp.FRIEND, friend2);
                                context.startActivity(intent);
                            } else {
                                Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                                intent.putExtra(MarketApp.FRIEND, friend2);
                                intent.putExtra(FriendDetailsActivity.DETAILED, 2);
                                context.startActivity(intent);
                            }
                        }
                    }
                });
                holder9.avatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                        FriendMesVo friend = new FriendMesVo("", userInfo.getAccount(), userInfo.getPicture(), "", userInfo.getUserName());
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                holder9.name.setText(mVo.getXmlVo().getTitle());
                holder9.sign.setText(mVo.getXmlVo().getDescription());
                // 显示名片里的头像
                Utils.downloadImg(true, context, holder9.img, mVo.getXmlVo().getPicUrl(), R.drawable.icon, listView);
                if (mVo.getStatus().equals("0")) {
                    holder9.status.setVisibility(View.GONE);
                } else {
                    holder9.status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.TEN:// 接收名片消息
                // 显示头像
                Utils.downloadImg(true, context, holder10.avatar, friend.getPicture(), R.drawable.icon, listView);
                holder10.layout.setTag(mVo);
                holder10.layout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                holder10.layout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                        MsgChatVo m = (MsgChatVo) v.getTag();
                        MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(m.getContent());
                        String friendType = xmlVo.getTargetType();
                        String friendId = xmlVo.getFriendId();
                        FriendMesVo friend = new FriendMesVo("", friendId, xmlVo.getPicUrl(), "", xmlVo.getTitle());
                        FriendMesVo friend2 = friendDb.getFriend(xmlVo.getFriendId());
                        if (null != friend2) {
                            if (friend2.getFriendType() == 2) {
                                intent = new Intent(MarketApp.app, PublicAccountDetailsActivity.class);
                                intent.putExtra(MarketApp.FRIEND, friend2);
                                context.startActivity(intent);
                            } else {
                                intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                                intent.putExtra(MarketApp.FRIEND, friend);
                                intent.putExtra(FriendDetailsActivity.DETAILED, 2);
                                context.startActivity(intent);
                            }
                        } else {
                            if (friendType.equals("2")) {
                                intent = new Intent(MarketApp.app, PublicAccountDetailsActivity.class);
                                friend.setIsFriend("false");
                                intent.putExtra(MarketApp.FRIEND, friend);
                                context.startActivity(intent);
                            } else {
                                intent = new Intent(MarketApp.app, FriendDetailsActivity.class);
                                intent.putExtra(MarketApp.FRIEND, friend);
                                intent.putExtra(FriendDetailsActivity.DETAILED, 0);
                                context.startActivity(intent);
                            }
                        }
                    }
                });
                holder10.avatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                // 显示名片里的头像
                Utils.downloadImg(true, context, holder10.img, mVo.getXmlVo().getPicUrl(), R.drawable.icon, listView);
                holder10.name.setText(mVo.getXmlVo().getTitle());
                holder10.sign.setText(mVo.getXmlVo().getDescription());
                break;
            case MarketApp.ELEVEN:// send video
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder11.userAvatar, picture, R.drawable.icon, listView);
                holder11.contentLayout.setTag(mVo);
                holder11.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                holder11.contentLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
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
                if (mVo.getStatus().equals("0")) {
                    holder11.status.setVisibility(View.GONE);
                } else {
                    holder11.status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.TWELVE:// receive video
                // 设置头像
                Utils.downloadImg(true, context, holder12.userAvatar, friend.getPicture(), R.drawable.icon, listView);
                holder12.contentLayout.setTag(R.id.tag_first, mVo);
                holder12.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag(R.id.tag_first);
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                if (mVo.getXmlVo().getVideoUrl() != null && mVo.getXmlVo().getVideoUrl().startsWith("http:")) {
                    String fileName = mVo.getXmlVo().getVideoUrl().substring(mVo.getXmlVo().getVideoUrl().lastIndexOf("/") + 1);
                    String filePath = videoDir + "/" + fileName;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        new FileDownloadTask(filePath).execute(mVo.getXmlVo().getVideoUrl());
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
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                break;
            case MarketApp.THIRTEEN:// send location
                // 设置头像
                picture = AdminUtils.getUserInfo(context).getPicture();
                Utils.downloadImg(true, context, holder13.userAvatar, picture, R.drawable.icon, listView);
                holder13.content.setText(mVo.getXmlVo().getLabel());
                holder13.contentLayout.setTag(mVo);
                holder13.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
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
                if (mVo.getStatus().equals("0")) {
                    holder13.status.setVisibility(View.GONE);
                } else {
                    holder13.status.setVisibility(View.VISIBLE);
                }
                break;
            case MarketApp.FOURTEEN:// receive location
                // 设置头像
                Utils.downloadImg(true, context, holder14.userAvatar, friend.getPicture(), R.drawable.icon, listView);
                holder14.content.setText(mVo.getXmlVo().getLabel());
                holder14.contentLayout.setTag(mVo);
                holder14.contentLayout.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        MsgChatVo mVo = (MsgChatVo) v.getTag();
                        showOneItemDialog(mVo, position);
                        return false;
                    }
                });
                holder14.contentLayout.setOnClickListener(new OnClickListener() {

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
                holder14.userAvatar.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, FriendDetailsActivity.class);
                        intent.putExtra(MarketApp.FRIEND, friend);
                        intent.putExtra(FriendDetailsActivity.DETAILED, 1);
                        context.startActivity(intent);
                    }
                });
                break;
            }
        }
        return viewConvert;
    }

    @Override
    public int getItemViewType(int position) {
        String type = msgChatVos.get(position).getType();
        if (type.equals(MarketApp.SEND_TEXT)) {
            return MarketApp.ZERO;
        } else if (type.equals(MarketApp.RECEIVE_TEXT)) {
            return MarketApp.ONE;
        } else if (type.equals(MarketApp.MESSAGE_TIME)) {
            return MarketApp.TWO;
        } else if (type.equals(MarketApp.SEND_SHARE)) {
            return MarketApp.THREE;
        } else if (type.equals(MarketApp.RECEIVE_SHARE)) {
            return MarketApp.FOUR;
        } else if (type.equals(MarketApp.SEND_VOICE)) {
            return MarketApp.FIVE;
        } else if (type.equals(MarketApp.RECEIVE_VOICE)) {
            return MarketApp.SIX;
        } else if (type.equals(MarketApp.SEND_PIC)) {
            return MarketApp.SEVEN;
        } else if (type.equals(MarketApp.RECEIVE_PIC)) {
            return MarketApp.EIGHT;
        } else if (type.equals(MarketApp.SEND_BUSINESSCARD)) {
            return MarketApp.NINE;
        } else if (type.equals(MarketApp.RECEIVE_BUSINESSCARD)) {
            return MarketApp.TEN;
        } else if (type.equals(MarketApp.SEND_VIDEO)) {
            return MarketApp.ELEVEN;
        } else if (type.equals(MarketApp.RECEIVE_VIDEO)) {
            return MarketApp.TWELVE;
        } else if (type.equals(MarketApp.SEND_LOCATION)) {
            return MarketApp.THIRTEEN;
        } else if (type.equals(MarketApp.RECEIVE_LOCATION)) {
            return MarketApp.FOURTEEN;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 15;
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
                    chatInfoDb.Isdelete(mVo, msgChatVos.get(index - 1), msgVo);
                    MarketApp.index = index;
                    ChatActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_SIX);

                    // 直接修改消息
                    MsgChatVo messageVo = chatInfoDb.getMessageVo(mVo);
                    if (messageVo != null) {
                        ChatRecordVo record = new ChatRecordVo(friend.getFriendAccount(), friend.getFriendName(), messageVo.getCreateTime(), 0, friend.getPicture(), 1, messageVo.getContent(), messageVo.getLoginUser(), messageVo.getStatus(), "", "");
                        chatRDb.insertRecord(record, false);
                    } else {
                        chatRDb.updateContent(friend.getFriendAccount());
                    }
                    // 更新消息
                    if (null != FriendListFragment.handler) {
                        FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
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
                    chatInfoDb.Isdelete(mVo, msgChatVos.get(index - 1), msgVo);
                    MarketApp.index = index;
                    ChatActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_SIX);

                    // 直接修改消息
                    MsgChatVo messageVo = chatInfoDb.getMessageVo(mVo);
                    if (messageVo != null) {
                        ChatRecordVo record = new ChatRecordVo(friend.getFriendAccount(), friend.getFriendName(), messageVo.getCreateTime(), 0, friend.getPicture(), 1, messageVo.getContent(), messageVo.getLoginUser(), messageVo.getStatus(), "", "");
                        chatRDb.insertRecord(record, false);
                    } else {
                        chatRDb.updateContent(friend.getFriendAccount());
                    }
                    // 更新消息
                    if (null != FriendListFragment.handler) {
                        FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
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
        // 设置对话框的标题
        builder.setTitle("菜单");
        builder.setItems(R.array.three_items_dialog, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case 0:// 删除消息
                    MsgChatVo msgVo = null;
                    if (msgChatVos.size() > index + 1) {
                        msgVo = msgChatVos.get(index + 1);
                    }
                    chatInfoDb.Isdelete(mVo, msgChatVos.get(index - 1), msgVo);
                    MarketApp.index = index;
                    ChatActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_SIX);

                    // 直接修改消息
                    MsgChatVo messageVo = chatInfoDb.getMessageVo(mVo);
                    if (messageVo != null) {
                        ChatRecordVo record = new ChatRecordVo(friend.getFriendAccount(), friend.getFriendName(), messageVo.getCreateTime(), 0, friend.getPicture(), 1, messageVo.getContent(), messageVo.getLoginUser(), messageVo.getStatus(), "", "");
                        chatRDb.insertRecord(record, false);
                    } else {
                        chatRDb.updateContent(friend.getFriendAccount());
                    }
                    // 更新消息
                    if (null != FriendListFragment.handler) {
                        FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                    }
                    break;
                case 1:// 复制消息
                    ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
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

    // 发送文本
    private static class MessageTypeHolder0 {
        private ImageView userAvatar;
        private TextView msg;
        private TextView tv_home_status;
    }

    // 接收文本
    private static class MessageTypeHolder1 {
        private ImageView userAvatar;
        private TextView msg;
    }

    // 时间
    private static class MessageTypeHolder2 {
        private TextView msg;
    }

    // 发送分享
    private static class MessageTypeHolder3 {
        private ImageView userAvatar;
        private ImageView pic;
        private TextView msg;
        private TextView nick;
        private TextView title;
        private LinearLayout chatinfo_share_ll;
        private TextView status;
    }

    // 接收分享
    private static class MessageTypeHolder4 {
        private ImageView userAvatar;
        private ImageView pic;
        private TextView msg;
        private TextView nick;
        private TextView title;
        private LinearLayout chatinfo_share_ll;
    }

    // 发送语音
    private static class MessageTypeHolder5 {
        private LinearLayout contentLayout;
        private ImageView userAvatar;
        private TextView tv_home_status;
    }

    // 接收语音
    private static class MessageTypeHolder6 {
        private LinearLayout contentLayout;
        private ImageView userAvatar;
    }

    // 发送图片
    private static class MessageTypeHolder7 {
        private ImageView userImage;
        private ImageView messageImage;
        private TextView status;
    }

    // 接收图片
    private static class MessageTypeHolder8 {
        private ImageView userImage;
        private ImageView messageImage;
    }

    // 发送名片
    private static class MessageTypeHolder9 {
        private LinearLayout layout;
        private ImageView avatar;
        private ImageView img;
        private TextView status;
        private TextView name;
        private TextView sign;
    }

    // 接收名片
    private static class MessageTypeHolder10 {
        private LinearLayout layout;
        private ImageView avatar;
        private ImageView img;
        private TextView name;
        private TextView sign;
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

    // 接收地理位置
    private static class MessageTypeHolder14 {
        private RelativeLayout contentLayout;
        private ImageView userAvatar;
        private TextView content;
    }
}
