package com.lenovo.market.vo.local;

import org.jivesoftware.smackx.packet.VCard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;

/**
 * 状态需要自己set<br/>
 * 调用load()填充数据目前只有 nice sign mContact age<br/>
 * 也可以自己填充 -- 构造函数<br>
 * 添加字段需要在 writeToParcel方法 和ChildInfo(Parcel in)中添加对应的数据<br>
 * 
 * @author muqiang
 * 
 */
public class ChildInfoVo implements Comparable<ChildInfoVo>, Parcelable {

    private int unReadCount = 0;// 未读条目
    private Bitmap avatar;// 头像
    private String nick; // 昵称
    private String sign; // 个性签名
    private String username; // 用户名
    private String mRemarkName;// 备注
    private String groupName;// 组名
    private boolean IsChecked = false;
    private boolean block;// 是否封锁
    private boolean showAvatar;// 是否显示头像
    private String mAid;
    private String sex;// 性别
    private String avatarPath;// 头像网络地址
    private String temjid = null; // jid
    private String email = null; // 邮箱
    private String iconStr = null; // 头像的URL地址
    private VCard mVcard = null;
    private double distance;// 距离--单位米
    private double offlineDuration = 0.0;// 上次在线时间,在搜索附近的人中用到-ybb
    private String avatarVersion;// 头像ID
    private String phone;// 手机号
    private int state = 3;// 在线状态
    private String age;// 年龄
    private boolean friend;// wsc 仅在 在附近中判断是否是好友 有用！！
    private boolean showCard;// 显示名片
    private boolean lbs = false;// ---ybb
    private String affiliation;// 群成员属性 (会员，管理员，创建者) wsc
    private String roomId;// 群房间Id wsc
    private boolean openChat = false;

    public ChildInfoVo(String nick, String sign, String username, String mRemarkName, String sex, String avatarPath, double distance, double offlineDuration) {
        super();
        this.nick = nick;
        this.sign = sign;
        this.username = username;
        this.mRemarkName = mRemarkName;
        this.sex = sex;
        this.avatarPath = avatarPath;
        this.distance = distance;
        this.offlineDuration = offlineDuration;
    }

    public String getAvatarVersion() {
        return avatarVersion;
    }

    public void setAvatarVersion(String avatarVersion) {
        this.avatarVersion = avatarVersion;
    }

    public boolean isOpenChat() {
        return openChat;
    }

