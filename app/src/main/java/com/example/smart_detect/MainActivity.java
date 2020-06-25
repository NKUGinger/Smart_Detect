package com.example.smart_detect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MemoryFile;
import android.os.Message;
import android.os.MessageQueue;
import android.telecom.StatusHints;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.MemoryHandler;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    List<Fragment> fragments;
    MenuItem menuItem;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final String TAG = "MainActivity";

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    boolean flag = false;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == 0x01)
            {
                fill_waterdrop_screen();
                if(bottomNavigationView.getVisibility()==View.GONE)
                {
                    hideBottomUIMenu();
                }
                else
                {
                    displayBottomUIMenu();
                }
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
    public String status_color = "#2A99F1";
    public int wait = 0;
    public int wait_time = 100;
    boolean display_toolbar = true;
    boolean display_toolbar_const = true;
    TextView title;
    public LinearLayout linearLayout;
    public ImageView picture,back,camera_select_switch,take_picture;
    private AutoFitTextureView textureView;
    // 摄像头ID（通常0代表后置摄像头，1代表前置摄像头）
    private String mCameraId = "1";
    // 定义代表摄像头的成员变量
    private CameraDevice cameraDevice;
    // 预览尺寸
    private Size previewSize;
    private CaptureRequest.Builder previewRequestBuilder;
    // 定义用于预览照片的捕获请求
    private CaptureRequest previewRequest;
    // 定义CameraCaptureSession成员变量
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture
                , int width, int height) {
            // 当TextureView可用时，打开摄像头
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture
                , int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        // 摄像头被打开时激发该方法
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            MainActivity.this.cameraDevice = cameraDevice;
            // 开始预览
            createCameraPreviewSession(); // ②
        }

        // 摄像头断开连接时激发该方法
        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            MainActivity.this.cameraDevice = null;
        }

        // 打开摄像头出现错误时激发该方法
        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            MainActivity.this.cameraDevice = null;
            MainActivity.this.finish();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setStatusColor(this,Color.parseColor("#000000"));
        setContentView(R.layout.activity_main);

        fragments=new ArrayList<>();
        fragments.add(new fragment_homepage());
        fragments.add(new fragment_discovery());
        fragments.add(new fragment_community());
        fragments.add(new fragment_mine());
        myAdapter adapter = new myAdapter(getSupportFragmentManager(),fragments);
        intView();
        viewPager.setAdapter(adapter);
        Display_Picture(1);

        File file1=new File(getExternalFilesDir(null).toString()+"/Current_Camera_ID.txt");
        file1.delete();
        try {
            file1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeToXML(file1.toString(),"1");

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.homepage:
                        viewPager.setCurrentItem(0);
                        StatusBarUtils.setStatusColor(MainActivity.this,Color.parseColor("#000000"));
                        break;
                    case R.id.discovery:
                        viewPager.setCurrentItem(1);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        bottomNavigationView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        StatusBarUtils.setStatusColor(MainActivity.this,Color.parseColor(status_color));
                        break;
                    case R.id.community:
                        viewPager.setCurrentItem(2);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        bottomNavigationView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        StatusBarUtils.setStatusColor(MainActivity.this,Color.parseColor(status_color));
                        break;
                    case R.id.mine:
                        viewPager.setCurrentItem(3);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        bottomNavigationView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        StatusBarUtils.setStatusColor(MainActivity.this,Color.parseColor(status_color));
                        break;
                }
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(menuItem == null)
                {
                    menuItem=bottomNavigationView.getMenu().getItem(0);
                }
                menuItem.setChecked(false);
                menuItem=bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
                if(position!=0)
                {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomNavigationView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    StatusBarUtils.setStatusColor(MainActivity.this, Color.parseColor(status_color));
                }
                else
                {
                    StatusBarUtils.setStatusColor(MainActivity.this,Color.parseColor("#000000"));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Picture_ViewActivity.class);
                startActivity(intent);
            }
        });
        camera_select_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDevice.close();
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"已切换至后置摄像头！",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureStillPicture();
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
    }

    private class myAdapter extends FragmentPagerAdapter
    {
        private List<Fragment>fragments;
        public myAdapter(@NonNull FragmentManager fm,List<Fragment>fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag=false;
    }

    public void intView()
    {
        viewPager = findViewById(R.id.viewPager1);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textureView = (AutoFitTextureView) findViewById(R.id.autoFitTextureView);
        //为该组件设置监听器
        textureView.setSurfaceTextureListener(mSurfaceTextureListener);
        take_picture=findViewById(R.id.imageView4);
        linearLayout=findViewById(R.id.linearLayout2);
        camera_select_switch=findViewById(R.id.imageView5);
        picture=findViewById(R.id.imageView);
        title=findViewById(R.id.textView);
        back=findViewById(R.id.imageView3);

    }

    public void Display_Picture(int num)
    {
        File file = new File(getExternalFilesDir(null), "picture"+num+".jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(file+"");
        picture.setImageBitmap(bitmap);
    }

    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);// 围绕原地进行旋转
        matrix.postScale(-1, 1);   //镜像水平翻转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    private void captureStillPicture()
    {
        try {
            if (cameraDevice == null)
            {
                return;
            }
            // 创建作为拍照的CaptureRequest.Builder
            final CaptureRequest.Builder captureRequestBuilder =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(imageReader.getSurface());
            // 设置自动对焦模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 设置自动曝光模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取设备方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION
                    , ORIENTATIONS.get(rotation));
            // 停止连续取景
            captureSession.stopRepeating();
            // 捕获静态图像
            captureSession.capture(captureRequestBuilder.build()
                    , new CameraCaptureSession.CaptureCallback() // ⑤
                    {
                        // 拍照完成时激发该方法
                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session
                                , CaptureRequest request, TotalCaptureResult result) {
                            try {
                                // 重设自动对焦模式
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                                        CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                                // 设置自动曝光模式
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                // 打开连续取景模式
                                captureSession.setRepeatingRequest(previewRequest, null,
                                        null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // 打开摄像头
    private void openCamera(int width, int height)
    {
        setUpCameraOutputs(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // 打开摄像头
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //           int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            assert manager != null;
            manager.openCamera(mCameraId, stateCallback, null); // ①
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession()
    {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);
            // 创建作为预览的CaptureRequest.Builder
            previewRequestBuilder = cameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将textureView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(new Surface(texture));
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            cameraDevice.createCaptureSession(Arrays.asList(surface
                    , imageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
                    {
                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            // 如果摄像头为null，直接结束方法
                            if (null == cameraDevice) {
                                return;
                            }

                            // 当摄像头已经准备好时，开始显示预览
                            captureSession = cameraCaptureSession;
                            try {
                                // 设置自动对焦模式
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // 设置自动曝光模式
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                // 开始显示相机预览
                                previewRequest = previewRequestBuilder.build();
                                // 设置预览时连续捕获图像数据
                                captureSession.setRepeatingRequest(previewRequest,
                                        null, null); // ④
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(MainActivity.this, "配置失败！"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCameraOutputs(int width, int height)
    {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // 获取指定摄像头的特性
            CameraCharacteristics characteristics
                    = manager.getCameraCharacteristics(mCameraId);
            // 获取摄像头支持的配置属性
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // 获取摄像头支持的最大尺寸
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            // 创建一个ImageReader对象，用于获取摄像头的图像数据
            imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, 2);
            imageReader.setOnImageAvailableListener(
                    new ImageReader.OnImageAvailableListener() {
                        // 当照片数据可用时激发该方法
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            // 获取捕获的照片数据

                            Image image = reader.acquireNextImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            // 使用IO流将照片写入指定文件
                            File file = new File(getExternalFilesDir(null), "picture1.jpg");
                            buffer.get(bytes);
                            Bitmap bitmap = Bytes2Bimap(bytes);
                            bitmap = rotateBitmap(bitmap,-90);
                            picture.setImageBitmap(bitmap);
                            bytes = Bitmap2Bytes(bitmap);
                            try (FileOutputStream output = new FileOutputStream(file))
                            {
                                output.write(bytes);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                image.close();
                            }
                        }
                    }, null);

            // 获取最佳的预览尺寸
            previewSize = chooseOptimalSize(map.getOutputSizes(
                    SurfaceTexture.class), width, height, largest);
            // 根据选中的预览尺寸来调整预览组件（TextureView的）的长宽比
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                textureView.setAspectRatio(
                        previewSize.getWidth(), previewSize.getHeight());
            } else {
                textureView.setAspectRatio(
                        previewSize.getHeight(), previewSize.getWidth());
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d(TAG, "出现错误");
        }
    }

    private static Size chooseOptimalSize(Size[] choices
            , int width, int height, Size aspectRatio)
    {
        // 收集摄像头支持的打过预览Surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        // 如果找到多个预览尺寸，获取其中面积最小的。
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            System.out.println("找不到合适的预览尺寸！！！");
            return choices[0];
        }
    }

    // 为Size定义一个比较器Comparator
    static class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(Size lhs, Size rhs) {
            // 强转为long保证不会发生溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
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

    public Bitmap Bytes2Bimap(byte[] b)
    {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    private byte[] Bitmap2Bytes(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void display_toolbar()
    {
        title.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        camera_select_switch.setVisibility(View.VISIBLE);
        picture.setVisibility(View.VISIBLE);
        take_picture.setVisibility(View.VISIBLE);
    }

    private void hide_toolbar()
    {
        title.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        camera_select_switch.setVisibility(View.GONE);
        picture.setVisibility(View.GONE);
        take_picture.setVisibility(View.GONE);
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
            //int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    //隐藏导航栏
    private void displayBottomUIMenu()
    {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
