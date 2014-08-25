package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.ChatRecordTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.ChatRecordVo;

public class ChatRecordDBHelper {

    private MarketDBHelper marketDB;

    public ChatRecordDBHelper() {
        super();
        marketDB = MarketDBHelper.getInstance(MarketApp.app);
        if (!marketDB.db.isOpen())
            marketDB.open();
    }

    /**
     * 已经有某个人的记录则更新 否则插入新记录
     *
     * @param record
     * @param isUnRead
     */
    public void insertRecord(ChatRecordVo record, boolean isUnRead) {
        String friendAccount = record.getFriendAccount();
        record.setFriendAccount(Utils.getUsernameFromJid(friendAccount));

        marketDB.getDb();
        String queryRecord;
        Cursor curser;
        if (TextUtils.isEmpty(record.getRoomId())) {
            queryRecord = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ";
            curser = marketDB.db.rawQuery(queryRecord, new String[]{record.getFriendAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        } else {
            queryRecord = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_ROOMID + "=? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ";
            curser = marketDB.db.rawQuery(queryRecord, new String[]{record.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        }

        ContentValues values = new ContentValues();
        values.put(ChatRecordTable.COLUMN_NAME_CONTENT, record.getContent());
        values.put(ChatRecordTable.COLUMN_NAME_CREATETIME, record.getCreateTime());
        values.put(ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT, record.getFriendAccount());
        values.put(ChatRecordTable.COLUMN_NAME_FRIENDNAME, record.getFriendName());
        values.put(ChatRecordTable.COLUMN_NAME_FRIENDPIC, record.getFriendPic());
        values.put(ChatRecordTable.COLUMN_NAME_FRIENDTYPE, record.getFriendType());
        values.put(ChatRecordTable.COLUMN_NAME_LOGINUSER, record.getLoginUser());
        values.put(ChatRecordTable.COLUMN_NAME_STATUS, record.getStatus());
        values.put(ChatRecordTable.COLUMN_NAME_ROOMID, record.getRoomId());
        values.put(ChatRecordTable.COLUMN_NAME_ROOMNAME, record.getRoomName());

        if (curser.moveToNext()) {
            if (isUnRead) {
                int count = curser.getInt(curser.getColumnIndex(ChatRecordTable.COLUMN_NAME_UNREADCOUNT));
                values.put(ChatRecordTable.COLUMN_NAME_UNREADCOUNT, count + 1);
            }
            if (TextUtils.isEmpty(record.getRoomId())) {
                marketDB.db.update(ChatRecordTable.TABLE_NAME, values, ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[]{record.getFriendAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
            } else {
                marketDB.db.update(ChatRecordTable.TABLE_NAME, values, ChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[]{record.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
            }
        } else {
            if (isUnRead) {
                values.put(ChatRecordTable.COLUMN_NAME_UNREADCOUNT, 1);
            } else {
                values.put(ChatRecordTable.COLUMN_NAME_UNREADCOUNT, 0);
            }
            marketDB.db.insert(ChatRecordTable.TABLE_NAME, null, values);
        }
    }

    // 修改未读信息为0
    public void update(ChatRecordVo record) {
        if (record == null || TextUtils.isEmpty(record.getFriendAccount())) {
            return;
        }
        marketDB.getDb();
        ContentValues values = new ContentValues();
        values.put(ChatRecordTable.COLUMN_NAME_UNREADCOUNT, 0);
        if (TextUtils.isEmpty(record.getRoomName())) {
            marketDB.db.update(ChatRecordTable.TABLE_NAME, values, ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[]{record.getFriendAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        } else {
            marketDB.db.update(ChatRecordTable.TABLE_NAME, values, ChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[]{record.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        }
    }

    // 修改未读信息内容为空
    public void updateContent(String friendAccount) {
        marketDB.getDb();
        ContentValues values = new ContentValues();
        values.put(ChatRecordTable.COLUMN_NAME_CONTENT, "");
        marketDB.db.update(ChatRecordTable.TABLE_NAME, values, ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[]{friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount()});
    }

    /**
     * 获取当前未读消息条目数
     *
     * @return
     * @author zl
     */
    public int getUnReadMsgCount() {

        String sql = "select " + ChatRecordTable.COLUMN_NAME_UNREADCOUNT + " from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " =?";
        marketDB.getDb();
        Cursor cursor = marketDB.db.rawQuery(sql, new String[]{AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        int unReadCount = 0;
        while (cursor.moveToNext()) {
            unReadCount += cursor.getInt(0);
        }
        cursor.close();
        return unReadCount;
    }

    /**
     * 得到所有的好友的聊天记录 目前只有好友才在record表存储数据
     *
     * @return
     */
    public ArrayList<ChatRecordVo> getAllRecords() {
        String queryAllRecords = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " =? " + "  order by " + ChatRecordTable.COLUMN_NAME_CREATETIME + " desc";
        ArrayList<ChatRecordVo> recordList = new ArrayList<ChatRecordVo>();
        marketDB.getDb();
        Cursor cursor = marketDB.db.rawQuery(queryAllRecords, new String[]{AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        recordList = parserCursor(cursor);
        return recordList;
    }

    public ChatRecordVo getRecord(String friendAccount) {
        String queryAllRecords = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT + "=?  and  " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " =?  ";
        ArrayList<ChatRecordVo> recordList = new ArrayList<ChatRecordVo>();
        marketDB.getDb();
        Cursor cursor = marketDB.db.rawQuery(queryAllRecords, new String[]{friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        recordList = parserCursor(cursor);
        if (null != recordList && recordList.size() > 0) {
            return recordList.get(0);
        }
        return null;
    }

    public ChatRecordVo getRecordGroup(String roomId) {
        marketDB.getDb();
        String sql = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? and " + ChatRecordTable.COLUMN_NAME_ROOMID + " = ?;";
        String currentUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        Cursor cursor = marketDB.db.rawQuery(sql, new String[]{currentUser, roomId});
        ChatRecordVo vo = null;
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_CONTENT));
            String sendUser = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT));
            String sendUserName = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDNAME));
            String roomname = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_ROOMNAME));
            String time = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_CREATETIME));
            int unReadCount = cursor.getInt(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_UNREADCOUNT));

            vo = new ChatRecordVo(sendUser, sendUserName, time, unReadCount, "", 3, content, currentUser, "0", roomId, roomname);
        }
        return vo;
    }

    public ArrayList<ChatRecordVo> parserCursor(Cursor cursor) {
        ArrayList<ChatRecordVo> recordList = new ArrayList<ChatRecordVo>();
        ChatRecordVo record;
        while (cursor.moveToNext()) {

            String createTime = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_CREATETIME));
            String friendAccount = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT));
            String friendName = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDNAME));
            String content = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_CONTENT));
            String friendPic = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDPIC));
            int friendType = cursor.getInt(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDTYPE));
            int unreadcount = cursor.getInt(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_UNREADCOUNT));
            String roomId = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_ROOMID));
            String roomName = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_ROOMNAME));

            record = new ChatRecordVo(friendAccount, friendName, createTime, unreadcount, friendPic, friendType, content, AdminUtils.getUserInfo(MarketApp.app).getAccount(), "0", roomId, roomName);
            recordList.add(record);
            record = null;
        }
        cursor.close();
        return recordList;
    }

    /**
     * 删除某个好友的记录
     */
    public void deleteRecordByName(String friendAccount) {
        marketDB.getDb();
        marketDB.db.delete(ChatRecordTable.TABLE_NAME, ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + "= ?", new String[]{friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount()});
    }

    // 修改群组名字
    public void updateRoomName(ChatRecordVo vo) {
        marketDB.getDb();
        ContentValues values = new ContentValues();
        values.put(ChatRecordTable.COLUMN_NAME_ROOMNAME, vo.getRoomName());
        marketDB.db.update(ChatRecordTable.TABLE_NAME, values, ChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + "= ?", new String[]{vo.getRoomId(), vo.getLoginUser()});
    }

    // 修改群组消息表消息内容
    public void updateContent(ChatRecordVo vo) {
        marketDB.getDb();

        String queryRecord;
        Cursor curser;
        if (TextUtils.isEmpty(vo.getRoomId())) {
            queryRecord = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ";
            curser = marketDB.db.rawQuery(queryRecord, new String[]{vo.getFriendAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        } else {
            queryRecord = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_ROOMID + "=? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ";
            curser = marketDB.db.rawQuery(queryRecord, new String[]{vo.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount()});
        }

        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(vo.getContent())) {
            values.put(ChatRecordTable.COLUMN_NAME_CONTENT, vo.getContent());
        } else {
            values.put(ChatRecordTable.COLUMN_NAME_CONTENT, "");
        }
        if (!TextUtils.isEmpty(vo.getFriendAccount())) {
            values.put(ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT, vo.getFriendAccount());
        }
        if (!TextUtils.isEmpty(vo.getFriendName())) {
            values.put(ChatRecordTable.COLUMN_NAME_FRIENDNAME, vo.getFriendName());
        }
        if (!TextUtils.isEmpty(vo.getCreateTime())) {
            values.put(ChatRecordTable.COLUMN_NAME_CREATETIME, vo.getCreateTime());
        }
        if (!TextUtils.isEmpty(vo.getRoomName())) {
            values.put(ChatRecordTable.COLUMN_NAME_ROOMNAME, vo.getRoomName());
        }
        if (!TextUtils.isEmpty(vo.getFriendPic())) {
            values.put(ChatRecordTable.COLUMN_NAME_FRIENDPIC, vo.getFriendPic());
        }
        values.put(ChatRecordTable.COLUMN_NAME_FRIENDTYPE, vo.getFriendType());


        if (curser.moveToNext()) {
            marketDB.db.update(ChatRecordTable.TABLE_NAME, values, ChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + ChatRecordTable.COLUMN_NAME_LOGINUSER + "= ?", new String[]{vo.getRoomId(), vo.getLoginUser()});
        } else {
            values.put(ChatRecordTable.COLUMN_NAME_ROOMID, vo.getRoomId());
            values.put(ChatRecordTable.COLUMN_NAME_LOGINUSER, vo.getLoginUser());
            marketDB.db.insert(ChatRecordTable.TABLE_NAME, null, values);
        }

    }

    public ArrayList<ChatRecordVo> getGroupChatRecordList() {
        marketDB.getDb();
        String sql = "select * from " + ChatRecordTable.TABLE_NAME + " where " + ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? order by " + ChatRecordTable.COLUMN_NAME_CREATETIME + " desc";
        String currentUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        Cursor cursor = marketDB.db.rawQuery(sql, new String[]{currentUser});
        ArrayList<ChatRecordVo> list = new ArrayList<ChatRecordVo>();
        ChatRecordVo vo = null;
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_CONTENT));
            String roomId = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_ROOMID));
            String sendUser = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDACCOUNT));
            String sendUserName = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_FRIENDNAME));
            String roomname = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_ROOMNAME));
            String time = cursor.getString(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_CREATETIME));
            int unReadCount = cursor.getInt(cursor.getColumnIndex(ChatRecordTable.COLUMN_NAME_UNREADCOUNT));

            vo = new ChatRecordVo(sendUser, sendUserName, time, unReadCount, "", 3, content, currentUser, "0", roomId, roomname);
            vo.setFriendName(sendUserName == null ? sendUser : sendUserName);
            list.add(vo);
        }
        cursor.close();
        return list;
    }

    /**
     * 删除表记录
     *
     * @param roomId
     */
    public void delete(String roomId) {
        marketDB.getDb();
        String currentUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        marketDB.db.delete(ChatRecordTable.TABLE_NAME, ChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? and " + ChatRecordTable.COLUMN_NAME_ROOMID + " = ?", new String[]{currentUser, roomId});
    }
}
