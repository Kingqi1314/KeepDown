package com.test.kingqi.keepdown;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        LitePalApplication.initialize(context);
        if (isFirst()){
            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(MyApplication.context).edit();
            editor.putBoolean("isFirst",false);
            //初始化数据库：
            Connector.getDatabase();
        }
    }
    private boolean isFirst(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MyApplication.context);
        return preferences.getBoolean("isFirst",true);
    }
    public static Context getContext(){
        return context;
    }
}
