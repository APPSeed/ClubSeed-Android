package cn.edu.ustc.appseed.clubseed.activity;

/*
* Show the detail content of the event which you select.
* Why to use a custom toolbar instead of the default toolbar in ActionBarActivity?
* Because the custom toolbar is very convenient to edit it and good to unify the GUI.
 */

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.cache.ImageLoader;
import cn.edu.ustc.appseed.clubseed.fragment.SlidingTabsFragment;
import cn.edu.ustc.appseed.clubseed.data.Event;
import cn.edu.ustc.appseed.clubseed.data.ViewActivityPhp;
import cn.edu.ustc.appseed.clubseed.fragment.StarFragment;
import cn.edu.ustc.appseed.clubseed.fragment.TabItemFragment;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;

public class EventContentActivity extends ActionBarActivity implements IUiListener, View.OnClickListener {
    //控件
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView mTextView;
    private ScrollView mScrollView;
    private TextView mLine;
    private ImageView mImageView;
    private TextView mTextViewTitle;
    private int mainColor;
    private FloatingActionButton actionStar, actionCalendar, actionShare;
    private FloatingActionsMenu floatingActionsMenu;
    private Event event = null;
    private boolean isSaved;
    private boolean isOnline;
    public static final String EXTRA_BITMAP = "bitmap";
    int ID;
    int CLUB_ID;

    //与QQ分享相关的参数
    private Tencent mTencent;
    private String summary = " ";
    private String url;
    private ViewActivityPhp mViewActivityPhp;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mTextView = (TextView) findViewById(R.id.textViewEventContent);
        mTextViewTitle = (TextView) findViewById(R.id.textViewTitle);
        mLine = (TextView) findViewById(R.id.divider);
        mImageView = (ImageView) findViewById(R.id.imgContent);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mScrollView.setVisibility(View.INVISIBLE);
        progressBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.app_main));
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.floatmenu_event_content);
        actionStar = (FloatingActionButton) findViewById(R.id.action_star);
        actionShare = (FloatingActionButton) findViewById(R.id.action_share);
        actionCalendar = (FloatingActionButton) findViewById(R.id.action_calendar);

        CLUB_ID = getIntent().getIntExtra(TabItemFragment.EXTRA_CLUB_ID, 0);
        if (AppUtils.colors.get(CLUB_ID) != null) {

            mainColor = AppUtils.colors.get(CLUB_ID);
        } else {
            mainColor = getResources().getColor(R.color.app_main);
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(mainColor);
        }

        final String photoUrl = getIntent().getStringExtra(StarFragment.EXTRA_URL);
//        Picasso.with(this).load(photoUrl).into(mImageView);

        new AsyncTask<Void,Void,Bitmap>(){

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap=null;
                try {
                    bitmap=null;
                    bitmap=AppUtils.getBitmapFromURL(photoUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap!=null){
                    mImageView.setImageBitmap(bitmap);
                    AppUtils.graphs.put(ID,bitmap);
                }
            }
        }.execute();

        mImageView.setOnClickListener(this);
        if (toolbar != null) {
            toolbar.setBackgroundColor(mainColor);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        mTextViewTitle.setTextColor(mainColor);
        mLine.setTextColor(mainColor);

        String title = getIntent().getStringExtra(TabItemFragment.EXTRA_TITLE);
        ID = getIntent().getIntExtra(TabItemFragment.EXTRA_ACTIVITY_ID, 0);
        setTitle(title);

        if (ID == 0) {
            Toast.makeText(this, "未知的网络错误！", Toast.LENGTH_SHORT).show();
        } else if (AppUtils.savedEvents.containsKey(ID)) {
            isSaved = true;
        }

        actionStar.setColorNormal(mainColor);
        actionStar.setColorPressed(mainColor);
        if (isSaved) actionStar.setIcon(R.drawable.ic_star_black);
        actionCalendar.setColorNormal(mainColor);
        actionCalendar.setColorPressed(mainColor);
        actionShare.setColorNormal(mainColor);
        actionShare.setColorPressed(mainColor);
        actionStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event == null) return;
                if (isSaved) {
                    new DelEventAsyncTask().execute((FloatingActionButton) v);
                } else {
                    new SaveEventAsyncTask().execute((FloatingActionButton) v);
                }
            }
        });
        actionCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event == null) return;
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("title", event.getTitle());
                intent.putExtra("description", event.getSummary());
                intent.putExtra("beginTime", event.getTime());
                intent.putExtra("endTime", event.getTime() + "7200000");
                startActivity(Intent.createChooser(intent, "请选择一个日历应用"));
            }
        });
        actionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----->actionshare");
                if (event == null) return;
                qqShare();
            }
        });


        new ContentAsyncTask().execute(ID);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_event_content, menu);
