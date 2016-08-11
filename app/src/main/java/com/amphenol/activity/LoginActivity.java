package com.amphenol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.amphenol.R;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;
import com.pgyersdk.update.PgyUpdateManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    private Button mButton;
    private Spinner mEnvSpinner;
    private EditText mUserNameEditText, mPasswordEditText;
    private ArrayAdapter<String> mStringArrayAdapter;
    private NetWorkAccessTools.RequestTaskListener mRequestTaskListener;
    private LoadingDialog mLoadingDialog;
    private static final int REQUEST_CODE_LOGIN_CHECK = 0x10;
    private View.OnClickListener mOnClickListener;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PgyUpdateManager.register(this);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_login_bt:
                        if (mStringArrayAdapter == null || mStringArrayAdapter.getCount() == 0) {
                            ShowToast("环境加载失败，不可登录");
                            return;
                        }
                        String username = mUserNameEditText.getText().toString().trim();
                        String password = mPasswordEditText.getText().toString().trim();
                        String env = mStringArrayAdapter.getItem(mEnvSpinner.getSelectedItemPosition());
                        handleLoginCheck(username, password, env);
                        break;
                }
            }
        };
        mRequestTaskListener = new NetWorkAccessTools.RequestTaskListener() {
            @Override
            public void onRequestStart(int requestCode) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
                mLoadingDialog = new LoadingDialog(LoginActivity.this);
                mLoadingDialog.show();
            }

            @Override
            public void onRequestLoading(int requestCode, long current, long count) {

            }

            @Override
            public void onRequestSuccess(JSONObject jsonObject, int requestCode) {
                try {
                    DecodeManager.decodeLoginCheck(jsonObject, requestCode, myHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowToast("服务器返回错误");
                } finally {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                    }
                }
            }

            @Override
            public void onRequestFail(int requestCode, int errorNo) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
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
        mButton = (Button) findViewById(R.id.activity_login_bt);
        mButton.setOnClickListener(mOnClickListener);
        mEnvSpinner = (Spinner) findViewById(R.id.activity_login_env_sp);
        mUserNameEditText = (EditText) findViewById(R.id.activity_login_username_et);
        mPasswordEditText = (EditText) findViewById(R.id.activity_login_password_et);
    }


    private void handleLoginCheck(String username, String password, String env) {
        if (TextUtils.isEmpty(username)) {
            ShowToast("用户名输入不能为空");
        } else if (TextUtils.isEmpty(password)) {
            ShowToast("密码输入不能为空");
        } else if (TextUtils.isEmpty(env)) {
            ShowToast("环境选择不能为空");
        } else {
            Map<String, String> param = new HashMap<>();
            param.put("username", username);
            param.put("password", password);
            param.put("env", env);
            NetWorkAccessTools.getInstance(getApplicationContext()).getAsyn(CommonTools.getUrl(PropertiesUtil.ACTION_LOGIN_CHECK, getApplicationContext()), param, REQUEST_CODE_LOGIN_CHECK, mRequestTaskListener);
        }
    }

    @Override
    public void initData() {
        myHandler = new MyHandler();
        List<String> envList = getIntent().getStringArrayListExtra("env_list");
        if (envList == null)
            return;
        mStringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, envList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        mStringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mEnvSpinner.setAdapter(mStringArrayAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PgyUpdateManager.unregister();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case REQUEST_CODE_LOGIN_CHECK:
                    if (bundle.getInt("code") == 1) {
                        HashMap<String, String> params = (HashMap<String, String>) bundle.getSerializable("params");
                        String username = params.get("username");
                        String env = params.get("env");
                        if (!TextUtils.equals(username, SessionManager.getUserName(getApplicationContext()))) {//若登入用户发送了改变,清空原有的warehouse以及warehouse_list信息
                            SessionManager.setWarehouse("",getApplicationContext());
                            SessionManager.setWarehouse_list(new ArrayList<String>(),getApplicationContext());
                        }
                        SessionManager.setUserName(username, getApplicationContext());
                        SessionManager.setEnv(env, getApplicationContext());

                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                        finish();
                    } else if (bundle.getInt("code") == 2) {
                        ShowToast("用户名或密码错误");
                    }
                    break;
            }
        }
    }
}
