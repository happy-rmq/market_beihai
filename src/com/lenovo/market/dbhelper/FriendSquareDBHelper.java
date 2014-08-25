package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.lenovo.market.activity.setting.friendscircle.FriendsCircleActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.FriendInfoTable;
import com.lenovo.market.dbhelper.DatabaseContract.FriendSquareCommentsTable;
import com.lenovo.market.dbhelper.DatabaseContract.FriendSquareImgTable;
import com.lenovo.market.dbhelper.DatabaseContract.FriendSquareShareTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.vo.server.MFriendZoneCommentVo;
import com.lenovo.market.vo.server.MFriendZoneImageVo;
import com.lenovo.market.vo.server.MFriendZoneTopicVo;

public class FriendSquareDBHelper {

    private MarketDBHelper marketDB;

    public FriendSquareDBHelper() {
        super();
        marketDB = MarketDBHelper.getInstance(MarketApp.app);
        if (!marketDB.db.isOpen())
            marketDB.open();
    }

    /**
     * 查询朋友圈信息(分页)
     * 
     * @return
     */
    public ArrayList<MFriendZoneTopicVo> getFriendSquareList(int totalCount, String account) {
        marketDB.getDb();
        FriendsCircleActivity.DBindex = totalCount - MarketApp.COUNT;
        String getActiveMenu = "select * from " + FriendSquareShareTable.TABLE_NAME + " where " + FriendSquareShareTable.COLUMN_NAME_LOGINUSER + " = '" + account + "' order by createTime desc limit " + MarketApp.COUNT + " offset " + FriendsCircleActivity.DBindex;
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        ArrayList<MFriendZoneTopicVo> messageList = new ArrayList<MFriendZoneTopicVo>();
        while (cursor.moveToNext()) {
            MFriendZoneTopicVo mv = new MFriendZoneTopicVo(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
            mv.setId(cursor.getString(0));
            messageList.add(mv);
            mv = null;
        }
        cursor.close();
        return messageList;
    }

    /**
     * 查询朋友圈有多少条主信息
     * 
     * @return
     */
    public int getFriendSquareCount() {
        marketDB.getDb();
        String getActiveMenu = "select count(*) from " + FriendSquareShareTable.TABLE_NAME + " where " + FriendSquareShareTable.COLUMN_NAME_LOGINUSER + " = '" + AdminUtils.getUserInfo(MarketApp.app).getAccount() + "'";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = Integer.parseInt(cursor.getString(0));
        }
        return count;
    }

    /**
     * 插入新消息
     * 
     * @param vo
     */
    public long insertNewMessage(MFriendZoneTopicVo vo) {
        ContentValues values = new ContentValues();
        values.put(FriendSquareShareTable.COLUMN_NAME_ID, vo.getId());
        values.put(FriendSquareShareTable.COLUMN_NAME_CONTENT, vo.getContent());
        values.put(FriendSquareShareTable.COLUMN_NAME_SETTING, vo.getSetting());
        values.put(FriendSquareShareTable.COLUMN_NAME_ISSHARE, vo.getIsShare());
        values.put(FriendSquareShareTable.COLUMN_NAME_SHARETITLE, vo.getShareTitle());
        values.put(FriendSquareShareTable.COLUMN_NAME_SHAREURL, vo.getShareUrl());
        values.put(FriendSquareShareTable.COLUMN_NAME_CREATEUSER, vo.getCreateUser());
        values.put(FriendSquareShareTable.COLUMN_NAME_CREATETIME, vo.getCreateTime());
        values.put(FriendSquareShareTable.COLUMN_NAME_LOGINUSER, vo.getLoginUser());
        marketDB.getDb();
        Long id = marketDB.db.insert(FriendSquareShareTable.TABLE_NAME, null, values);
        return id;
    }

    /**
     * 删除消息
     * 
     */
    public void delCMessage(String id) {
        marketDB.getDb();
        marketDB.db.delete(FriendSquareShareTable.TABLE_NAME, FriendSquareShareTable.COLUMN_NAME_ID + " = ? ", new String[] { id });
    }

