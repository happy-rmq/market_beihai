//package com.lenovo.market.dbhelper;
//
//import java.util.ArrayList;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.text.TextUtils;
//
//import com.lenovo.market.common.MarketApp;
//import com.lenovo.market.dbhelper.DatabaseContract.GroupChatRecordTable;
//import com.lenovo.market.dbhelper.DatabaseContract.GroupChatTable;
//import com.lenovo.market.util.AdminUtils;
//import com.lenovo.market.vo.local.GroupRecordVo;
//
///**
// * 群聊记录表dbhelper
// * 
// * @author zhouyang
// * 
// */
//public class GroupRecordDBHelper {
//
//    private MarketDBHelper dbHelper;
//
//    public GroupRecordDBHelper() {
//        super();
//        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
//        if (!dbHelper.db.isOpen())
//            dbHelper.open();
//    }
//
//    /**
//     * 向群聊表插入一条记录
//     * 
//     * @param group
//     * @return
//     */
//    public void insert(GroupRecordVo vo, boolean isUnread) {
//        dbHelper.getDb();
//        String sql = "select * from " + GroupChatRecordTable.TABLE_NAME //
//                + " where " + GroupChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " //
//                + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? ";
//
//        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { vo.getRoomId(), vo.getLoginUser() });
//
//        ContentValues values = new ContentValues();
//        values.put(GroupChatRecordTable.COLUMN_NAME_FROMUSERACCOUNT, vo.getSendUser());
//        values.put(GroupChatRecordTable.COLUMN_NAME_LOGINUSER, vo.getLoginUser());
//        values.put(GroupChatRecordTable.COLUMN_NAME_ROOMID, vo.getRoomId());
//        values.put(GroupChatRecordTable.COLUMN_NAME_ROOMNAME, vo.getRoomname());
//        if (!TextUtils.isEmpty(vo.getTime())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_CREATETIME, vo.getTime());
//        }
//        if (!TextUtils.isEmpty(vo.getContent())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_CONTENT, vo.getContent());
//        }
//        if (!TextUtils.isEmpty(vo.getSendUserName())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_FROMUSERNAME, vo.getSendUserName());
//        }
//
//        if (cursor.moveToNext()) {
//            if (isUnread) {
//                int count = cursor.getInt(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_UNREADCOUNT));
//                values.put(GroupChatRecordTable.COLUMN_NAME_UNREADCOUNT, count + 1);
//            }
//            dbHelper.db.update(GroupChatRecordTable.TABLE_NAME, values, GroupChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + "= ?", new String[] { vo.getRoomId(), vo.getLoginUser() });
//        } else {
//            if (isUnread) {
//                values.put(GroupChatRecordTable.COLUMN_NAME_UNREADCOUNT, 1);
//            }
//            dbHelper.db.insert(GroupChatRecordTable.TABLE_NAME, null, values);
//        }
//    }
//
//    // 修改群组消息表未读信息为0
//    public void update(GroupRecordVo vo) {
//        dbHelper.getDb();
//        ContentValues values = new ContentValues();
//        values.put(GroupChatRecordTable.COLUMN_NAME_UNREADCOUNT, 0);
//        dbHelper.db.update(GroupChatRecordTable.TABLE_NAME, values, GroupChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + "= ?", new String[] { vo.getRoomId(), vo.getLoginUser() });
//    }
//
//    // 修改群组消息表消息内容
//    public void updateContent(GroupRecordVo vo) {
//        dbHelper.getDb();
//        ContentValues values = new ContentValues();
//        if (!TextUtils.isEmpty(vo.getContent())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_CONTENT, vo.getContent());
//        } else {
//            values.put(GroupChatRecordTable.COLUMN_NAME_CONTENT, "");
//        }
//        if (!TextUtils.isEmpty(vo.getSendUser())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_FROMUSERACCOUNT, vo.getSendUser());
//        }
//        if (!TextUtils.isEmpty(vo.getSendUserName())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_FROMUSERNAME, vo.getSendUserName());
//        }
//        if (!TextUtils.isEmpty(vo.getTime())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_CREATETIME, vo.getTime());
//        }
//        if (!TextUtils.isEmpty(vo.getRoomname())) {
//            values.put(GroupChatRecordTable.COLUMN_NAME_ROOMNAME, vo.getRoomname());
//        }
//        dbHelper.db.update(GroupChatRecordTable.TABLE_NAME, values, GroupChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + "= ?", new String[] { vo.getRoomId(), vo.getLoginUser() });
//    }
//
//    // 修改群组名字
//    public void updateRoomName(GroupRecordVo vo) {
//        dbHelper.getDb();
//        ContentValues values = new ContentValues();
//        values.put(GroupChatRecordTable.COLUMN_NAME_ROOMNAME, vo.getRoomname());
//        dbHelper.db.update(GroupChatRecordTable.TABLE_NAME, values, GroupChatRecordTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + "= ?", new String[] { vo.getRoomId(), vo.getLoginUser() });
//    }
//
//    public ArrayList<GroupRecordVo> getGroupChatRecordList() {
//        dbHelper.getDb();
//        String sql = "select * from " + GroupChatRecordTable.TABLE_NAME + " where " + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? order by " + GroupChatTable.COLUMN_NAME_CREATETIME + " desc";
//        String currentUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
//        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { currentUser });
//        ArrayList<GroupRecordVo> list = new ArrayList<GroupRecordVo>();
//        GroupRecordVo vo = null;
//        while (cursor.moveToNext()) {
//            String content = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_CONTENT));
//            String roomId = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_ROOMID));
//            String sendUser = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_FROMUSERACCOUNT));
//            String sendUserName = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_FROMUSERNAME));
//            String roomname = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_ROOMNAME));
//            String time = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_CREATETIME));
//            int unReadCount = cursor.getInt(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_UNREADCOUNT));
//
//            vo = new GroupRecordVo(roomId, currentUser, sendUser, content, time);
//            vo.setUnReadCount(unReadCount);
//            vo.setRoomname(roomname);
//            vo.setSendUserName(sendUserName == null ? sendUser : sendUserName);
//            list.add(vo);
//        }
//        cursor.close();
//        return list;
//    }
//
//    public GroupRecordVo getRecord(String roomId) {
//        dbHelper.getDb();
//        String sql = "select * from " + GroupChatRecordTable.TABLE_NAME + " where " + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? and " + GroupChatRecordTable.COLUMN_NAME_ROOMID + " = ?;";
//        String currentUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
//        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { currentUser, roomId });
//        GroupRecordVo vo = null;
//        while (cursor.moveToNext()) {
//            String content = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_CONTENT));
//            String sendUser = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_FROMUSERACCOUNT));
//            String sendUserName = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_FROMUSERNAME));
//            String roomname = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_ROOMNAME));
//            String time = cursor.getString(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_CREATETIME));
//            int unReadCount = cursor.getInt(cursor.getColumnIndex(GroupChatRecordTable.COLUMN_NAME_UNREADCOUNT));
//
//            vo = new GroupRecordVo(roomId, currentUser, sendUser, content, time);
//            vo.setUnReadCount(unReadCount);
//            vo.setRoomname(roomname);
//            vo.setSendUserName(sendUserName);
//        }
//        return vo;
//    }
//
//    /**
//     * 删除表记录
//     * 
//     * @param roomId
//     */
//    public void delete(String roomId) {
//        dbHelper.getDb();
//        String currentUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
//        dbHelper.db.delete(GroupChatRecordTable.TABLE_NAME, GroupChatRecordTable.COLUMN_NAME_LOGINUSER + " = ? and " + GroupChatRecordTable.COLUMN_NAME_ROOMID + " = ?", new String[] { currentUser, roomId });
//    }
//
//    /**
//     * 获取当前未读消息条目数
//     * 
//     * @param senduser
//     * @return
//     */
//    public int getUnReadMsgCount() {
//
//        String sql = "select " + GroupChatRecordTable.COLUMN_NAME_UNREADCOUNT + " from " + GroupChatRecordTable.TABLE_NAME + " where " + GroupChatRecordTable.COLUMN_NAME_LOGINUSER + "=?";
//        dbHelper.getDb();
//        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { AdminUtils.getUserInfo(MarketApp.app).getAccount() });
//        int unReadCount = 0;
//        while (cursor.moveToNext()) {
//            unReadCount += cursor.getInt(0);
//        }
//        return unReadCount;
//    }
//}
