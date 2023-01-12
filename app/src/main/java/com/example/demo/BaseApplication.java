package com.example.demo;

import android.app.Application;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(BaseApplication.this, "初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(BaseApplication.this, "初始化失败："+msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
