package com.example.smart_detect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    TextView tv1;
    TextView tv2;
    TextView tv3;
    EditText et1;
    EditText et2;
    Button login;
    Button register;
    CheckBox Remember_Password;
    CheckBox Automatic_Login;
    String rootXMLPath = Environment.getExternalStorageDirectory().getPath()+"/Smart_Detect";
    boolean flag = false;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 0x01)
            {
                fill_waterdrop_screen();
                hideBottomUIMenu();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        intView();
        if (readFromXML(rootXMLPath+"/RP_Set.txt").equals("Remember_Password_On"))
        {
            Remember_Password.setChecked(true);
            Load_account_and_password();
        }
        if (readFromXML(rootXMLPath+"/AL_Set.txt").equals("Automatic_Login"))
        {
            Automatic_Login.setChecked(true);
            Login();
        }
        login.setOnClickListener(new L1());
        register.setOnClickListener(new L2());
        Remember_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_RA_Dir();
                Check_AP_Dir();
                if (Remember_Password.isChecked())
                {
                    writeToXML(rootXMLPath+"/Account_and_password/Account.txt",et1.getText().toString());
                    writeToXML(rootXMLPath+"/Account_and_password/Password.txt",et2.getText().toString());
                    writeToXML(rootXMLPath+"/RP_Set.txt","Remember_Password_On");
                    Toast.makeText(LoginActivity.this,"记住密码",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    File file3 = new File(rootXMLPath+"/Account_and_password/Account.txt");
                    file3.delete();
                    File file4 = new File(rootXMLPath+"/Account_and_password/Password.txt");
                    file4.delete();
                    writeToXML(rootXMLPath+"/RP_Set.txt","Remember_Password_Off");
                    Toast.makeText(LoginActivity.this,"取消记住密码",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Automatic_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_RA_Dir();
                if(Automatic_Login.isChecked())
                {
                    Toast.makeText(LoginActivity.this,"自动登录",Toast.LENGTH_SHORT).show();
                    writeToXML(rootXMLPath+"/AL_Set.txt","Automatic_Login");
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"取消自动登录",Toast.LENGTH_SHORT).show();
                    writeToXML(rootXMLPath+"/AL_Set.txt","Manual_Login");
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
                    Message message = new Message();
                    message.what = 0x01;
                    message.obj = "";
                    handler.sendMessage(message);
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
    //横屏
    private void load_LandScape()
    {
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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

    //引用控件
    private void intView()
    {
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);
        tv3=findViewById(R.id.tv3);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        login=findViewById(R.id.b1);
        register=findViewById(R.id.b2);
        Remember_Password=findViewById(R.id.checkBox8);
        Automatic_Login=findViewById(R.id.checkBox9);
    }

    //加载记住的账号和密码
    private void Load_account_and_password()
    {
        et1.setText(readFromXML(rootXMLPath+"/Account_and_password/Account.txt"));
        et2.setText(readFromXML(rootXMLPath+"/Account_and_password/Password.txt"));
    }

    //检查账号密码的存储位置
    void Check_AP_Dir()
    {
        File file1=new File(rootXMLPath);
        if(!file1.exists())
        {
            file1.mkdir();
        }
        File file2=new File(rootXMLPath+"/Account_and_password");
        if(!file2.exists())
        {
            file2.mkdir();
        }
        File file3 = new File(rootXMLPath+"/Account_and_password/Account.txt");
        file3.delete();
        try {
            file3.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file4 = new File(rootXMLPath+"/Account_and_password/Password.txt");
        file4.delete();
        try {
            file4.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //检查记住密码和自动登录的存储位置
    void Check_RA_Dir()
    {
        File file1=new File(rootXMLPath);
        if(!file1.exists())
        {
            file1.mkdir();
        }
        File file6 = new File(rootXMLPath+"/AL_Set.txt");
        file6.delete();
        try {
            file6.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file7 = new File(rootXMLPath+"/RP_Set.txt");
        file7.delete();
        try {
            file7.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //登录
    void Login()
    {
        if (et1.getText().toString().equals("")){
            tv3.setText("请输入账号！");
            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
        }
        else {
            if(!(et1.getText().toString().equals("123456"))){
                tv3.setText("对不起，此账号不存在！");
                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
            }
            else {
                if(et2.getText().toString().equals("")){
                    tv3.setText("请输入密码！");
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!(et2.getText().toString().equals("123456"))) {
                        tv3.setText("密码错误，请重新输入！");
                        Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Check_RA_Dir();
                        if (Remember_Password.isChecked())
                        {
                            Check_AP_Dir();
                            if(Automatic_Login.isChecked())
                            {
                                Toast.makeText(LoginActivity.this,"记住密码 自动登录 登录成功",Toast.LENGTH_SHORT).show();
                                writeToXML(rootXMLPath+"/AL_Set.txt","Automatic_Login");
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,"记住密码 登录成功",Toast.LENGTH_SHORT).show();
                                writeToXML(rootXMLPath+"/AL_Set.txt","Manual_Login");
                            }
                            writeToXML(rootXMLPath+"/Account_and_password/Account.txt",et1.getText().toString());
                            writeToXML(rootXMLPath+"/Account_and_password/Password.txt",et2.getText().toString());
                            writeToXML(rootXMLPath+"/RP_Set.txt","Remember_Password_On");
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            writeToXML(rootXMLPath+"/RP_Set.txt","Remember_Password_Off");
                        }
                        tv3.setText("密码正确，登录成功！");
                        if (readFromXML(getExternalFilesDir(null).toString()+"/Current_Camera_ID.txt").equals("4"))
                        {
                            Intent intent1 = new Intent(LoginActivity.this , Main2Activity.class);
                            startActivity(intent1);
                        }
                        else
                        {
                            Intent intent1 = new Intent(LoginActivity.this , MainActivity.class);
                            startActivity(intent1);
                        }
                    }
                }
            }
        }
    }

    //写入文件
    public static boolean writeToXML(String filepath, String content)
    {
        FileOutputStream fileOutputStream;
        BufferedWriter bufferedWriter;
        File file = new File(filepath);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //读取文件
    public static String readFromXML(String filePath)
    {
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(filePath);
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return stringBuilder.toString();
    }

    //L1监听按钮
    public class L1 implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            Login();
        }
    }

    //L2监听按钮
    public class L2 implements View.OnClickListener
    {
        @Override
        public void onClick(View view){
//            Toast.makeText(LoginActivity.this,"功能未开通！",Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(LoginActivity.this , Python_TestActivity.class);
            startActivity(intent1);
        }
    }
}

