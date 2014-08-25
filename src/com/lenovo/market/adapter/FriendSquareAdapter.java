package com.lenovo.market.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lenovo.market.R;
import com.lenovo.market.activity.home.PictureViewActivity;
import com.lenovo.market.activity.home.WebViewActivity;
import com.lenovo.market.activity.setting.friendscircle.AlertdialogActivity;
import com.lenovo.market.activity.setting.friendscircle.CommentDialogActivity;
import com.lenovo.market.activity.setting.friendscircle.FriendsCircleActivity;
import com.lenovo.market.common.CommonUtil;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.FriendSquareDBHelper;
import com.lenovo.market.dbhelper.UserDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.DateUtil;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.MFriendZoneCommentVo;
import com.lenovo.market.vo.server.MFriendZoneTopicVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.XmppUtils;

public class FriendSquareAdapter extends BaseAdapter {

    private ArrayList<MFriendZoneTopicVo> friendSquareList;
    private UserDBHelper uDb;
    private FriendInfoDBHelper fDb;
    private FriendSquareDBHelper fsDb;
    private String toName;// 回复给某人
    private int indexNum = -1;
    private ListView actualListView;
    private String account;
    private long endTime;// 最后一次点赞的时间
    private Context context;

    public FriendSquareAdapter(Context context, ArrayList<MFriendZoneTopicVo> friendSquareList, ListView actualListView, String account) {
        super();
        this.friendSquareList = friendSquareList;
        this.uDb = new UserDBHelper();
        this.fDb = new FriendInfoDBHelper();
        this.fsDb = new FriendSquareDBHelper();
        this.actualListView = actualListView;
        this.account = account;
        this.context = context;
    }

