package cn.edu.ustc.appseed.clubseed.activity;
/*
* the first activity when ClubSeed starts,
* loading the global variables and initializing the application's configuration.
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.data.Club;
import cn.edu.ustc.appseed.clubseed.data.ListPhp;
import cn.edu.ustc.appseed.clubseed.data.Event;
import cn.edu.ustc.appseed.clubseed.data.ViewActivityPhp;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;
import cn.edu.ustc.appseed.clubseed.utils.Debug;

public class StartActivity extends Activity {


    private final int SPLASH_DISPLAY_LENGHT = 1000; //启动界面显示两秒
    private boolean needUpdate=false;
    public String localVersion;
    private String newVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);




        AppUtils.sAppContext = getApplicationContext();
        AppUtils.sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppUtils.sAppContext);
        AppUtils.isReadingMode = AppUtils.sSharedPreferences.getBoolean(getString(R.string.pref_reading_mode), true);
        AppUtils.sNullListPhp = setNullListPhp();
        AppUtils.savedEvents = new HashMap<>();
        AppUtils.savedClubIds = new ArrayList<>();
        AppUtils.savedClubNames = new ArrayList<>();
        AppUtils.colors.put(0,getResources().getColor(R.color.app_main));
        try {
            AppUtils.packageInfo=getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String phoneInfo = "Product: " + android.os.Build.PRODUCT;
        phoneInfo += ", VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE;
        phoneInfo += ", MODEL: " + android.os.Build.MODEL;
        phoneInfo += ", SDK: " + android.os.Build.VERSION.SDK;
        phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
        phoneInfo += ", DEVICE: " + android.os.Build.DEVICE;
        phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY;
        phoneInfo += ", BRAND: " + android.os.Build.BRAND;
        phoneInfo += ", BOARD: " + android.os.Build.BOARD;
        phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT;
        phoneInfo += ", ID: " + android.os.Build.ID;
        phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER;
        phoneInfo += ", USER: " + android.os.Build.USER;
        AppUtils.phoneInfo = phoneInfo;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                File[] files = AppUtils.sAppContext.getFilesDir().listFiles(new FileFilter() {
//                    @Override
//                    public boolean accept(File pathname) {
//                        return pathname.getName().contains(".event");
//                    }
//                });
//                for (File file : files) {
//                    Log.d("HR", "Name:" + file.getName());
//                    try {
//                        String jsonString = AppUtils.loadString(file.getName());
//                        Event event = (Event) JSON.parseObject(jsonString, Event.class);Debug.showLog(event.getSummary());
//
//
//                        File bitmapFile = new File(AppUtils.sAppContext.getFilesDir()+"/"+event.getID() + ".png");
//                        if (bitmapFile.exists()) {
//                            Debug.showLog(bitmapFile.getName());
//                            Bitmap bitmap = AppUtils.loadBitmap(bitmapFile.getName());
//                            event.setBitmap(bitmap);
//                        }
//                        AppUtils.savedEvents.put(Integer.parseInt(event.getID()), event);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//            }
//        }).run();
        new CheckUpdateAsyncTask().execute();
        initClubInfo();
//        if (!needUpdate) {
//
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
//                    StartActivity.this.startActivity(mainIntent);
//                    StartActivity.this.finish();
//                }
//            }, SPLASH_DISPLAY_LENGHT);
//        }
    }

    private ListPhp setNullListPhp() {
        Event data = new Event();
        data.setClubid("0");
        data.setClubname("NULL");
        data.setID("0");
        data.setActivitytime("299311090000");
        data.setCreatetime("199311090000");
        data.setPlace("Nowhere");
        data.setRecommend("F");
        data.setTag("NULL");
        data.setTitle("NULL");
        data.setSummary("Ha");
        LinkedList<Event> linkedList = new LinkedList<>();
        linkedList.add(data);
        return new ListPhp(0, "", linkedList);
    }
    private class CheckUpdateAsyncTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("http://clubseed.sinaapp.com/api/version.php").build();
                newVersion = client.newCall(request).execute().body().string();

                localVersion = AppUtils.packageInfo.versionName;
                String string = newVersion+"";
                if (newVersion==null){
                    return false;
                }
//                for (int i = 0; i < localVersion.length(); i+=2) {
//                    if (localVersion.charAt(i) < newVersion.charAt(i)) {
//                        needUpdate=true;
//                        return true;
//                    }
//                }

                else if (newVersion.compareTo(localVersion)>0){
                    needUpdate=true;
                    return true;
                }

                return false;

            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                new AlertDialog.Builder(StartActivity.this).setTitle("ClubSeed有更新！").setMessage(newVersion+"版本已更新,您要现在下载吗？").setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_uri_browsers = Uri.parse("http://clubseed.sinaapp.com/download/clubseed.apk");
                        intent.setData(content_uri_browsers);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton(R.string.alert_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                        finish();
                    }
                }).show();
            }else {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                        StartActivity.this.startActivity(mainIntent);
                        StartActivity.this.finish();
                    }
                }, SPLASH_DISPLAY_LENGHT);
            }
        }
    }



    private void initClubInfo(){
        Boolean isFirst = AppUtils.sSharedPreferences.getBoolean("isFirst",true);
        SharedPreferences.Editor editor = AppUtils.sSharedPreferences.edit();
        if (isFirst) {
            editor.putInt("CLUBID1", 1);
            AppUtils.savedClubIds.add(1);
            editor.putString("CLUBNAME1", "AppSeed");
            AppUtils.savedClubNames.add("AppSeed");
            editor.putInt("CLUBID2", 2);
            AppUtils.savedClubIds.add(2);
            editor.putString("CLUBNAME2", "化院学生会");
            AppUtils.savedClubNames.add("化院学生会");
            editor.putInt("FOCUSCLUBNUM", 2);
            editor.putBoolean("isFirst",false);
            editor.commit();

        }

    }
}
