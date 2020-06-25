package com.example.smart_detect;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_discovery extends Fragment {
    GridView gridView;
    SimpleAdapter simpleAdapter;
    String[] titles = new String[]{"一键修复","关于我们","联系我们","一键刷新",
            "搜索查询","疑问咨询","发送信息","常用工具","一键修改","一键返回"};
    int[] images = new int[]{R.drawable.t1,R.drawable.t2,R.drawable.t3,R.drawable.tb4,
            R.drawable.tb5,R.drawable.t6,R.drawable.t7,R.drawable.t8,R.drawable.t9,R.drawable.t10};
    ListView listView;
    SimpleAdapter adapter;
    String[] titles2=new String[]{"南开大学建校100周年", "南开大学建校100周年", "南开大学建校100周年", "南开大学建校100周年", "南开大学建校100周年", "南开大学建校100周年", "南开大学建校100周年", "南开大学建校100周年"};
    String[] contents2=new String[]{"热烈庆祝南开大学建校100周年！","热烈庆祝南开大学建校100周年！","热烈庆祝南开大学建校100周年！","热烈庆祝南开大学建校100周年！","热烈庆祝南开大学建校100周年！","热烈庆祝南开大学建校100周年！","热烈庆祝南开大学建校100周年！","热烈庆祝南开大学建校100周年！"};
    int[] images2 = new int[]{R.drawable.l1, R.drawable.l2, R.drawable.l3, R.drawable.l4, R.drawable.l5, R.drawable.l6, R.drawable.l7, R.drawable.l8};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery,container,false);
        gridView=view.findViewById(R.id.GridView);
        listView=view.findViewById(R.id.ListView);
        List<Map<String,Object>> list =new ArrayList<>();
        for(int i = 0;i<titles.length;i++)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("name",titles[i]);
            map.put("image", images[i]);
            list.add(map);
        }
        simpleAdapter=new SimpleAdapter(getActivity(),list,R.layout.gridview2,new String[]{"name","image"},new int[]{R.id.textView9,R.id.imageView14});
        gridView.setAdapter(simpleAdapter);
        List<Map<String, Object>> list2 = new ArrayList<>();//String为编号，Object为内容
        for(int i=0;i < titles2.length;i++)
        {
            Map<String, Object> map2 = new HashMap<>();
            map2.put("titles",titles2[i]);
            map2.put("contents",contents2[i]);
            map2.put("images", images2[i]);
            list2.add(map2);
        }
        adapter=new SimpleAdapter(getActivity(),list2, R.layout.adpater2,new String[]{"titles","contents","images"},
                new int[]{R.id.textView8,R.id.textView6,R.id.imageView13});
        listView.setAdapter(adapter);
        return view;
    }
}
