package cn.edu.ustc.appseed.clubseed.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amulyakhare.textdrawable.TextDrawable;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.activity.ClubInfoActivity;
import cn.edu.ustc.appseed.clubseed.activity.EventContentActivity;
import cn.edu.ustc.appseed.clubseed.activity.MainActivity;
import cn.edu.ustc.appseed.clubseed.cache.ImageLoader;
import cn.edu.ustc.appseed.clubseed.data.Club;
import cn.edu.ustc.appseed.clubseed.data.ClubPhp;
import cn.edu.ustc.appseed.clubseed.data.Event;
import cn.edu.ustc.appseed.clubseed.data.GetPhp;
import cn.edu.ustc.appseed.clubseed.data.ListPhp;
import cn.edu.ustc.appseed.clubseed.data.ViewActivityPhp;
import cn.edu.ustc.appseed.clubseed.listener.EndlessScrollListener;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;
import cn.edu.ustc.appseed.clubseed.utils.Constant;
import cn.edu.ustc.appseed.clubseed.utils.ToastManager;

import static android.view.View.VISIBLE;

/**
 * Created by shenaolin on 15/4/12.
 */
public class TabItemFragment extends Fragment {
    private boolean isLogo = false;
    private BaseAdapter mBaseAdapter;
    //NoticeActivity
    private MainActivity context;
    //列表
    private ListView mListView;
    private ListPhp newListPhp;
    private SliderLayout mSliderLayout;
    private ProgressBar mProgressBar;
    private int latest_id;
    private int mainColor;



    /*
     * 下拉可以刷新的布局
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListPhp mListPhp;
    private ToastManager toastManager;
    private int listmode; // 0: First load; 1: Refresh; 2: load more
    private int slidermode = 0;

    public static final String EXTRA_ACTIVITY_ID = "ID";
    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_CLUB_ID = "CLUBID";
    public static final String EXTRA_MAIN_COLOR = "MAINCOLOR";
    public static final String EXTRA_URL="URL";



    public static final String TAG = "TAG";
    public static final String TAGID = "TAGID";
    public static final String LATEST_ID = "latest_id";

    private Bundle mBundle = new Bundle();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latest_id=AppUtils.sSharedPreferences.getInt(LATEST_ID,1);


    }

    public void setTitle(String title,int id){
        mBundle.putString(TAG,title);
        mBundle.putInt(TAGID,id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_notice,container,false);
        listmode=0;
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        mListView = (ListView) v.findViewById(R.id.listViewEvents);
        mProgressBar=(ProgressBar)v.findViewById(R.id.progressbar);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            /**
             * 下拉刷新的方法
             * 执行1.Toast报告2.模式转换3.两个异步任务的执行
             */
            @Override
            public void onRefresh() {
                //Toast.makeText(getActivity(), "刷新中...", Toast.LENGTH_SHORT).show();
                toastManager.toastShow(getResources().getString(R.string.refreshing), Toast.LENGTH_SHORT);
                listmode = 1;
                slidermode = 1;
                new ListViewAsyncTask().execute(1);
                //new SliderAsyncTask().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });

        context=(MainActivity)this.getActivity();
        toastManager=new ToastManager(context);
        new ListViewAsyncTask().execute(1);
        //new SliderAsyncTask().execute();
        setHasOptionsMenu(true);
        return  v;

    }

