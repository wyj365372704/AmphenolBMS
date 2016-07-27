package com.amphenol.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.amphenol.amphenol.R;

/**
 * Created by Carl on 2016/7/14/014.
 */
public class LoadingDialog{
    private Activity context;
    private PopupWindow popupDialog;
    private LayoutInflater layoutInflater;
    private View layout;
    private LinearLayout layout_bg;//背景
    private View circleView;
    private RotateAnimation rotateAnim;
    private AlphaAnimation alphaAnim_in;
    private AlphaAnimation alphaAnim_out;
    public LoadingDialog(Activity context) {
        layoutInflater = LayoutInflater.from(context.getApplicationContext());
        this.context = context;
    }
    private void initAnim() {
        rotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(2000);
        rotateAnim.setRepeatMode(Animation.RESTART);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setInterpolator(new LinearInterpolator());
        alphaAnim_in = new AlphaAnimation(0f, 1f);
        alphaAnim_in.setFillAfter(true);
        alphaAnim_in.setDuration(500);
        alphaAnim_in.setInterpolator(new LinearInterpolator());
        alphaAnim_out = new AlphaAnimation(1f, 0f);
        alphaAnim_out.setFillAfter(true);
        alphaAnim_out.setDuration(100);
        alphaAnim_out.setInterpolator(new LinearInterpolator());
        alphaAnim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                dismiss();
            }
        });
    }

    /**
     * 判断是否显示
     * @return
     */
    public boolean isShowing() {
        if (popupDialog != null && popupDialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * 显示
     */
    public void show() {
        dismiss();
        initAnim();
        layout = (View) layoutInflater.inflate(R.layout.loading_dialog_layout, null);
        circleView = (View) layout.findViewById(R.id.loading_dialog);
        layout_bg = (LinearLayout) layout.findViewById(R.id.loading_back);
        popupDialog = new PopupWindow(layout, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        final View parentView = ((Activity) context).getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        parentView.post(new Runnable() {
            @Override
            public void run() {
                popupDialog.showAtLocation(parentView, Gravity.CENTER, 0, 0);
                layout_bg.startAnimation(alphaAnim_in);
                circleView.startAnimation(rotateAnim);
            }
        });
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        if (popupDialog != null && popupDialog.isShowing()) {
            layout_bg.clearAnimation();
            circleView.clearAnimation();
            popupDialog.dismiss();
        }
    }
}