    public void setOpenChat(boolean openChat) {
        this.openChat = openChat;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    /**
     * 是否是群<br>
     * 可能有错 by wsc
     * 
     * @return
     * @author wsc
     */
    public boolean isGroup() {// 是否是群
        if (username != null)
            return username.contains(MarketApp.ROOM_SERVER_NAME);
        else
            return false;
    }

    public boolean isShowCard() {
        return showCard;
    }

    public void setShowCard(boolean showCard) {
        this.showCard = showCard;
    }

    public VCard getmVcard() {
        return mVcard;
    }

    public void setmVcard(VCard mVcard) {
        this.mVcard = mVcard;
    }

    /**
     * Available = 0 Away = 1 UnAvailable = 2 Busy = 3
     */
    /**
     * is it login form mobile
     */
    private boolean isLookMobile = false;

    /**
     * the holder position in the mHolder we keep
     * 
     * see class PhoneExpandableList
     */
    private int position = -1;

    private String mResource = null;

    public String getTemjid() {
        return temjid;
    }

    public void setTemjid(String temjid) {
        this.temjid = temjid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIconStr() {
        return iconStr;
    }

    public void setIconStr(String iconStr) {
        this.iconStr = iconStr;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isShowAvatar() {
        return showAvatar;
    }

    public void setShowAvatar(boolean showAvatar) {
        this.showAvatar = showAvatar;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ChildInfoVo(String nick, Bitmap avatar, String sign, String Contact, String aid, String remark) {

        this.nick = nick;
        this.sign = sign;
        this.avatar = avatar;
        this.username = Contact;
        this.mAid = aid;
        this.mRemarkName = remark;

    }

    public ChildInfoVo(String nick) {
        this.nick = nick;
    }

    public ChildInfoVo() {

    }

    /**
     * 调getNick()
     * 
     * @author muqiang
     */
    @Deprecated
    public String getNickName() {

        if (nick != null && nick.length() > 0) {
            return nick;
        }
        return username;
    }

    public String getPureNickName() {

        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username.contains("@"))
            username = username.split("@")[0];
        this.username = username;
    }

    /**
     * 如果没有特殊需求 一定要显示昵称 则请调用getShowName()
     * 
     * @return
     * @author muqiang
     * @date 2013-3-19 下午2:40:26
     */
    public String getNick() {

        if (!TextUtils.isEmpty(nick))
            return nick;
        return username;
    }

    public String getRemarkName() {

        return mRemarkName;
    }

    public void setRemarkName(String name) {
        this.mRemarkName = name;
    }

    public String getSign() {
        return sign;
    }

    public String getJid() {
        if (TextUtils.isEmpty(username)) {
            return "";
        }
        return username + "@" + MarketApp.ROOM_SERVER_NAME;

    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap a) {
        avatar = a;
    }

    public void setImageId(Bitmap avatar) {
        this.avatar = avatar;
    }

    public boolean getChecked() {
        return this.IsChecked;
    }

    public void setChecked(boolean value) {
        this.IsChecked = value;
    }

    public boolean getMobile() {
        return false;
    }

    public void setMobile(boolean value) {
        this.isLookMobile = value;
    }

    public String getResource() {
        return this.mResource;
    }

    public void setResource(String value) {
        this.mResource = value;
    }

    public void setPosition(int value) {
        this.position = value;
    }

    public int getPosition() {
        return this.position;
    }

    public String getAid() {

        return mAid;
    }

    public int compareTo(ChildInfoVo another) {
        Integer me = state;
        Integer his = another.getState();
        return me.compareTo(his);
    }

    public void setGroup(String name) {
        this.groupName = name;
    }

    public String getGroup() {

        return groupName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String Phone() {

        return phone;
    }

    public void setDistance(String dis) {
        try {
            this.distance = Double.parseDouble(dis);
        } catch (Exception e) {
            e.printStackTrace();
            this.distance = 0;
        }

    }

    public double getDistance() {

        return distance;
    }

    public void setOfflineDuration(double dis) {

        this.offlineDuration = dis;
    }

    public double getOfflineDuration() {

        return offlineDuration;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setAvatarPath(String path) {

        this.avatarPath = path;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    /**
     * 
     * @param path
     *            路径为本地路径
     * @author wsc
     */
    public void setAvatarByPath(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        setAvatar(bitmap);
    }

    public boolean isOnline() {
        if (state == MarketApp.USER_ONLINE || state == MarketApp.USER_LEAVE || state == MarketApp.USER_BUSY) {
            return true;
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nick);
        dest.writeString(sign);
        dest.writeString(username);
        dest.writeString(mRemarkName);
        dest.writeString(groupName);
        dest.writeString(sex);
        dest.writeString(avatarPath);
        if (avatar != null && !avatar.isRecycled())
            dest.writeParcelable(avatar, flags);
        else
            dest.writeParcelable(null, flags);
        dest.writeString(mAid);
        dest.writeInt(block ? 1 : 0);
        dest.writeInt(showAvatar ? 1 : 0);
        dest.writeInt(friend ? 1 : 0);

        dest.writeString(temjid);
        dest.writeString(email);
        dest.writeString(iconStr);
        dest.writeInt(unReadCount);
        dest.writeDouble(distance);
        dest.writeDouble(offlineDuration);
        dest.writeString(phone);
        dest.writeInt(state);
        dest.writeString(age);
        dest.writeInt(isLookMobile ? 1 : 0);
        dest.writeInt(position);
        dest.writeString(mResource);
        dest.writeInt(IsChecked ? 1 : 0);
        dest.writeInt(showCard ? 1 : 0);
        dest.writeInt(lbs ? 1 : 0);
        dest.writeString(affiliation);
        dest.writeInt(openChat ? 1 : 0);
    }

    public static final Parcelable.Creator<ChildInfoVo> CREATOR = new Parcelable.Creator<ChildInfoVo>() {
        public ChildInfoVo createFromParcel(Parcel in) {
            return new ChildInfoVo(in);
        }

        public ChildInfoVo[] newArray(int size) {
            return new ChildInfoVo[size];
        }
    };

    private ChildInfoVo(Parcel in) {
        nick = in.readString();
        sign = in.readString();
        username = in.readString();
        mRemarkName = in.readString();
        groupName = in.readString();
        sex = in.readString();
        avatarPath = in.readString();
        avatar = in.readParcelable(null);
        mAid = in.readString();
        block = in.readInt() == 1 ? true : false;
        showAvatar = in.readInt() == 1 ? true : false;
        friend = in.readInt() == 1 ? true : false;
        temjid = in.readString();
        email = in.readString();
        iconStr = in.readString();
        unReadCount = in.readInt();
        distance = in.readDouble();
        offlineDuration = in.readDouble();
        phone = in.readString();
        state = in.readInt();
        age = in.readString();
        isLookMobile = in.readInt() == 1 ? true : false;
        position = in.readInt();
        mResource = in.readString();
        IsChecked = in.readInt() == 1 ? true : false;
        showCard = in.readInt() == 1 ? true : false;
        lbs = in.readInt() == 1 ? true : false;
        affiliation = in.readString();
        openChat = in.readInt() == 1 ? true : false;
    }

    /**
     * 用来判断list里面,是否已经存在该ChildInfo,主要用在搜索附近的人 <br>
     * 判断条件--username
     * 
     * @author muqiang
     */
    @Override
    public boolean equals(Object o) {
        ChildInfoVo info = (ChildInfoVo) o;
        if (info.getUsername().equals(this.username)) {
            return true;
        } else {
            return false;
        }
    }
}
