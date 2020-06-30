package com.example.smart_detect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_mine extends Fragment {
    ImageView my_avatar;
    ListView listView1,listView2;
    SimpleAdapter adapter1,adapter2;
    int[] images1 = new int[]{R.drawable.t1,R.drawable.t2,R.drawable.t3};
    String[] titles1 = new String[]{"我的日记","我的活动","我的收藏"};
    int[] images2 = new int[]{R.drawable.t1,R.drawable.t2,R.drawable.t3,R.drawable.t4,R.drawable.t5,R.drawable.t6,R.drawable.t7};
    String[] titles2 = new String[]{"消息中心","帮助与反馈","用户使用协议","个人隐私","设置中心","账号管理","关于我们"};
    int[] enter = new int[] {R.drawable.chevron_right};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine,container,false);
        intView(view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tb1);
        bitmap = BitmapUtil.getRoundedCornerBitmap(bitmap,250);
        my_avatar.setImageBitmap(bitmap);
        List<Map<String, Object>> list1 = new ArrayList<>();
        for(int i=0;i < titles1.length;i++)
        {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("images1",images1[i]);
            map1.put("titles1",titles1[i]);
            map1.put("enter", enter[0]);
            list1.add(map1);
        }
        adapter1=new SimpleAdapter(getActivity(),list1, R.layout.adapter,new String[]{"images1","titles1","enter"},
                new int[]{R.id.imageView17,R.id.textView14,R.id.imageView18});
        listView1.setAdapter(adapter1);
        List<Map<String, Object>> list2 = new ArrayList<>();
        for(int i=0;i < titles2.length;i++)
        {
            Map<String, Object> map2 = new HashMap<>();
            map2.put("images2",images2[i]);
            map2.put("titles2",titles2[i]);
            map2.put("enter", enter[0]);
            list2.add(map2);
        }
        adapter2=new SimpleAdapter(getActivity(),list2, R.layout.adapter,new String[]{"images2","titles2","enter"},
                new int[]{R.id.imageView17,R.id.textView14,R.id.imageView18});
        listView2.setAdapter(adapter2);
        return view;
    }

    private void intView(View view)
    {
        my_avatar = view.findViewById(R.id.imageView9);
        listView1 = view.findViewById(R.id.ListView1);
        listView2 = view.findViewById(R.id.ListView2);
    }
}