//        if (isSaved) {
//            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_star_black));
//        }
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
//        if (id == R.id.action_calendar) {
//            if (event == null) return false;
//            Intent intent = new Intent(Intent.ACTION_EDIT);
//            intent.setType("vnd.android.cursor.item/event");
//            intent.putExtra("title", event.getTitle());
//            intent.putExtra("description", event.getSummary());
//            intent.putExtra("beginTime", event.getTime());
//            intent.putExtra("endTime", event.getTime() + "7200000");
//            startActivity(Intent.createChooser(intent, "请选择一个日历应用"));
//            return true;
//        }
//
//        if (id == R.id.action_star) {
//            if (event == null) return false;
//            if (isSaved) {
//                new DelEventAsyncTask().execute(item);
//            } else {
//                new SaveEventAsyncTask().execute(item);
//            }
//        }
//        //点击分享按钮
//        if (id == R.id.action_share) {
//            System.out.println("----->actionshare");
//            if (event == null) return false;
//            qqShare();
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }

    void qqShare() {
        System.out.println("Success");
        mTencent = Tencent.createInstance(getString(R.string.app_id), this.getApplicationContext());
        //mTencent.login(this,getString(R.string.scope),this);
        final Bundle params = new Bundle();
        if (event != null) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, event.getTitle());//设置标题为标题栏上标题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, event.getSummary());//摘要为正文前40字加省略号
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://clubseed.sinaapp.com/api/shareactivity.php?ID=" + event.getID());//此处地址为点击链接转移的地址，需要改！！

            if (mViewActivityPhp.getData().getPhotoURL().length() > 5) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mViewActivityPhp.getData().getPhotoURL());//图片地址
            }

            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "AppSeed" + getString(R.string.app_id));//应用名加ID，不要改
            //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);//支持直接发送到qq空间
            mTencent.shareToQQ(this, params, this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, GraphOnlyActivity.class);
        i.putExtra(EXTRA_BITMAP, ID);
        startActivity(i);

    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }

    private class SaveEventAsyncTask extends AsyncTask<FloatingActionButton, Void, FloatingActionButton> {
        @Override
        protected FloatingActionButton doInBackground(FloatingActionButton... params) {
            try {
                AppUtils.savedEvents.put(ID, event);
                Bitmap bitmap = event.getBitmap();
                event.setBitmap(null);
                String jsonString = JSON.toJSONString(event);
                AppUtils.saveString(event.getID() + ".event", jsonString);
                if (bitmap != null) {
                    event.setBitmap(bitmap);
                    AppUtils.saveBitmap(event.getID() + ".png", bitmap);
                }
                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(FloatingActionButton item) {
            if (item != null) {
                item.setIcon(R.drawable.ic_star_black);
                isSaved = true;
                Toast.makeText(EventContentActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EventContentActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DelEventAsyncTask extends AsyncTask<FloatingActionButton, Void, FloatingActionButton> {

        @Override
        protected FloatingActionButton doInBackground(FloatingActionButton... params) {
            try {
                AppUtils.deleteStar(event.getID() + ".event");
                AppUtils.deleteStar(event.getID() + ".png");
                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(FloatingActionButton item) {
            if (item != null) {
                item.setIcon(R.drawable.ic_star_outline_black);
                isSaved = false;
                Toast.makeText(EventContentActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                AppUtils.savedEvents.remove(ID);
            } else {
                Toast.makeText(EventContentActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ContentAsyncTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            String url = "http://clubseed.sinaapp.com/api/viewactivity.php?format=json2&ID=" + params[0];
            mViewActivityPhp = null;
            try {
                String jsonString = AppUtils.getJSONString(url);
                mViewActivityPhp =
                        JSON.parseObject(
                                jsonString,
                                ViewActivityPhp.class);
                event = mViewActivityPhp.getData();
            } catch (Exception e) {
                Log.d("HR", e.toString());
            }
            if (event != null) {
                isOnline = true;
                try {
                    //Bitmap bitmap = AppUtils.getBitmapFromURL(event.getPhotoURL());
                    Bitmap bitmap = AppUtils.graphs.get(Integer.parseInt(event.getID()));
                    event.setBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    event.setBitmap(null);
                }
            } else {
                isOnline = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (isOnline) {
                progressBar.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                String text = event.getContent();
                Bitmap bitmap = event.getBitmap();
                Log.d("HR", "Content:" + event.getClubname());
                if (text == null) {

                    Toast.makeText(EventContentActivity.this, "未知的网络错误", Toast.LENGTH_SHORT).show();
                } else
                    mTextView.setText(text);
                mTextViewTitle.setText(event.getTitle());

                if (bitmap == null) {
                    mImageView = null;
                } else {

                    mImageView.setImageBitmap(bitmap);
                    mImageView.setVisibility(View.VISIBLE);


                }
            } else {
                Toast.makeText(EventContentActivity.this, "未知的网络错误", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
