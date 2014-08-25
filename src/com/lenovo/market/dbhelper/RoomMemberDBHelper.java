package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.RoomMemberTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.vo.local.RoomMemberVo;

/**
 * 房间成员dbhelper
 * 
 * @author zhouyang
 * 
 */
public class RoomMemberDBHelper {

    private MarketDBHelper dbHelper;

    public RoomMemberDBHelper() {
        super();
        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
        if (!dbHelper.db.isOpen())
            dbHelper.open();
    }

    /**
     * 向群聊表插入一条记录
     * 
     * @param group
     * @return
     */
    public long insert(RoomMemberVo member) {
        dbHelper.getDb();

        String sql = "select * from " + RoomMemberTable.TABLE_NAME + " where "//
                + RoomMemberTable.COLUMN_NAME_ROOMID + " = ? and " + RoomMemberTable.COLUMN_NAME_MEMBERID + " = ? "; //
        String[] args = new String[] { member.getRoomId(), member.getMemberId() };
        Cursor cursor = dbHelper.db.rawQuery(sql, args);

        ContentValues values = new ContentValues();
        values.put(RoomMemberTable.COLUMN_NAME_ROOMID, member.getRoomId());
        values.put(RoomMemberTable.COLUMN_NAME_MEMBERID, member.getMemberId());
        values.put(RoomMemberTable.COLUMN_NAME_ACCOUNT, member.getAccount());
        values.put(RoomMemberTable.COLUMN_NAME_USERNAME, member.getUserName());
        values.put(RoomMemberTable.COLUMN_NAME_NICKNAME, member.getNickName());
        values.put(RoomMemberTable.COLUMN_NAME_AVATAR, member.getAvatar());
        long num = 0;
        if (cursor.moveToFirst()) {
            dbHelper.db.update(RoomMemberTable.TABLE_NAME, values, RoomMemberTable.COLUMN_NAME_ROOMID + " = ? and "//
                    + RoomMemberTable.COLUMN_NAME_MEMBERID + " = ? ", args);
        } else {
            num = dbHelper.db.insert(RoomMemberTable.TABLE_NAME, null, values);
        }
        cursor.close();
        return num;
    }

    public boolean IsExist(RoomMemberVo member) {
        dbHelper.getDb();
        boolean exists = false;
        String sql = "select * from " + RoomMemberTable.TABLE_NAME + " where "//
                + RoomMemberTable.COLUMN_NAME_ROOMID + " = ? and " + RoomMemberTable.COLUMN_NAME_MEMBERID + " = ? "; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { member.getRoomId(), member.getMemberId() });
        if (cursor.moveToFirst()) {
            exists = true;
        }
        cursor.close();
        return exists;
    }

    public ArrayList<RoomMemberVo> getMembers(String room_id) {
        dbHelper.getDb();
        String sql = "select * from " + RoomMemberTable.TABLE_NAME + " where "//
                + RoomMemberTable.COLUMN_NAME_ROOMID + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { room_id });
        ArrayList<RoomMemberVo> list = new ArrayList<RoomMemberVo>();
        RoomMemberVo vo = null;
        while (cursor.moveToNext()) {
            String member_id = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_MEMBERID));
            String account = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_ACCOUNT));
            String user_name = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_USERNAME));
            String nick_name = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_NICKNAME));
            String avatar = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_AVATAR));

            vo = new RoomMemberVo(room_id, member_id, account, user_name, nick_name, avatar);
            list.add(vo);
        }
        cursor.close();
        return list;
    }

    /**
     * 查看是否有房间
     * 
     * @return
     */
    public boolean getIsMember() {
        boolean blean = false;
        dbHelper.getDb();
        String sql = "select count(*) from " + RoomMemberTable.TABLE_NAME; //
        Cursor cursor = dbHelper.db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            blean = true;
        }
        cursor.close();
        return blean;
    }

    public RoomMemberVo getMember(String room_id, String member_account) {
        RoomMemberVo memberVo = null;
        dbHelper.getDb();
        String sql = "select * from " + RoomMemberTable.TABLE_NAME + " where "//
                + RoomMemberTable.COLUMN_NAME_ROOMID + " = ? and "//
                + RoomMemberTable.COLUMN_NAME_ACCOUNT + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { room_id, member_account });
        if (cursor.moveToNext()) {
            String member_id = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_MEMBERID));
            String user_name = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_USERNAME));
            String nick_name = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_NICKNAME));
            String avatar = cursor.getString(cursor.getColumnIndex(RoomMemberTable.COLUMN_NAME_AVATAR));

            memberVo = new RoomMemberVo(room_id, member_id, member_account, user_name, nick_name, avatar);
        }
        cursor.close();
        return memberVo;
    }

    public int getMember(String room_id) {
        int count = 0;
        dbHelper.getDb();
        String sql = "select count(*) from " + RoomMemberTable.TABLE_NAME + " where "//
                + RoomMemberTable.COLUMN_NAME_ROOMID + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { room_id });
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * 删除指定房间的指定成员，如果member_id为null的话就是把自己从指定房间中移除
     * 
     * @param room_id
     * @param member_id
     * @return
     */
    public int delete(String room_id, String member_id) {
        if (member_id == null) {
            member_id = AdminUtils.getUserInfo(MarketApp.app).getUid();
        }
        dbHelper.getDb();
        int delete = dbHelper.db.delete(RoomMemberTable.TABLE_NAME, RoomMemberTable.COLUMN_NAME_ROOMID + " = ? and "//
                + RoomMemberTable.COLUMN_NAME_MEMBERID + " = ?", new String[] { room_id, member_id });
        return delete;
    }

    public int deleteRoomMembers(String room_id) {
        dbHelper.getDb();
        int delete = dbHelper.db.delete(RoomMemberTable.TABLE_NAME, RoomMemberTable.COLUMN_NAME_ROOMID + " = ?", new String[] { room_id });
        return delete;
    }
}
