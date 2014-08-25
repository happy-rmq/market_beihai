package com.lenovo.market.dbhelper;

import android.provider.BaseColumns;

/**
 * @author zhouyang <br/>
 *         <a href="http://developer.android.com/training/basics/data-storage/databases.html#ReadDbRow">参见官网范例</a>
 */
public class DatabaseContract {

    public static final String MARKETDB_NAME = "marketDb.db";
    /**
     * 数据库版本号
     */
    public static final int DATABASE_VERSION = 34;// 2014/5/21升级到34

    // This class cannot be instantiated
    private DatabaseContract() {
    }

    /**
     * 群聊表
     * 
     * @author zhouyang
     */
    public static abstract class GroupChatTable implements BaseColumns {

        public static final String TABLE_NAME = "groupchat";
        public static final String COLUMN_NAME_MESSAGEID = "messageId";// openfire消息的id
        public static final String COLUMN_NAME_ROOMID = "roomId";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_MSGTYPE = "msgType";
        public static final String COLUMN_NAME_CREATETIME = "createTime";
        public static final String COLUMN_NAME_FROMUSERNAME = "fromUserName";
        public static final String COLUMN_NAME_TOUSERNAME = "toUserName";// 存储房间的jid
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        public static final String COLUMN_NAME_STATUS = "status";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + " (" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + //
                COLUMN_NAME_MESSAGEID + " TEXT," + //
                COLUMN_NAME_ROOMID + " TEXT," + //
                COLUMN_NAME_MSGTYPE + " TEXT," + //
                COLUMN_NAME_TYPE + " TEXT," + //
                COLUMN_NAME_CREATETIME + " TEXT," + //
                COLUMN_NAME_FROMUSERNAME + " TEXT," + //
                COLUMN_NAME_TOUSERNAME + " TEXT," + //
                COLUMN_NAME_CONTENT + " TEXT," + //
                COLUMN_NAME_LOGINUSER + " TEXT," + //
                COLUMN_NAME_STATUS + " TEXT" + //
                " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /**
     * 群聊记录表
     * 
     * @author zhouyang
     */
    public static abstract class GroupChatRecordTable implements BaseColumns {

        public static final String TABLE_NAME = "grouprecord";
        public static final String COLUMN_NAME_ROOMID = "roomId";
        public static final String COLUMN_NAME_FROMUSERACCOUNT = "fromUserAccount";
        public static final String COLUMN_NAME_FROMUSERNAME = "fromUserName";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_CREATETIME = "createTime";
        public static final String COLUMN_NAME_ROOMNAME = "roomName";
        public static final String COLUMN_NAME_UNREADCOUNT = "unreadCount";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + 
                TABLE_NAME + "(" + 
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + 
                COLUMN_NAME_ROOMID + " TEXT," + 
                COLUMN_NAME_FROMUSERACCOUNT + " TEXT," + 
                COLUMN_NAME_FROMUSERNAME + " TEXT," + 
                COLUMN_NAME_CONTENT + " TEXT," + 
                COLUMN_NAME_CREATETIME + " TEXT," + 
                COLUMN_NAME_ROOMNAME + " TEXT," + //
                COLUMN_NAME_UNREADCOUNT + " TEXT," + 
                COLUMN_NAME_LOGINUSER + " TEXT" + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 房间表
     * 
     * @author zhouyang
     */
    public static abstract class RoomTable implements BaseColumns {
        private RoomTable() {
        }

        public static final String TABLE_NAME = "room";
        public static final String COLUMN_NAME_ROOMID = "roomId";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        public static final String COLUMN_NAME_ISKICKED = "isKicked";// 0---在房间 1---被踢出
        public static final String COLUMN_NAME_ROOMNAME = "roomName";// 房间名

