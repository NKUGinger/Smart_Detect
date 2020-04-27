package com.example.smart_detect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class Picture_ViewActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    ImageView picture;
    TextView back,title;
    boolean flag = false;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 0x01)
            {
                if(msg.obj.equals("OK"))
                {
                    display_toolbar = false;
                }
                if(display_toolbar != display_toolbar_const)
                {
                    if(display_toolbar)
                    {
                        display_toolbar();
                    }
                    else
                    {
                        hide_toolbar();
                    }
                    display_toolbar_const = display_toolbar;
                }
            }
        }
    };
    public int wait = 0;
    boolean display_toolbar = true;
    boolean display_toolbar_const = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        fill_waterdrop_screen();
        hideBottomUIMenu();
        intView();
        Display_Picture(1);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill_waterdrop_screen();
                hideBottomUIMenu();
                if(display_toolbar)
                {
                    display_toolbar = false;
                }
                else
                {
                    display_toolbar = true;
                    wait = 0;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        flag=true;
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                while (flag)
                {
                    if(wait <= 50)
                    {
                        wait = wait + 1;
                    }
                    if(wait == 51)
                    {
                        Message message = new Message();
                        message.what = 0x01;
                        message.obj = "OK";
                        handler.sendMessage(message);
                        wait = 52;
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = 0x01;
                        message.obj = "NO";
                        handler.sendMessage(message);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag=false;
    }

    public void intView()
    {
        constraintLayout=findViewById(R.id.constraintlayout);
        picture=findViewById(R.id.imageView2);
        back=findViewById(R.id.textView3);
        title=findViewById(R.id.textView2);
    }

    public void Display_Picture(int num)
    {
        File file = new File(getExternalFilesDir(null), "picture"+num+".jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(file+"");
        picture.setImageBitmap(bitmap);
    }

    //隐藏导航栏
    private void hideBottomUIMenu()
    {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    //填满水滴屏
    private void fill_waterdrop_screen()
    {
        if (Build.VERSION.SDK_INT >= 28) {
            // 延伸显示区域到刘海
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
            // 设置页面全屏显示
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void display_toolbar()
    {
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
    }

    private void hide_toolbar()
    {
        back.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
    }
}
