package com.example.smart_detect;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtils {


    /**
     * 设置状态栏颜色
     * 状态栏没有覆盖在页面布局上
     * 设置为白色，使用{@link StatusBarUtils#setStatusColorWhite(Activity)} 方法
     * @param activity  activity
     * @param color 状态栏颜色
     */
    public static void setStatusColor(Activity activity, int color){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以上直接设置状态栏颜色
            Window window = activity.getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(color);
//
        } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
            rootView.setPadding(0, getStateBarHeight(activity), 0, 0);
            //根布局添加占位状态栏
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStateBarHeight(activity));
            statusBarView.setBackgroundColor(color);
            decorView.addView(statusBarView, lp);
        }

    }

    public static int getStateBarHeight(Activity activity){
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 状态栏颜色设置为白色
     * 状态栏没有覆盖在页面布局上
     * android6.0以上系统，状态栏字体设置为黑色
     * android5.+系统，状态栏设置为透明
     * android4.4 系统，状态栏设置为透明
     * android 4.4以下系统，不能设置状态栏颜色
     * @param activity
     */
    public static void setStatusColorWhite(Activity activity){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //5.0 以上直接设置状态栏颜色
            Window window = activity.getWindow();
            //After LOLLIPOP not translucent status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(Color.WHITE);
            //设置状态栏字体颜色
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
            //5.+ 不能设置状态栏字体颜色，状态栏直接设置为白色，则状态栏字体颜色看不清，因此设置为透明色
            setTranslucentStatus(activity);
        }else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
            rootView.setPadding(0, getStateBarHeight(activity), 0, 0);
            //根布局添加占位状态栏
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStateBarHeight(activity));
            statusBarView.setBackgroundColor(Color.WHITE);
            decorView.addView(statusBarView, lp);
        }

    }


    /**
     * 设置状态栏透明
     * @param activity
     */
    public static void setTranslucentStatus(Activity activity) {
        //设置5.0及其以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 设置沉浸模式，并且状态栏颜色为透明
     * 其中状态栏为透明且覆盖在页面布局上
     * android系统必须4.4及其以上
     * @param activity activity
     */
    public static void setImmersionModel(Activity activity) {
        //设置5.0及其以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
