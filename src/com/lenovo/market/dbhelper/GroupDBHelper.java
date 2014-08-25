package com.lenovo.market.dbhelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.dbhelper.DatabaseContract.GroupChatTable;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.XMLUtil;
import com.lenovo.market.vo.xmpp.MsgGroupVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;

/**
 * 群聊信息表
 * 
 * @author zhouyang
 * 
 */
public class GroupDBHelper {

    private MarketDBHelper dbHelper;

    public GroupDBHelper() {
        super();
        dbHelper = MarketDBHelper.getInstance(MarketApp.app);
        if (!dbHelper.db.isOpen())
            dbHelper.open();
    }

    /**
     * 判断是否需要加入时间(传入的消息的时间和最后一条记录的时间相差5分钟以上则返回true否则为false)
     * 
     * @param vo
     * @return
     */
    public boolean needInsertTime(MsgGroupVo vo) {
        dbHelper.getDb();
        String sql = "select " + GroupChatTable.COLUMN_NAME_CREATETIME + "," + GroupChatTable.COLUMN_NAME_MSGTYPE + " from " + GroupChatTable.TABLE_NAME + " where " + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " = ? order by _id desc";
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { vo.getRoomId(), vo.getLoginUser() });
        boolean need = false;
        if (cursor.moveToFirst()) {
            String lastTime = cursor.getString(0);
            String msgType = cursor.getString(1);
            String time = vo.getCreateTime();
            if (!msgType.equals(MarketApp.MESSAGE_TIME) && !TextUtils.isEmpty(lastTime) && !TextUtils.isEmpty(lastTime)) {
                need = Long.parseLong(time) - Long.parseLong(lastTime) > 5 * 60 * 1000;
            }
        }
        cursor.close();
        return need;
    }

    /**
     * 判断消息是否重复
     * 
     * @param vo
     * @return
     */
    public boolean isMsgRepeated(MsgGroupVo vo) {
        dbHelper.getDb();

        String sql = "select * from " + GroupChatTable.TABLE_NAME + " where "//
                + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and "//
                + GroupChatTable.COLUMN_NAME_MESSAGEID + " = ?"; //
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { vo.getRoomId(), vo.getMessageId() });
        boolean isRepeated = false;
        if (cursor.moveToFirst()) {
            isRepeated = true;
        }
        cursor.close();
        return isRepeated;
    }

    /**
     * 向群聊表插入一条记录
     * 
     * @param group
     * @return
     */
    public long insert(MsgGroupVo group) {
        dbHelper.getDb();

        ContentValues newValues = new ContentValues();
        newValues.put(GroupChatTable.COLUMN_NAME_CONTENT, group.getContent());
        newValues.put(GroupChatTable.COLUMN_NAME_CREATETIME, group.getCreateTime());
        newValues.put(GroupChatTable.COLUMN_NAME_FROMUSERNAME, group.getFromUserName());
        newValues.put(GroupChatTable.COLUMN_NAME_LOGINUSER, group.getLoginUser());
        newValues.put(GroupChatTable.COLUMN_NAME_MESSAGEID, group.getMessageId());
        newValues.put(GroupChatTable.COLUMN_NAME_MSGTYPE, group.getMsgType());
        newValues.put(GroupChatTable.COLUMN_NAME_TYPE, group.getType());
        newValues.put(GroupChatTable.COLUMN_NAME_ROOMID, group.getRoomId());
        newValues.put(GroupChatTable.COLUMN_NAME_STATUS, group.getStatus());
        newValues.put(GroupChatTable.COLUMN_NAME_TOUSERNAME, group.getToUserName());

        long num = dbHelper.db.insert(GroupChatTable.TABLE_NAME, null, newValues);
        return num;
    }

    public ArrayList<MsgGroupVo> getMessageList(String room, int totalCount) {
        dbHelper.getDb();
        String sql_getMessageList;
        if (totalCount < MarketApp.COUNT) {
            int num = totalCount % MarketApp.COUNT;
            sql_getMessageList = "select * from " + GroupChatTable.TABLE_NAME + " where "//
                    + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " //
                    + GroupChatTable.COLUMN_NAME_LOGINUSER + " = ? order by " + BaseColumns._ID + " limit " + num + " offset 0";
        } else {
            totalCount = totalCount - MarketApp.COUNT;
            GroupChatActivity.DBindex = totalCount;
            sql_getMessageList = "select * from " + GroupChatTable.TABLE_NAME + " where "//
                    + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " //
                    + GroupChatTable.COLUMN_NAME_LOGINUSER + " = ? order by " + BaseColumns._ID + " limit " + MarketApp.COUNT + " offset " + GroupChatActivity.DBindex;
        }
        String sendUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        Cursor cursor = dbHelper.db.rawQuery(sql_getMessageList, new String[] { room, sendUser });
        ArrayList<MsgGroupVo> list = new ArrayList<MsgGroupVo>();
        MsgGroupVo vo = null;
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
            String content = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_CONTENT));
            String createTime = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_CREATETIME));
            String fromUserName = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_FROMUSERNAME));
            String loginUser = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_LOGINUSER));
            String messageId = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_MESSAGEID));
            String type = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_TYPE));
            String roomId = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_ROOMID));
            String status = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_STATUS));
            String toUserName = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_TOUSERNAME));

            vo = new MsgGroupVo(messageId, roomId, type, createTime, fromUserName, toUserName, content, loginUser, status, "");
            vo.setId(id);
            list.add(vo);
        }
        cursor.close();
        return list;
    }

    public void delete(String room_id) {
        dbHelper.getDb();
        String currentUser = AdminUtils.getUserInfo(MarketApp.app).getAccount();
        dbHelper.db.delete(GroupChatTable.TABLE_NAME, GroupChatTable.COLUMN_NAME_LOGINUSER + " = ? and " + GroupChatTable.COLUMN_NAME_ROOMID + " = ?", new String[] { currentUser, room_id });
    }

    /**
     * 获取一共有多少数据
     * 
     * @return
     */
    public int getTotalCount(String room_id) {
        int itemnumbers = 0;
        dbHelper.getDb();
        String sqlNum = "select count(*) from " + GroupChatTable.TABLE_NAME + " where " + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " = ?";
        Cursor cursor = dbHelper.db.rawQuery(sqlNum, new String[] { room_id, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        if (cursor.moveToFirst()) {
            itemnumbers = cursor.getInt(0);
        }
        cursor.close();
        return itemnumbers;
    }

    /**
     * 通过id删除内容
     * 
     * @param _id
     * @param receiverUser
     */
    public void Isdelete(MsgGroupVo mVo, MsgGroupVo vo, MsgGroupVo msgVo) {
        dbHelper.getDb();
        // 先删除当前选中的内容
        dbHelper.db.delete(GroupChatTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { mVo.getId() });

        Cursor cursor;
        if (msgVo != null) {
            boolean blean = false;
            String sql = "select type from " + GroupChatTable.TABLE_NAME + " where " + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " =? and " + BaseColumns._ID + "=? ";
            cursor = dbHelper.db.rawQuery(sql, new String[] { mVo.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), msgVo.getId() });
            if (cursor.moveToFirst()) {
                String type = cursor.getString(0);
                if (type.equals(MarketApp.MESSAGE_TIME)) {
                    blean = true;
                }
            }

            if (blean && vo != null) {
                sql = "select type from " + GroupChatTable.TABLE_NAME + " where " + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " =? and " + BaseColumns._ID + "=? ";
                cursor = dbHelper.db.rawQuery(sql, new String[] { mVo.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount(), vo.getId() });
                if (cursor.moveToFirst()) {
                    String type = cursor.getString(0);
                    if (type.equals(MarketApp.MESSAGE_TIME)) {
                        MarketApp.indexBool = true;
                        dbHelper.db.delete(GroupChatTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { vo.getId() });
                    }
                }
            }
        }

        String sql = "select _id,type from " + GroupChatTable.TABLE_NAME + " where " + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " =? order by _id desc";
        cursor = dbHelper.db.rawQuery(sql, new String[] { mVo.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            String type = cursor.getString(1);
            if (type.equals(MarketApp.MESSAGE_TIME)) {
                MarketApp.indexBool = true;
                dbHelper.db.delete(GroupChatTable.TABLE_NAME, BaseColumns._ID + " = ? ", new String[] { id });
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
    public MsgGroupVo getMessageVo(MsgGroupVo mVo) {
        dbHelper.getDb();
        String sql = "select * from " + GroupChatTable.TABLE_NAME + " where " + GroupChatTable.COLUMN_NAME_ROOMID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " =? and " + GroupChatTable.COLUMN_NAME_MSGTYPE + " != '" + MarketApp.MESSAGE_NOTICE + "' and " + GroupChatTable.COLUMN_NAME_MSGTYPE + " != '" + MarketApp.MESSAGE_TIME + "' order by " + BaseColumns._ID + " desc ";
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { mVo.getRoomId(), AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        MsgGroupVo msgGroupVo = null;
        if (cursor.moveToFirst()) {
            String createTime = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_CREATETIME));
            String fromUserName = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_FROMUSERNAME));
            String toUserName = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_TOUSERNAME));
            String loginUser = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_LOGINUSER));
            String xmlContent = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_CONTENT));
            String msgType = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_MSGTYPE));
            String roomId = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_ROOMID));
            String messageId = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_MESSAGEID));
            String status = cursor.getString(cursor.getColumnIndex(GroupChatTable.COLUMN_NAME_STATUS));
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
            } else if (msgType.equals(MarketApp.MESSAGE_NOTICE)) {
                content = xmlVo.getContent();
            } else if (msgType.equals(MarketApp.SEND_VIDEO)) {
                content = "[视 频]";
            } else if (msgType.equals(MarketApp.SEND_LOCATION)) {
                content = "[位 置]";
            }
            msgGroupVo = new MsgGroupVo(messageId, roomId, "", createTime, fromUserName, toUserName, content, loginUser, status, msgType);
        }
        cursor.close();
        return msgGroupVo;
    }

    /**
     * 更新
     * 
     * @param vo
     * @return
     */
    public long update(String id, String status) {
        dbHelper.getDb();

        ContentValues values = new ContentValues();
        values.put(GroupChatTable.COLUMN_NAME_STATUS, status);

        long num = dbHelper.db.update(GroupChatTable.TABLE_NAME, values, BaseColumns._ID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " = ? ", new String[] { id, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        return num;
    }

    /**
     * 查询messageId
     * 
     * @param id
     * @return
     */
    public String getMessageId(String id) {
        dbHelper.getDb();
        String messageId = "";
        String sql = "select " + GroupChatTable.COLUMN_NAME_MESSAGEID + " from " + GroupChatTable.TABLE_NAME + " where " + BaseColumns._ID + " = ? and " + GroupChatTable.COLUMN_NAME_LOGINUSER + " =? ";
        Cursor cursor = dbHelper.db.rawQuery(sql, new String[] { id, AdminUtils.getUserInfo(MarketApp.app).getAccount() });
        if (cursor.moveToFirst()) {
            messageId = cursor.getString(0);
        }
        cursor.close();
        return messageId;
    }
}
