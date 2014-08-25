package com.lenovo.market.vo.xmpp;

import java.util.ArrayList;

import com.lenovo.market.vo.server.GraphicVo;


public class MsgXmlVo {

    private String msgType;
    private String createTime;
    private String fromUserName;
    private String toUserName;

    private String MsgId;// 消息id，64位整型 (备用字段)
    private String FriendId;// 好友id
    private String TargetType;// 判断是普通账号、公众账号还是朋友圈
    private String Content;// 文本内容
    private String MediaId;// 图片、语音、视频id
    private String Format;// 语音格式，如amr，speex等
    private String Title;// 图文、音乐、视频消息标题
    private String Description;// 图文、音乐、视频消息描述
    private String Location_X;// 地理位置维度
    private String Location_Y;// 地理位置经度
    private String Scale;// 地图缩放大小
    private String Label;// 地理位置信息
    private String MusicUrl;// 音乐链接
    private String HQMusicUrl;// 高质量音乐链接，WIFI环境优先使用该链接播放音乐
    private String ThumbMediaId;// 缩略图的媒体id，通过上传多媒体文件，得到的id

    private String ArticleCount;// 图文消息个数，限制为10条以内
    private String PicUrl;// 图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
    private String VideoUrl;// 视频路径
    private String VoiceUrl;// 音频路径
    private String Url;// 点击图文消息跳转链接
    private String Event;// 自定义菜单
    private String EventKey;// 自定义菜单
    private String Errcode;// 错误码
    private String Errmsg;// 错误消息

    private ArrayList<GraphicVo> nVos = new ArrayList<GraphicVo>();

    public MsgXmlVo() {
        super();
    }

    public String getErrcode() {
        return Errcode;
    }

    public void setErrcode(String errcode) {
        Errcode = errcode;
    }

    public String getErrmsg() {
        return Errmsg;
    }

    public void setErrmsg(String errmsg) {
        Errmsg = errmsg;
    }

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public ArrayList<GraphicVo> getnVos() {
        return nVos;
    }

    public void setnVos(ArrayList<GraphicVo> nVos) {
        this.nVos = nVos;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public String getVoiceUrl() {
        return VoiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        VoiceUrl = voiceUrl;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public String getTargetType() {
        return TargetType;
    }

    public void setTargetType(String targetType) {
        TargetType = targetType;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation_X() {
        return Location_X;
    }

    public void setLocation_X(String location_X) {
        Location_X = location_X;
    }

    public String getLocation_Y() {
        return Location_Y;
    }

    public void setLocation_Y(String location_Y) {
        Location_Y = location_Y;
    }

    public String getScale() {
        return Scale;
    }

    public void setScale(String scale) {
        Scale = scale;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getMusicUrl() {
        return MusicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        MusicUrl = musicUrl;
    }

    public String getHQMusicUrl() {
        return HQMusicUrl;
    }

    public void setHQMusicUrl(String hQMusicUrl) {
        HQMusicUrl = hQMusicUrl;
    }

    public String getThumbMediaId() {
        return ThumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        ThumbMediaId = thumbMediaId;
    }

    public String getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(String articleCount) {
        ArticleCount = articleCount;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
