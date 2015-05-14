package cn.edu.ustc.appseed.clubseed.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.data.Club;
import cn.edu.ustc.appseed.clubseed.data.ClubPhp;
import cn.edu.ustc.appseed.clubseed.data.ViewActivityPhp;
import cn.edu.ustc.appseed.clubseed.fragment.TabItemFragment;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;

/**
 * Created by shenaolin on 15/4/14.
 */
public class ClubInfoActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView clubInfoPre;
    private TextView masterInfoPre;
    private TextView connInfoPre;
    private TextView connInfo;
    private TextView masterInfo;
    private TextView clubInfo;
    private ImageView imageView;
    private int mainColor;
    private Club club;
    private int ID;
    private boolean isSaved;
    private String title;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_info);
        ID = getIntent().getIntExtra(TabItemFragment.EXTRA_CLUB_ID, 0);
        if (AppUtils.colors.get(ID)!=null) {

            mainColor = AppUtils.colors.get(ID);
        }else {
            mainColor = getResources().getColor(R.color.app_main);
        }

        AppUtils.needRefresh=false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //mainColor = getIntent().getIntExtra(TabItemFragment.EXTRA_MAIN_COLOR,0);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT>=21) {
            window.setStatusBarColor(mainColor);
        }
        clubInfo = (TextView) findViewById(R.id.textViewClubInfo);
        masterInfo = (TextView) findViewById(R.id.textViewMasterInfo);
        connInfo = (TextView) findViewById(R.id.textViewConnInfo);

        imageView = (ImageView)findViewById(R.id.clubLogo);
        clubInfoPre = (TextView)findViewById(R.id.clubInfo);
        masterInfoPre = (TextView)findViewById(R.id.masterInfo);
        connInfoPre = (TextView)findViewById(R.id.connectInfo);
        title = getIntent().getStringExtra(TabItemFragment.EXTRA_TITLE);

        clubInfoPre.setTextColor(mainColor);
        masterInfoPre.setTextColor(mainColor);
        connInfoPre.setTextColor(mainColor);
        if (toolbar != null) {
            toolbar.setBackgroundColor(mainColor);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onBackPressed();
                    if (AppUtils.needRefresh) {
                        Intent intent = new Intent(ClubInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        onBackPressed();
                    }
                }
            });
        }
        setTitle(title);





        if (ID == 0){
            Toast.makeText(this, "未知的网络错误！", Toast.LENGTH_SHORT).show();
        }else {
            isSaved = false;
            for (int i = 1;i<=AppUtils.sSharedPreferences.getInt("FOCUSCLUBNUM",0);i++){

                if (AppUtils.sSharedPreferences.getInt("CLUBID"+i,0)==ID){
                    isSaved=true;
                    break;
                }
            }

        }
        new ContentAsyncTask().execute(ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_club_info, menu);
        if (isSaved){
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_star_black));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==R.id.focusOn){
            upDateSaved();
            AppUtils.needRefresh=true;
            if (isSaved){
                for (int i=0;i<AppUtils.savedClubIds.size();i++){
                    if (AppUtils.savedClubIds.get(i)==ID){
                        AppUtils.savedClubIds.remove(i);
                        AppUtils.savedClubNames.remove(i);
                        break;
                    }
                }
                isSaved = false;
                item.setIcon(getResources().getDrawable(R.drawable.ic_star_outline_black));

            }else {
                AppUtils.savedClubNames.add(title);
                AppUtils.savedClubIds.add(ID);
                isSaved = true;
                item.setIcon(getResources().getDrawable(R.drawable.ic_star_black));

            }

            upDatePreference();


        }
        return false;
    }

    public void upDateSaved(){
        AppUtils.savedClubNames.clear();
        AppUtils.savedClubIds.clear();
        for (int i=1;i<=AppUtils.sSharedPreferences.getInt("FOCUSCLUBNUM",0);i++){
            AppUtils.savedClubNames.add(AppUtils.sSharedPreferences.getString("CLUBNAME"+i,""));
            AppUtils.savedClubIds.add(AppUtils.sSharedPreferences.getInt("CLUBID"+i,0));
        }
    }
    public void upDatePreference(){
        SharedPreferences.Editor editor = AppUtils.sSharedPreferences.edit();
        int clubNum = AppUtils.sSharedPreferences.getInt("FOCUSCLUBNUM",0);
        for (int i=1;i<=clubNum;i++){
            editor.remove("CLUBID"+i);
            editor.remove("CLUBNAME"+i);


        }
        for (int i=1;i<=AppUtils.savedClubIds.size();i++){
            editor.putInt("CLUBID"+i,AppUtils.savedClubIds.get(i-1));
            editor.putString("CLUBNAME"+i,AppUtils.savedClubNames.get(i-1));
        }
        editor.putInt("FOCUSCLUBNUM",AppUtils.savedClubIds.size());
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK&&AppUtils.needRefresh){
            Intent intent = new Intent(ClubInfoActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ContentAsyncTask extends AsyncTask<Integer, Void, Club> {

        Bitmap bitmap;
        @Override
        protected Club doInBackground(Integer... params) {
            String url = "http://clubseed.sinaapp.com/api/club.php?format=json2&clubid=" + params[0];
            ClubPhp clubPhp;
            Club club = null;
            try {

                String jsonString = AppUtils.getJSONString(url);Log.d("Z",jsonString);
                clubPhp =(ClubPhp)
                        JSON.parseObject(
                                jsonString,
                                ClubPhp.class);
                club=clubPhp.getData().getFirst();


            } catch (Exception e) {
                Log.d("HR", e.toString());
            }
            if (club!=null) {
                try {
                    bitmap = AppUtils.getBitmapFromURL(club.getImage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return club;
        }

        @Override
        protected void onPostExecute(Club club) {
            String text;
            Bitmap clubLogo=AppUtils.logos.get(ID);
            if (club==null){
                text="找不到内容";
            }else {
                text = club.getInformation();
            }
            if (clubLogo!=null) {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                masterInfo.setText(club.getManagerName());
                connInfo.setText(club.getEmail());
            }
            //Bitmap bitmap = event.getBitmap();
            //Log.d("HR", "Content:" + event.getClubname());
            if (text == null) {
                Toast.makeText(ClubInfoActivity.this, "未知的网络错误", Toast.LENGTH_SHORT).show();

            } else {
                clubInfo.setText(text);


            }

        }
    }



}
