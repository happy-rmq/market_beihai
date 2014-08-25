package com.lenovo.market.activity.contacts;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.market.R;
import com.lenovo.market.activity.BaseActivity;
import com.lenovo.market.adapter.BusinessAdapter;
import com.lenovo.market.common.MarketApp;
import com.lenovo.market.common.TaskConstant;
import com.lenovo.market.dbhelper.DepartmentDBHelper;
import com.lenovo.market.dbhelper.DepartmentMemberDBHelper;
import com.lenovo.market.listener.TaskListener;
import com.lenovo.market.util.AdminUtils;
import com.lenovo.market.util.NetUtils;
import com.lenovo.market.util.ResultParser;
import com.lenovo.market.util.Utils;
import com.lenovo.market.vo.local.BusinessContactVo;
import com.lenovo.market.vo.local.DepartmentMemberVo;
import com.lenovo.market.vo.local.DepartmentVo;
import com.lenovo.market.vo.server.EmpUserVo;
import com.lenovo.market.vo.server.OrgVo;
import com.lenovo.market.vo.server.ResultVo;
import com.lenovo.market.vo.server.UserVo;

/**
 * 企业通讯录
 *
 * @author zhouyang
 */
public class BusinessContactsActivity extends BaseActivity implements OnClickListener {

    private DepartmentDBHelper departmentDBHelper;
    private DepartmentMemberDBHelper memberDBHelper;
    private ArrayList<BusinessContactVo> list = new ArrayList<BusinessContactVo>();
    private ListView listview;
    private BusinessAdapter adapter;
    private LinearLayout ll_shortcut;
    private HorizontalScrollView sv_shortcut;
    public boolean needRefresh;
    private Handler handler;

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_business_contacts);
        departmentDBHelper = new DepartmentDBHelper();
        memberDBHelper = new DepartmentMemberDBHelper();
        handler = new ContactsHandler(this);
    }

    private void downloadBusinussContacts(String code) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("companyCode", code);
        ArrayList<BusinessContactVo> depts = departmentDBHelper.getDepartments("");
        if (MarketApp.network_available && NetUtils.hasNetwork()) {
            pd = Utils.createProgressDialog(BusinessContactsActivity.this, "正在获取企业通讯录……");
            pd.show();
            needRefresh = true;
            NetUtils.startTask(listener, maps, MarketApp.GET_ADDRESSBOOK_METHODNAME, MarketApp.USERSERVICE, TaskConstant.GET_DATA_38);
        } else {
            if (depts.size() > 0) {
                BusinessContactVo bcv = depts.get(0);
                if (bcv != null && bcv instanceof DepartmentVo) {
                    DepartmentVo dv = (DepartmentVo) bcv;
                    ArrayList<BusinessContactVo> departments = departmentDBHelper.getDepartments(dv.getDepartmentId());
                    ArrayList<BusinessContactVo> members = memberDBHelper.getDepartmentMembers(dv.getDepartmentId());
                    Collections.sort(members);
                    list.clear();
                    list.addAll(departments);
                    list.addAll(members);
                    adapter.setData(list);
                    adapter.notifyDataSetChanged();
                    listview.setSelection(0);
                }
            }
        }
    }

    @Override
    protected void findViewById() {
        setTitleBarText("企业通讯录");
        setTitleBarRightBtnText("搜索");
        setTitleBarLeftBtnText();
        ll_shortcut = (LinearLayout) findViewById(R.id.ll_shortcut);
        sv_shortcut = (HorizontalScrollView) findViewById(R.id.scrollview);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new BusinessAdapter(list, listview);
        UserVo userVo = AdminUtils.getUserInfo(this);
        if (userVo != null && !TextUtils.isEmpty(userVo.getCompanyId())) {
            downloadBusinussContacts(userVo.getCompanyId());
        }

    }

    @Override
    protected void setListener() {
        btn_left_.setOnClickListener(this);
        btn_right_.setOnClickListener(this);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusinessContactVo vo = adapter.getItem(position);
                if (vo instanceof DepartmentVo) {
                    DepartmentVo dvo = (DepartmentVo) vo;

                    int childCount = ll_shortcut.getChildCount();
                    if (childCount > 0) {
                        LayoutParams img_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        ImageView img = new ImageView(context);
                        img.setBackgroundResource(R.drawable.common_arrow);
                        img.setLayoutParams(img_params);
                        ll_shortcut.addView(img);
                    }
                    LayoutParams tv_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    TextView tv = new TextView(context);
                    int padding = Utils.dip2px(BusinessContactsActivity.this, 10);
                    tv.setPadding(padding, padding, padding, padding);
                    tv.setTextColor(Color.BLACK);
                    tv.setText(dvo.getName());
                    tv.setBackgroundResource(R.drawable.csl_business_contacts_navigation);
                    tv.setLayoutParams(tv_params);
                    tv.setTag(dvo);
                    tv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int index = ll_shortcut.indexOfChild(v);
                            int count = ll_shortcut.getChildCount();
                            ll_shortcut.removeViews(index + 1, count - (index + 1));
                            DepartmentVo vo = (DepartmentVo) v.getTag();
                            ArrayList<BusinessContactVo> departments = departmentDBHelper.getDepartments(vo.getDepartmentId());
                            ArrayList<BusinessContactVo> members = memberDBHelper.getDepartmentMembers(vo.getDepartmentId());
                            list.clear();
                            list.addAll(departments);
                            Collections.sort(members);
                            list.addAll(members);
                            adapter.setData(list);
                            adapter.notifyDataSetChanged();
                            listview.setSelection(0);
                        }
                    });
                    ll_shortcut.addView(tv);
                    sv_shortcut.fullScroll(View.FOCUS_RIGHT);

                    downloadBusinussContacts(dvo.getDepartmentId());
                } else if (vo instanceof DepartmentMemberVo) {
                    DepartmentMemberVo mvo = (DepartmentMemberVo) vo;
                    Intent intent = new Intent(BusinessContactsActivity.this, DepartmentMemberActivity.class);
                    intent.putExtra("member", mvo);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.btn_right:
                Intent intent = new Intent(this, BusinessContactsSearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    TaskListener listener = new TaskListener() {

        @Override
        public void onError(int errorCode, String message) {
            if (pd != null)
                pd.dismiss();
        }

        @Override
        public void onComplete(final String resultstr) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ResultVo rVo = ResultParser.parseJSON(resultstr, ResultVo.class);
                    if (rVo != null) {
                        String result = rVo.getResult();
                        log.d("result--->" + result);
                        if (!TextUtils.isEmpty(result) && "success".equals(result)) {
                            OrgVo orgVo = ResultParser.parseJSON(rVo.getMsg().toString(), OrgVo.class);
                            insertIntoDb(orgVo);
                            if (needRefresh) {
                                Message mess = handler.obtainMessage(MarketApp.HANDLERMESS_ZERO);
                                mess.obj = orgVo;
                                mess.sendToTarget();
                                return;
                            }
                        }
                    }
                    handler.sendEmptyMessage(MarketApp.HANDLERMESS_ZERO);
                }
            }).start();
        }

        @Override
        public void onCancel() {
            if (pd != null)
                pd.dismiss();
        }
    };

    protected void insertIntoDb(OrgVo orgVO) {
        if (orgVO == null)
            return;
        String name = orgVO.getName();
        String id = orgVO.getId();
        String pid = orgVO.getPid();
        ArrayList<OrgVo> orgs = orgVO.getOrgVOs();
        ArrayList<EmpUserVo> users = orgVO.getUsers();
        DepartmentVo vo = new DepartmentVo();
        vo.setDepartmentId(id);
        vo.setName(name);
        vo.setParentDepartmentId(pid);
        departmentDBHelper.delete(id);
        departmentDBHelper.insert(vo);
        if (users != null) {
            memberDBHelper.delete(id);
            for (EmpUserVo user : users) {
                String uid = user.getId();
                String orgId = user.getOrgId();
                String u_name = user.getName();
                String mobile = user.getMobile();
                String email = user.getEmail();
                String photo = user.getPhoto();
                String account = user.getAccount();
                String isSync = user.getIsSync();

                DepartmentMemberVo memberVo = new DepartmentMemberVo();
                memberVo.setId(uid);
                memberVo.setParentDepartmentId(orgId);
                memberVo.setName(u_name);
                memberVo.setPhonenum(mobile);
                memberVo.setPic(photo);
                memberVo.setAccount(account);
                memberVo.setEmail(email);
                memberVo.setIsSync(isSync);
                memberDBHelper.insert(memberVo);
            }
        }
        if (orgs != null) {
            for (OrgVo org : orgs) {
                name = org.getName();
                id = org.getId();
                pid = org.getPid();
                vo = new DepartmentVo();
                vo.setDepartmentId(id);
                vo.setName(name);
                vo.setParentDepartmentId(pid);
                departmentDBHelper.insert(vo);
            }
        }
    }

    protected void initListViewData(OrgVo orgVO) {
        if (orgVO == null)
            return;
        DepartmentVo vo = new DepartmentVo();
        String name = orgVO.getName();
        String id = orgVO.getId();
        String pid = orgVO.getPid();
        vo.setDepartmentId(id);
        vo.setName(name);
        vo.setParentDepartmentId(pid);
        if (pid != null && pid.equals("")) {
            LayoutParams tv_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(context);
            int padding = Utils.dip2px(this, 10);
            tv.setPadding(padding, padding, padding, padding);
            tv.setTextColor(Color.BLACK);
            tv.setText(name);
            tv.setLayoutParams(tv_params);
            tv.setBackgroundResource(R.drawable.csl_business_contacts_navigation);
            tv.setClickable(true);
            tv.setTag(vo);
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int index = ll_shortcut.indexOfChild(v);
                    int count = ll_shortcut.getChildCount();
                    ll_shortcut.removeViews(index + 1, count - (index + 1));
                    DepartmentVo vo = (DepartmentVo) v.getTag();
                    ArrayList<BusinessContactVo> departments = departmentDBHelper.getDepartments(vo.getDepartmentId());
                    ArrayList<BusinessContactVo> members = memberDBHelper.getDepartmentMembers(vo.getDepartmentId());
                    list.clear();
                    list.addAll(departments);
                    Collections.sort(members);
                    list.addAll(members);
                    adapter.setData(list);
                    adapter.notifyDataSetChanged();
                    listview.setSelection(0);
                }
            });
            ll_shortcut.removeAllViews();
            ll_shortcut.addView(tv);
        }

        ArrayList<BusinessContactVo> departments = new ArrayList<BusinessContactVo>();
        ArrayList<BusinessContactVo> members = new ArrayList<BusinessContactVo>();
        ArrayList<OrgVo> orgs = orgVO.getOrgVOs();
        if (orgs != null) {
            for (OrgVo org : orgs) {
                vo = new DepartmentVo();
                name = org.getName();
                id = org.getId();
                pid = org.getPid();
                vo.setDepartmentId(id);
                vo.setName(name);
                vo.setParentDepartmentId(pid);
                departments.add(vo);
            }
        }
        ArrayList<EmpUserVo> users = orgVO.getUsers();
        if (users != null) {
            for (EmpUserVo user : users) {
                String uid = user.getId();
                String orgId = user.getOrgId();
                String u_name = user.getName();
                String mobile = user.getMobile();
                String email = user.getEmail();
                String photo = user.getPhoto();
                String account = user.getAccount();
                String isSync = user.getIsSync();

                DepartmentMemberVo memberVo = new DepartmentMemberVo();
                memberVo.setId(uid);
                memberVo.setParentDepartmentId(orgId);
                memberVo.setName(u_name);
                memberVo.setPhonenum(mobile);
                memberVo.setPic(photo);
                memberVo.setAccount(account);
                memberVo.setEmail(email);
                memberVo.setIsSync(isSync);
                members.add(memberVo);
            }
        }
        list.clear();
        list.addAll(departments);
        Collections.sort(members);
        list.addAll(members);
        adapter = new BusinessAdapter(list, listview);
        listview.setAdapter(adapter);
    }

    static class ContactsHandler extends Handler {
        WeakReference<BusinessContactsActivity> mActivity;

        public ContactsHandler(BusinessContactsActivity activity) {
            mActivity = new WeakReference<BusinessContactsActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BusinessContactsActivity activity = mActivity.get();
            if (null == mActivity) {
                return;
            }
            switch (msg.what) {
                case MarketApp.HANDLERMESS_ZERO:
                    if (activity != null && activity.pd != null) {
                        activity.pd.dismiss();
                    }
                    OrgVo orgVo = (OrgVo) msg.obj;
                    // 刷新listview
                    activity.initListViewData(orgVo);
                    break;

            }
        }
    }
}