        /**
         * 房间表创建语句
         */
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + //
                COLUMN_NAME_ROOMID + " TEXT NOT NULL," + //
                COLUMN_NAME_ISKICKED + " INTEGER DEFAULT 0," + //
                COLUMN_NAME_LOGINUSER + " TEXT NOT NULL," + //
                COLUMN_NAME_ROOMNAME + " TEXT" + //
                " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 房间成员表
     * 
     * @author zhouyang
     */
    public static abstract class RoomMemberTable implements BaseColumns {
        private RoomMemberTable() {
        }

        public static final String TABLE_NAME = "roommember";
        public static final String COLUMN_NAME_ROOMID = "roomId";
        public static final String COLUMN_NAME_MEMBERID = "memberId";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_USERNAME = "userName";
        public static final String COLUMN_NAME_NICKNAME = "nickName";
        public static final String COLUMN_NAME_AVATAR = "avatar";

        /**
         * 房间成员表创建语句
         */
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + //
                COLUMN_NAME_ROOMID + " TEXT NOT NULL," + //
                COLUMN_NAME_MEMBERID + " TEXT NOT NULL," + //
                COLUMN_NAME_ACCOUNT + " TEXT NOT NULL," + //
                COLUMN_NAME_USERNAME + " TEXT," + //
                COLUMN_NAME_NICKNAME + " TEXT," + //
                COLUMN_NAME_AVATAR + "  TEXT" + //
                " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    /**
     * 部门表
     * 
     * @author zhouyang
     */
    public static abstract class DepartmentTable implements BaseColumns {
        // This class cannot be instantiated
        private DepartmentTable() {
        }

        public static final String TABLE_NAME = "department";
        public static final String COLUMN_NAME_NAME = "name";// 部门名称
        public static final String COLUMN_NAME_DEPARTMENTID = "departmentId";// 部门id
        public static final String COLUMN_NAME_PARENTDEPARTMENTID = "parentDepartmentId";// 父级部门id

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_NAME + " TEXT NOT NULL, " + //
                COLUMN_NAME_DEPARTMENTID + " TEXT NOT NULL, " + //
                COLUMN_NAME_PARENTDEPARTMENTID + " TEXT NOT NULL" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 部门成员表
     * 
     * @author zhouyang
     */
    public static abstract class DepartmentMemberTable implements BaseColumns {
        // This class cannot be instantiated
        private DepartmentMemberTable() {
        }

        public static final String TABLE_NAME = "departmentmember";
        public static final String COLUMN_NAME_MEMBERID = "memberId";// [成员id]
        public static final String COLUMN_NAME_NAME = "name";// [姓名]
        public static final String COLUMN_NAME_PARENTDEPARTMENTID = "parentDepartmentId";// [父级部门id]
        public static final String COLUMN_NAME_ACCOUNT = "account";// [账号]
        public static final String COLUMN_NAME_PHONENUM = "phoneNum";// [手机号]
        public static final String COLUMN_NAME_EMAIL = "email";// [电子邮件]
        public static final String COLUMN_NAME_PIC = "pic";// [头像]
        public static final String COLUMN_NAME_ISSYNC = "isSync";// [是否为为服务平台用户]

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_MEMBERID + " TEXT NOT NULL, " + //
                COLUMN_NAME_PARENTDEPARTMENTID + " TEXT NOT NULL, " + //
                COLUMN_NAME_NAME + " TEXT NOT NULL, " + //
                COLUMN_NAME_ACCOUNT + " TEXT NOT NULL, " + //
                COLUMN_NAME_PHONENUM + " TEXT, " + //
                COLUMN_NAME_EMAIL + " TEXT, " + //
                COLUMN_NAME_ISSYNC + " TEXT, " + //
                COLUMN_NAME_PIC + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 自定义菜单
     * 
     * @author muqiang
     */
    public static abstract class CustomMenuTable implements BaseColumns {
        // This class cannot be instantiated
        private CustomMenuTable() {
        }

