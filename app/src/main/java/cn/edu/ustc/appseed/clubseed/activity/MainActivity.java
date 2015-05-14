package cn.edu.ustc.appseed.clubseed.activity;



import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.adapter.DrawerListViewAdapter;
import cn.edu.ustc.appseed.clubseed.cache.ImageLoader;

public class MainActivity extends ActionBarActivity {


    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int mainColor;
    private ListView drawerList;
    private DrawerListViewAdapter navigationDrawerAdapter;
    private String[] drawerListData = {"活动信息","我的收藏", "设置"};
    private int[] drawerListImageData={R.drawable.activities,
            R.drawable.saved,
            R.drawable.setting};
    private int[] drawerListColor = {R.color.activities_red,
            R.color.saved_yellow,
            R.color.setting_green
    };
    private Toolbar toolbar;
    private boolean isFirstOpen = true;
    private Fragment[] fragments = new Fragment[3];
    private int debug = 0;
    public ImageLoader imageLoader=new ImageLoader(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("S---->", "main_init");
        mainColor=getResources().getColor(R.color.app_main);
        drawerList = (ListView) findViewById(R.id.drawerList);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter = new DrawerListViewAdapter(this, drawerListData,drawerListImageData);
        drawerList.setAdapter(navigationDrawerAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFragment(position);
                TextView item = (TextView)view.findViewById(R.id.drawer_listitem_textview);
                item.setTextColor(getResources().getColor(drawerListColor[position]));
                for (int i=0;i<drawerList.getChildCount();i++){
                    if (i!=position){
                        TextView otherItem = (TextView)drawerList.getChildAt(i).findViewById(R.id.drawer_listitem_textview);
                        otherItem.setTextColor(getResources().getColor(R.color.content_text));
                    }
                }
            }

        });
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT>=21) {
            window.setStatusBarColor(getResources().getColor(R.color.dark_blue));
        }

        if (toolbar != null) {
            toolbar.setTitle("ClubSeed");

            setSupportActionBar(toolbar);

        }
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (isFirstOpen) {
                    TextView textView = (TextView) drawerList.getChildAt(0).findViewById(R.id.drawer_listitem_textview);
                    textView.setTextColor(getResources().getColor(drawerListColor[0]));
                    isFirstOpen=false;
                }
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        FragmentManager fm = getSupportFragmentManager();
        fragments[0] = fm.findFragmentById(R.id.notice_fragment);
        fragments[1] = fm.findFragmentById(R.id.star_fragment);
        fragments[2] = fm.findFragmentById(R.id.settings_fragment);
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.commit();
        for (int i = 1; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        Log.e("S--->","sssssss");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void selectFragment(int position) {
        // update the main content by replacing fragments
        drawerLayout.closeDrawer(drawerList);
        switch (position) {
            case 0:
                toolbar.setTitle("ClubSeed");
                break;
            case 1:
                toolbar.setTitle("我的收藏");
                break;
            case 2:
                toolbar.setTitle("设置");
                break;
        }
        showFragment(position, false);
    }

    public ImageLoader getImageLoader(){
        return this.imageLoader;
    }
}
