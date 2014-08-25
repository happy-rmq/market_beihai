package com.lenovo.market.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lenovo.market.dbhelper.DatabaseContract.ChatInfoTable;
import com.lenovo.market.dbhelper.DatabaseContract.ChatRecordTable;
import com.lenovo.market.dbhelper.DatabaseContract.CustomMenuTable;
import com.lenovo.market.dbhelper.DatabaseContract.DepartmentMemberTable;
import com.lenovo.market.dbhelper.DatabaseContract.DepartmentTable;
import com.lenovo.market.dbhelper.DatabaseContract.FriendInfoTable;
import com.lenovo.market.dbhelper.DatabaseContract.FriendSquareCommentsTable;
import com.lenovo.market.dbhelper.DatabaseContract.FriendSquareImgTable;
import com.lenovo.market.dbhelper.DatabaseContract.FriendSquareShareTable;
import com.lenovo.market.dbhelper.DatabaseContract.GroupChatRecordTable;
import com.lenovo.market.dbhelper.DatabaseContract.GroupChatTable;
import com.lenovo.market.dbhelper.DatabaseContract.MessageInfoTable;
import com.lenovo.market.dbhelper.DatabaseContract.NewFriendTable;
import com.lenovo.market.dbhelper.DatabaseContract.RoomMemberTable;
import com.lenovo.market.dbhelper.DatabaseContract.RoomTable;
import com.lenovo.market.dbhelper.DatabaseContract.UserInfoTable;

public class MarketDBHelper {

    public SQLiteDatabase db;
    private Context con;
    private SqlLiteHelper dbHelper;
    private static MarketDBHelper marketDBHelper;

    private MarketDBHelper(Context context) {
        super();
        con = context;
        dbHelper = new SqlLiteHelper(con);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            con.getDatabasePath(DatabaseContract.MARKETDB_NAME);
            db = dbHelper.getWritableDatabase();
        }
    }

    public static MarketDBHelper getInstance(Context context) {
        if (marketDBHelper == null) {
            marketDBHelper = new MarketDBHelper(context);
        }
        return marketDBHelper;
    }

    private class SqlLiteHelper extends SQLiteOpenHelper {

        public SqlLiteHelper(Context context) {
            super(context, DatabaseContract.MARKETDB_NAME, null, DatabaseContract.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MessageInfoTable.CREATE_TABLE);
            db.execSQL(FriendSquareShareTable.CREATE_TABLE);
            db.execSQL(FriendSquareCommentsTable.CREATE_TABLE);
            db.execSQL(FriendSquareImgTable.CREATE_TABLE);
            db.execSQL(UserInfoTable.CREATE_TABLE);
            db.execSQL(ChatRecordTable.CREATE_TABLE);
            db.execSQL(ChatInfoTable.CREATE_TABLE);
            db.execSQL(FriendInfoTable.CREATE_TABLE);
            db.execSQL(NewFriendTable.CREATE_TABLE);
            db.execSQL(GroupChatTable.CREATE_TABLE);
            // db.execSQL(GroupChatRecordTable.CREATE_TABLE);
            db.execSQL(RoomTable.CREATE_TABLE);
            db.execSQL(RoomMemberTable.CREATE_TABLE);
            db.execSQL(DepartmentTable.CREATE_TABLE);
            db.execSQL(DepartmentMemberTable.CREATE_TABLE);
            db.execSQL(CustomMenuTable.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 33) {
                db.execSQL(MessageInfoTable.DELETE_TABLE);
                db.execSQL(FriendSquareShareTable.DELETE_TABLE);
                db.execSQL(FriendSquareCommentsTable.DELETE_TABLE);
                db.execSQL(FriendSquareImgTable.DELETE_TABLE);
                db.execSQL(UserInfoTable.DELETE_TABLE);
                db.execSQL(ChatRecordTable.DELETE_TABLE);
                db.execSQL(ChatInfoTable.DELETE_TABLE);
                db.execSQL(FriendInfoTable.DELETE_TABLE);
                db.execSQL(NewFriendTable.DELETE_TABLE);
                db.execSQL(GroupChatTable.DELETE_TABLE);
                db.execSQL(GroupChatRecordTable.DELETE_TABLE);
                db.execSQL(RoomTable.DELETE_TABLE);
                db.execSQL(RoomMemberTable.DELETE_TABLE);
                db.execSQL(DepartmentTable.DELETE_TABLE);
                db.execSQL(DepartmentMemberTable.DELETE_TABLE);
                db.execSQL(CustomMenuTable.DELETE_TABLE);
            }
            db.execSQL(RoomTable.DELETE_TABLE);
            onCreate(db);
        }
    }

    public MarketDBHelper open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public MarketDBHelper getDb() {
        if (!db.isOpen()) {
            open();
        }
        return this;
    }
}
