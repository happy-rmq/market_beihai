package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.RoomTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.vo.local.RoomVo;

/**
 * 房间dbhelper
 *
 * @author zhouyang
 */
public class RoomDBHelper {

    private MarketDBHelper dbHelper;

    public RoomDBHelper() {
        super();
        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
        if (!dbHelper.db.isOpen())
            dbHelper.open();
    }

    /**
     * @param room_id
     * @param iskicked 0(default在房间) ,1(被踢出)
     * @return
     */
    public long insert(String room_id, int iskicked) {
        String current_user = AdminUtils.getUserInfo(MarketApp.app).getAccount();

        dbHelper.getDb();
        String sql = "select * from " + RoomTable.TABLE_NAME + " where "//
                + RoomTable.COLUMN_NAME_ROOMID + " = ? and "//
                + RoomTable.COLUMN_NAME_LOGINUSER + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{room_id, current_user});
        long num = 0;
        if (!cursor.moveToNext()) {
            ContentValues newValues = new ContentValues();
            newValues.put(RoomTable.COLUMN_NAME_ROOMID, room_id);
            newValues.put(RoomTable.COLUMN_NAME_LOGINUSER, current_user);
            newValues.put(RoomTable.COLUMN_NAME_ISKICKED, iskicked);

            num = dbHelper.db.insert(RoomTable.TABLE_NAME, null, newValues);
        }

        return num;
    }

    /**
     * @return 当前登录用户所加入过的房间
     */
    public ArrayList<RoomVo> getRooms() {
        ArrayList<RoomVo> list = new ArrayList<RoomVo>();
        String loginUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();

        dbHelper.getDb();
        String sql = "select * from " + RoomTable.TABLE_NAME + " where "//
                + RoomTable.COLUMN_NAME_LOGINUSER + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{loginUser});
        while (cursor.moveToNext()) {
            String room_id = cursor.getString(cursor.getColumnIndex(RoomTable.COLUMN_NAME_ROOMID));
            String room_name = cursor.getString(cursor.getColumnIndex(RoomTable.COLUMN_NAME_ROOMNAME));
            int iskicked = cursor.getInt(cursor.getColumnIndex(RoomTable.COLUMN_NAME_ISKICKED));

            RoomVo room = new RoomVo();
            room.setRoomId(room_id);
            room.setLoginUser(loginUser);
            room.setIskicked(iskicked);
            room.setName(room_name);
            list.add(room);
        }
        cursor.close();
        return list;
    }

    /**
     * 返回指定的房间信息
     *
     * @param room
     * @return
     */
    public RoomVo getRoom(String room) {
        RoomVo roomVo = null;
        String loginUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        dbHelper.getDb();
        String sql = "select * from " + RoomTable.TABLE_NAME + " where "//
                + RoomTable.COLUMN_NAME_LOGINUSER + " = ? and "//
                + RoomTable.COLUMN_NAME_ROOMID + " = ?";
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{loginUser, room});
        if (cursor.moveToFirst()) {
            int iskicked = cursor.getInt(cursor.getColumnIndex(RoomTable.COLUMN_NAME_ISKICKED));
            String room_name = cursor.getString(cursor.getColumnIndex(RoomTable.COLUMN_NAME_ROOMNAME));

            roomVo = new RoomVo();
            roomVo.setRoomId(room);
            roomVo.setLoginUser(loginUser);
            roomVo.setIskicked(iskicked);
            roomVo.setName(room_name);
        }
        cursor.close();
        return roomVo;
    }

    public void delete(String room_id) {
        String current_user = AdminUtils.getUserInfo(MarketApp.app).getAccount();

        dbHelper.getDb();
        dbHelper.db.delete(RoomTable.TABLE_NAME, RoomTable.COLUMN_NAME_LOGINUSER + " = ? and "//
                + RoomTable.COLUMN_NAME_ROOMID + " = ?", new String[]{current_user, room_id});
    }

    public void deleteAll() {
        String current_user = AdminUtils.getUserInfo(MarketApp.app).getAccount();

        dbHelper.getDb();
        dbHelper.db.delete(RoomTable.TABLE_NAME, RoomTable.COLUMN_NAME_LOGINUSER + " = ?", new String[]{current_user});
    }

    /**
     * @param room_id
     * @param isKicked 0(default在房间) ,1(被踢出)
     */
    public void modifyKicked(String room_id, int isKicked) {
        String current_user = AdminUtils.getUserInfo(MarketApp.app).getAccount();

        dbHelper.getDb();
        ContentValues values = new ContentValues();
        values.put(RoomTable.COLUMN_NAME_ISKICKED, isKicked);
        dbHelper.db.update(RoomTable.TABLE_NAME, values, RoomTable.COLUMN_NAME_LOGINUSER + " = ? and "//
                + RoomTable.COLUMN_NAME_ROOMID + " = ?", new String[]{current_user, room_id});
    }

    /**
     * @param room_id
     * @param roomName
     */
    public void updateRoomName(String room_id, String roomName) {
        String current_user = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        dbHelper.getDb();
        ContentValues values = new ContentValues();
        values.put(RoomTable.COLUMN_NAME_ROOMNAME, roomName);
        dbHelper.db.update(RoomTable.TABLE_NAME, values, RoomTable.COLUMN_NAME_LOGINUSER + " = ? and "//
                + RoomTable.COLUMN_NAME_ROOMID + " = ?", new String[]{current_user, room_id});
    }
}
