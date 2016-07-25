package com.amphenol.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amphenol.amphenol.R;

public class SetUpActivity extends BaseActivity {
    private TextView mWareHouseTextView;
    private View.OnClickListener mOnClickListener;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_set_up);
    }

    @Override
    public void initViews() {
        mWareHouseTextView = (TextView) findViewById(R.id.activity_set_up_warehouse_tv);
        mWareHouseTextView.setOnClickListener(mOnClickListener);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_set_up_warehouse_tv:

                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }
}