    /**
     * 查询朋友圈评论信息
     * 
     * @return
     */
    private boolean getFriendSquareCommentIS(String id) {
        marketDB.getDb();
        boolean blean = false;
        String getActiveMenu = "select 1 from " + FriendSquareCommentsTable.TABLE_NAME + " where " + FriendSquareCommentsTable.COLUMN_NAME_LOGINUSER + " = '" + AdminUtils.getUserInfo(MarketApp.app).getAccount() + "' and " + FriendSquareCommentsTable.COLUMN_NAME_ID + " = '" + id + "' order by createTime";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        if (cursor.moveToFirst()) {
            blean = true;
        }
        return blean;
    }

    /**
     * 插入评论及赞消息
     * 
     * @param vo
     */
    public long insertCommentMessage(MFriendZoneCommentVo vo) {
        boolean is = getFriendSquareCommentIS(vo.getId());
        long id = -1;
        if (!is) {
            ContentValues values = new ContentValues();
            values.put(FriendSquareCommentsTable.COLUMN_NAME_ID, vo.getId());
            values.put(FriendSquareCommentsTable.COLUMN_NAME_TOPICID, vo.getTopicId());
            values.put(FriendSquareCommentsTable.COLUMN_NAME_CONTENT, vo.getContent());
            values.put(FriendSquareCommentsTable.COLUMN_NAME_PID, vo.getPid());
            values.put(FriendSquareCommentsTable.COLUMN_NAME_TYPE, vo.getType());
            values.put(FriendSquareCommentsTable.COLUMN_NAME_CREATEUSER, vo.getCreateUser());
            values.put(FriendSquareCommentsTable.COLUMN_NAME_CREATETIME, vo.getCreateTime());
            values.put(FriendSquareCommentsTable.COLUMN_NAME_LOGINUSER, vo.getLoginUser());
            marketDB.getDb();
            id = marketDB.db.insert(FriendSquareCommentsTable.TABLE_NAME, null, values);
        }
        return id;
    }

    /**
     * 删除赞及评论
     * 
     */
    public boolean delCommentMessage(String id) {
        boolean is = getFriendSquareCommentIS(id);
        if (is) {
            marketDB.getDb();
            marketDB.db.delete(FriendSquareCommentsTable.TABLE_NAME, FriendSquareCommentsTable.COLUMN_NAME_ID + " = ? ", new String[] { id });
            return true;
        }
        return false;
    }

    /**
     * 删除主信息相关的所有赞及评论
     * 
     */
    public void delCommentMessageAll(String id) {
        marketDB.getDb();
        marketDB.db.delete(FriendSquareCommentsTable.TABLE_NAME, FriendSquareCommentsTable.COLUMN_NAME_TOPICID + " = ? ", new String[] { id });
    }