    @Override
    public int getCount() {
        return friendSquareList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return friendSquareList.get(arg0);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder holder = null;
        final MFriendZoneTopicVo mv = friendSquareList.get(position);
        if (convertView == null) {
            convertView = View.inflate(MarketApp.app, R.layout.listitem_friendsquare, null);
            holder = new ViewHolder();
            holder.pic = (ImageView) convertView.findViewById(R.id.friendsquare_iv);
            holder.content = (TextView) convertView.findViewById(R.id.friendsquare_tv);
            holder.name = (TextView) convertView.findViewById(R.id.friendsquare_name_tv);
            holder.date = (TextView) convertView.findViewById(R.id.friendsquare_date_tv);
            holder.friendsquare_tv_z = (TextView) convertView.findViewById(R.id.friendsquare_tv_z);
            holder.friendsquare_tv_p = (TextView) convertView.findViewById(R.id.friendsquare_tv_p);
            holder.friendsquare_z_tv = (TextView) convertView.findViewById(R.id.friendsquare_z_tv);
            holder.friendsquare_ll = (LinearLayout) convertView.findViewById(R.id.friendsquare_ll);
            holder.friendsquare_img = (ImageView) convertView.findViewById(R.id.friendsquare_img);
            holder.friendsquare_gv = (GridView) convertView.findViewById(R.id.friendsquare_gv);
            holder.friendsquare_delete_tv = (TextView) convertView.findViewById(R.id.friendsquare_delete_tv);
            holder.friendsquare_share_ll = (LinearLayout) convertView.findViewById(R.id.friendsquare_share_ll);
            holder.friendsquare_share_tv = (TextView) convertView.findViewById(R.id.friendsquare_share_tv);
            holder.friendsquare_sharetitle = (TextView) convertView.findViewById(R.id.friendsquare_sharetitle);
            holder.friendsquare_shareimg = (ImageView) convertView.findViewById(R.id.friendsquare_shareimg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.pic.setVisibility(View.GONE);
            holder.content.setText("");
            holder.name.setText("");
            holder.date.setText("");
            holder.friendsquare_ll.setVisibility(View.GONE);
            holder.friendsquare_ll.removeAllViews();
            holder.friendsquare_ll.addView(holder.friendsquare_z_tv);
            holder.friendsquare_z_tv.setText("");
            holder.friendsquare_z_tv.setVisibility(View.GONE);
            holder.friendsquare_img.setVisibility(View.GONE);
            holder.friendsquare_gv.setVisibility(View.GONE);
            holder.friendsquare_delete_tv.setVisibility(View.INVISIBLE);
        }

        if (mv.getIsShare().equals("2")) {
            holder.friendsquare_share_ll.setVisibility(View.VISIBLE);
            holder.friendsquare_share_tv.setVisibility(View.VISIBLE);
            holder.friendsquare_sharetitle.setText(mv.getShareTitle());
            // 加载图片
            if (mv.getImages().size() > 0) {
                // 异步下载图片
                Utils.downloadImg(false, context, holder.friendsquare_shareimg, mv.getImages().get(0).getUrl(), R.drawable.albumshareurl_icon, actualListView);
                holder.friendsquare_share_ll.setTag(R.id.shareurl, mv.getShareUrl());
                holder.friendsquare_share_ll.setTag(R.id.sharetitle, mv.getShareTitle());
                holder.friendsquare_share_ll.setTag(R.id.sharefilepath, mv.getImages().get(0).getUrl());
                holder.friendsquare_share_ll.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String url = (String) v.getTag(R.id.shareurl);
                        String title = (String) v.getTag(R.id.sharetitle);
                        String filepath = (String) v.getTag(R.id.sharefilepath);
                        Intent intent = new Intent(MarketApp.app, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.URL, url);
                        intent.putExtra(WebViewActivity.TITLE, title);
                        intent.putExtra(WebViewActivity.SHARETITLE, title);
                        intent.putExtra(WebViewActivity.SHAREFILEPATH, filepath);
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.friendsquare_shareimg.setImageResource(R.drawable.albumshareurl_icon);
            }
        } else {
            holder.friendsquare_share_ll.setVisibility(View.GONE);
            holder.friendsquare_share_tv.setVisibility(View.GONE);
        }

        // 删除当前登录用户的主信息
        holder.friendsquare_delete_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketApp.app, AlertdialogActivity.class);
                intent.putExtra("index", position + "");
                intent.putExtra("mv", mv);
                context.startActivity(intent);
            }
        });

        // 发照片是可以不发送文字
        if (TextUtils.isEmpty(mv.getContent())) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(mv.getContent());
        }

        // 显示照片
        if (mv.getImages() != null && mv.getImages().size() > 0 && mv.getIsShare().equals("1")) {
            if (mv.getImages().size() == 1) {
                holder.friendsquare_img.setVisibility(View.VISIBLE);
                final String filePath = mv.getImages().get(0).getUrl();

                // 异步下载图片
                Utils.downloadImg(false, context, holder.friendsquare_img, filePath, R.drawable.moren, actualListView);
                holder.friendsquare_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MarketApp.app, PictureViewActivity.class);
                        intent.putExtra("filePath", filePath);
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.friendsquare_gv.setVisibility(View.VISIBLE);
                LayoutParams layoutParams = holder.friendsquare_gv.getLayoutParams();
                if (mv.getImages().size() > 6) {
                    layoutParams.height = Utils.dip2px(MarketApp.app, 229);
                } else if (mv.getImages().size() > 3) {
                    layoutParams.height = Utils.dip2px(MarketApp.app, 152);
                } else {
                    layoutParams.height = Utils.dip2px(MarketApp.app, 75);
                }
                holder.friendsquare_gv.setLayoutParams(layoutParams);
                holder.friendsquare_gv.setAdapter(new GridAdapter(context, mv.getImages(), holder.friendsquare_gv));
            }
        }

        // 显示发布时间
        holder.date.setText(DateUtil.getHoursOrDaysAgo(mv.getCreateTime()));

        // 检查集合中有没有数据
        if (!TextUtils.isEmpty(mv.getId())) {
            holder.friendsquare_tv_p.setVisibility(View.VISIBLE);
            holder.friendsquare_tv_z.setVisibility(View.VISIBLE);
            holder.friendsquare_tv_p.setText("评论");
            holder.pic.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.VISIBLE);
        } else {
            holder.friendsquare_tv_p.setVisibility(View.INVISIBLE);
            holder.friendsquare_tv_z.setVisibility(View.INVISIBLE);
        }

        // 显示已赞和评论的内容
        final LinearLayout friendsquare_ll = holder.friendsquare_ll;
        final TextView friendsquare_z_tv = holder.friendsquare_z_tv;
        String name = "";
        if (mv.getComments() != null && mv.getComments().size() > 0) {
            holder.friendsquare_ll.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            sb.append("❤  ");
            for (int i = 0; i < mv.getComments().size(); i++) {
                if (mv.getComments().get(i).getCreateUser().equals(AdminUtils.getUserInfo(MarketApp.app).getAccount())) {
                    name = AdminUtils.getUserInfo(MarketApp.app).getUserName();
                } else {
                    FriendMesVo friend = fDb.getFriend(mv.getComments().get(i).getCreateUser());
                    if (friend == null) {
                        name = mv.getComments().get(i).getCreateUser();
                    } else {
                        name = friend.getFriendName();
                    }
                }
                final String name1 = name;
                if (mv.getComments().get(i).getType().equals("1")) {
                    sb.append(name + "  ");
                    holder.friendsquare_z_tv.setText(sb.toString());
                    holder.friendsquare_z_tv.setVisibility(View.VISIBLE);
                } else {
                    final TextView tv = new TextView(MarketApp.app);
                    tv.setTextColor(MarketApp.app.getResources().getColor(R.color.black));
                    if (TextUtils.isEmpty(mv.getComments().get(i).getPid())) {
                        String comments = name + ":  " + mv.getComments().get(i).getContent();
                        int indexOf = comments.indexOf(":");
                        SpannableStringBuilder style = new SpannableStringBuilder(comments);
                        style.setSpan(new ForegroundColorSpan(MarketApp.app.getResources().getColor(R.color.blue21)), 0, indexOf, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        tv.setText(style);
                    } else {
                        if (fsDb.getCreateUser(mv.getComments().get(i).getPid(), account).equals(AdminUtils.getUserInfo(MarketApp.app).getAccount())) {
                            toName = AdminUtils.getUserInfo(MarketApp.app).getUserName();
                        } else {
                            FriendMesVo friend = fDb.getFriend(fsDb.getCreateUser(mv.getComments().get(i).getPid(), account));
                            if (friend == null) {
                                toName = fsDb.getCreateUser(mv.getComments().get(i).getPid(), account);
                            } else {
                                toName = friend.getFriendName();
                            }
                        }
                        String comments = name + "回复" + toName + ":  " + mv.getComments().get(i).getContent();
                        int indexOfOne = comments.indexOf("回复");
                        int indexOfTwo = comments.indexOf(":");
                        SpannableStringBuilder style = new SpannableStringBuilder(comments);
                        style.setSpan(new ForegroundColorSpan(MarketApp.app.getResources().getColor(R.color.blue21)), 0, indexOfOne, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(MarketApp.app.getResources().getColor(R.color.blue21)), indexOfOne + 2, indexOfTwo, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        tv.setText(style);
                    }
                    tv.setTag(R.id.tag_first, i);
                    tv.setTag(R.id.tag_second, position);
                    holder.friendsquare_ll.addView(tv);
                    tv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int index = (Integer) tv.getTag(R.id.tag_first);
                            FriendsCircleActivity.listIndex = (Integer) tv.getTag(R.id.tag_second);
                            if (indexNum != -1 && indexNum < index) {
                                index = index - 1;
                                indexNum = -1;
                            }
                            if (!mv.getComments().get(index).getCreateUser().equals(AdminUtils.getUserInfo(MarketApp.app).getAccount())) {
                                FriendsCircleActivity.getContent("回复 " + name1);
                                FriendsCircleActivity.getVisibility(View.VISIBLE);
                                MarketApp.PID = mv.getComments().get(index).getId();
                            } else {
                                FriendsCircleActivity.getVisibility(View.GONE);
                                mv.getComments().remove(tv.getTag());
                                Intent intent = new Intent(MarketApp.app, CommentDialogActivity.class);
                                intent.putExtra("index", index + "");
                                intent.putExtra("position", position + "");
                                context.startActivity(intent);
                            }
                        }
                    });
                }
            }
        } else {
            holder.friendsquare_ll.setVisibility(View.GONE);
        }

        // 显示头像
        if (!TextUtils.isEmpty(mv.getCreateUser())) {
            if (mv.getCreateUser().equals(AdminUtils.getUserInfo(MarketApp.app).getAccount())) {
                UserVo myInfo = uDb.getUserInfo(mv.getCreateUser());
                mv.setCreateUserPic(myInfo.getPicture());
                holder.name.setText(myInfo.getUserName());
                holder.friendsquare_delete_tv.setVisibility(View.VISIBLE);
            } else {
                FriendMesVo friend = fDb.getFriend(mv.getCreateUser());
                if (null != friend) {
                    mv.setCreateUserPic(friend.getPicture());
                    holder.name.setText(friend.getFriendName());
                }
            }
            String pic = mv.getCreateUserPic();
            // 异步下载图片
            Utils.downloadImg(true, context, holder.pic, pic, R.drawable.icon, actualListView);
        }

        // 评论
        holder.friendsquare_tv_p.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MarketApp.PID = "";
                FriendsCircleActivity.getContent("");
                FriendsCircleActivity.listIndex = position;
                FriendsCircleActivity.getVisibility(View.VISIBLE);
            }
        });

        // 赞
        if (holder.friendsquare_z_tv.getVisibility() == View.GONE || !fsDb.getZan(account, mv.getId())) {
            holder.friendsquare_tv_z.setText("❤ 赞");
        } else {
            holder.friendsquare_tv_z.setText("已赞");
        }
        final TextView friendsquare_tv_z = holder.friendsquare_tv_z;
        holder.friendsquare_tv_z.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!MarketApp.network_available) {
                    Utils.showToast(context, "网络不可用,请连接网络！");
                    return;
                }
                String tv_z = friendsquare_tv_z.getText().toString().trim();
                final long startTime = System.currentTimeMillis();
                if (startTime - endTime < 1000) {
                    return;
                }
                if (tv_z.equals("❤ 赞")) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    final MFriendZoneCommentVo mVO = new MFriendZoneCommentVo(mv.getId(), "", "", "1", AdminUtils.getUserInfo(MarketApp.app).getAccount(), currentTimeMillis + "");
                    String id = Utils.getDeviceUUID();
                    mVO.setId(id);
                    mVO.setLoginUser(AdminUtils.getUserInfo(MarketApp.app).getAccount());
                    fsDb.insertCommentMessage(mVO);
                    LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
                    maps.put("id", id);
                    maps.put("topicId", mv.getId());
                    maps.put("content", null);
                    maps.put("pid", null);
                    maps.put("type", "1");
                    maps.put("createUser", AdminUtils.getUserInfo(MarketApp.app).getAccount());
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
                                    endTime = startTime;
                                    // Utils.showToast(MarketApp.app, result);
                                    friendsquare_ll.setVisibility(View.VISIBLE);
                                    friendsquare_z_tv.setVisibility(View.VISIBLE);
                                    friendsquare_z_tv.setText("❤   " + fsDb.getZanList(mv.getId()) + AdminUtils.getUserInfo(MarketApp.app).getUserName() + "  ");
                                    mv.getComments().add(mVO);
                                    friendsquare_tv_z.setText("已赞");
                                    ArrayList<FriendMesVo> friendAll = fDb.getFriendAll("1");
                                    for (int i = 0; i < friendAll.size(); i++) {
                                        org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message(Utils.getJidFromUsername(friendAll.get(i).getFriendAccount()), org.jivesoftware.smack.packet.Message.Type.chat);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(mVO);
                                        MsgXmlVo mxVo = new MsgXmlVo();
                                        mxVo.setMsgType(MarketApp.SEND_TEXT);
                                        mxVo.setContent(json);
                                        mxVo.setTargetType("2");
                                        mxVo.setCreateTime(mv.getCreateTime());
                                        String sendxml = XMLUtil.createXML(mxVo, MarketApp.SEND_TEXT);
                                        message.setBody(sendxml);
                                        if (XmppUtils.getInstance().getConnection() != null) {
                                            XmppUtils.getInstance().getConnection().sendPacket(message);
                                        } else {
                                            Utils.showToast(context, "连接已经断开,正在重连,请稍后再试...");
                                            CommonUtil.ConnectionXmpp(context);
                                        }
                                    }
                                } else {
                                    Utils.showToast(MarketApp.app, result);
                                }
                            }
                        }

                        @Override
                        public void onCancel() {
                        }
                    }, maps, MarketApp.FRIENDSQUARE_SENDCOMMENT, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_15);
                } else {
                    String id = "";
                    for (int i = 0; i < mv.getComments().size(); i++) {
                        if (mv.getComments().get(i).getType().equals("1") && mv.getComments().get(i).getCreateUser().equals(AdminUtils.getUserInfo(MarketApp.app).getAccount())) {
                            id = mv.getComments().get(i).getId();
                            fsDb.delCommentMessage(id);
                            mv.getComments().remove(i);
                            indexNum = i;
                            break;
                        }
                    }
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
                                    endTime = startTime;
                                    friendsquare_tv_z.setText("❤ 赞");
                                    String zanList = fsDb.getZanList(mv.getId());
                                    if (TextUtils.isEmpty(zanList)) {
                                        friendsquare_z_tv.setVisibility(View.GONE);
                                    } else {
                                        friendsquare_z_tv.setText("❤   " + zanList);
                                        friendsquare_z_tv.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Utils.showToast(MarketApp.app, result);
                                }
                            }
                        }

                        @Override
                        public void onCancel() {
                        }
                    }, maps, MarketApp.FRIENDSQUARE_CLEARCOMMENT, MarketApp.FRIENDSQUARE, TaskConstant.GET_DATA_17);
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private ImageView pic;
        private TextView content;
        private TextView name;
        private TextView date;
        private TextView friendsquare_tv_z;
        private TextView friendsquare_tv_p;
        private TextView friendsquare_z_tv;
        private LinearLayout friendsquare_ll;
        private ImageView friendsquare_img;
        private GridView friendsquare_gv;
        private TextView friendsquare_delete_tv;
        private LinearLayout friendsquare_share_ll;
        private TextView friendsquare_share_tv;
        private TextView friendsquare_sharetitle;
        private ImageView friendsquare_shareimg;
    }
}