        public static final String TABLE_NAME = "custommenu";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_KEYWORD = "keyWord";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PARENTID = "parentId";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        public static final String COLUMN_NAME_EMPID = "empId";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_TYPE + " TEXT, " + //
                COLUMN_NAME_NAME + " TEXT, " + //
                COLUMN_NAME_KEYWORD + " TEXT, " + //
                COLUMN_NAME_KEY + " TEXT, " + //
                COLUMN_NAME_URL + " TEXT, " + //
                COLUMN_NAME_PARENTID + " TEXT, " + //
                COLUMN_NAME_EMPID + " TEXT, " + //
                COLUMN_NAME_LOGINUSER + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 用户信息表
     * 
     * @author zhouyang
     */
    public static abstract class UserInfoTable implements BaseColumns {
        // This class cannot be instantiated
        private UserInfoTable() {
        }

        public static final String TABLE_NAME = "userinfo";// 表名
        public static final String COLUMN_NAME_ACCOUNT = "account";// 账号
        public static final String COLUMN_NAME_PASSWORD = "passWord";// 密码
        public static final String COLUMN_NAME_UID = "uId";//
        public static final String COLUMN_NAME_NAME = "name";// 昵称
        public static final String COLUMN_NAME_PHONE = "phone";// 电话
        public static final String COLUMN_NAME_HEADURL = "headUrl";// 头像
        public static final String COLUMN_NAME_SIGN = "sign";// 个性签名
        public static final String COLUMN_NAME_SEX = "sex";// 性别
        public static final String COLUMN_NAME_QRCODE = "qrCode";// 二维码
        public static final String COLUMN_NAME_DEFAULTSERVACCOUNT = "defaultServAccount";// 平台账号
        public static final String COLUMN_NAME_DEFAULTSERVID = "defaultServId";// 平台id
        public static final String COLUMN_NAME_COMPANYID = "companyId";// 企业id(企业通讯录)
        public static final String COLUMN_NAME_SERVACCOUNT = "servAccount";// 企业账号(首页)
        public static final String COLUMN_NAME_SERVID = "servId";// 企业id(首页)

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_ACCOUNT + " TEXT NOT NULL, " + //
                COLUMN_NAME_PASSWORD + " TEXT, " + //
                COLUMN_NAME_UID + " TEXT NOT NULL, " + //
                COLUMN_NAME_NAME + " TEXT, " + //
                COLUMN_NAME_PHONE + " TEXT, " + //
                COLUMN_NAME_HEADURL + " TEXT, " + //
                COLUMN_NAME_SIGN + " TEXT, " + //
                COLUMN_NAME_SEX + " TEXT, " + //
                COLUMN_NAME_QRCODE + " TEXT, " + //
                COLUMN_NAME_DEFAULTSERVACCOUNT + " TEXT, " + //
                COLUMN_NAME_DEFAULTSERVID + " TEXT, " + //
                COLUMN_NAME_COMPANYID + " TEXT, " + //
                COLUMN_NAME_SERVACCOUNT + " TEXT, " + //
                COLUMN_NAME_SERVID + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 新朋友
     * 
     * @author muqiang
     */
    public static abstract class NewFriendTable implements BaseColumns {
        // This class cannot be instantiated
        private NewFriendTable() {
        }

        public static final String TABLE_NAME = "newfriendinfo";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_AREA = "area";
        public static final String COLUMN_NAME_SIGN = "sign";
        public static final String COLUMN_NAME_SEX = "sex";
        public static final String COLUMN_NAME_SUBSCRIPTION = "subscription";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        public static final String COLUMN_NAME_STATE = "state";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_ACCOUNT + " TEXT, " + //
                COLUMN_NAME_NAME + " TEXT, " + //
                COLUMN_NAME_PICTURE + " TEXT, " + //
                COLUMN_NAME_AREA + " TEXT, " + //
                COLUMN_NAME_SIGN + " TEXT, " + //
                COLUMN_NAME_SEX + " TEXT, " + //
                COLUMN_NAME_SUBSCRIPTION + " TEXT, " + //
                COLUMN_NAME_LOGINUSER + " TEXT, " + //
                COLUMN_NAME_STATE + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 首页消息表
     * 
     * @author muqiang
     */
    public static abstract class MessageInfoTable implements BaseColumns {
        // This class cannot be instantiated
        private MessageInfoTable() {
        }

