package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.ChatInfoTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

public class ChatInfoDBHelper {

    private MarketDBHelper marketDB;

    public ChatInfoDBHelper() {
        super();
        marketDB = MarketDBHelper.getInstance(MarketApp.app);
        if (!marketDB.db.isOpen())
            marketDB.open();
    }

    /**
     * 通过发送者查消息
     * 
     * @param senduser
     * @return
     */
    public ArrayList<MsgChatVo> getChatInfoListByName(String friendAccount, int totalCount) {
        String sql_getMessageList = "";
        if (totalCount < MarketApp.COUNT) {
            int num = totalCount % MarketApp.COUNT;
            sql_getMessageList = "select * from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =? order by " + BaseColumns._ID + " limit "
                    + num + " offset 0";
        } else {
            totalCount = totalCount - MarketApp.COUNT;
            ChatActivity.DBindex = totalCount;
            sql_getMessageList = "select * from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =? order by " + BaseColumns._ID + " limit "
                    + MarketApp.COUNT + " offset " + ChatActivity.DBindex;
        }
        marketDB.getDb();
        Cursor cursor = marketDB.db.rawQuery(sql_getMessageList, new String[] { friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        ArrayList<MsgChatVo> msgChatVos = new ArrayList<MsgChatVo>();
        while (cursor.moveToNext()) {
            MsgChatVo msgChatVo = new MsgChatVo();
            msgChatVo.setId(cursor.getString(cursor.getColumnIndex(BaseColumns._ID)));
            msgChatVo.setCreateTime(cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_CREATETIME)));
            msgChatVo.setContent(cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_CONTENT)));
            msgChatVo.setFromUserName(cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_FROMUSERNAME)));
            msgChatVo.setToUserName(cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_TOUSERNAME)));
            msgChatVo.setType(cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_TYPE)));
            msgChatVo.setStatus(cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_STATUS)));
            msgChatVo.setLoginUser(cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_STATUS)));
            msgChatVos.add(msgChatVo);
            msgChatVo = null;
        }
        cursor.close();
        return msgChatVos;
    }

    /**
     * 获取一共有多少数据
     * 
     * @param receiverUser
     * @param sendUser
     * @return
     */
    public int getTotalCount(String friendAccount) {
        int itemnumbers = 0;
        marketDB.getDb();
        String sqlNum = "select count(*) from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =?";
        Cursor cursor = marketDB.db.rawQuery(sqlNum, new String[] { friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), friendAccount, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        if (cursor.moveToFirst()) {
            itemnumbers = cursor.getInt(0);
        }
        cursor.close();
        return itemnumbers;
    }

    /**
     * 插入新消息
     * 
     * @param vo
     * @return
     */
    public long insertNewMessage(MsgChatVo msgChatVo) {
        ContentValues values = new ContentValues();
        values.put(ChatInfoTable.COLUMN_NAME_TYPE, msgChatVo.getType());
        values.put(ChatInfoTable.COLUMN_NAME_MSGTYPE, msgChatVo.getMsgType());
        values.put(ChatInfoTable.COLUMN_NAME_CREATETIME, msgChatVo.getCreateTime());
        values.put(ChatInfoTable.COLUMN_NAME_FROMUSERNAME, msgChatVo.getFromUserName());
        values.put(ChatInfoTable.COLUMN_NAME_TOUSERNAME, msgChatVo.getToUserName());
        values.put(ChatInfoTable.COLUMN_NAME_CONTENT, msgChatVo.getContent());
        values.put(ChatInfoTable.COLUMN_NAME_LOGINUSER, msgChatVo.getLoginUser());
        values.put(ChatInfoTable.COLUMN_NAME_STATUS, msgChatVo.getStatus());
        marketDB.getDb();
        Long id = marketDB.db.insert(ChatInfoTable.TABLE_NAME, null, values);
        return id;
    }

    /**
     * 添加时间
     * 
     * @param receiverUser
     * @param sendTime
     * @return
     */
    public long getCreatMessageDate(MsgChatVo msgChatVo) {
        long id = -1;
        String getlastestMessage = "select max(" + ChatInfoTable.COLUMN_NAME_CREATETIME + ") from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER
                + " =?";
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
     * 通过id删除内容
     * 
     * @param _id
     * @param receiverUser
     */
    public void Isdelete(MsgChatVo mVo, MsgChatVo vo, MsgChatVo msgVo) {
        marketDB.getDb();
        // 先删除当前选中的内容
        marketDB.db.delete(ChatInfoTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { mVo.getId() });

        Cursor cursor;
        if (msgVo != null) {
            boolean blean = false;
            String sql = "select type from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =? and " + BaseColumns._ID + "=? ";
            cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), msgVo.getId() });
            if (cursor.moveToFirst()) {
                String type = cursor.getString(0);
                if (type.equals(MarketApp.MESSAGE_TIME)) {
                    blean = true;
                }
            }

            if (blean) {
                sql = "select type from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =? and " + BaseColumns._ID + "=? ";
                cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), vo.getId() });
                if (cursor.moveToFirst()) {
                    String type = cursor.getString(0);
                    if (type.equals(MarketApp.MESSAGE_TIME)) {
                        MarketApp.indexBool = true;
                        marketDB.db.delete(ChatInfoTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { vo.getId() });
                    }
                }
            }
        }

        String sql = "select _id,type from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =? order by _id desc";
        cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            String type = cursor.getString(1);
            if (type.equals(MarketApp.MESSAGE_TIME)) {
                MarketApp.indexBool = true;
                marketDB.db.delete(ChatInfoTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { id });
            }
        }
        cursor.close();
    }

    /**
     * 获取最后一条信息
     * 
     * @param sendUser
     * @param receiverUser
     * @return
     */
    public MsgChatVo getMessageVo(MsgChatVo mVo) {
        marketDB.getDb();
        String sql = "select * from " + ChatInfoTable.TABLE_NAME + " where ((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =? and " + ChatInfoTable.COLUMN_NAME_TYPE + " != '"
                + MarketApp.MESSAGE_TIME + "' order by " + ChatInfoTable.COLUMN_NAME_CREATETIME + " desc ";
        Cursor cursor = marketDB.db.rawQuery(sql, new String[] { mVo.getFromUserName(), mVo.getToUserName(), mVo.getToUserName(), mVo.getFromUserName(), AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        MsgChatVo msgChatVo = null;
        if (cursor.moveToFirst()) {
            String createTime = cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_CREATETIME));
            String fromUserName = cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_FROMUSERNAME));
            String status = cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_STATUS));
            String loginUser = cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_LOGINUSER));
            String xmlContent = cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_CONTENT));
            String msgType = cursor.getString(cursor.getColumnIndex(ChatInfoTable.COLUMN_NAME_MSGTYPE));
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

    /**
     * 删除整个好友的信息
     * 
     * @param receiverUser
     */
    public void delete(String friendAccount) {
        marketDB.getDb();
        String account = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        marketDB.db.delete(ChatInfoTable.TABLE_NAME, "((" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " = ?) or (" + ChatInfoTable.COLUMN_NAME_FROMUSERNAME + " = ? and " + ChatInfoTable.COLUMN_NAME_TOUSERNAME + " =?)) and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " =?", new String[] { friendAccount, account, account, friendAccount, account });
    }

    // 修改信息的发送状态
    public void updateContent(String id, String status) {
        marketDB.getDb();
        ContentValues values = new ContentValues();
        values.put(ChatInfoTable.COLUMN_NAME_STATUS, status);
        marketDB.db.update(ChatInfoTable.TABLE_NAME, values, BaseColumns._ID + " = ? and " + ChatInfoTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[] { id, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
    }
}
