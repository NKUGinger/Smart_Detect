package com.example.smart_detect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_community extends Fragment {
    ListView listView1;
    SimpleAdapter adapter1;
    int[] images1 = new int[]{R.drawable.ytsq1,R.drawable.ytsq2,R.drawable.ytsq3,R.drawable.ytsq4,R.drawable.ytsq1,R.drawable.ytsq2,R.drawable.ytsq3,R.drawable.ytsq4,R.drawable.ytsq1,R.drawable.ytsq2,R.drawable.ytsq3,R.drawable.ytsq4};
    String[] titles1 = new String[]{"文科","理科","工科","医科","文科","理科","工科","医科","文科","理科","工科","医科"};
    String[] follows1 = new String[]{"关注3.9万","关注3.6万","关注2.1万","关注2.1万","关注1.8万","关注1.7万","关注1.4万","关注1.3万","关注1.3万","关注1.2万","关注1.1万","关注9852"};
    String[] topics1 = new String[]{"话题2149","话题1888","话题1235","话题1230","话题1029","话题1000","话题752","话题742","话题653","话题712","话题501","话题443"};
    String[] degrees1 = new String[]{"320°","310°","250°","240°","230°","220°","240°","200°","170°","150°","160°","140°"};
    String[] tags1 = new String[]{"热门社区","最新社区","推荐社区","热门社区","最新社区","推荐社区","热门社区","最新社区","推荐社区","热门社区","最新社区","推荐社区"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community,container,false);
        intView(view);
        List<Map<String, Object>> list1 = new ArrayList<>();
        for(int i=0;i < titles1.length;i++)
        {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("images1",images1[i]);
            map1.put("titles1",titles1[i]);
            map1.put("follows1",follows1[i]);
            map1.put("topics1",topics1[i]);
            map1.put("degrees1",degrees1[i]);
            map1.put("tags1",tags1[i]);
            list1.add(map1);
        }
        adapter1=new SimpleAdapter(getActivity(),list1, R.layout.listview,new String[]{"images1","titles1","follows1","topics1","degrees1","tags1"},
                new int[]{R.id.imageView32,R.id.textView34,R.id.textView37,R.id.textView36,R.id.textView38,R.id.textView39});
        listView1.setAdapter(adapter1);
        return view;
    }

    private void intView(View view)
    {
        listView1 = view.findViewById(R.id.ListView);
    }
}
