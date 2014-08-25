package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.FriendInfoTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.vo.server.FriendMesVo;

/**
 * 好友信息dbhelper
 *
 * @author zhouyang
 */
public class FriendInfoDBHelper {

    private MarketDBHelper dbHelper;

    public FriendInfoDBHelper() {
        super();
        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
        if (!dbHelper.db.isOpen())
            dbHelper.open();
    }

    /**
     * 保存好友列表
     *
     * @param list
     */
    public void saveFriendList(ArrayList<FriendMesVo> list) {
        for (FriendMesVo vo : list) {
            saveFriend(vo);
        }
    }

    /**
     * 保存好友信息
     *
     * @param friend
     */
    public void saveFriend(FriendMesVo friend) {
        if (friend == null) {
            return;
        }
        dbHelper.getDb();
        String friendId = friend.getFriendId();
        String sql_getFriend = "select * from " + FriendInfoTable.TABLE_NAME + " where " + FriendInfoTable.COLUMN_NAME_FRIENDID + " =? and " + FriendInfoTable.COLUMN_NAME_MYID + "=?";
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[]{friendId, AdminUtils.getUserInfo(MarketApp.app).getUid()});
        if (cursor.moveToNext()) {
            update(friend);
        } else {
            insert(friend);
        }
        cursor.close();
    }

    /**
     * 添加朋友信息
     *
     * @param friend
     * @return
     */
    private long insert(FriendMesVo friend) {
        dbHelper.getDb();
        ContentValues newValues = new ContentValues();
        newValues.put(FriendInfoTable.COLUMN_NAME_MYID, AdminUtils.getUserInfo(MarketApp.app).getUid());
        newValues.put(FriendInfoTable.COLUMN_NAME_FRIENDID, friend.getFriendId());
        newValues.put(FriendInfoTable.COLUMN_NAME_ACCOUNT, friend.getFriendAccount());
        newValues.put(FriendInfoTable.COLUMN_NAME_FRIENDTYPE, friend.getFriendType());
        newValues.put(FriendInfoTable.COLUMN_NAME_NAME, friend.getFriendName());
        newValues.put(FriendInfoTable.COLUMN_NAME_PICTURE, friend.getPicture());
        newValues.put(FriendInfoTable.COLUMN_NAME_AREA, friend.getArea());
        newValues.put(FriendInfoTable.COLUMN_NAME_SEX, friend.getSex());
        newValues.put(FriendInfoTable.COLUMN_NAME_SIGN, friend.getSign());
        long num = dbHelper.db.insert(FriendInfoTable.TABLE_NAME, null, newValues);
        return num;
    }

    /**
     * 修改好友信息
     *
     * @param friend
     * @return
     */
    private long update(FriendMesVo friend) {
        dbHelper.getDb();
        ContentValues newValues = new ContentValues();
        newValues.put(FriendInfoTable.COLUMN_NAME_FRIENDID, friend.getFriendId());
        newValues.put(FriendInfoTable.COLUMN_NAME_FRIENDTYPE, friend.getFriendType());
        newValues.put(FriendInfoTable.COLUMN_NAME_ACCOUNT, friend.getFriendAccount());
        newValues.put(FriendInfoTable.COLUMN_NAME_NAME, friend.getFriendName());
        newValues.put(FriendInfoTable.COLUMN_NAME_PICTURE, friend.getPicture());
        newValues.put(FriendInfoTable.COLUMN_NAME_AREA, friend.getArea());
        newValues.put(FriendInfoTable.COLUMN_NAME_SEX, friend.getSex());
        newValues.put(FriendInfoTable.COLUMN_NAME_SIGN, friend.getSign());

        long num = dbHelper.db.update(FriendInfoTable.TABLE_NAME, newValues, FriendInfoTable.COLUMN_NAME_FRIENDID + " = '" + friend.getFriendId() + "'", null);
        return num;
    }

    /**
     * 删除好友信息
     *
     * @param account
     * @return
     */
    public long delete(String account) {
        if (TextUtils.isEmpty(account)) {
            return 0;
        }
        dbHelper.getDb();
        long num = dbHelper.db.delete(FriendInfoTable.TABLE_NAME, FriendInfoTable.COLUMN_NAME_ACCOUNT + " = '" + account + "' and " + FriendInfoTable.COLUMN_NAME_MYID + " = '" + AdminUtils.getUserInfo(MarketApp.app).getUid() + "'", null);
        return num;
    }

    /**
     * 根据账号从本地数据库查找好友
     *
     * @param account
     * @return
     */
    public FriendMesVo getFriend(String account) {
        FriendMesVo friendVo = null;
        String sql_getFriend = "select * from " + FriendInfoTable.TABLE_NAME + " where " + FriendInfoTable.COLUMN_NAME_ACCOUNT + " = ? and " + FriendInfoTable.COLUMN_NAME_MYID + " = ?";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[]{account, AdminUtils.getUserInfo(MarketApp.app).getUid()});
        if (cursor.moveToNext()) {
            friendVo = getParserCursor(cursor);
        }
        cursor.close();
        return friendVo;
    }


    /**
     * 根据friendId从本地数据库查找好友
     *
     * @param friendId
     * @return
     */
    public FriendMesVo getFriendById(String friendId) {
        FriendMesVo friendVo = null;
        String sql_getFriend = "select * from " + FriendInfoTable.TABLE_NAME + " where " + FriendInfoTable.COLUMN_NAME_FRIENDID + " = ? and " + FriendInfoTable.COLUMN_NAME_MYID + " = ?";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[]{friendId, AdminUtils.getUserInfo(MarketApp.app).getUid()});
        if (cursor.moveToNext()) {
            friendVo = getParserCursor(cursor);
        }
        cursor.close();
        return friendVo;
    }

    /**
     * 根据账号从本地数据库查找所有好友
     *
     * @return
     */
    public ArrayList<FriendMesVo> getFriendAll() {
        ArrayList<FriendMesVo> friendVos = new ArrayList<FriendMesVo>();
        String sql_getFriend = "select * from " + FriendInfoTable.TABLE_NAME + " where " + FriendInfoTable.COLUMN_NAME_MYID + " = ?";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[]{AdminUtils.getUserInfo(MarketApp.app).getUid()});
        while (cursor.moveToNext()) {
            friendVos.add(getParserCursor(cursor));
        }
        cursor.close();
        return friendVos;
    }
    
    /**
     * 根据账号从本地数据库查找好友
     *
     * @param friendType 好友类型
     * @return
     */
    public ArrayList<FriendMesVo> getFriendAll(String friendType) {
        ArrayList<FriendMesVo> friendVos = new ArrayList<FriendMesVo>();
        String sql_getFriend = "select * from " + FriendInfoTable.TABLE_NAME + " where " + FriendInfoTable.COLUMN_NAME_MYID + " = ? and " + FriendInfoTable.COLUMN_NAME_FRIENDTYPE + " = ?";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[]{AdminUtils.getUserInfo(MarketApp.app).getUid(), friendType});
        while (cursor.moveToNext()) {
            friendVos.add(getParserCursor(cursor));
        }
        cursor.close();
        return friendVos;
    }

    /**
     * 获取好友列表除了指定的好友
     *
     * @param friendId_ 好友id
     * @return
     */
    public ArrayList<FriendMesVo> getFriendsExceptSpecifyFriend(String friendId_) {
        ArrayList<FriendMesVo> friendVos = new ArrayList<FriendMesVo>();
        String sql_getFriend = "select * from " + FriendInfoTable.TABLE_NAME + " where " + FriendInfoTable.COLUMN_NAME_MYID + " = ? and " + FriendInfoTable.COLUMN_NAME_FRIENDTYPE + " = ? and " + FriendInfoTable.COLUMN_NAME_FRIENDID + " != ?";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[]{AdminUtils.getUserInfo(MarketApp.app).getUid(), "1", friendId_});
        while (cursor.moveToNext()) {
            friendVos.add(getParserCursor(cursor));
        }
        cursor.close();
        return friendVos;
    }

    private FriendMesVo getParserCursor(Cursor cursor) {
        String friendId = cursor.getString(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_FRIENDID));
        String account = cursor.getString(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_ACCOUNT));
        String name = cursor.getString(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_NAME));
        String picture = cursor.getString(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_PICTURE));
        String sex = cursor.getString(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_SEX));
        String sign = cursor.getString(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_SIGN));
        String area = cursor.getString(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_AREA));
        int friendType = cursor.getInt(cursor.getColumnIndex(FriendInfoTable.COLUMN_NAME_FRIENDTYPE));

        FriendMesVo friendVo = new FriendMesVo(account);
        friendVo.setFriendId(friendId);
        friendVo.setFriendName(name);
        friendVo.setPicture(picture);
        friendVo.setSex(sex);
        friendVo.setSign(sign);
        friendVo.setArea(area);
        friendVo.setFriendType(friendType);
        return friendVo;
    }
}
