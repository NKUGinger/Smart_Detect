package com.example.smart_detect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.srplab.www.starcore.*;

public class Python_TestActivity extends AppCompatActivity {
    public StarSrvGroupClass SrvGroup;
    public LinearLayout linearLayout;
    public TextView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_python_test);

        fill_waterdrop_screen();
        hideBottomUIMenu();
        intView();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fill_waterdrop_screen();
                hideBottomUIMenu();
            }
        });

        final File appFile = getFilesDir();  /*-- /data/data/packageName/files --*/
        final String appLib = getApplicationInfo().nativeLibraryDir;
        AsyncTask.execute(new Runnable()
        {
            @Override
            public void run()
            {
                loadPy(appFile,appLib);
            }
        });
    }

    private void intView()
    {
        linearLayout=findViewById(R.id.linearLayout4);
        test=findViewById(R.id.textView2);
    }

    void loadPy(File appFile,String appLib)
    {
        //拷贝Python相关环境
        File pythonLibFile = new File(appFile, "python3.7.zip");
        if (!pythonLibFile.exists()) {
            copyFile(this, "python3.7.zip");
            copyFile(this, "_struct.cpython-37m.so");
            copyFile(this, "binascii.cpython-37m.so");
            copyFile(this, "time.cpython-37m.so");
            copyFile(this, "zlib.cpython-37m.so");
        }

        // 拷贝Python 代码
        copyFile(this, "calljava.py");
        copyFile(this, "test.py");

        try {
            // 加载Python解释器
            System.load(appLib + File.separator + "libpython3.7m.so");

            // 除了将代码直接拷贝，还支持将代码压缩为zip包，通过Install方法解压到指定路径
            InputStream dataSource = getAssets().open("py_code.zip");
            StarCoreFactoryPath.Install(dataSource, appFile.getPath(),true );
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*----init starcore----*/
        StarCoreFactoryPath.StarCoreCoreLibraryPath = appLib;
        StarCoreFactoryPath.StarCoreShareLibraryPath = appLib;
        StarCoreFactoryPath.StarCoreOperationPath = appFile.getPath();

        StarCoreFactory starcore = StarCoreFactory.GetFactory();
        StarServiceClass Service = starcore._InitSimple("test", "123", 0, 0);
        SrvGroup = (StarSrvGroupClass) Service._Get("_ServiceGroup");
        Service._CheckPassword(false);

        /*----run python code----*/
        SrvGroup._InitRaw("python", Service);
        StarObjectClass python = Service._ImportRawContext("python", "", false, "");

        // 设置Python模块加载路径

        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        //从此句代码开始出错闪退
        python._Call("import","sys");
//        StarObjectClass pythonSys = python._GetObject("sys");
//        StarObjectClass pythonPath = (StarObjectClass) pythonSys._Get("path");
//        pythonPath._Call("insert", 0, appFile.getPath()+ File.separator +"python3.7.zip");
//        pythonPath._Call("insert", 0, appLib);
//        pythonPath._Call("insert", 0, appFile.getPath());

        test.setText("代码运行这里了！");

//        //调用Python代码
//        Service._DoFile("python", appFile.getPath() + "/py_code.py", "");
//        long time = python._Calllong("get_time");
//        Log.d("", "form python time="+time);
//
//        Service._DoFile("python", appFile.getPath() + "/test.py", "");
//        int result = python._Callint("add", 5, 2);
//        Log.d("", "result="+result);
//
//        python._Set("JavaClass", Log.class);
//        Service._DoFile("python", appFile.getPath() + "/calljava.py", "");
    }

    private void copyFile(Context c, String Name)
    {
        File outfile = new File(c.getFilesDir(), Name);
        BufferedOutputStream outStream = null;
        BufferedInputStream inStream = null;
        try
        {
            outStream = new BufferedOutputStream(new FileOutputStream(outfile));
            inStream = new BufferedInputStream(c.getAssets().open(Name));

            byte[] buffer = new byte[1024 * 10];
            int readLen = 0;
            while ((readLen = inStream.read(buffer)) != -1)
            {
                outStream.write(buffer, 0, readLen);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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
}