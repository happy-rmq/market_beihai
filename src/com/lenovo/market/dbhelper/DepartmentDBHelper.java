package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.DepartmentTable;
import com.lenovo.market.vo.local.BusinessContactVo;
import com.lenovo.market.vo.local.DepartmentVo;

/**
 * 部门dbhelper
 *
 * @author zhouyang
 */
public class DepartmentDBHelper {

    private MarketDBHelper dbHelper;

    public DepartmentDBHelper() {
        super();
        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
        if (!dbHelper.db.isOpen())
            dbHelper.open();
    }

    public boolean hasData() {
        dbHelper.getDb();
        String sql = "select * from " + DepartmentTable.TABLE_NAME;
        Cursor cursor = dbHelper.db.rawQuery(sql, null);
        boolean flag = false;
        ;
        if (cursor.moveToFirst()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    public DepartmentVo getDepart(String name) {
        DepartmentVo vo = null;
        dbHelper.getDb();
        String sql = "select * from " + DepartmentTable.TABLE_NAME + " where "//
                + DepartmentTable.COLUMN_NAME_NAME + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{name});
        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(DepartmentTable.COLUMN_NAME_DEPARTMENTID));
            String parentId = cursor.getString(cursor.getColumnIndex(DepartmentTable.COLUMN_NAME_PARENTDEPARTMENTID));
            vo = new DepartmentVo();
            vo.setName(name);
            vo.setDepartmentId(id);
            vo.setParentDepartmentId(parentId);
        }
        return vo;
    }

    public long insert(DepartmentVo vo) {
        dbHelper.getDb();
        long num = 0;
        String sql = "select * from " + DepartmentTable.TABLE_NAME + " where "//
                + DepartmentTable.COLUMN_NAME_DEPARTMENTID + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{vo.getDepartmentId()});
        ContentValues newValues = new ContentValues();
        newValues.put(DepartmentTable.COLUMN_NAME_DEPARTMENTID, vo.getDepartmentId());
        newValues.put(DepartmentTable.COLUMN_NAME_NAME, vo.getName());
        newValues.put(DepartmentTable.COLUMN_NAME_PARENTDEPARTMENTID, vo.getParentDepartmentId());
        if (cursor.moveToFirst()) {
            num = dbHelper.db.update(DepartmentTable.TABLE_NAME, newValues, DepartmentTable.COLUMN_NAME_DEPARTMENTID + " = ?", new String[]{vo.getDepartmentId()});
        } else {
            num = dbHelper.db.insert(DepartmentTable.TABLE_NAME, null, newValues);
        }
        cursor.close();
        return num;
    }

    public ArrayList<BusinessContactVo> getDepartments(String parentDepartment) {
        dbHelper.getDb();
        String sql = "select * from " + DepartmentTable.TABLE_NAME + " where "//
                + DepartmentTable.COLUMN_NAME_PARENTDEPARTMENTID + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{parentDepartment});
        ArrayList<BusinessContactVo> list = new ArrayList<BusinessContactVo>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(DepartmentTable.COLUMN_NAME_DEPARTMENTID));
            String name = cursor.getString(cursor.getColumnIndex(DepartmentTable.COLUMN_NAME_NAME));
            DepartmentVo vo = new DepartmentVo();
            vo.setDepartmentId(id);
            vo.setName(name);
            vo.setParentDepartmentId(parentDepartment);
            list.add(vo);
        }
        cursor.close();
        return list;
    }

    /**
     * 查找指定id对应的部门
     *
     * @param id
     * @return
     */
    public DepartmentVo getDepartment(String id) {
        dbHelper.getDb();
        String sql = "select * from " + DepartmentTable.TABLE_NAME + " where "//
                + DepartmentTable.COLUMN_NAME_DEPARTMENTID + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[]{id});
        DepartmentVo vo = null;
        if (cursor.moveToFirst()) {
            String pid = cursor.getString(cursor.getColumnIndex(DepartmentTable.COLUMN_NAME_PARENTDEPARTMENTID));
            String name = cursor.getString(cursor.getColumnIndex(DepartmentTable.COLUMN_NAME_NAME));
            vo = new DepartmentVo();
            vo.setDepartmentId(id);
            vo.setName(name);
            vo.setParentDepartmentId(pid);
        }
        cursor.close();
        return vo;
    }

    public long delete(String id) {
        dbHelper.getDb();
        long count = dbHelper.db.delete(DepartmentTable.TABLE_NAME, DepartmentTable.COLUMN_NAME_DEPARTMENTID + " like '" + id + "%' and " //
                + DepartmentTable.COLUMN_NAME_DEPARTMENTID + " != ?", new String[]{id});
        return count;
    }

}