        public static final String TABLE_NAME = "messageinfo";
        public static final String COLUMN_NAME_MSGTYPE = "msgType";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_CREATETIME = "createTime";
        public static final String COLUMN_NAME_FROMUSERNAME = "fromUserName";
        public static final String COLUMN_NAME_TOUSERNAME = "toUserName";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        public static final String COLUMN_NAME_STATUS = "status";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_MSGTYPE + " TEXT, " + //
                COLUMN_NAME_TYPE + " TEXT, " + //
                COLUMN_NAME_CREATETIME + " TEXT, " + //
                COLUMN_NAME_FROMUSERNAME + " TEXT, " + //
                COLUMN_NAME_TOUSERNAME + " TEXT, " + //
                COLUMN_NAME_CONTENT + " TEXT, " + //
                COLUMN_NAME_LOGINUSER + " TEXT, " + //
                COLUMN_NAME_STATUS + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 单聊消息表
     * 
     * @author muqiang
     */
    public static abstract class ChatInfoTable implements BaseColumns {
        // This class cannot be instantiated
        private ChatInfoTable() {
        }

        public static final String TABLE_NAME = "chatinfo";
        public static final String COLUMN_NAME_MSGTYPE = "msgType";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_CREATETIME = "createTime";
        public static final String COLUMN_NAME_FROMUSERNAME = "fromUserName";
        public static final String COLUMN_NAME_TOUSERNAME = "toUserName";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        public static final String COLUMN_NAME_STATUS = "status";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_MSGTYPE + " TEXT, " + //
                COLUMN_NAME_TYPE + " TEXT, " + //
                COLUMN_NAME_CREATETIME + " TEXT, " + //
                COLUMN_NAME_FROMUSERNAME + " TEXT, " + //
                COLUMN_NAME_TOUSERNAME + " TEXT, " + //
                COLUMN_NAME_CONTENT + " TEXT, " + //
                COLUMN_NAME_LOGINUSER + " TEXT, " + //
                COLUMN_NAME_STATUS + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 单聊会话消息表
     * 
     * @author muqiang
     */
    public static abstract class ChatRecordTable implements BaseColumns {
        // This class cannot be instantiated
        private ChatRecordTable() {
        }

        public static final String TABLE_NAME = "chatrecord";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_FRIENDACCOUNT = "friendAccount";
        public static final String COLUMN_NAME_FRIENDNAME = "friendName";
        public static final String COLUMN_NAME_CREATETIME = "createTime";
        public static final String COLUMN_NAME_UNREADCOUNT = "unreadcount";
        public static final String COLUMN_NAME_FRIENDPIC = "friendPic";
        public static final String COLUMN_NAME_FRIENDTYPE = "FriendType";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_ROOMID = "roomId";
        public static final String COLUMN_NAME_ROOMNAME = "roomName";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_CONTENT + " TEXT, " + //
                COLUMN_NAME_FRIENDACCOUNT + " TEXT, " + //
                COLUMN_NAME_FRIENDNAME + " TEXT, " + //
                COLUMN_NAME_CREATETIME + " TEXT, " + //
                COLUMN_NAME_UNREADCOUNT + " TEXT," + //
                COLUMN_NAME_FRIENDPIC + " TEXT," + //
                COLUMN_NAME_FRIENDTYPE + " TEXT," + //
                COLUMN_NAME_LOGINUSER + " TEXT," + //
                COLUMN_NAME_STATUS + " TEXT," + //
                COLUMN_NAME_ROOMID + " TEXT," + //
                COLUMN_NAME_ROOMNAME + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 好友信息表
     * 
     * @author muqiang
     */
    public static abstract class FriendInfoTable implements BaseColumns {
        // This class cannot be instantiated
        private FriendInfoTable() {
        }

