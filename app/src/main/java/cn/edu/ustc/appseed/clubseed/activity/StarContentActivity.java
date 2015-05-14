package cn.edu.ustc.appseed.clubseed.activity;

/*
* Show the detail content of the event which you select.
* Why to use a custom toolbar instead of the default toolbar in ActionBarActivity?
* Because the custom toolbar is very convenient to edit it and good to unify the GUI.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;

import java.util.Stack;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.data.Event;

import cn.edu.ustc.appseed.clubseed.fragment.StarFragment;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;

public class StarContentActivity extends ActionBarActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView mTextView;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private TextView mTitle;
    private TextView mLine;
    private TextView nullTextView;
    private Event mEvent;
    private FloatingActionsMenu fam;
    int ID;
    int CLUB_ID;
    int mainColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_content);

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextView = (TextView) findViewById(R.id.textViewEventContent);
        mImageView = (ImageView) findViewById(R.id.imgContent);
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mTitle = (TextView)findViewById(R.id.textViewTitle);
        mLine = (TextView)findViewById(R.id.divider);
        fam = (FloatingActionsMenu)findViewById(R.id.floatmenu_event_content);
        fam.setVisibility(View.INVISIBLE);
        mImageView.setOnClickListener(this);
        if (toolbar != null) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
            toolbar.setBackgroundColor(mainColor);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        ID = getIntent().getIntExtra(StarFragment.EXTRA_ACTIVITY_ID, 0);
        CLUB_ID = getIntent().getIntExtra(StarFragment.EXTRA_CLUB_ID,0);
        mEvent = AppUtils.savedEvents.get(ID);
        setTitle(mEvent.getClubname());
        mTextView.setText(mEvent.getContent());
        final String photoUrl = getIntent().getStringExtra(StarFragment.EXTRA_URL);
        //Picasso.with(this).load(photoUrl).into(mImageView);
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
        if (mTextView!=null){
            mProgressBar.setVisibility(View.GONE);
        }
        mTitle.setText(mEvent.getTitle());




        mLine.setTextColor(mainColor);
        mTitle.setTextColor(mainColor);


    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, GraphOnlyActivity.class);
        i.putExtra(EventContentActivity.EXTRA_BITMAP, ID);
        startActivity(i);

    }
}
