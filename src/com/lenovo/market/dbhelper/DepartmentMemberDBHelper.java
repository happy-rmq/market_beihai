package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.DepartmentMemberTable;
import com.lenovo.market.vo.local.BusinessContactVo;
import com.lenovo.market.vo.local.DepartmentMemberVo;

/**
 * 房间dbhelper
 *
 * @author zhouyang
 */
public class DepartmentMemberDBHelper {

    private MarketDBHelper dbHelper;

    public DepartmentMemberDBHelper() {
        super();
        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
        if (!dbHelper.db.isOpen())
            dbHelper.open();
    }

    public long insert(DepartmentMemberVo vo) {
        dbHelper.getDb();

        long num = 0;
        String sql = "select * from " + DepartmentMemberTable.TABLE_NAME + " where "//
                + DepartmentMemberTable.COLUMN_NAME_MEMBERID + " = ?"; //
        ContentValues newValues = new ContentValues();
        newValues.put(DepartmentMemberTable.COLUMN_NAME_MEMBERID, vo.getId());
        newValues.put(DepartmentMemberTable.COLUMN_NAME_NAME, vo.getName());
        newValues.put(DepartmentMemberTable.COLUMN_NAME_ACCOUNT, vo.getAccount());
        newValues.put(DepartmentMemberTable.COLUMN_NAME_PHONENUM, vo.getPhonenum());
        newValues.put(DepartmentMemberTable.COLUMN_NAME_EMAIL, vo.getEmail());
        newValues.put(DepartmentMemberTable.COLUMN_NAME_ISSYNC, vo.getIsSync());
        newValues.put(DepartmentMemberTable.COLUMN_NAME_PIC, vo.getPic());
        newValues.put(DepartmentMemberTable.COLUMN_NAME_PARENTDEPARTMENTID, vo.getParentDepartmentId());
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{vo.getId()});
        if (cursor.moveToFirst()) {
            num = dbHelper.db.update(DepartmentMemberTable.TABLE_NAME, newValues, DepartmentMemberTable.COLUMN_NAME_MEMBERID + " = ?", new String[]{vo.getId()});
        } else {
            num = dbHelper.db.insert(DepartmentMemberTable.TABLE_NAME, null, newValues);
        }

        return num;
    }

    /**
     * 查询指定的父部门下的成员
     *
     * @param parentDepartment
     * @return
     */
    public ArrayList<BusinessContactVo> getDepartmentMembers(String parentDepartment) {
        dbHelper.getDb();
        String sql = "select * from " + DepartmentMemberTable.TABLE_NAME + " where "//
                + DepartmentMemberTable.COLUMN_NAME_PARENTDEPARTMENTID + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{parentDepartment});
        ArrayList<BusinessContactVo> list = new ArrayList<BusinessContactVo>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_MEMBERID));
            String name = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_NAME));
            String account = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_ACCOUNT));
            String phonenum = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_PHONENUM));
            String email = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_EMAIL));
            String isSync = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_ISSYNC));
            String pic = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_PIC));

            DepartmentMemberVo vo = new DepartmentMemberVo();
            vo.setId(id);
            vo.setParentDepartmentId(parentDepartment);
            vo.setName(name);
            vo.setAccount(account);
            vo.setPhonenum(phonenum);
            vo.setEmail(email);
            vo.setPic(pic);
            vo.setIsSync(isSync);

            list.add(vo);
        }
        cursor.close();
        return list;
    }

    /**
     * 查询名字符符合关键字key的所有成员
     *
     */
    public ArrayList<DepartmentMemberVo> searchDepartmentMembers(String key) {
        dbHelper.getDb();
        String sql = "select * from " + DepartmentMemberTable.TABLE_NAME + " where "//
                + DepartmentMemberTable.COLUMN_NAME_NAME + " LIKE ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{"%" + key + "%"});
        ArrayList<DepartmentMemberVo> list = new ArrayList<DepartmentMemberVo>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_MEMBERID));
            String name = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_NAME));
            String account = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_ACCOUNT));
            String phonenum = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_PHONENUM));
            String email = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_EMAIL));
            String isSync = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_ISSYNC));
            String pic = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_PIC));
            String parentDepartment = cursor.getString(cursor.getColumnIndex(DepartmentMemberTable.COLUMN_NAME_PARENTDEPARTMENTID));

            DepartmentMemberVo vo = new DepartmentMemberVo();
            vo.setId(id);
            vo.setParentDepartmentId(parentDepartment);
            vo.setName(name);
            vo.setAccount(account);
            vo.setPhonenum(phonenum);
            vo.setEmail(email);
            vo.setIsSync(isSync);
            vo.setPic(pic);

            list.add(vo);
        }
        cursor.close();
        return list;
    }

    public boolean hasData() {
        dbHelper.getDb();
        String sql = "select * from " + DepartmentMemberTable.TABLE_NAME;
        Cursor cursor = dbHelper.db.rawQuery(sql, null);
        boolean flag = false;
        if (cursor.moveToFirst()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    public long delete(String id) {
        dbHelper.getDb();
        long count = dbHelper.db.delete(DepartmentMemberTable.TABLE_NAME, DepartmentMemberTable.COLUMN_NAME_MEMBERID + " like '" + id + "%'"
                , null);
        return count;
    }
}