    private void setEmptySliderLayout() {
        for (int i = 0; i < 5; i++) {
            TextSliderView mTextSliderView = new TextSliderView(getActivity());
            mTextSliderView.description("").image(R.drawable.empty_slider);
            mTextSliderView.getBundle().putString(EXTRA_ACTIVITY_ID, "0");
            mTextSliderView.getBundle().putString(EXTRA_TITLE, "");
            mTextSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    int ID = Integer.parseInt(slider.getBundle().getString(EXTRA_ACTIVITY_ID));
                    String title = slider.getBundle().getString(EXTRA_TITLE);
                    Intent i = new Intent(getActivity(), EventContentActivity.class);
                    i.putExtra(EXTRA_ACTIVITY_ID, ID);
                    i.putExtra(EXTRA_TITLE, title);
                    startActivity(i);
                }
            });
            mSliderLayout.addSlider(mTextSliderView);
        }
    }



    private  void setListView(ListPhp listPhp){

        mBaseAdapter=new EventListViewAdapter(getActivity(),listPhp);
        mListView.setAdapter(mBaseAdapter);
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                listmode = 2;
                new ListViewAsyncTask().execute(page);
            }
        });

        mListView.setVisibility(VISIBLE);
        mBaseAdapter.notifyDataSetChanged();
    }

    private void setEmptyListView(){
        mBaseAdapter = new EventListViewAdapter(getActivity(), AppUtils.sNullListPhp);
        //must keep order of "addHeader, setAdapter, setOnXxxListener"
        mListView.setAdapter(mBaseAdapter);
        mListView.setVisibility(View.INVISIBLE);
        mBaseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class ListViewAsyncTask extends AsyncTask<Integer,Void,ListPhp> {

        @Override
        protected ListPhp doInBackground(Integer... params) {
            String jsonString = null;
            String newJsonString = null;
            ListPhp listPhp = new ListPhp();
            String url;
            String newurl;
            int tagId = mBundle.getInt(TAGID);
            if (tagId==0) {
                url = "http://clubseed.sinaapp.com/api/list.php?format=json2&perpage=3&page=" + params[0];
                newurl = "http://clubseed.sinaapp.com/api/list.php?format=json2&perpage=3&page=" + params[0]+"&id_after="+latest_id;
            }else{
                url = "http://clubseed.sinaapp.com/api/list.php?format=json2&perpage=3&page=" + params[0]+"&clubid="+tagId;
                newurl = "http://clubseed.sinaapp.com/api/list.php?format=json2&perpage=3&page=" + params[0]+"&clubid="+tagId+"&id_after="+latest_id;

            }
            Log.e("S",newurl);
            try {
                jsonString=AppUtils.getJSONString(url);
                listPhp=(ListPhp)JSON.parseObject(jsonString,ListPhp.class);
                newJsonString=AppUtils.getJSONString(newurl);
                newListPhp=JSON.parseObject(newJsonString,ListPhp.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return listPhp;
        }
        @Override
        protected void onPostExecute(ListPhp listPhp) {
            if(listPhp!=null){
                mProgressBar.setVisibility(View.INVISIBLE);
            }else{
                mProgressBar.setVisibility(VISIBLE);
            }
            switch (listmode) {
                case 0:
                    if (listPhp == null) {
                        //Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_SHORT).show();
                        toastManager.toastShow(getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT);
                        setEmptyListView();
                    } else {
                        mListPhp = listPhp;
                        setListView(listPhp);
                    }
                    break;
                case 1:
                    if (listPhp == null) {
                        //Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_SHORT).show();
                        toastManager.toastShow(getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT);
                    } else if (newListPhp!=null){
                        if (newListPhp.getData().size()>0) {
                            mListPhp = listPhp;
                            setListView(listPhp);

                        }
                    }
                    listmode=0;
                    break;
                case 2:
                    if (listPhp == null) {
                        //Toast.makeText(getActivity(), "无法连接网络", Toast.LENGTH_SHORT).show();
                        toastManager.toastShow(getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT);
                    } else {
                        mListPhp.appendData(listPhp.getData());
                        mBaseAdapter.notifyDataSetChanged();
                    }
                    break;
            }




        }


    }

    public static class ViewHolder{
        public TextView textViewClubName;
        public TextView textViewTitle;
        public TextView textViewPlace;
        public TextView textViewTime;
        public TextView textViewSummary;

        public RelativeLayout clubInfo;
        public LinearLayout textInfo;

        public ImageView imageViewThumbnail;
        public ImageView mImageView;
        public TextView firstLine;
        public TextView secondLine;
    }

    public static class Colors{
        public int mainColor;

    }

    private class EventListViewAdapter extends BaseAdapter {
        private Activity activity;
        private LayoutInflater inflater;
        private LinkedList<Event> mEvents;
        public static final int red = 0xfffa3746;

        public EventListViewAdapter(Activity activity, ListPhp listPhp) {
            this.activity = activity;
            this.mEvents = listPhp.getData();

        }




        @Override
        public int getCount() {
            return mEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return mEvents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            final ViewHolder holder;
            final Colors colors=new Colors();

            colors.mainColor=getResources().getColor(R.color.app_main);
            final Event event = mEvents.get(position);

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_event, null);
                holder=new ViewHolder();
                holder.textViewClubName = (TextView) convertView.findViewById(R.id.textViewClubName);
                holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
                holder.textViewPlace = (TextView) convertView.findViewById(R.id.textViewPlace);
                holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
                holder.textViewSummary = (TextView) convertView.findViewById(R.id.textViewSummary);

                holder.clubInfo = (RelativeLayout)convertView.findViewById(R.id.clubInfoTop);
                holder.textInfo = (LinearLayout)convertView.findViewById(R.id.textInfoMiddle);

                holder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
                holder.secondLine = (TextView) convertView.findViewById(R.id.secondLine);
                holder.imageViewThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
                holder.mImageView = (ImageView)convertView.findViewById(R.id.main_picture);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            int blue = getResources().getColor(R.color.app_main);
            holder.firstLine.setTextColor(blue);
            holder.secondLine.setTextColor(blue);



            if (Integer.parseInt(event.getID())>latest_id){
                SharedPreferences.Editor editor = AppUtils.sSharedPreferences.edit();
                editor.putInt(LATEST_ID,Integer.parseInt(event.getID()));
                editor.commit();
            }

            holder.textViewClubName.setText(event.getClubname());
            holder.textViewTitle.setText(event.getTitle());
            holder.textViewPlace.setText(event.getPlace());
            holder.textViewTime.setText(event.getTime());
            holder.textViewSummary.setText(event.getSummary());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(mEvents.get(position).getClubname().substring(0, 1), getResources().getColor(R.color.light_blue));
            holder.imageViewThumbnail.setImageDrawable(drawable);
            holder.imageViewThumbnail.setTag(event.getClubid());
            AppUtils.colors.put(Integer.parseInt(event.getClubid()), colors.mainColor);
            if (event.getPhotoURL()==null||event.getPhotoURL().isEmpty()){
                holder.mImageView.setVisibility(View.GONE);
            }





            new AsyncTask<Void,Void,Void>(){

                String headUrl;

                @Override
                protected Void doInBackground(Void... params) {
                    String clubUrl = "http://clubseed.sinaapp.com/api/club.php?format=json2&clubid="+event.getClubid();
                    ClubPhp clubPhp = null;
                    Club club = null;
                    try {
                        String jsonString = AppUtils.getJSONString(clubUrl);
                        clubPhp = (ClubPhp) JSON.parseObject(jsonString, ClubPhp.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (clubPhp != null) {
                        club = clubPhp.getData().getFirst();
                    }
                    if (club != null) {
                        try {
                            headUrl = club.getImage();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (headUrl!=null&&!headUrl.isEmpty()) {

                        Picasso.with(context).load(headUrl).resize(250,250).into(holder.imageViewThumbnail);
                        holder.imageViewThumbnail.setDrawingCacheEnabled(true);
                        Bitmap logoBitmap =holder.imageViewThumbnail.getDrawingCache();
                        Palette palette = Palette.generate(logoBitmap);
                        holder.imageViewThumbnail.setDrawingCacheEnabled(false);
                        int logoColor = palette.getVibrantColor(colors.mainColor);
                        holder.firstLine.setTextColor(logoColor);
                        holder.secondLine.setTextColor(logoColor);
                        AppUtils.colors.put(Integer.parseInt(event.getClubid()), logoColor);
                        AppUtils.logos.put(Integer.parseInt(event.getClubid()),logoBitmap);


                    }
                    if (event.getPhotoURL()!=null&&!event.getPhotoURL().isEmpty()) {
                        String photoUrl = event.getPhotoURL();
                        Log.e("S", photoUrl);
                        Picasso.with(context).load(photoUrl).resize(800,800).into(holder.mImageView);
                        holder.mImageView.setVisibility(View.VISIBLE);

                        holder.mImageView.buildDrawingCache();
                        Bitmap graph = holder.mImageView.getDrawingCache();

                        //Bitmap graph = ((BitmapDrawable)holder.mImageView.getDrawable()).getBitmap();
                        if (graph != null) {

                            AppUtils.graphs.put(Integer.parseInt(event.getClubid()), graph);



                        }

                    }
                }
            }.execute();



            holder.clubInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ClubInfoActivity.class);
                    i.putExtra(EXTRA_ACTIVITY_ID, Integer.parseInt(mEvents.get(position).getID()));
                    i.putExtra(EXTRA_TITLE, mEvents.get(position).getClubname());
                    i.putExtra(EXTRA_CLUB_ID, Integer.parseInt(mEvents.get(position).getClubid()));
                    i.putExtra(EXTRA_MAIN_COLOR,colors.mainColor);
                    startActivity(i);


                }
            });
            holder.textInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), EventContentActivity.class);
                    i.putExtra(EXTRA_ACTIVITY_ID, Integer.parseInt(mEvents.get(position).getID()));
                    i.putExtra(EXTRA_TITLE, mEvents.get(position).getClubname());
                    i.putExtra(EXTRA_CLUB_ID, Integer.parseInt(mEvents.get(position).getClubid()));
                    i.putExtra(EXTRA_MAIN_COLOR,colors.mainColor);
                    i.putExtra(EXTRA_URL,mEvents.get(position).getPhotoURL());

                    startActivity(i);
                }
            });

            return convertView;
        }
    }

}




