package com.lenovo.market.activity.contacts;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lenovo.market.R;
import com.lenovo.market.activity.ViewPaperMenuActivity;
import com.lenovo.market.adapter.FriendVoAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.FriendInfoDBHelper;
import com.lenovo.market.dbhelper.NewFriendInfoDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.view.SideBarView;
import com.lenovo.market.view.SideBarView.OnTouchingLetterChangedListener;
import com.lenovo.market.vo.server.FriendMesVo;
import com.lenovo.market.vo.server.PageDateVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;

/**
 * 通讯录
 *
 * @author zhouyang
 */
public class ContactsFragment extends Fragment implements OnTouchingLetterChangedListener, OnClickListener {

    public static Handler handler;
    public ArrayList<FriendMesVo> friends;

    private ListView lvShow;
    private TextView overlay;
    private SideBarView myView;
    private FriendVoAdapter adapter;
    private OverlayThread overlayThread;
    private LinearLayout newFriendsLayout;
    private LinearLayout contacts_marketing_account;
    private FriendInfoDBHelper friendInfoDB_;
    private TextView newfriend_count;
    private NewFriendInfoDBHelper newFriendDB;
    private LinearLayout business_contacts;
    private ProgressDialog pd;
    private RelativeLayout ic_contacts_title;
    private LinearLayout contacts_group_chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_contacts, container, false);
        setContentView();
        findViewById(inflater, view);
        setListener();
        return view;
    }

    protected void setContentView() {
        handler = new ContactsHandler(this);
        friendInfoDB_ = new FriendInfoDBHelper();
        newFriendDB = new NewFriendInfoDBHelper();
        overlayThread = new OverlayThread();
    }

    protected void findViewById(LayoutInflater inflater, View view) {
        ic_contacts_title = (RelativeLayout) view.findViewById(R.id.ic_contacts_title);
        ic_contacts_title.setVisibility(View.GONE);
        lvShow = (ListView) view.findViewById(R.id.lvShow);
        myView = (SideBarView) view.findViewById(R.id.myView);
        overlay = (TextView) view.findViewById(R.id.tvLetter);

        lvShow.setTextFilterEnabled(true);
        overlay.setVisibility(View.INVISIBLE);

        View headerLayout = inflater.inflate(R.layout.layout_contactslist_header, null);
        newfriend_count = (TextView) headerLayout.findViewById(R.id.newfriend_count);

        int unreadCount = newFriendDB.getNewFriendSubscription();
        if (unreadCount > 0) {
            newfriend_count.setVisibility(View.VISIBLE);
        }
        EditText search = (EditText) headerLayout.findViewById(R.id.et_search);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lvShow.addHeaderView(headerLayout);

        newFriendsLayout = (LinearLayout) headerLayout.findViewById(R.id.contacts_newfriends);
        contacts_group_chat = (LinearLayout) headerLayout.findViewById(R.id.contacts_group_chat);
        contacts_marketing_account = (LinearLayout) headerLayout.findViewById(R.id.contacts_marketing_account);
        business_contacts = (LinearLayout) headerLayout.findViewById(R.id.business_contacts);
        newFriendsLayout.setVisibility(View.VISIBLE);
        contacts_group_chat.setVisibility(View.VISIBLE);
        contacts_marketing_account.setVisibility(View.VISIBLE);
        UserVo userVo = AdminUtils.getUserInfo(getActivity());
        if (userVo != null && !TextUtils.isEmpty(userVo.getCompanyId())) {
            business_contacts.setVisibility(View.VISIBLE);
        } else {
            business_contacts.setVisibility(View.GONE);
        }

        friends = new ArrayList<FriendMesVo>();
        adapter = new FriendVoAdapter(getActivity(),R.drawable.ic_single_chat,friends, lvShow);
        lvShow.setAdapter(adapter);
        ArrayList<FriendMesVo> friendAll = friendInfoDB_.getFriendAll(MarketApp.FRIEND_TYPE_FRIEND);
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            if (friendAll == null) {
                pd = Utils.createProgressDialog(getActivity(), "正在加载数据中......");
                pd.show();
            } else {
                friends.addAll(friendAll);
                Collections.sort(friends);
                adapter.notifyDataSetChanged();
            }
            initFriends();
        } else {
            if (friendAll != null) {
                friends.addAll(friendAll);
                Collections.sort(friends);
                adapter.notifyDataSetChanged();
            }
        }
    }

    protected void setListener() {
        myView.setOnTouchingLetterChangedListener(this);

        newFriendsLayout.setOnClickListener(this);
        contacts_group_chat.setOnClickListener(this);
        contacts_marketing_account.setOnClickListener(this);
        business_contacts.setOnClickListener(this);

        lvShow.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), FriendDetailsActivity.class);
                FriendMesVo friend = (FriendMesVo) adapterView.getAdapter().getItem(position);
                intent.putExtra(MarketApp.FRIEND, friend);
                intent.putExtra(FriendDetailsActivity.DETAILED, 2);
                startActivity(intent);
            }
        });
    }

    public void initFriends() {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("uid", MarketApp.uid);
        maps.put("keystr", null);
        maps.put("currentPageNO", 1);
        maps.put("pageSize", 5000);
        NetUtils.startTask(new TaskListener() {

            @Override
            public void onError(int errorCode, String message) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public void onComplete(String resulte) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                ResultVo rVo = ResultParser.parseJSON(resulte, ResultVo.class);

                if (rVo != null) {
                    String result = rVo.getResult();
                    if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                        TypeToken<PageDateVo<FriendMesVo>> typeToken = new TypeToken<PageDateVo<FriendMesVo>>() {
                        };
                        PageDateVo<FriendMesVo> pageDataVo = ResultParser.parseJSON(rVo.getMsg().toString(), typeToken);
                        ArrayList<FriendMesVo> dataList = pageDataVo.getDateList();
                        if (null != dataList) {
                            friends.clear();
                            friends.addAll(dataList);
                            Collections.sort(friends);
                            adapter.notifyDataSetChanged();
                            // 保存好友信息到本地数据库
                            for (FriendMesVo vo : friends) {
                                vo.setFriendType(1);// 设置好友类型为普通好友
                            }
                            friendInfoDB_.saveFriendList(friends);
                        }
                    }
                }
            }

            @Override
            public void onCancel() {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        }, maps, MarketApp.USER_FRIEND_LIST_METHODNAME, MarketApp.USER_FRIEND_SERVICE, TaskConstant.GET_DATA_23);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        overlay.setText(s);
        overlay.setVisibility(View.VISIBLE);
        handler.removeCallbacks(overlayThread);
        handler.postDelayed(overlayThread, 1000);
        if (alphaIndexer(s) > 0) {
            int position = alphaIndexer(s);
            Log.i("coder", "position:" + position);
            lvShow.setSelection(position);
        }
    }

    private int alphaIndexer(String s) {
        int position = -1;
        if (null == friends)
            return position;
        Locale locale = Locale.getDefault();
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getPy().toUpperCase(locale).startsWith(s)) {
                position = i;
                break;
            }
        }
        return position + 1;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.contacts_newfriends:
                newFriendDB.updateStateAll();
                if (null != ViewPaperMenuActivity.tab_contacts_count) {
                    ViewPaperMenuActivity.tab_contacts_count.setVisibility(View.GONE);
                }
                newfriend_count.setVisibility(View.GONE);
                intent = new Intent(getActivity(), NewFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.contacts_group_chat:
                intent = new Intent(getActivity(),GroupChatListActivity.class);
                startActivity(intent);
                break;
            case R.id.contacts_marketing_account:
                intent = new Intent(getActivity(), MarketingAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.business_contacts:
                intent = new Intent(getActivity(), BusinessContactsActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void updateData(String friendAccount) {
        if (!TextUtils.isEmpty(friendAccount)) {
            String tempAccount = null;
            for (FriendMesVo vo : friends) {
                tempAccount = vo.getFriendAccount();
                if (null != tempAccount && tempAccount.equals(friendAccount)) {
                    friends.remove(vo);
                    Collections.sort(friends);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    static class ContactsHandler extends Handler {
        WeakReference<ContactsFragment> mFragment;

        public ContactsHandler(ContactsFragment fragment) {
            mFragment = new WeakReference<ContactsFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ContactsFragment fragment = mFragment.get();
            if (null == fragment) {
                return;
            }
            switch (msg.what) {
                case MarketApp.HANDLERMESS_ZERO:
                    String friendAccount = (String) msg.obj;
                    fragment.updateData(friendAccount);
                    break;
                case MarketApp.HANDLERMESS_ONE:
                    fragment.initFriends();
                    MarketApp.needUpdateContacts_ = false;
                    break;
                case MarketApp.HANDLERMESS_TWO:
                    int sCount = fragment.newFriendDB.getNewFriendSubscription();
                    if (sCount > 0) {
                        fragment.newfriend_count.setVisibility(View.VISIBLE);
                    } else {
                        fragment.newfriend_count.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    }

    private class OverlayThread implements Runnable {

        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }
}
