package cn.edu.ustc.appseed.clubseed.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.serializer.IntegerCodec;
import com.amulyakhare.textdrawable.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.activity.EventContentActivity;
import cn.edu.ustc.appseed.clubseed.activity.StarContentActivity;
import cn.edu.ustc.appseed.clubseed.data.Event;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;
import cn.edu.ustc.appseed.clubseed.utils.Debug;
import cn.edu.ustc.appseed.clubseed.widget.SwipeListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class StarFragment extends Fragment implements SwipeListView.RemoveListener {
    private SwipeListView mListView;
    private TextView mTextView;
    StarListViewAdapter adapter;
    public static String EXTRA_ID = "ID";
    public static final String EXTRA_ACTIVITY_ID = "ID";
    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_CLUB_ID = "CLUBID";
    public static final String EXTRA_MAIN_COLOR = "MAINCOLOR";
    public static final String EXTRA_URL="URL";

    public StarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_star, container, false);
        mTextView = (TextView) v.findViewById(R.id.listViewNoStar);
        mListView = (SwipeListView) v.findViewById(R.id.listViewStars);
        adapter = new StarListViewAdapter(getActivity());
        if (AppUtils.savedEvents.isEmpty()) {
            mTextView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        } else {
            mTextView.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
            adapter.resetData(AppUtils.savedEvents);
            mListView.setAdapter(adapter);
            mListView.setRemoveListener(this);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event event = (Event) parent.getItemAtPosition(position);
                    Intent i = new Intent(getActivity(), StarContentActivity.class);
                    i.putExtra(EXTRA_ACTIVITY_ID, Integer.parseInt(event.getID()));
                    i.putExtra(EXTRA_CLUB_ID, Integer.parseInt(event.getClubid()));
                    startActivity(i);
                }
            });
        }
        return v;
    }

    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if(AppUtils.savedEvents.isEmpty()){
            mTextView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }else{
            mTextView.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
            Debug.isNull(adapter);
            adapter.resetData(AppUtils.savedEvents);
            mListView.setAdapter(adapter);
            mListView.setRemoveListener(this);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event event = (Event) parent.getItemAtPosition(position);
                    Intent i = new Intent(getActivity(), StarContentActivity.class);
                    i.putExtra(EXTRA_ACTIVITY_ID, Integer.parseInt(event.getID()));
                    i.putExtra(EXTRA_CLUB_ID, Integer.parseInt(event.getClubid()));
                    i.putExtra(EXTRA_URL,event.getPhotoURL());


                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void removeItem(SwipeListView.RemoveDirection direction, int position) {
        Debug.showLog(String.valueOf(position));
        Event event = (Event)adapter.getItem(position);
        adapter.remove(event);
        mListView.setAdapter(adapter);
        AppUtils.savedEvents.remove(Integer.parseInt(event.getID()));
        new DelEventAsyncTask().execute(event);
        if(AppUtils.savedEvents.isEmpty()){
            mTextView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }
    }

    private class DelEventAsyncTask extends AsyncTask<Event, Void, Void> {

        @Override
        protected Void doInBackground(Event... params) {
            Event event = params[0];
            try {
                AppUtils.deleteStar(event.getID() + ".event");
                AppUtils.deleteStar(event.getID() + ".png");
            } catch (Exception e) {
                e.printStackTrace();
            }return null;
        }
    }

    private class StarListViewAdapter extends BaseAdapter {
        private Activity activity;
        private LayoutInflater inflater;
        private LinkedList<Event> mEvents;
        public static final int light_blue = 0xff4fc3f7;

        public StarListViewAdapter(Activity activity){
            this.activity = activity;
            mEvents = new LinkedList<>();
        }

        public StarListViewAdapter(Activity activity, HashMap<Integer, Event> events) {
            this.activity = activity;
            mEvents.addAll(events.values());
        }

        public void resetData(HashMap<Integer, Event> eventHashMap) {
            mEvents.clear();
            mEvents.addAll(eventHashMap.values());
        }

        public void remove(Event event){
            mEvents.remove(event);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            Event event = mEvents.get(position);
            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_item_event, null);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
            Date curDate = new Date(System.currentTimeMillis());
            String date = formatter.format(curDate);
            String eventdate = mEvents.get(position).getTime();

            TextView textViewClubName = (TextView) convertView.findViewById(R.id.textViewClubName);
            TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            TextView textViewPlace = (TextView) convertView.findViewById(R.id.textViewPlace);
            TextView textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            TextView textViewSummary = (TextView) convertView.findViewById(R.id.textViewSummary);

            ImageView imageViewThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
            ImageView imageViewPoster = (ImageView)convertView.findViewById(R.id.main_picture);

            textViewClubName.setText(mEvents.get(position).getClubname());
            textViewTitle.setText(mEvents.get(position).getTitle());
            textViewPlace.setText(mEvents.get(position).getPlace());
            if (eventdate.compareTo(date) > 0) {
                textViewTime.setText(eventdate);
            } else {
                String text = "<font color='#fa3746'>"+eventdate + " 【已过期】</font>";
                textViewTime.setText(Html.fromHtml(text));
            }
            textViewSummary.setText(mEvents.get(position).getSummary());

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(mEvents.get(position).getClubname().substring(0, 1), light_blue);
            imageViewThumbnail.setImageDrawable(drawable);
            Bitmap logo = AppUtils.logos.get(Integer.parseInt(event.getClubid()));
            Bitmap graph = AppUtils.graphs.get(Integer.parseInt(event.getID()));
//            if (logo!=null){
//                imageViewThumbnail.setImageBitmap(logo);
//            }
//            if (graph!=null){
//                imageViewPoster.setImageBitmap(graph);
//                imageViewPoster.setVisibility(View.VISIBLE);
//
//            }


            return convertView;
        }
    }

}
