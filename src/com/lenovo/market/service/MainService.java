package com.lenovo.market.service;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lenovo.market.util.*;
import org.apache.http.conn.ClientConnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Status;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.activity.circle.FriendListFragment;
import com.lenovo.market.activity.circle.friends.ChatActivity;
import com.lenovo.market.activity.circle.friends.PublicChatActivity;
import com.lenovo.market.activity.circle.group.GroupChatActivity;
import com.lenovo.market.activity.home.HomePageFragment;
import com.lenovo.market.activity.home.WebHomePageFragment;
import com.lenovo.market.activity.setting.friendscircle.FriendsCircleActivity;
import com.lenovo.market.common.ErrorCode;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.Task;
import com.lenovo.market.common.TaskConstant.ResultType;
import com.lenovo.market.common.TaskConstant.TaskType;
import com.lenovo.market.common.TaskRunnable;
import com.lenovo.market.dbhelper.ChatInfoDBHelper;
import com.lenovo.market.dbhelper.ChatRecordDBHelper;
import com.lenovo.market.dbhelper.CustomMenuDBHelper;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.GroupDBHelper;
import com.lenovo.market.dbhelper.MessageDBHelper;
import com.lenovo.market.dbhelper.RoomDBHelper;
import com.lenovo.market.dbhelper.RoomMemberDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.vo.local.ChatRecordVo;
import com.lenovo.market.vo.local.RoomMemberVo;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.UserVo;
import com.lenovo.market.vo.xmpp.MsgChatVo;
import com.lenovo.market.vo.xmpp.MsgGroupVo;
import com.lenovo.market.vo.xmpp.MsgXmlVo;
import com.lenovo.platform.xmpp.XmppUtils;

/**
 * 后台运行的service
 * 
 * @author muqiang
 */
@SuppressLint("UseSparseArrays")
public class MainService extends PlateformService implements Runnable {

    private static boolean isStop; // 分发任务线程运行标记
    private static Thread sTaskThread; // 分发任务的线程
    private static ExecutorService sExecutorService; // 执行任务线程池
    private static LinkedList<Integer> allTask = new LinkedList<Integer>(); // 任务列表
    private static Object lock = new Object();
    private String fileName = "market";

    public final static int ADD_FRIEND_SUC_UPDATE_SERVER = 4;// 好友添加成功后更新webservice服务器
    public final static int DELET_FRIEND_UPDATE_WEBSERVICE = 5;
    public final static int ATTENTION_EXHIBITION_SUC = 6;// 请求关注成功
    // 任务列表
    public static ArrayList<Activity> allActivity = new ArrayList<Activity>(); // 维护所有打开的activity的引用
    public static MainService sMainService;// service 运行状态
    public static boolean serviceState;
    public static Map<Integer, Task> sTaskMap = new HashMap<Integer, Task>();
    public static String USER_PATH;
    public MessageDBHelper messageDb;
    public ChatInfoDBHelper chatInfoDb;
    public ChatRecordDBHelper recordDb;
    private FriendInfoDBHelper friendDb;
    private GroupDBHelper groupDb;
    // private GroupRecordDBHelper groupRecordDb;
    private RoomMemberDBHelper roomMemberDb;
    private RoomDBHelper roomDBHelper;
    private CustomMenuDBHelper customMenuDb;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MainService.class", "mainservice oncreate");
        // 初始化
        messageDb = new MessageDBHelper();
        chatInfoDb = new ChatInfoDBHelper();
        recordDb = new ChatRecordDBHelper();
        friendDb = new FriendInfoDBHelper();
        groupDb = new GroupDBHelper();
        // groupRecordDb = new GroupRecordDBHelper();
        roomMemberDb = new RoomMemberDBHelper();
        roomDBHelper = new RoomDBHelper();
        customMenuDb = new CustomMenuDBHelper();
        sMainService = this;
        serviceState = true;
        isStop = false;
        if (!com.lenovo.market.util.SDFileUtil.getInstance().isFileExist(fileName)) {
            SDFileUtil.getInstance().createDir(fileName);
        }
        USER_PATH = SDFileUtil.getInstance().getSDPath() + "/" + fileName;
        USER_PATH = SDFileUtil.getInstance().getSDPath();