        public static final String TABLE_NAME = "friendinfo";
        public static final String COLUMN_NAME_MYID = "myId";
        public static final String COLUMN_NAME_FRIENDTYPE = "friendType";
        public static final String COLUMN_NAME_FRIENDID = "friendId";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_AREA = "area";
        public static final String COLUMN_NAME_SIGN = "sign";
        public static final String COLUMN_NAME_SEX = "sex";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //
                COLUMN_NAME_MYID + " TEXT, " + //
                COLUMN_NAME_FRIENDTYPE + " TEXT, " + //
                COLUMN_NAME_FRIENDID + " TEXT, " + //
                COLUMN_NAME_ACCOUNT + " TEXT, " + //
                COLUMN_NAME_NAME + " TEXT, " + //
                COLUMN_NAME_PICTURE + " TEXT," + //
                COLUMN_NAME_AREA + " TEXT," + //
                COLUMN_NAME_SIGN + " TEXT," + //
                COLUMN_NAME_SEX + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 好友圈分享消息
     * 
     * @author muqiang
     */
    public static abstract class FriendSquareShareTable implements BaseColumns {
        // This class cannot be instantiated
        private FriendSquareShareTable() {
        }

        public static final String TABLE_NAME = "friendsquareshare";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SETTING = "setting";
        public static final String COLUMN_NAME_ISSHARE = "isShare";
        public static final String COLUMN_NAME_SHARETITLE = "shareTitle";
        public static final String COLUMN_NAME_SHAREURL = "shareUrl";
        public static final String COLUMN_NAME_CREATEUSER = "createUser";
        public static final String COLUMN_NAME_CREATETIME = "createTime";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                COLUMN_NAME_ID + " TEXT, " + //
                COLUMN_NAME_CONTENT + " TEXT, " + //
                COLUMN_NAME_SETTING + " TEXT, " + //
                COLUMN_NAME_ISSHARE + " TEXT, " + //
                COLUMN_NAME_SHARETITLE + " TEXT, " + //
                COLUMN_NAME_SHAREURL + " TEXT, " + //
                COLUMN_NAME_CREATEUSER + " TEXT," + //
                COLUMN_NAME_CREATETIME + " TEXT," + //
                COLUMN_NAME_LOGINUSER + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 好友圈评论消息
     * 
     * @author muqiang
     */
    public static abstract class FriendSquareCommentsTable implements BaseColumns {
        // This class cannot be instantiated
        private FriendSquareCommentsTable() {
        }

        public static final String TABLE_NAME = "friendsquarecomments";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TOPICID = "topicId";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_CREATEUSER = "createUser";
        public static final String COLUMN_NAME_CREATETIME = "createTime";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                COLUMN_NAME_ID + " TEXT, " + //
                COLUMN_NAME_TOPICID + " TEXT, " + //
                COLUMN_NAME_CONTENT + " TEXT, " + //
                COLUMN_NAME_PID + " TEXT, " + //
                COLUMN_NAME_TYPE + " TEXT, " + //
                COLUMN_NAME_CREATEUSER + " TEXT," + //
                COLUMN_NAME_CREATETIME + " TEXT," + //
                COLUMN_NAME_LOGINUSER + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * 好友圈图片
     * 
     * @author muqiang
     */
    public static abstract class FriendSquareImgTable implements BaseColumns {
        // This class cannot be instantiated
        private FriendSquareImgTable() {
        }

        public static final String TABLE_NAME = "friendsquareimg";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TOPICID = "topicId";
        public static final String COLUMN_NAME_FILEID = "fileId";
        public static final String COLUMN_NAME_FILENAME = "fileName";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_LOGINUSER = "loginUser";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + //
                TABLE_NAME + "(" + //
                COLUMN_NAME_ID + " TEXT, " + //
                COLUMN_NAME_TOPICID + " TEXT, " + //
                COLUMN_NAME_FILEID + " TEXT, " + //
                COLUMN_NAME_FILENAME + " TEXT, " + //
                COLUMN_NAME_URL + " TEXT, " + //
                COLUMN_NAME_LOGINUSER + " TEXT" + //
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
