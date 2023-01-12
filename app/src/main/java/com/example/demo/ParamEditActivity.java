package com.example.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ParamEditActivity extends Activity{
    public boolean changeStatus = false;
    public String tag = "";
    public String mData = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置标题
        setContentView(R.layout.edit);
        Button back = (Button)findViewById(R.id.back);
        final Button edit = (Button)findViewById(R.id.edit);
        final ListView listView = (ListView)findViewById(R.id.params);
        //获取淘客参数和自定义参数
        Intent intent = getIntent();
        if(intent!=null){
            tag = intent.getStringExtra("tag");
            Bundle bundle = intent.getExtras();
            if(tag.equals("trackParams")){
                mData = (String)intent.getStringExtra("trackParams");
            }else if(tag.equals("taokeParams")){
                mData = (String)intent.getStringExtra("taokeParams");
            }
            final ParamAdapter paramAdapter = new ParamAdapter(this,mData,changeStatus);
            listView.setAdapter(paramAdapter);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(changeStatus){
                        changeStatus = false;
                        paramAdapter.changeStatus = false;
                        edit.setText("Edit");
                        paramAdapter.notifyDataSetChanged();
                        mData = getText(paramAdapter.list);
                        listView.requestLayout();
                    }else {
                        changeStatus = true;
                        paramAdapter.changeStatus = true;
                        edit.setText("完成");
                        paramAdapter.notifyDataSetChanged();
                        listView.requestLayout();
                    }
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(v.getContext(),MainActivity.class);
                    intent.putExtra("tag",tag);
                    intent.putExtra("mData",mData);
                    setResult(1002, intent);
                    ParamEditActivity.super.onBackPressed();
                }
            });
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        intent.putExtra("tag",tag);
        intent.putExtra("mData",mData);
        this.setResult(1002, intent);
        super.onBackPressed();
        finish();
    }
    public String getText(ArrayList<MainActivity.Param> object){
        StringBuilder text = new StringBuilder();
        text.append("{");
        for(int i = 0;i<object.size();i++){
            text.append(object.get(i).key+"="+object.get(i).value);
            if(i<object.size()-1){
                text.append(",");
            }
        }
        text.append("}");
        return text.toString();
    }
}
