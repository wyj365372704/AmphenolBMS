package com.amphenol.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.adapter.MenuAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.MenuItem;
import com.amphenol.ui.MenuItemDecoration;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MenuActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private MenuAdapter menuAdapter;
    private MenuAdapter.OnMenuItemClickListener onMenuItemClickListener;
    private List<List<MenuItem>> menuDrawer = new ArrayList<>();//菜单抽屉集合
    private static final int REQUEST_CODE_GET_MENU = 0X10;
    private static final int REQUEST_CODE_QUERY_WAREHOUSE = 0X11;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private MyHandler myHandler = new MyHandler();
    private Toolbar mToolbar;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_menu);
    }

    @Override
    public void initListeners() {
        onMenuItemClickListener = new MenuAdapter.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int menuCode) {
                Intent intent = new Intent();
                ComponentName componentName = null;
                switch (menuCode) {
                    case MenuItem.MENU_CODE_PURCHASE_RECEIPT:
                        componentName = new ComponentName(MenuActivity.this, PurchaseReceiptActivity.class);
                        break;
                    case MenuItem.MENU_CODE_PURCHASE_RETURN:

                        break;
                    case MenuItem.MENU_CODE_SET_UP_WAREHOUSE:
                        componentName = new ComponentName(MenuActivity.this, WareHouseSetUpActivity.class);
                        break;
                    case MenuItem.MENU_CODE_CREATE_REQUISITION:
                        componentName = new ComponentName(MenuActivity.this, CreateRequisitionActivity.class);
                        break;
                    case MenuItem.MENU_CODE_FAST_REQUISITION:
                        componentName = new ComponentName(MenuActivity.this, FastRequisitionActivity.class);
                        break;
                    case MenuItem.MENU_CODE_CHECK_REQUISITION:
                        componentName = new ComponentName(MenuActivity.this, CheckRequisitionActivity.class);
                        break;
                }
                if (componentName != null) {
                    intent.setComponent(componentName);
                    startActivity(intent);
                }
            }
        };
        mRequestTaskListener = new NetWorkAccessTools.RequestTaskListener() {
            @Override
            public void onRequestStart(int requestCode) {

            }

            @Override
            public void onRequestLoading(int requestCode, long current, long count) {

            }

            @Override
            public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
                switch (requestCode) {
                    case REQUEST_CODE_GET_MENU:
                        try {
                            DecodeManager.decodeGetMenu(jsonObject, requestCode, myHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ShowToast("服务器返回错误");
                        }
                        break;
                    case REQUEST_CODE_QUERY_WAREHOUSE:
                        try {
                            DecodeManager.decodeQueryWarehouse(jsonObject, requestCode, myHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ShowToast("服务器返回错误");
                        }
                        break;
                }
            }

            @Override
            public void onRequestFail(int requestCode, int errorNo) {
                if (errorNo == 0) {
                    ShowToast("与服务器连接失败");
                } else {
                    ShowToast("服务器返回错误");
                }
            }
        };
    }

    @Override
    public void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_menu_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new MenuItemDecoration(this, MenuItemDecoration.VERTICAL_LIST));
        mToolbar = (Toolbar) findViewById(R.id.activity_menu_tb);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    @Override
    public void initData() {
        menuAdapter = new MenuAdapter(this, menuDrawer, onMenuItemClickListener);
        mRecyclerView.setAdapter(menuAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InquireMenu();
    }


    /**
     * 根据
     *
     * @param i
     * @return
     */
    private int getMenuDrawID(int i) {
        switch (i) {
            case MenuItem.MENU_CODE_PURCHASE_RECEIPT:
                return R.mipmap.menu_icon_shouhuo;
            case MenuItem.MENU_CODE_PURCHASE_RETURN:
                return R.mipmap.menu_icon_tuihuo;
            case MenuItem.MENU_CODE_CHECK_REQUISITION:
                return R.mipmap.check_requisition;
            case MenuItem.MENU_CODE_CREATE_REQUISITION:
                return R.mipmap.create_requisition;
            case MenuItem.MENU_CODE_FAST_REQUISITION:
                return R.mipmap.fast_requisition;
            case MenuItem.MENU_CODE_SET_UP_SYSTEM:
            case MenuItem.MENU_CODE_SET_UP_WAREHOUSE:
        }
        return R.mipmap.ic_launcher;
    }

    /**
     * 联网请求菜单
     */
    private void InquireMenu() {
        menuDrawer.clear();
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getApplicationContext()));
        param.put("env", SessionManager.getEnv(getApplicationContext()));
        NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_GET_MENU, getApplicationContext()), param, REQUEST_CODE_GET_MENU, mRequestTaskListener);
    }

    private void InquireWareHouse() {
        Map<String, String> param = new HashMap<>();
        param.put("username", SessionManager.getUserName(getApplicationContext()));
        param.put("env", SessionManager.getEnv(getApplicationContext()));
        NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_QUERY_WAREHOUSE, getApplicationContext()), param, REQUEST_CODE_QUERY_WAREHOUSE, mRequestTaskListener);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_GET_MENU:
                    if (bundle.getInt("code") == 1) {
                        ArrayList<HashMap<String, String>> menuList = (ArrayList<HashMap<String, String>>) bundle.getSerializable("menu_list");
                        if (menuList != null) {
                            for (HashMap<String, String> menu : menuList) {
                                List<MenuItem> menuItems = new ArrayList<>();
                                Iterator<String> iterator = menu.keySet().iterator();
                                while (iterator.hasNext()) {
                                    String menuID = iterator.next();
                                    String menuName = menu.get(menuID);
                                    menuItems.add(new MenuItem(menuName, Integer.parseInt(menuID), getMenuDrawID(Integer.parseInt(menuID))));
                                }
                                if (menuItems.size() > 0) {
                                    menuDrawer.add(menuItems);
                                }
                            }
                            List<MenuItem> setUpMenu = new ArrayList<>();
                            setUpMenu.add(new MenuItem("仓库设置", MenuItem.MENU_CODE_SET_UP_WAREHOUSE, getMenuDrawID(MenuItem.MENU_CODE_SET_UP_WAREHOUSE)));
                            menuDrawer.add(setUpMenu);
                        }
                        InquireWareHouse();
                    } else {
                        ShowToast("获取功能菜单失败");
                    }
                    break;
                case REQUEST_CODE_QUERY_WAREHOUSE:
                    if (bundle.getInt("code") == 1) {
                        String warehouse = bundle.getString("warehouse");
                        List<String> warehouseStringlist = bundle.getStringArrayList("warehouse_list");
                        SessionManager.setWarehouse(warehouse, getApplicationContext());
                        SessionManager.setWarehouse_list(warehouseStringlist, getApplicationContext());
                        menuAdapter.notifyDataSetChanged();
                    } else {
                        ShowToast("获取仓库列表失败");
                    }
                    break;
            }
        }
    }
}