        Log.d("d", "service has created");
        System.out.println("service created");
    }

    public static MainService getInstans() {
        if (sMainService != null)
            return sMainService;
        return null;
    }

    /**
     * 根据activity的名称获取回调刷新的activity
     * 
     * @param activity
     * @return
     * @throws NotActiveException
     * @throws Exception
     */
    public static Activity getActivityByName(String activity) throws NotActiveException {
        Activity wa = null;
        for (Activity w : allActivity) {
            if (w.getClass().getName().indexOf(activity) > 0) {
                wa = w;
                break;
            }
        }
        if (wa == null) {
            throw new NotActiveException("the refresh activity not found");
        }
        return wa;
    }

    /**
     * 根据任务id获取任务
     * 
     * @param taskID
     *            任务id
     * @return 任务
     */
    public static Task getTaskByID(int taskID) {
        synchronized (sTaskMap) {
            if (sTaskMap.containsKey(taskID)) {
                Task task = sTaskMap.get(taskID);
                if (task != null) {
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * 根据id删除任务
     * 
     * @param taskID
     */
    private static void removeTaskByID(int taskID) {
        synchronized (sTaskMap) {
            sTaskMap.remove(taskID);
        }
    }

    /**
     * 根据任务类型取消任务
     * 
     * @param taskID
     *            任务类型
     * @return 是否取消成功
     */
    public static boolean cancelTask(int taskID) {

        Task task = getTaskByID(taskID);
        if (task != null) {
            ClientConnectionManager ccm = task.getCcm();
            if (ccm != null) { // 正在执行网络操作
                task.setCancel(true);
                ccm.shutdown();
                System.out.println("取消了任务");
                return true;
            } else { // 还未执行网络操作
                task.setCancel(true);
                System.out.println("还未网络操作");
                return true;
            }
        } else {
            System.out.println("没有该任务");
            return false;
        }
    }

    /**
     * 处理任务结果
     */
    public static Handler sHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ADD_FRIEND_SUC_UPDATE_SERVER:
                UserVo userVo = AdminUtils.getUserInfo(MarketApp.app);
                // 更新服务器
                if (null != userVo) {
                    String friendNameJid = (String) msg.obj;// jid
                    String friendName = Utils.getUsernameFromJid(friendNameJid);
                    ContactsUtils.addFriend(userVo.getAccount(), friendName);
                }
                break;
            default:
                synchronized (sTaskMap) {

                    if (sTaskMap.containsKey(msg.arg1)) {
                        Task task = sTaskMap.get(msg.arg1);
                        ProgressDialog pd = task.getLodingDialog();
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        TaskListener listener = task.getListener();
                        if (listener != null) { // 需要回调
                            switch (msg.what) {
                            case ResultType.SUCCEED: // 数据获取成功
                                listener.onComplete(msg.obj == null ? "success" : msg.obj.toString(), msg.arg1);
                                break;
                            case ResultType.FAILD:
                                listener.onError(msg.arg2, msg.obj == null ? "error" : msg.obj.toString());
                                break;
                            case ResultType.CANCEL: // 取消了任务
                                listener.onCancel();
                                break;
                            }
                        }
                        sTaskMap.remove(task.getTaskID()); // 删除任务
                    }
                }
                break;
            }
            super.handleMessage(msg);
        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        if (sTaskThread == null) {
            createTaskThread(this); // 创建分发线程
        }
        Log.d("d", "service has started");
        System.out.println("service has started");
        super.onStart(intent, startId);
    }

    /**
     * 添加新任务
     * 
     * @param taskID
     */

    public static void addNewTask(int taskID) {
        Log.d("d", "a new task add to service");
        if (sTaskThread == null && sExecutorService == null) {
            if (allActivity.size() > 0) {
                allActivity.get(0).startService(new Intent(allActivity.get(0), MainService.class));
            }
        }
        synchronized (allTask) {
            allTask.add(taskID);
        }

        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void onDestroy() {
        destroyService();
        MyLogger.commLog().d("onDestroy");
        super.onDestroy();
    }

    /**
     * 创建分发任务线程
     */
    private static void createTaskThread(Runnable r) {
        sExecutorService = Executors.newFixedThreadPool(4);
        isStop = false;
        sTaskThread = new Thread(r);
        sTaskThread.setName("dispatcher task thread");
        sTaskThread.start();
        Log.d("d", "a new task thread has created");
    }

    /**
     * 结束service
     */
    private void destroyService() {
        serviceState = false;
        isStop = true;
        sTaskThread.interrupt();
        sTaskThread = null;
        sExecutorService.shutdown();
        sExecutorService = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        int taskID;
        boolean isLock = false;
        while (!isStop) {
            synchronized (allTask) {
                if (!allTask.isEmpty()) {
                    taskID = allTask.get(0);
                    try {
                        doTask(taskID);
                        allTask.remove(0);
                    } catch (Exception e) {
                        removeTaskByID(taskID); // 执行任务时异常，删除任务
                        Log.e("error", "" + e);
                    }
                } else {
                    isLock = true;
                }
            }
            if (isLock) {
                synchronized (lock) {
                    try {
                        isLock = false;
                        // 07-10 10:35:11.998: W/System.err(1691):
                        // java.lang.InterruptedException错误信息
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据任务id执行任务
     * 
     * @param taskID
     */
    private void doTask(int taskID) {
        Log.d("d", "service do task");

        if (!MarketApp.network_available) { // 没有网络
            BaseActivity.log.d("------------------网络不可用!-----------------");
            Message message = Message.obtain();
            message.arg1 = taskID;
            message.arg2 = ErrorCode.NET_UNAVAILABLE;
            message.obj = ErrorCode.STR_NET_UNAVAILABLE;
            message.what = ResultType.FAILD;
            sHandler.sendMessage(message);
            return;
        }

        Task task = getTaskByID(taskID);
        if (task == null) {
            return; // 没有任务
        }

        TaskRunnable.setTaskID(taskID);
        switch (task.getTaskType()) {
        case TaskType.TYPE_SOLOMO: // 执行solomo任务
            sExecutorService.execute(TaskRunnable.solomoRunnable);
            break;
        }
    }

    private int offlineMsgCount = 0;

    /**
     * 接受组聊的信息
     */
    @Override
    public void processMutilChat(org.jivesoftware.smack.packet.Message message) {

        String content = message.getBody();

        // 屏蔽历史垃圾数据
        if (content == null || content.contains("$#%!#@∑Ψ^")) {
            return;
        }

        // 屏蔽消息
        PacketExtension extension_muc_user = message.getExtension("x", "http://jabber.org/protocol/muc#user");
        if (extension_muc_user instanceof MUCUser) {
            MUCUser mucUser = (MUCUser) extension_muc_user;
            Status status = mucUser.getStatus();
            if (null != status && null != status.getCode()) {
                if (status.getCode().equals("100")) {
                    Log.d(message.getFrom(), message.getBody());
                    return;
                }
            }
        }

        String subject = message.getSubject();
        if (!TextUtils.isEmpty(subject)) {
            Log.d("subject", subject);
            return;
        }

        // 获取消息发送者和房间id
        String room = "";
        String fromUser = "";
        String from = message.getFrom();
        if (from.indexOf("@") > 0) {
            room = from.split("@")[0];
            fromUser = from.split("@")[0];
            if (from.indexOf("/") > 0) {
                fromUser = from.split("/")[1];
            } else {
                // 如果jid不包含“/” 说明是房间发送的消息;暂时不处理
                return;
            }
        }

        // 当前的登录账号
        UserVo userInfo = AdminUtils.getUserInfo(this);
        String account = userInfo.getAccount();

        // 接收离线消息
        boolean isOfflineMsg = false;
        String time = System.currentTimeMillis() + "";
        PacketExtension extension_delay = message.getExtension("x", "jabber:x:delay");
        if (extension_delay instanceof DelayInformation) {
            isOfflineMsg = true;
            DelayInformation information = (DelayInformation) extension_delay;
            long delay_time = information.getStamp().getTime();
            time = delay_time + "";
            String record_time = AdminUtils.getGroupChatTimeFromSP(MarketApp.app, room);
            if (null != record_time) {
                long recordTime = Long.parseLong(record_time);
                if (delay_time <= recordTime) {
                    return;
                }
            }
        }

        // 如果是最新的消息则是xml格式
        MsgXmlVo pullXMLResolve;
        if (content.contains("<?xml version='1.0' encoding='utf-8' ?>")) {
            pullXMLResolve = XMLUtil.pullXMLResolve(content);
        } else {
            // 不是则不处理
            return;
        }

        MsgGroupVo vo = new MsgGroupVo(pullXMLResolve.getMsgId(), room, pullXMLResolve.getMsgType(), pullXMLResolve.getCreateTime(), fromUser, room, message.getBody(), account, "0", pullXMLResolve.getMsgType());
        if (groupDb.isMsgRepeated(vo)) {
            // 消息重复直接返回
            return;
        }

        // 发消息的房间的成员
        String room_members = (String) message.getProperty("room_members");
        if (!TextUtils.isEmpty(room_members)) {
            TypeToken<ArrayList<RoomMemberVo>> typeToken = new TypeToken<ArrayList<RoomMemberVo>>() {
            };
            ArrayList<RoomMemberVo> members = ResultParser.parseJSON(room_members, typeToken);
            if (members != null) {
                for (RoomMemberVo memberVo : members) {
                    roomMemberDb.insert(memberVo);
                }
            }
        }

        ArrayList<MsgGroupVo> list = new ArrayList<MsgGroupVo>();
        ChatRecordVo recordVo = new ChatRecordVo(account, fromUser, time, 0, "", 3, pullXMLResolve.getContent(), account, "0", room, "");

        XMPPConnection connection = XmppUtils.getInstance().getConnection();
        String roomname = "未命名";
        try {
            RoomInfo roomInfo = MultiUserChat.getRoomInfo(connection, room + "@" + MarketApp.ROOM_SERVER_NAME);
            String subject2 = roomInfo.getSubject();
            if (subject2 != null && !subject2.equals("未命名")) {
                roomname = subject2;
            } else {
                roomname = roomInfo.getDescription();
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(roomname)){
            roomDBHelper.updateRoomName(room,roomname);
        }

        RoomMemberVo member = roomMemberDb.getMember(room, fromUser);
        recordVo.setRoomName(roomname == null ? "" : roomname);
        if (member != null) {
            String name = member.getUserName();
            recordVo.setFriendName(name == null ? fromUser : name);
        }

        if (pullXMLResolve.getMsgType().equals(MarketApp.SEND_PIC)) {
            if (fromUser.equals(account)) {
                vo.setType(MarketApp.SEND_PIC);
            } else {
                vo.setType(MarketApp.RECEIVE_PIC);
            }
            recordVo.setContent("[图 片]");
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.SEND_VOICE)) {
            if (fromUser.equals(account)) {
                vo.setType(MarketApp.SEND_VOICE);
            } else {
                vo.setType(MarketApp.RECEIVE_VOICE);
            }
            recordVo.setContent("[语 音]");
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.SEND_TEXT)) {
            if (fromUser.equals(account)) {
                vo.setType(MarketApp.SEND_TEXT);
            } else {
                vo.setType(MarketApp.RECEIVE_TEXT);
            }
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.SEND_VIDEO)) {
            if (fromUser.equals(account)) {
                vo.setType(MarketApp.SEND_VIDEO);
            } else {
                vo.setType(MarketApp.RECEIVE_VIDEO);
            }
            recordVo.setContent("[视 频]");
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.SEND_SHARE)) {
            if (fromUser.equals(account)) {
                vo.setType(MarketApp.SEND_SHARE);
            } else {
                vo.setType(MarketApp.RECEIVE_SHARE);
            }
            recordVo.setContent("[链 接]");
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.SEND_BUSINESSCARD)) {
            if (fromUser.equals(account)) {
                vo.setType(MarketApp.SEND_BUSINESSCARD);
            } else {
                vo.setType(MarketApp.RECEIVE_BUSINESSCARD);
            }
            recordVo.setContent("[名 片]");
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.SEND_LOCATION)) {
            if (fromUser.equals(account)) {
                vo.setType(MarketApp.SEND_LOCATION);
            } else {
                vo.setType(MarketApp.RECEIVE_LOCATION);
            }
            recordVo.setContent("[位 置]");
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.MESSAGETYPE_GROUPCHAT_LEAVEROOM)) {
            // 离开房间消息
            String uid = pullXMLResolve.getFriendId();
            roomMemberDb.delete(room, uid);
            vo.setMsgType(MarketApp.MESSAGE_NOTICE);
            vo.setType(MarketApp.MESSAGE_NOTICE);
            long id = groupDb.insert(vo);
            vo.setId(id + "");
            list.add(vo);
            if (GroupChatActivity.isActive && GroupChatActivity.roomId != null && GroupChatActivity.roomId.equals(room)) {
                GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST, list).sendToTarget();
            }
            return;
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.MESSAGETYPE_GROUPCHAT_ADDMEMBER)) {
            String url = MarketApp.GROUP_IMG_REMOTE_PATH + room;
            String fileName = Utils.getMD5Str(url);
            ImageDownloader.removeBitmapFromMemoryCache(url);
            Utils.deleteCacheFile(this,"pictures",fileName);
            Utils.deleteCacheFile(this,"picture",fileName);
            // 向房间添加成员消息
            String uids = pullXMLResolve.getFriendId();// 格式：uid,uid2,uid3
            if (uids.contains(userInfo.getUid())) {
                // 被踢掉的用户需要启用聊天界面右上角被禁用的设置按钮
                roomDBHelper.modifyKicked(room, 0);
            }
            vo.setMsgType(MarketApp.MESSAGE_NOTICE);
            vo.setType(MarketApp.MESSAGE_NOTICE);
            long id = groupDb.insert(vo);
            vo.setId(id + "");
            list.add(vo);
            if (GroupChatActivity.isActive && GroupChatActivity.roomId != null && GroupChatActivity.roomId.equals(room)) {
                Message msg = GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST);
                msg.obj = list;
                if (uids.contains(userInfo.getUid())) {
                    // 被踢掉的用户需要启用聊天界面右上角被禁用的设置按钮
                    msg.arg1 = MarketApp.HANDLERMESS_ONE;
                }
                msg.sendToTarget();
            }
            return;
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.MESSAGETYPE_GROUPCHAT_KICKMEMBER)) {
            String url = MarketApp.GROUP_IMG_REMOTE_PATH + room;
            String fileName = Utils.getMD5Str(url);
            ImageDownloader.removeBitmapFromMemoryCache(url);
            Utils.deleteCacheFile(this, "pictures", fileName);
            Utils.deleteCacheFile(this,"picture",fileName);
            // 移除房间成员消息
            String uid = pullXMLResolve.getFriendId();
            if (uid.equals(userInfo.getUid())) {
                roomDBHelper.modifyKicked(room, 1);
            }
            roomMemberDb.delete(room, uid);
            vo.setMsgType(MarketApp.MESSAGE_NOTICE);
            vo.setType(MarketApp.MESSAGE_NOTICE);
            long id = groupDb.insert(vo);
            vo.setId(id + "");
            list.add(vo);
            if (GroupChatActivity.isActive && GroupChatActivity.roomId != null && GroupChatActivity.roomId.equals(room)) {
                Message msg = GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST);
                msg.obj = list;
                if (uid.equals(userInfo.getUid())) {
                    // 被踢掉的用户需要禁用聊天界面右上角的设置按钮
                    msg.arg1 = MarketApp.HANDLERMESS_ONE;
                }
                msg.sendToTarget();
            }
            return;
        } else if (pullXMLResolve.getMsgType().equals(MarketApp.MESSAGE_NOTICE) || pullXMLResolve.getMsgType().equals(MarketApp.MESSAGE_TIME)) {
            long id = groupDb.insert(vo);
            vo.setId(id + "");
            list.add(vo);
            if (GroupChatActivity.isActive && GroupChatActivity.roomId != null && GroupChatActivity.roomId.equals(room)) {
                Message msg = GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST);
                msg.obj = list;
                msg.sendToTarget();
            }
            recordDb.updateRoomName(recordVo);
            if (null != FriendListFragment.handler) {
                FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
            }
            return;
        }

        if (isOfflineMsg) {
            offlineMsgCount += 1;
        } else {
            offlineMsgCount = 0;
        }
        if (offlineMsgCount <= 1 && !fromUser.equals(account)) {
            // 震动
            Utils.Vibrate(300);
        }

        long id = groupDb.insert(vo);
        vo.setId(id + "");
        list.add(vo);

        if (GroupChatActivity.isActive && GroupChatActivity.roomId != null && GroupChatActivity.roomId.equals(room)) {
            recordDb.insertRecord(recordVo, false);
            GroupChatActivity.handler.obtainMessage(MarketApp.UPDATE_MESSAGE_LIST, list).sendToTarget();
        } else {
            recordDb.insertRecord(recordVo, true);
            if (null != FriendListFragment.handler) {
                FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
            }
        }

        if (ViewPaperMenuActivity.handler != null) {
            ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
        }
    }

    // openfire返回的数据(单聊)
    @Override
    public void processSinginChat(org.jivesoftware.smack.packet.Message mMsg) {

        if (TextUtils.isEmpty(mMsg.getBody()))
            return;

        MsgXmlVo mVo = XMLUtil.pullXMLResolve(mMsg.getBody());
        if (mVo == null) {
            return;
        }

        if (!TextUtils.isEmpty(mVo.getErrmsg())) {
            Utils.showToast(this, mVo.getErrmsg() + "    " + mVo.getErrcode());
            HomePageFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_SIX);
            return;
        }

        // 创建群组时把人员信息发送出去
        String room_members = (String) mMsg.getProperty("room_members");
        if (!TextUtils.isEmpty(room_members)) {
            // 群组通知消息
            TypeToken<ArrayList<RoomMemberVo>> typeToken = new TypeToken<ArrayList<RoomMemberVo>>() {
            };
            ArrayList<RoomMemberVo> list = ResultParser.parseJSON(room_members, typeToken);
            if (list != null) {
                for (RoomMemberVo memberVo : list) {
                    roomMemberDb.insert(memberVo);
                }
            }
            return;
        }

        // -公众账号和运营账号
        if (TextUtils.isEmpty(mVo.getTargetType())) {
            // 公众账号(包括运营账号)返回的消息
            processPublicAccountMessage(mMsg);
            return;
        }

        // 返回发送者的后缀名 服务器是Smack
        // String msgFrom = mMsg.getFrom().substring(mMsg.getFrom().lastIndexOf("/" + 1));
        // 发送者的账号
        String fromAccount = Utils.getUsernameFromJid(mMsg.getFrom());
        // 当前登录的账号
        String account = AdminUtils.getUserInfo(this).getAccount();
        // mVo.getTargetType() 1-单聊、2-朋友圈
        if (mVo.getTargetType().equals("1")) {
            // 普通单聊返回的消息
            Utils.Vibrate(300);
            MsgChatVo msgChatVo = new MsgChatVo();
            msgChatVo.setCreateTime(mVo.getCreateTime());
            msgChatVo.setFromUserName(fromAccount);
            msgChatVo.setToUserName(account);
            msgChatVo.setLoginUser(account);
            msgChatVo.setStatus("0");
            msgChatVo.setMsgType(mVo.getMsgType());
            msgChatVo.setType("r" + mVo.getMsgType());
            msgChatVo.setContent(mMsg.getBody());
            ArrayList<MsgChatVo> mVos = new ArrayList<MsgChatVo>();
            // 判断是否需要插入时间
            MsgChatVo mv = new MsgChatVo(MarketApp.MESSAGE_TIME, mVo.getCreateTime(), fromAccount, account, "", account, "0", MarketApp.MESSAGE_TIME);
            long id = chatInfoDb.getCreatMessageDate(mv);
            if (id > 0) {
                mv.setId(id + "");
                mVos.add(mv);
            }
            id = chatInfoDb.insertNewMessage(msgChatVo);
            msgChatVo.setId(id + "");
            mVos.add(msgChatVo);
            // 生成recordvo 实体
            FriendMesVo friend = friendDb.getFriend(fromAccount);
            ChatRecordVo record = new ChatRecordVo(fromAccount, friend.getFriendName(), mVo.getCreateTime(), 0, friend.getPicture(), 1, "", account, "0", "", "");
            if (mVo.getMsgType().equals(MarketApp.SEND_PIC)) {
                record.setContent("[图 片]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_VOICE)) {
                record.setContent("[语 音]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_BUSINESSCARD)) {
                record.setContent("[名 片]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_SHARE)) {
                record.setContent("[链 接]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_TEXT)) {
                record.setContent(XMLUtil.pullXMLResolve(mMsg.getBody()).getContent());
            } else if (mVo.getMsgType().equals(MarketApp.SEND_VIDEO)) {
                record.setContent("[视 频]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_LOCATION)) {
                record.setContent("[位 置]");
            }
            if (ChatActivity.ChatActivityIsVisible) {
                // 当前用户与发消息的好友聊天界面处于前台的情况 , 向记录表插入一条聊天记录
                recordDb.insertRecord(record, false);
                // 向handler发送消息更新界面
                Message msg = new Message();
                msg.what = MarketApp.HANDLERMESS_ZERO;
                msg.obj = mVos;
                ChatActivity.handler.sendMessage(msg);
                if (FriendListFragment.handler != null) {
                    FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                }
            } else {
                // 当前用户与发消息的好友聊天界面没有处于前台的情况
                recordDb.insertRecord(record, true);
                // 更新未读消息条数
                if (ViewPaperMenuActivity.handler != null)
                    ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
                if (FriendListFragment.handler != null) {
                    FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                }
            }
        } else if (mVo.getTargetType().equals("2")) {
            // 朋友圈返回的消息
            if (FriendsCircleActivity.handler != null) {
                MyLogger.commLog().d("朋友圈评论的消息:" + mVo.getContent());
                Message squareMsg = FriendsCircleActivity.handler.obtainMessage(MarketApp.HANDLERMESS_TWO);
                squareMsg.obj = mVo.getContent();
                squareMsg.sendToTarget();
            }
        }
    }

    @Override
    public void processPublicAccountMessage(org.jivesoftware.smack.packet.Message mMsg) {

        // 消息发送者账号
        String fromAccount = Utils.getUsernameFromJid(mMsg.getFrom());
        // 运营账号
        String operationalUser = AdminUtils.getOperationalAccount(this);
        // 当前登录的账号
        String account = AdminUtils.getUserInfo(this).getAccount();

        MsgXmlVo mVo = XMLUtil.pullXMLResolve(mMsg.getBody());

        // 判断是不是网页首页的消息
        if (fromAccount.equals(operationalUser)) {
            SharedPreferences sp = getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, MODE_PRIVATE);
            String home_page = sp.getString(MarketApp.HOME_PAGE, "");
            if (!TextUtils.isEmpty(home_page)) {
                Message updateMsg = new Message();
                updateMsg.what = MarketApp.HANDLERMESS_ZERO;
                updateMsg.obj = mVo.getnVos().get(0).getUrl();
                WebHomePageFragment.handler.sendMessage(updateMsg);
                return;
            }
        }

        // 自定义菜单
        if (mVo.getMsgType() != null && mVo.getMsgType().equals(MarketApp.MSG_TYPE_MENU) && mVo.getEvent() != null) {
            Message updateMsg = new Message();
            updateMsg.what = MarketApp.HANDLERMESS_FIVE;
            Bundle bundle = new Bundle();
            bundle.putString("fromAccount", fromAccount);
            bundle.putString("event", mVo.getEvent());
            customMenuDb.delete(fromAccount);
            updateMsg.setData(bundle);
            if (fromAccount.equals(operationalUser) && HomePageFragment.handler != null) {
                HomePageFragment.handler.sendMessage(updateMsg);
            } else if (PublicChatActivity.handler != null) {
                PublicChatActivity.handler.sendMessage(updateMsg);
            }
            return;
        }

        MsgChatVo msgChatVo = new MsgChatVo();
        msgChatVo.setCreateTime(mVo.getCreateTime());
        msgChatVo.setFromUserName(fromAccount);
        msgChatVo.setToUserName(account);
        msgChatVo.setLoginUser(account);
        msgChatVo.setStatus("0");
        String s = "";
        if (mVo.getnVos() != null && mVo.getMsgType().equals(MarketApp.SEND_NEWS)) {
            if (mVo.getnVos().size() > 1) {
                s = "s";
            }
        }
        msgChatVo.setType("r" + mVo.getMsgType() + s);
        msgChatVo.setMsgType(mVo.getMsgType());
        msgChatVo.setContent(mMsg.getBody());

        ArrayList<MsgChatVo> mVos = new ArrayList<MsgChatVo>();
        // 判断是否需要插入时间
        MsgChatVo mv = new MsgChatVo(MarketApp.MESSAGE_TIME, mVo.getCreateTime(), fromAccount, account, "", account, "0", MarketApp.MESSAGE_TIME);
        long id = messageDb.getCreatMessageDate(mv);
        if (id > 0) {
            mv.setId(id + "");
            mVos.add(mv);
        }
        id = messageDb.insertNewMessage(msgChatVo);
        msgChatVo.setId(id + "");
        mVos.add(msgChatVo);

        if (fromAccount.equals(operationalUser)) {
            // 震动
            Utils.Vibrate(300);
            HomePageFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_SIX);
            SharedPreferences sp = getSharedPreferences(MarketApp.SHARED_PREFERENCES_LENOVO, MODE_PRIVATE);
            String home_page = sp.getString(MarketApp.HOME_PAGE, "");
            if (TextUtils.isEmpty(home_page)) {
                // 向handler发送消息更新界面
                Message msg = new Message();
                msg.what = MarketApp.HANDLERMESS_ZERO;
                msg.obj = mVos;
                HomePageFragment.handler.sendMessage(msg);
            }
        } else {
            // 震动
            Utils.Vibrate(300);
            FriendMesVo friend = friendDb.getFriend(fromAccount);
            ChatRecordVo record = new ChatRecordVo(fromAccount, friend.getFriendName(), mVo.getCreateTime(), 0, friend.getPicture(), 2, "", account, "0", "", "");
            if (mVo.getMsgType().equals(MarketApp.SEND_PIC)) {
                record.setContent("[图 片]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_VOICE)) {
                record.setContent("[语 音]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_BUSINESSCARD)) {
                record.setContent("[名 片]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_SHARE)) {
                record.setContent("[链 接]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_TEXT)) {
                record.setContent(XMLUtil.pullXMLResolve(mMsg.getBody()).getContent());
            } else if (mVo.getMsgType().equals(MarketApp.SEND_VIDEO)) {
                record.setContent("[视 频]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_NEWS)) {
                record.setContent("[图 文]");
            } else if (mVo.getMsgType().equals(MarketApp.SEND_LOCATION)) {
                record.setContent("[位 置]");
            }
            if (PublicChatActivity.isActive) {
                // 当前用户与发消息的好友聊天界面处于前台的情况
                recordDb.insertRecord(record, false);
                if (FriendListFragment.handler != null) {
                    FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                }
                // 向handler发送消息更新界面
                Message msg = new Message();
                msg.what = MarketApp.HANDLERMESS_ZERO;
                msg.obj = mVos;
                PublicChatActivity.handler.sendMessage(msg);
                if (FriendListFragment.handler != null) {
                    FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                }
            } else {
                recordDb.insertRecord(record, true);
                // 更新未读消息条数
                if (ViewPaperMenuActivity.handler != null) {
                    ViewPaperMenuActivity.handler.sendEmptyMessage(MarketApp.HANDLERMESS_THREE);
                }
                if (FriendListFragment.handler != null) {
                    FriendListFragment.handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                }
            }
        }
    }
}
