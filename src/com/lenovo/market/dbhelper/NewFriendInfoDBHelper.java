package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.NewFriendTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.vo.server.FriendMesVo;

/**
 * 新朋友dbhelper
 * 
 * @author muqiang
 * 
 */
public class NewFriendInfoDBHelper {

    private MarketDBHelper dbHelper;

    public NewFriendInfoDBHelper() {
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
    public long insertNewFriend(Context context, FriendMesVo friend) {
        boolean newFriend = getNewFriend(friend);
        long num = -1;
        dbHelper.getDb();
        if (!newFriend) {
            ContentValues newValues = new ContentValues();
            newValues.put(NewFriendTable.COLUMN_NAME_ACCOUNT, friend.getFriendAccount());
            newValues.put(NewFriendTable.COLUMN_NAME_NAME, friend.getFriendName());
            newValues.put(NewFriendTable.COLUMN_NAME_SUBSCRIPTION, friend.getSubscription());
            newValues.put(NewFriendTable.COLUMN_NAME_PICTURE, friend.getPicture());
            newValues.put(NewFriendTable.COLUMN_NAME_AREA, friend.getArea());
            newValues.put(NewFriendTable.COLUMN_NAME_LOGINUSER, AdminUtils.getUserInfo(MarketApp.app).getAccount());
            newValues.put(NewFriendTable.COLUMN_NAME_STATE, friend.getState());
            num = dbHelper.db.insert(NewFriendTable.TABLE_NAME, null, newValues);
        }
        return num;
    }

    /**
     * 根据状态从本地数据库查找好友
     * 
     * @param account
     * @return
     */
    public int getNewFriendSubscription() {
        int count = 0;
        String sql_getFriend = "select state from " + NewFriendTable.TABLE_NAME + " where " + NewFriendTable.COLUMN_NAME_LOGINUSER + " = ? and " + NewFriendTable.COLUMN_NAME_STATE + " !=2";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[] { AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        while (cursor.moveToNext()) {
            count += Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        return count;
    }

    /**
     * 根据账号从本地数据库查找好友
     * 
     * @param account
     * @return
     */
    public boolean getNewFriend(FriendMesVo friend) {
        boolean b = false;
        String sql_getFriend = "select 1 from " + NewFriendTable.TABLE_NAME + " where " + NewFriendTable.COLUMN_NAME_LOGINUSER + " = ? and " + NewFriendTable.COLUMN_NAME_ACCOUNT + " = ?";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[] { AdminUtils.getUserInfo(MarketApp.app).getAccount(), friend.getFriendAccount() });
        if (cursor.moveToFirst()) {
            b = true;
            update(friend);
        }
        cursor.close();
        return b;
    }

    /**
     * 根据账号从本地数据库查找好友
     * 
     * @param account
     * @return
     */
    public ArrayList<FriendMesVo> getNewFriendAll(String subscription) {
        ArrayList<FriendMesVo> friendVos = new ArrayList<FriendMesVo>();
        String sql_getFriend = "select * from " + NewFriendTable.TABLE_NAME + " where " + NewFriendTable.COLUMN_NAME_LOGINUSER + " = ? and " + NewFriendTable.COLUMN_NAME_STATE + " !=2 and " + NewFriendTable.COLUMN_NAME_SUBSCRIPTION + "='" + subscription + "' order by " + BaseColumns._ID + " desc ";
        dbHelper.getDb();
        Cursor cursor = dbHelper.db.rawQuery(sql_getFriend, new String[] { AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        while (cursor.moveToNext()) {
            String account = cursor.getString(cursor.getColumnIndex(NewFriendTable.COLUMN_NAME_ACCOUNT));
            String name = cursor.getString(cursor.getColumnIndex(NewFriendTable.COLUMN_NAME_NAME));
            String picture = cursor.getString(cursor.getColumnIndex(NewFriendTable.COLUMN_NAME_PICTURE));
            String sex = cursor.getString(cursor.getColumnIndex(NewFriendTable.COLUMN_NAME_SEX));
            String sign = cursor.getString(cursor.getColumnIndex(NewFriendTable.COLUMN_NAME_SIGN));
            String area = cursor.getString(cursor.getColumnIndex(NewFriendTable.COLUMN_NAME_AREA));

            FriendMesVo friendVo = new FriendMesVo(account);
            friendVo.setFriendName(name);
            friendVo.setPicture(picture);
            friendVo.setSex(sex);
            friendVo.setSign(sign);
            friendVo.setArea(area);
            friendVo.setSubscription(subscription);
            friendVos.add(friendVo);
            friendVo = null;
        }
        cursor.close();
        return friendVos;
    }

    /**
     * 修改好友状态
     * 
     * @param friend
     * @return
     */
    public long update(FriendMesVo friend) {
        dbHelper.getDb();
        ContentValues newValues = new ContentValues();
        newValues.put(NewFriendTable.COLUMN_NAME_SUBSCRIPTION, friend.getSubscription());

        long num = dbHelper.db.update(NewFriendTable.TABLE_NAME, newValues, NewFriendTable.COLUMN_NAME_ACCOUNT + " = '" + friend.getFriendAccount() + "'", null);
        return num;
    }

    public long updateStateAll() {
        dbHelper.getDb();
        ContentValues newValues = new ContentValues();
        newValues.put(NewFriendTable.COLUMN_NAME_STATE, "0");

        long num = dbHelper.db.update(NewFriendTable.TABLE_NAME, newValues, NewFriendTable.COLUMN_NAME_LOGINUSER + " = '" + AdminUtils.getUserInfo(MarketApp.app).getAccount() + "' and " + NewFriendTable.COLUMN_NAME_STATE + " != 2 ", null);
        return num;
    }

    public long updateState() {
        dbHelper.getDb();
        ContentValues newValues = new ContentValues();
        newValues.put(NewFriendTable.COLUMN_NAME_STATE, "2");

        long num = dbHelper.db.update(NewFriendTable.TABLE_NAME, newValues, NewFriendTable.COLUMN_NAME_LOGINUSER + " = '" + AdminUtils.getUserInfo(MarketApp.app).getAccount() + "' and " + NewFriendTable.COLUMN_NAME_SUBSCRIPTION + " = 'both'", null);
        return num;
    }

    /**
     * 删除新朋友
     * 
     * @param friend
     * @return
     */
    public void delete() {
        dbHelper.getDb();
        dbHelper.db.delete(NewFriendTable.TABLE_NAME, NewFriendTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[] { AdminUtils.getUserInfo(MarketApp.app).getAccount() });
    }

    /**
     * 删除新朋友
     * 
     * @param friend
     * @return
     */
    public void delete(String account) {
        dbHelper.getDb();
        dbHelper.db.delete(NewFriendTable.TABLE_NAME, NewFriendTable.COLUMN_NAME_LOGINUSER + " = ? and " + NewFriendTable.COLUMN_NAME_ACCOUNT + " = ? ", new String[] { AdminUtils.getUserInfo(MarketApp.app).getAccount(), account });
    }
}