    /**
     * 查询朋友圈评论信息
     * 
     * @return
     */
    public ArrayList<MFriendZoneCommentVo> getFriendSquareCommenList(String id, String account) {
        marketDB.getDb();
        String getActiveMenu = "select * from " + FriendSquareCommentsTable.TABLE_NAME + " where " + FriendSquareCommentsTable.COLUMN_NAME_LOGINUSER + " = '" + account + "' and topicId = '" + id + "' order by createTime";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        ArrayList<MFriendZoneCommentVo> messageList = new ArrayList<MFriendZoneCommentVo>();
        while (cursor.moveToNext()) {
            MFriendZoneCommentVo mv = new MFriendZoneCommentVo(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            mv.setId(cursor.getString(0));
            messageList.add(mv);
            mv = null;
        }
        return messageList;
    }

    /**
     * 查询朋友圈分享信息
     * 
     * @return
     */
    public boolean getFriendSquareIS(String id) {
        marketDB.getDb();
        boolean blean = false;
        String getActiveMenu = "select 1 from " + FriendSquareShareTable.TABLE_NAME + " where " + FriendSquareShareTable.COLUMN_NAME_LOGINUSER + " = '" + AdminUtils.getUserInfo(MarketApp.app).getAccount() + "' and " + FriendSquareShareTable.COLUMN_NAME_ID + " = '" + id + "' order by createTime";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        if (cursor.moveToFirst()) {
            blean = true;
        }
        return blean;
    }

    /**
     * 通过pid查出发送者
     */
    public String getCreateUser(String pid, String account) {
        marketDB.getDb();
        String createUser = "";
        String getActiveMenu = "select createUser from " + FriendSquareCommentsTable.TABLE_NAME + " where " + FriendSquareCommentsTable.COLUMN_NAME_LOGINUSER + " = '" + account + "' and " + FriendSquareCommentsTable.COLUMN_NAME_ID + " = '" + pid + "' order by createTime";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        if (cursor.moveToFirst()) {
            createUser = cursor.getString(0);
        }
        return createUser;
    }

    /**
     * 通过createUser查出是否评论
     */
    public boolean getZan(String createUser, String id) {
        marketDB.getDb();
        boolean bl = false;
        String getActiveMenu = "select * from " + FriendSquareCommentsTable.TABLE_NAME + " where " + FriendSquareCommentsTable.COLUMN_NAME_LOGINUSER + " = '" + createUser + "' and createUser = '" + createUser + "' and type = 1 and topicId = '" + id + "' order by createTime";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        if (cursor.moveToFirst()) {
            bl = true;
        }
        return bl;
    }

    public String getZanList(String id) {
        StringBuffer sb = new StringBuffer();
        ArrayList<String> lists = new ArrayList<String>();
        marketDB.getDb();
        String getActiveMenu = "select createUser from " + FriendSquareCommentsTable.TABLE_NAME + " where " + FriendSquareCommentsTable.COLUMN_NAME_LOGINUSER + " = '" + AdminUtils.getUserInfo(MarketApp.app).getAccount() + "' and type = 1 and topicId = '" + id + "' order by createTime";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        while (cursor.moveToNext()) {
            lists.add(cursor.getString(0));
        }
        marketDB.getDb();
        String sql_getFriend = "select * from " + FriendInfoTable.TABLE_NAME + " where " + FriendInfoTable.COLUMN_NAME_ACCOUNT + " = ? and " + FriendInfoTable.COLUMN_NAME_MYID + " = ?";
        for (int i = 0; i < lists.size(); i++) {
            Cursor cursor1 = marketDB.db.rawQuery(sql_getFriend, new String[] { lists.get(i), AdminUtils.getUserInfo(MarketApp.app).getUid() });
            if (cursor1.moveToNext()) {
                String name = cursor1.getString(cursor1.getColumnIndex(FriendInfoTable.COLUMN_NAME_NAME));
                sb.append(name + "  ");
            }
        }
        return sb.toString();
    }

    /**
     * 插入新消息
     * 
     * @param vo
     */
    public long insertFriendSquareImg(MFriendZoneImageVo vo) {
        ContentValues values = new ContentValues();
        values.put(FriendSquareImgTable.COLUMN_NAME_ID, vo.getId());
        values.put(FriendSquareImgTable.COLUMN_NAME_TOPICID, vo.getTopicId());
        values.put(FriendSquareImgTable.COLUMN_NAME_FILEID, vo.getFileId());
        values.put(FriendSquareImgTable.COLUMN_NAME_FILENAME, vo.getFileName());
        values.put(FriendSquareImgTable.COLUMN_NAME_URL, vo.getUrl());
        values.put(FriendSquareImgTable.COLUMN_NAME_LOGINUSER, vo.getLoginUser());
        marketDB.getDb();
        Long id = marketDB.db.insert(FriendSquareImgTable.TABLE_NAME, null, values);
        return id;
    }

    /**
     * 查询朋友圈图片信息
     * 
     * @return
     */
    public ArrayList<MFriendZoneImageVo> getFriendSquareImg(String id, String account) {
        marketDB.getDb();
        String getActiveMenu = "select * from " + FriendSquareImgTable.TABLE_NAME + " where " + FriendSquareImgTable.COLUMN_NAME_LOGINUSER + " = '" + account + "' and topicId = '" + id + "'";
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, null);
        ArrayList<MFriendZoneImageVo> messageList = new ArrayList<MFriendZoneImageVo>();
        while (cursor.moveToNext()) {
            MFriendZoneImageVo mv = new MFriendZoneImageVo(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            mv.setId(cursor.getString(0));
            messageList.add(mv);
            mv = null;
        }
        return messageList;
    }
}
