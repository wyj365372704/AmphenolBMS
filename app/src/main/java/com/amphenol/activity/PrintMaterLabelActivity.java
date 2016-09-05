package com.amphenol.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amphenol.amphenol.R;

import java.util.Calendar;

import com.graduate.squirrel.ui.wheel.ScreenInfo;
import com.graduate.squirrel.ui.wheel.WheelMain;

/**
 * Created by Carl on 2016-09-05 005.
 */
public class PrintMaterLabelActivity extends BaseActivity {

    private Button mStorButton;
    private TextView mDateEditText;
    private View.OnClickListener mOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_print_mater_label);
    }

    @Override
    public void initViews() {
        mDateEditText = (TextView) findViewById(R.id.activity_print_mater_label_date);
        mDateEditText.setOnClickListener(mOnClickListener);
    }

    @Override
    public void initListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_print_mater_label_date:
                        Calendar calendar = Calendar.getInstance();
                        showSetDatePicker(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void collapseButton() {
        if (mStorButton.getVisibility() == View.GONE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mStorButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mStorButton.startAnimation(animation);
    }

    private void popUpButton() {
        if (mStorButton.getVisibility() == View.VISIBLE)
            return;
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(300);
        mStorButton.setVisibility(View.VISIBLE);
        mStorButton.startAnimation(animation);
    }

    private void showSetDatePicker(int year, int month, int day) {

        final WheelMain wheelMain;

        LayoutInflater inflater = LayoutInflater.from(this);
        View timepickerview = inflater.inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(this);
        wheelMain = new WheelMain(timepickerview);
        wheelMain.screenheight = screenInfo.getHeight();
        wheelMain.initDateTimePicker(year, month, day);
        new AlertDialog.Builder(this)
                .setTitle("选择日期")
                .setView(timepickerview)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newDate = wheelMain.getTime();
                        String[] split = newDate.split("-");
                        if (split[1].length() == 1) {
                            split[1] = "0" + split[1];
                        }
                        if (split[2].length() == 1) {
                            split[2] = "0" + split[2];
                        }
                        newDate = split[0] + "/" + split[1] + "/" + split[2];
                        mDateEditText.setText(newDate);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
