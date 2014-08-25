package com.lenovo.market.dbhelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.vo.server.UserVo;

public class UserDBHelper {

    private MarketDBHelper marketDB;

    public UserDBHelper() {
        super();
        marketDB = MarketDBHelper.getInstance(MarketApp.app);
        if (!marketDB.db.isOpen())
            marketDB.open();
    }

    public long saveUserInfo(UserVo user) {
        marketDB.getDb();
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(user.getUid())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_UID, user.getUid());
        }
        if (!TextUtils.isEmpty(user.getAccount())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_ACCOUNT, user.getAccount());
        }
        if (!TextUtils.isEmpty(user.getPassword())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_PASSWORD, user.getPassword());
        }
        if (!TextUtils.isEmpty(user.getUserName())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_NAME, user.getUserName());
        }
        if (!TextUtils.isEmpty(user.getPhone())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_PHONE, user.getPhone());
        }
        if (!TextUtils.isEmpty(user.getPicture())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_HEADURL, user.getPicture());
        }
        if (!TextUtils.isEmpty(user.getQrCode())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_QRCODE, user.getQrCode());
        }
        if (!TextUtils.isEmpty(user.getSign())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_SIGN, user.getSign());
        }
        if (!TextUtils.isEmpty(user.getSex())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_SEX, user.getSex());
        }
        if (!TextUtils.isEmpty(user.getCompanyId())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_COMPANYID, user.getCompanyId());
        }
        if (!TextUtils.isEmpty(user.getDefaultServAccount())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_DEFAULTSERVACCOUNT, user.getDefaultServAccount());
        }
        if (!TextUtils.isEmpty(user.getDefaultServId())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_DEFAULTSERVID, user.getDefaultServId());
        }
        if (!TextUtils.isEmpty(user.getServAccount())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_SERVACCOUNT, user.getServAccount());
        }
        if (!TextUtils.isEmpty(user.getServId())) {
            values.put(DatabaseContract.UserInfoTable.COLUMN_NAME_SERVID, user.getServId());
        }

        String sql = "select * from " + DatabaseContract.UserInfoTable.TABLE_NAME + " where "//
                + DatabaseContract.UserInfoTable.COLUMN_NAME_ACCOUNT + " = ?"; //
        Cursor cursor = marketDB.db.rawQuery(sql, new String[]{user.getAccount()});
        long num = 0;
        if (cursor.moveToFirst()) {
            num = marketDB.db.update(DatabaseContract.UserInfoTable.TABLE_NAME, values, DatabaseContract.UserInfoTable.COLUMN_NAME_ACCOUNT + " = ?", new String[]{user.getAccount()});
        } else {
            num = marketDB.db.insert(DatabaseContract.UserInfoTable.TABLE_NAME, null, values);
        }
        cursor.close();
        return num;
    }

    public UserVo getUserInfo(String account) {
        marketDB.getDb();
        String sql = "select * from " + DatabaseContract.UserInfoTable.TABLE_NAME + " where "//
                + DatabaseContract.UserInfoTable.COLUMN_NAME_ACCOUNT + " = ?"; //
        Cursor cursor = marketDB.db.rawQuery(sql, new String[]{account});
        UserVo user = null;
        if (cursor.moveToFirst()) {
            String user_account = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_ACCOUNT));
            String uid = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_UID));
            String password = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_PASSWORD));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_PHONE));
            String headUrl = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_HEADURL));
            String qrcode = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_QRCODE));
            String sign = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_SIGN));
            String sex = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_SEX));
            String companyId = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_COMPANYID));
            String default_serv_account = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_DEFAULTSERVACCOUNT));
            String default_servId = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_DEFAULTSERVID));
            String servAccount = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_SERVACCOUNT));
            String servId = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserInfoTable.COLUMN_NAME_SERVID));

            user = new UserVo();
            user.setAccount(user_account);
            user.setUid(uid);
            user.setPassword(password);
            user.setUserName(name);
            user.setPhone(phone);
            user.setPicture(headUrl);
            user.setQrCode(qrcode);
            user.setSign(sign);
            user.setSex(sex);
            user.setCompanyId(companyId);
            user.setDefaultServAccount(default_serv_account);
            user.setDefaultServId(default_servId);
            user.setServAccount(servAccount);
            user.setServId(servId);
        }
        cursor.close();
        return user;
    }
}
