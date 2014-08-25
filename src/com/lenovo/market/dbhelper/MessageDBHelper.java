package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.MessageInfoTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

public class MessageDBHelper {

    private MarketDBHelper marketDB;

    public MessageDBHelper() {
        super();
        marketDB = MarketDBHelper.getInstance(MarketApp.app);
        if (!marketDB.db.isOpen())
            marketDB.open();
    }

    /**
     * 获取与某个活动聊天纪录
     * 
     * @param totalCount
     * @param blean
     * @return
     */
    public ArrayList<MsgChatVo> getOperationalUserMessage(String friendAccount, int totalCount, boolean blean) {
        ArrayList<MsgChatVo> msgChatVos = new ArrayList<MsgChatVo>();
        if(TextUtils.isEmpty(friendAccount)){
            return msgChatVos;
        }
        marketDB.getDb();
        String getActiveMenu;
        if (blean) {
            if (totalCount < MarketApp.COUNT) {
                int num = totalCount % MarketApp.COUNT;
                getActiveMenu = "select * from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? order by " + BaseColumns._ID
                        + " limit " + num + " offset 0";
            } else {
                totalCount = totalCount - MarketApp.COUNT;
                HomePageFragment.DBindex = totalCount;
                getActiveMenu = "select * from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? order by " + BaseColumns._ID
                        + " limit " + MarketApp.COUNT + " offset " + HomePageFragment.DBindex;
            }
        } else {
            if (totalCount < MarketApp.COUNT) {
                int num = totalCount % MarketApp.COUNT;
                getActiveMenu = "select * from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? order by " + BaseColumns._ID
                        + " limit " + num + " offset 0";
            } else {
                totalCount = totalCount - MarketApp.COUNT;
                PublicChatActivity.DBindex = totalCount;
                getActiveMenu = "select * from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? order by " + BaseColumns._ID
                        + " limit " + MarketApp.COUNT + " offset " + PublicChatActivity.DBindex;
            }
        }
        Cursor cursor = marketDB.db.rawQuery(getActiveMenu, new String[] { friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        msgChatVos = parserCursor(cursor);
        return msgChatVos;
    }

    /**
     * 获取一共有多少数据
     * 
     * @param friendAccount
     * @return
     */
    public int getTotalCount(String friendAccount) {
        if(TextUtils.isEmpty(friendAccount)){
            return 0;
        }
        int itemnumbers = 0;
        marketDB.getDb();
        String sqlNum = "select count(*) from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =?";
        Cursor cursor = marketDB.db.rawQuery(sqlNum, new String[] { friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        if (cursor.moveToFirst()) {
            itemnumbers = cursor.getInt(0);
        }
        cursor.close();
        return itemnumbers;
    }

    public ArrayList<MsgChatVo> parserCursor(Cursor cursor) {
        ArrayList<MsgChatVo> msgChatVos = new ArrayList<MsgChatVo>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
            String createTime = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_CREATETIME));
            String fromUserName = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_FROMUSERNAME));
            String content = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_CONTENT));
            String loginUser = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_LOGINUSER));
            String type = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_TYPE));
            String status = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_STATUS));
            String toUserName = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_TOUSERNAME));
            MsgChatVo mChatVo = new MsgChatVo(type, createTime, fromUserName, toUserName, content, loginUser, status, "");
            mChatVo.setId(id);
            msgChatVos.add(mChatVo);
        }
        cursor.close();
        return msgChatVos;
    }

    /**
     * 更新
     * 
     * @return
     */
    public long update(String id, String status) {
        marketDB.getDb();
        
        ContentValues values = new ContentValues();
        values.put(MessageInfoTable.COLUMN_NAME_STATUS, status);

        long num = marketDB.db.update(MessageInfoTable.TABLE_NAME, values, BaseColumns._ID + " = ? and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[] { id, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        return num;
    }

    /**
     * 插入新消息
     * 
     * @param vo
     * @return
     */
    public long insertNewMessage(MsgChatVo vo) {
        ContentValues values = new ContentValues();
        values.put(MessageInfoTable.COLUMN_NAME_CONTENT, vo.getContent());
        values.put(MessageInfoTable.COLUMN_NAME_CREATETIME, vo.getCreateTime());
        values.put(MessageInfoTable.COLUMN_NAME_FROMUSERNAME, vo.getFromUserName());
        values.put(MessageInfoTable.COLUMN_NAME_LOGINUSER, vo.getLoginUser());
        values.put(MessageInfoTable.COLUMN_NAME_MSGTYPE, vo.getMsgType());
        values.put(MessageInfoTable.COLUMN_NAME_TYPE, vo.getType());
        values.put(MessageInfoTable.COLUMN_NAME_STATUS, vo.getStatus());
        values.put(MessageInfoTable.COLUMN_NAME_TOUSERNAME, vo.getToUserName());
        marketDB.getDb();
        Long id = marketDB.db.insert(MessageInfoTable.TABLE_NAME, null, values);
        return id;
    }

    /**
     * 添加时间
     * 
     * @returnin
     */
    public long getCreatMessageDate(MsgChatVo msgChatVo) {
        long id = -1;
        String getlastestMessage = "select max(" + MessageInfoTable.COLUMN_NAME_CREATETIME + ") from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and "
                + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =?";
        marketDB.getDb();
        Cursor cursor = marketDB.db.rawQuery(getlastestMessage, new String[] { msgChatVo.getFromUserName(), msgChatVo.getToUserName(), msgChatVo.getToUserName(), msgChatVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        String time = "";
        if (cursor.moveToFirst()) {
            time = cursor.getString(0);
        }
        if ((!TextUtils.isEmpty(time) && (Long.parseLong(msgChatVo.getCreateTime()) - Long.parseLong(time)) > (5 * 60 * 1000)) || TextUtils.isEmpty(time)) {
            id = insertNewMessage(msgChatVo);
        }
        cursor.close();
        return id;
    }

    /**
     * 删除整个好友的信息
     * 
     */
    public void delete(String friendAccount) {
        marketDB.getDb();
        String account = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        marketDB.db.delete(MessageInfoTable.TABLE_NAME, "((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =?", new String[] { friendAccount, account, account,
                friendAccount, account });
    }

    /**
     * 通过id删除内容
     * 
     */
    public void Isdelete(MsgChatVo mVo, MsgChatVo vo, MsgChatVo msgVo) {
        marketDB.getDb();
        // 先删除当前选中的内容
        marketDB.db.delete(MessageInfoTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { mVo.getId() });

        Cursor cursor;
        if (msgVo != null) {
            boolean blean = false;
            String sql = "select type from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? and " + BaseColumns._ID + "=? ";
            cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), msgVo.getId() });
            if (cursor.moveToFirst()) {
                String type = cursor.getString(0);
                if (type.equals(MarketApp.MESSAGE_TIME)) {
                    blean = true;
                }
            }

            if (blean) {
                sql = "select type from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? and " + BaseColumns._ID + "=? ";
                cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), vo.getId() });
                if (cursor.moveToFirst()) {
                    String type = cursor.getString(0);
                    if (type.equals(MarketApp.MESSAGE_TIME)) {
                        MarketApp.indexBool = true;
                        marketDB.db.delete(MessageInfoTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { vo.getId() });
                    }
                }
            }
        }

        String sql = "select _id,type from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? order by _id desc";
        cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            String type = cursor.getString(1);
            if (type.equals(MarketApp.MESSAGE_TIME)) {
                MarketApp.indexBool = true;
                marketDB.db.delete(MessageInfoTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { id });
            }
        }
        cursor.close();
    }

    /**
     * 获取最后一条信息
     * 
     * @return
     */
    public MsgChatVo getMessageVo(MsgChatVo mVo) {
        marketDB.getDb();
        String sql = "select * from " + MessageInfoTable.TABLE_NAME + " where ((" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + MessageInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + MessageInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + MessageInfoTable.COLUMN_NAME_LOGINUSER + " =? and "
                + MessageInfoTable.COLUMN_NAME_TYPE + " != '" + MarketApp.MESSAGE_TIME + "' order by " + MessageInfoTable.COLUMN_NAME_CREATETIME + " desc ";
        Cursor cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        MsgChatVo msgChatVo = null;
        if (cursor.moveToFirst()) {
            String createTime = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_CREATETIME));
            String fromUserName = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_FROMUSERNAME));
            String status = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_STATUS));
            String loginUser = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_LOGINUSER));
            String xmlContent = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_CONTENT));
            String msgType = cursor.getString(cursor.getColumnIndex(MessageInfoTable.COLUMN_NAME_MSGTYPE));
            MsgXmlVo xmlVo = XMLUtil.pullXMLResolve(xmlContent);
            String content = null;
            if (msgType.equals(MarketApp.SEND_PIC)) {
                content = "[图 片]";
            } else if (msgType.equals(MarketApp.SEND_VOICE)) {
                content = "[语 音]";
            } else if (msgType.equals(MarketApp.SEND_BUSINESSCARD)) {
                content = "[名 片]";
            } else if (msgType.equals(MarketApp.SEND_SHARE)) {
                content = "[链 接]";
            } else if (msgType.equals(MarketApp.SEND_TEXT)) {
                content = xmlVo.getContent();
            } else if (msgType.equals(MarketApp.SEND_VIDEO)) {
                content = "[视 频]";
            } else if (msgType.equals(MarketApp.SEND_LOCATION)) {
                content = "[位 置]";
            }
            msgChatVo = new MsgChatVo("", createTime, fromUserName, "", content, loginUser, status, msgType);
        }
        cursor.close();
        return msgChatVo;
    }
}
