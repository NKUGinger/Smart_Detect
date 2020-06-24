package com.example.smart_detect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class fragment_homepage extends Fragment {
    private LinearLayout linearLayout;
    private TextView title;
    private ImageView picture,back,camera_select_switch,take_picture;

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
    public int wait_time = 100;
    boolean display_toolbar = true;
    boolean display_toolbar_const = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage,container,false);

        intView(view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        flag=true;
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                while (flag)
                {
                    if(wait <= wait_time)
                    {
                        wait = wait + 1;
                    }
                    if(wait == wait_time + 1)
                    {
                        Message message = new Message();
                        message.what = 0x01;
                        message.obj = "OK";
                        handler.sendMessage(message);
                        wait = wait_time + 2;
                    }
                    else
                    {
                        Message message = new Message();
                        message.what = 0x01;
                        message.obj = "NO";
                        handler.sendMessage(message);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag=false;
    }

    private void intView(View view)
    {
        back=view.findViewById(R.id.imageView3);
        linearLayout=view.findViewById(R.id.linearLayout2);
        take_picture=view.findViewById(R.id.imageView4);
        camera_select_switch=view.findViewById(R.id.imageView5);
        picture=view.findViewById(R.id.imageView);
        title=view.findViewById(R.id.textView);
    }

    private void display_toolbar()
    {
        title.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        camera_select_switch.setVisibility(View.VISIBLE);
        picture.setVisibility(View.VISIBLE);
        take_picture.setVisibility(View.VISIBLE);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    private void hide_toolbar()
    {
        title.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        camera_select_switch.setVisibility(View.GONE);
        picture.setVisibility(View.GONE);
        take_picture.setVisibility(View.GONE);
        ViewPager viewPager = getActivity().findViewById(R.id.viewPager1);
        if(viewPager.getCurrentItem()==0)
        {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setBackgroundColor(Color.parseColor("#000000"));
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

}
