package cn.edu.ustc.appseed.clubseed.fragment;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;
import cn.edu.ustc.appseed.clubseed.utils.Constant;
import cn.edu.ustc.appseed.clubseed.widget.ViewPagerIndicator;

/**
 * Created by shenaolin on 15/4/12.
 */
public class SlidingTabsFragment extends Fragment {
    /**
     * 是否第一次打开应用的标志
     *
     */
    private boolean isFirst=true;
    /**
     * 顶部标签的列表
     */
    private List<String> tags=new ArrayList<>();
    private List<Integer> tagsId = new ArrayList<>();
    /**
     * ViewPager控制滑动fragment的控制器的对象
     */
    private ViewPager mViewPager;
    /**
     * ViewPagerIndicator顶部标签滑动的控制器对象
     */
    private ViewPagerIndicator mIndicator;
    /**
     * ViewPagerAdapter适配器
     */
    private FragmentPagerAdapter mAdatprt;
    /**
     * 标签对应的fragment列表
     */
    private List<Fragment> mTabContents = new ArrayList<>();
    /**
     * 获取屏幕宽度
     */
    public static float scaleX=0;

    private View v;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sliding_tabs, container, false);
        initTags();

        initIndicator(v);
        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppUtils.needRefresh){
            initTags();
            initIndicator(v);
            AppUtils.needRefresh=false;
        }
    }

    public void initTags(){
        int tagsNum = AppUtils.sSharedPreferences.getInt("FOCUSCLUBNUM",0);
        tags.clear();
        tagsId.clear();
        tags.add("热点");
        tagsId.add(0);
        for (int i=1;i<=tagsNum;i++){
            tags.add(AppUtils.sSharedPreferences.getString("CLUBNAME"+i,""));
            tagsId.add(AppUtils.sSharedPreferences.getInt("CLUBID"+i,0));
        }

    }
//    public void initTags(){
//        isFirst= AppUtils.sSharedPreferences.getBoolean("isFirst",true);
//        SharedPreferences.Editor editor;
//        editor = AppUtils.sSharedPreferences.edit();
//        //判断是否第一次打开应用,如果是则初始化标签,否则从SharePreference中加载设置
//        //建议清除缓存重新写
//        if (isFirst){
//            for (int i=0;i<5;i++){
//                tags.add(Constant.tagPage[i]);
//            }
//            editor.putBoolean("isFirst",false);
//            editor.putInt("tagLenth",tags.size());
//            for (int i=0;i<tags.size();i++){
//                editor.putString("tag"+i,tags.get(i).toString());
//            }
//        }
//        else {
//            tags=new ArrayList<>();
//
//            int tagLenth=AppUtils.sSharedPreferences.getInt("tagLenth",0);
//            for (int i=0;i<tagLenth;i++){
//                tags.add(AppUtils.sSharedPreferences.getString("tag"+i,""));
//            }
//
//        }
//        editor.putBoolean("isFirst",false);
//        editor.commit();
//    }

    public void initIndicator(View v){
        mTabContents.clear();
        for (int i=0;i<tags.size();i++){
            TabItemFragment fragment = new TabItemFragment();
            fragment.setTitle(tags.get(i),tagsId.get(i));

            mTabContents.add(fragment);
        }
        mAdatprt=new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }

            @Override
            public int getCount() {
                return mTabContents.size();
            }
        };

        mIndicator=(ViewPagerIndicator)v.findViewById(R.id.id_indicator);
        mViewPager=(ViewPager)v.findViewById(R.id.pager);

        mIndicator.setTabItemTitles(tags);
        mViewPager.setAdapter(mAdatprt);
        mViewPager.setOffscreenPageLimit(4);
        mIndicator.setViewPager(mViewPager,0);

    }


}
