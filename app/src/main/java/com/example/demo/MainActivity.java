package com.example.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.alibaba.alibclinkpartner.linkpartner.constants.ALPLinkKeyType;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcShopPage;
import com.alibaba.baichuan.trade.biz.AlibcTradeCallback;
import com.alibaba.baichuan.trade.biz.applink.adapter.AlibcFailModeType;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.alibaba.baichuan.trade.common.utils.AlibcLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    public HashMap<String,String> trackParams = new HashMap<>();
    public AlibcTaokeParams taokeParams = new AlibcTaokeParams("", "", "");
    public AlibcShowParams showParams = new AlibcShowParams();
    public OpenType openType = OpenType.Auto;
    public String clientType = ALPLinkKeyType.TAOBAO;
    public AlibcFailModeType nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpH5;
    public EditText trackParamEdit = null;
    public EditText taokeParamEdit = null;
    public boolean taokeEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //首先设置默认淘客参数
        taokeParams.adzoneid = "110257500101";
        taokeParams.subPid = "";
        taokeParams.pid = "mm_98836808_1507450087_110257500101";
        taokeParams.unionId = "";
        taokeParams.extraParams = new HashMap<>();
        taokeParams.extraParams.put("taokeAppkey", "23281718");
        taokeParams.extraParams.put("sellerId", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置淘客打点方式：同步或异步
        Switch switchTaokeSync = findViewById(R.id.taoke_sync);
        switchTaokeSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AlibcTradeSDK.setSyncForTaoke(isChecked);
            }
        });

        //是否使用全局淘客参数
        Switch switchTaokeParam = findViewById(R.id.global_taoke_param);
        switchTaokeParam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    taokeParams = new AlibcTaokeParams("mm_100713040_22792955_75330474", "", "");
                } else {
                    taokeParams = new AlibcTaokeParams("", "", "");
                }
                AlibcTradeSDK.setTaokeParams(taokeParams);
            }
        });

        //打开方式
        RadioGroup openTypeGroup = findViewById(R.id.open_type);
        openTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.open_auto:
                        openType = OpenType.Auto;
                        break;
                    case R.id.open_native:
                        openType = OpenType.Native;
                        break;
                }
            }
        });

        //唤端类型
        RadioGroup linkTypeGroup = findViewById(R.id.link_type);
        linkTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.link_taobao:
                        clientType = ALPLinkKeyType.TAOBAO;
                        break;
                    case R.id.link_tianmao:
                        clientType = ALPLinkKeyType.TMALL;
                        break;
                    default:
                }
            }
        });

        //设置打开失败模式，支持h5，download,browser,none
        RadioGroup failModeGroup = findViewById(R.id.fail_mode);
        failModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.mode_h5:
                        nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpH5;
                        break;
                    case R.id.mode_download:
                        nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpDOWNLOAD;
                        break;
                    case R.id.mode_browser:
                        nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpBROWER;
                        break;
                    case R.id.mode_none:
                        nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeNONE;
                        break;

                }
            }
        });

        trackParamEdit = findViewById(R.id.track_param);
        trackParams.put("testKey", "testValue");
        trackParamEdit.setText(trackParams.toString());

        //设置跟踪参数
        Button setTrackParam = findViewById(R.id.set_track_param);
        setTrackParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ParamEditActivity.class);
                intent.putExtra("trackParams",trackParamEdit.getText().toString());
                intent.putExtra("tag","trackParams");
                startActivityForResult(intent,1000);
            }
        });


        taokeParamEdit = findViewById(R.id.taoke_param);
        HashMap<String,String> taokeMap= new HashMap<String,String>();
        taokeMap.put("pid",taokeParams.pid);
        taokeMap.put("subPid",taokeParams.subPid);
        taokeMap.put("unionId",taokeParams.unionId);
        taokeMap.put("adzoneid",taokeParams.adzoneid);
        taokeMap.put("taokeAppkey", taokeParams.extraParams.get("taokeAppkey"));
        taokeMap.put("sellerId", taokeParams.extraParams.get("sellerId"));
        taokeParamEdit.setText(taokeMap.toString());

        //设置淘客参数
        Button setTaokeParam = findViewById(R.id.set_taoke_param);
        setTaokeParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ParamEditActivity.class);
                intent.putExtra("taokeParams",taokeParamEdit.getText().toString());
                intent.putExtra("tag","taokeParams");
                startActivityForResult(intent,1001);
            }
        });

        //是否使用淘客参数
        Switch switchTaoke= findViewById(R.id.enable_taoke_param);
        switchTaoke.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    taokeEnable = true;
                } else {
                    taokeEnable = false;
                    switchTaokeParam.setChecked(false);
                }
            }
        });

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlibcLogin alibcLogin = AlibcLogin.getInstance();
                alibcLogin.showLogin(new AlibcLoginCallback() {
                    @Override
                    public void onSuccess(int result, String userId, String nick) {
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button isLogin = findViewById(R.id.isLogin);
        isLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, AlibcLogin.getInstance().getSession().toString(), Toast.LENGTH_LONG).show();
            }
        });

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlibcLogin alibcLogin = AlibcLogin.getInstance();
                alibcLogin.logout(new AlibcLoginCallback() {
                    @Override
                    public void onSuccess(int i, String s, String s1) {
                        Toast.makeText(MainActivity.this, "登出成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(MainActivity.this, "登出成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        Button openByUrl = findViewById(R.id.open_by_url);
        openByUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlibcShowParams showParams = new AlibcShowParams();
//                showParams.setOpenType(OpenType.Auto);
//                showParams.setClientType("taobao");
//                showParams.setBackUrl("");
//                showParams.setNativeOpenFailedMode(AlibcFailModeType.AlibcNativeFailModeJumpH5);

//                AlibcTaokeParams taokeParams = new AlibcTaokeParams("", "", "");
//                taokeParams.setPid("mm_112883640_11584347_72287650277");
//
//                Map<String, String> trackParams = new HashMap<>();

                //初始化所需参数
                initParams();

                // 通过百川内部的webview打开页面
                AlibcTrade.openByUrl(MainActivity.this, "", "https://uland.taobao.com/coupon/edetail?e=oSSExmvWXYYGQASttHIRqdYQwfcs8zoyxKXGKLqne1Hsx8cAhaH1SiZlT35kVCJr5R4kLBbVNWVsYgQTrXiDpq0TeAL%2BmcF17w9v818T2zNzQzL%2FHTq%2BPBemP0hpIIPvjDppvlX%2Bob8NlNJBuapvQ2MDg9t1zp0RRkY43XGTK8ko1aiZVhb9ykMuxoRQ3C%2BH5vl92ZYH25Cie%2FpBy9wBFg%3D%3D&traceId=0b15099215669559409745730e&union_lens=lensId:0b0b9f56_0c4c_16cd5da2c7f_3b31&xId=PwB9ZSWQxCtEwHxtbQc8iynshj5KEW16KP6OV6MAlpGpKCKmVGQMnjwQNhiGQpRY1gFyQHtqnYiv5wxGKTyCdf&tj1=1&tj2=1&relationId=518419440&activityId=23f4487e169647bd98b0d7fb2645947a", null,
                        new WebViewClient(), new WebChromeClient(),
                        showParams, taokeParams, trackParams, new AlibcTradeCallback() {
                            @Override
                            public void onTradeSuccess(AlibcTradeResult tradeResult) {
                                AlibcLogger.i("MainActivity", "request success");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                AlibcLogger.e("MainActivity", "code=" + code + ", msg=" + msg);
                                if (code == -1) {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        Button openByCode = findViewById(R.id.open_by_code);
        openByCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlibcShowParams showParams = new AlibcShowParams();
//                showParams.setOpenType(OpenType.Native);
//                showParams.setClientType("taobao");
//                showParams.setBackUrl("");
//                showParams.setNativeOpenFailedMode(AlibcFailModeType.AlibcNativeFailModeJumpDOWNLOAD);

//                AlibcTaokeParams taokeParams = new AlibcTaokeParams("", "", "");
//                taokeParams.setPid("mm_112883640_11584347_72287650277");
//
//                Map<String, String> trackParams = new HashMap<>();

                //初始化所需参数
                initParams();

//                AlibcBasePage page = new AlibcDetailPage("597022118445");
//                AlibcTrade.openByBizCode(MainActivity.this, page, null, new WebViewClient(), new WebChromeClient(),
//                        "detail", showParams, taokeParams, trackParams, new AlibcTradeCallback() {
//                            @Override
//                            public void onTradeSuccess(AlibcTradeResult tradeResult) {
//                                // 交易成功回调（其他情形不回调）
//                                AlibcLogger.i("MainActivity", "open detail page success");
//                            }
//
//                            @Override
//                            public void onFailure(int code, String msg) {
//                                AlibcLogger.e("MainActivity", "code=" + code + ", msg=" + msg);
//                                if (code == -1) {
//                                    Toast.makeText(MainActivity.this, "唤端失败，失败模式为none", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });

                AlibcBasePage shopPage = new AlibcShopPage("65626181");

                AlibcTrade.openByBizCode(MainActivity.this, shopPage, null, new WebViewClient(), new WebChromeClient(),
                        "shop", showParams, taokeParams, trackParams, new AlibcTradeCallback() {
                            @Override
                            public void onTradeSuccess(AlibcTradeResult tradeResult) {
                                // 交易成功回调（其他情形不回调）
                                AlibcLogger.i("MainActivity", "open detail page success");
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                AlibcLogger.e("MainActivity", "code=" + code + ", msg=" + msg);
                                if (code == -1) {
                                    Toast.makeText(MainActivity.this, "唤端失败，失败模式为none", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        Button openWebview = findViewById(R.id.open_by_webview);
        openWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", "https://uland.taobao.com/coupon/edetail?e=oSSExmvWXYYGQASttHIRqdYQwfcs8zoyxKXGKLqne1Hsx8cAhaH1SiZlT35kVCJr5R4kLBbVNWVsYgQTrXiDpq0TeAL%2BmcF17w9v818T2zNzQzL%2FHTq%2BPBemP0hpIIPvjDppvlX%2Bob8NlNJBuapvQ2MDg9t1zp0RRkY43XGTK8ko1aiZVhb9ykMuxoRQ3C%2BH5vl92ZYH25Cie%2FpBy9wBFg%3D%3D&traceId=0b15099215669559409745730e&union_lens=lensId:0b0b9f56_0c4c_16cd5da2c7f_3b31&xId=PwB9ZSWQxCtEwHxtbQc8iynshj5KEW16KP6OV6MAlpGpKCKmVGQMnjwQNhiGQpRY1gFyQHtqnYiv5wxGKTyCdf&tj1=1&tj2=1&relationId=518419440&activityId=23f4487e169647bd98b0d7fb2645947a");
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        String params;
        if(data!=null){
            if (requestCode == 1000 && resultCode == 1002) {
                params = (String) data.getSerializableExtra("mData");
                trackParamEdit.setText(params);
            } else if (requestCode == 1001 && resultCode==1002) {
                params  = (String) data.getSerializableExtra("mData");
                taokeParamEdit.setText(params);
            }
        }
    }


    public void initParams() {
        showParams.setOpenType(openType);
        showParams.setClientType(clientType);
        showParams.setBackUrl("");
        showParams.setNativeOpenFailedMode(nativeOpenFailedMode);
        if(taokeEnable) {
            taokeParams = getTaokeMap(getList(taokeParamEdit.getText().toString().trim()));
        }else{
            taokeParams = null;
        }
        trackParams = getTrackMap(getList(trackParamEdit.getText().toString().trim()));
    }

    public static class Param{
        String key;
        String value;
        public Param(String key,String value){
            this.key = key;
            this.value = value;
        }
        public String getKey(){
            return key;
        }
        public  String getValue(){
            return value;
        }
    }

    public ArrayList<Param> getList(String text){
        if(TextUtils.isEmpty(text)){
            return null;
        }
        ArrayList<Param> list = new ArrayList<>();
        String subString = "";
        if(text.endsWith("}")&&text.startsWith("{")){
            subString = text.substring(text.indexOf("{")+1,text.lastIndexOf("}"));
            String [] array = subString.split(",");
            for (int i= 0;i<array.length;i++){
                if(array[i].split("=").length==0){
                    list.add(new Param("",""));
                }
                else if(array[i].startsWith("=")) {
                    list.add(new Param("", array[i].split("=")[1]));
                }else if(array[i].endsWith("=")){
                    list.add(new Param(array[i].split("=")[0], ""));
                }else{
                    list.add(new Param(array[i].split("=")[0], array[i].split("=")[1]));
                }
            }
            return list;
        }
        return  list;
    }

    public HashMap<String,String> getTrackMap(ArrayList<Param> list){
        if(list == null) {
            return null;
        }
        trackParams.clear();
        for(int i=0;i<list.size();i++){
            if(list!=null&&list.get(i)!=null){
                trackParams.put(list.get(i).key.trim(),list.get(i).value.trim());
            }
        }
        return trackParams;
    }

    public AlibcTaokeParams getTaokeMap(ArrayList<Param> list){
        if(list == null){
            return null;
        }
        if(taokeParams == null){
            taokeParams = new AlibcTaokeParams("", "", "");
        }
        for(int i=0;i<list.size();i++){
            String key = list.get(i).getKey().trim();
            String value = list.get(i).getValue().trim();
            if("pid".equals(key)){
                taokeParams.pid = value;
            }else if("unionId".equals(key)){
                taokeParams.unionId = value;
            }else if("subPid".equals(key)){
                taokeParams.subPid = value;
            }else if("adzoneid".equals(key)){
                taokeParams.adzoneid = value;
            }else if ("taokeAppkey".equals(key)) {
                taokeParams.extraParams.put("taokeAppkey", value);
            }else if ("sellerId".equals(key)) {
                taokeParams.extraParams.put("sellerId", value);
            }
        }
        return taokeParams;
    }

}
