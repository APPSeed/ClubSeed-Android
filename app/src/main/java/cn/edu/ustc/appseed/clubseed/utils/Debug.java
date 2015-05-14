package cn.edu.ustc.appseed.clubseed.utils;

import android.util.Log;

/**
 * Created by Hengruo on 2015/3/7.
 */
public class Debug {
    public static void isNull(Object object){
        if(object==null) Log.d("HR", "NULL:");
        else Log.d("HR","Not NULL:"+object.getClass());
    }
    public static void showLog(String string){
        Log.d("HR",string);
    }
}
