package com.example.rico.customerview.activity;

import android.content.res.AssetManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.ListLinkageAdapter;
import com.example.rico.customerview.bean.ProvinceBean;
import com.example.rico.customerview.bean.WheelChildData;
import com.example.rico.customerview.bean.WheelData;
import com.example.rico.customerview.layoutManager.WheelLayoutManager;
import com.example.rico.customerview.view.WheelLayoutView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tmp on 2019/8/16.
 */
public class ListLinkageActivity extends BaseActivity {
    RecyclerView rv;
    ListLinkageAdapter adapter;
    LinearSnapHelper helper;
    WheelLayoutManager manager;
    WheelLayoutView wheelLL;

    @Override
    public int bindLayout() {
        return R.layout.activity_list_linkage;
    }


    @Override
    public void doBusiness() {
        adapter = new ListLinkageAdapter(this);
        rv = findViewById(R.id.rv);
        manager = new WheelLayoutManager(this);
        rv.setLayoutManager(manager);
//        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rv);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            strings.add(" " + i + " 哈哈哈哈哈哈哈哈哈哈哈哈");
        }
        adapter.addData(strings);
        addWheel();
    }


    private void addWheel() {
        wheelLL = findViewById(R.id.wheel_ll);
        String str = getJSON();
        Type type = new TypeToken<List<ProvinceBean>>() {
        }.getType();
        List<ProvinceBean> provinceBeanList = new Gson().fromJson(str, type);

        ArrayList<WheelData> wheelDataList = new ArrayList<>();
        for (int i = 0; i < provinceBeanList.size(); i++) {
            WheelData wheelData = new WheelData();
            wheelData.setData(provinceBeanList.get(i).getName());
            ArrayList<WheelChildData> childList = new ArrayList<>();

            for (int k = 0; k < provinceBeanList.get(i).getCity().size(); k++) {
                WheelChildData data = new WheelChildData();
                data.setData(provinceBeanList.get(i).getCity().get(k).getName());
                ArrayList<String> strings =
                        new ArrayList<>(provinceBeanList.get(i).getCity().get(k).getArea());
                data.setStrings(strings);
                childList.add(data);
            }
            wheelData.setChildList(childList);
            wheelDataList.add(wheelData);
        }
        wheelLL.setData(wheelDataList);
        wheelLL.setListener((parent, middle, child, parentPos, middlePos, childPos) -> Log.e("position", "selected: " + parent + middle + child));
    }

    public String getJSON() {
        AssetManager am = getAssets();
        try {
            // 从assets文件夹里获取fileName数据流解析
            InputStream inputStream = am.open("json");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
