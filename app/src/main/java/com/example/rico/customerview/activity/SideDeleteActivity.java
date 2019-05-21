package com.example.rico.customerview.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.DeleteAdapter;
import com.example.rico.customerview.view.ItemDecoration;

/**
 * Created by Tmp on 2019/5/16.
 */
public class SideDeleteActivity extends BaseActivity {
    RecyclerView rvDelete;
    DeleteAdapter adapter;
    TextView tv1,tv2;

    @Override
    public int bindLayout() {
        return R.layout.activity_side_delete;
    }

    @Override
    public void doBusiness() {
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);
        rvDelete=findViewById(R.id.rv_delete);
        rvDelete.setLayoutManager(new LinearLayoutManager(this));
        rvDelete.addItemDecoration(new ItemDecoration());
        adapter=new DeleteAdapter(this);
        for (int i = 0; i < 20; i++) {
            adapter.addItem("第一");
        }
        rvDelete.setAdapter(adapter);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SideDeleteActivity.this,"tv1点击",Toast.LENGTH_SHORT).show();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SideDeleteActivity.this,"tv2点击",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
