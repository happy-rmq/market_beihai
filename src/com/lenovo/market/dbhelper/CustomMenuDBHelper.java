package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.CustomMenuTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.vo.server.MenuVo;
import com.lenovo.market.vo.server.UserVo;

public class CustomMenuDBHelper {
    
    private MarketDBHelper dbHelper;

    public CustomMenuDBHelper() {
        super();
        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
        if (!dbHelper.db.isOpen())
            dbHelper.open();
    }

    /**
     * 添加自定义菜单信息
     * 
     */
    public long insert(MenuVo menuVo, Context context) {
        dbHelper.getDb();
        ContentValues newValues = new ContentValues();
        newValues.put(CustomMenuTable.COLUMN_NAME_TYPE, menuVo.getType());
        newValues.put(CustomMenuTable.COLUMN_NAME_NAME, menuVo.getName());
        newValues.put(CustomMenuTable.COLUMN_NAME_KEY, menuVo.getKey());
        newValues.put(CustomMenuTable.COLUMN_NAME_KEYWORD, menuVo.getKeyword());
        newValues.put(CustomMenuTable.COLUMN_NAME_URL, menuVo.getUrl());
        newValues.put(CustomMenuTable.COLUMN_NAME_PARENTID, menuVo.getParentid());
        newValues.put(CustomMenuTable.COLUMN_NAME_LOGINUSER, AdminUtils.getUserInfo(MarketApp.app).getAccount());
        newValues.put(CustomMenuTable.COLUMN_NAME_EMPID, menuVo.getEmpid());
        long num = dbHelper.db.insert(CustomMenuTable.TABLE_NAME, null, newValues);
        return num;
    }

    public ArrayList<MenuVo> getMenuVo(String empid, Context context) {
        dbHelper.getDb();
        UserVo userInfo = AdminUtils.getUserInfo(context);
        String sql = "select * from " + CustomMenuTable.TABLE_NAME + " where "//
                + CustomMenuTable.COLUMN_NAME_LOGINUSER + " = ? and " + CustomMenuTable.COLUMN_NAME_EMPID + " = ? "; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] {userInfo.getAccount(), empid });
        ArrayList<MenuVo> menuVos = new ArrayList<MenuVo>();
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_TYPE));
            String name = cursor.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_NAME));
            String key = cursor.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_KEY));
            String url = cursor.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_URL));
            String keyword = cursor.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_KEYWORD));
            String parentid = cursor.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_PARENTID));
            MenuVo menuVo = new MenuVo();
            menuVo.setType(type);
            menuVo.setName(name);
            menuVo.setKey(key);
            menuVo.setUrl(url);
            menuVo.setKeyword(keyword);
            menuVo.setParentid(parentid);
            menuVo.setEmpid(empid);
            if (TextUtils.isEmpty(parentid)) {
                String sqlC = "select * from " + CustomMenuTable.TABLE_NAME + " where "//
                        + CustomMenuTable.COLUMN_NAME_LOGINUSER + " = ? and " + CustomMenuTable.COLUMN_NAME_PARENTID + " = ? and " + CustomMenuTable.COLUMN_NAME_EMPID + " = ? "; //
                Cursor cursorC = dbHelper.db.rawQuery(sqlC, new String[] { userInfo.getAccount(), menuVo.getKey(), empid });
                while (cursorC.moveToNext()) {
                    String typeC = cursorC.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_TYPE));
                    String nameC = cursorC.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_NAME));
                    String keyC = cursorC.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_KEY));
                    String urlC = cursorC.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_URL));
                    String keywordC = cursorC.getString(cursor.getColumnIndex(CustomMenuTable.COLUMN_NAME_KEYWORD));
                    MenuVo menuVoC = new MenuVo();
                    menuVoC.setType(typeC);
                    menuVoC.setName(nameC);
                    menuVoC.setKey(keyC);
                    menuVoC.setUrl(urlC);
                    menuVoC.setKeyword(keywordC);
                    menuVoC.setEmpid(empid);
                    menuVo.getSubMenus().add(menuVoC);
                }
                menuVos.add(menuVo);
            }
        }
        cursor.close();
        return menuVos;
    }

    /**
     * 删除菜单信息
     * 
     * @param empId
     * @return
     */
    public long delete(String empId) {
        if (TextUtils.isEmpty(empId)) {
            return 0;
        }
        dbHelper.getDb();
        long num = dbHelper.db.delete(CustomMenuTable.TABLE_NAME, CustomMenuTable.COLUMN_NAME_EMPID + " = '" + empId + "'", null);
        return num;
    }
}
