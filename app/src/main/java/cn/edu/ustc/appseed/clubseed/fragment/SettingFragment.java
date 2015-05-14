package cn.edu.ustc.appseed.clubseed.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.activity.AuthorActivity;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;
import cn.edu.ustc.appseed.clubseed.widget.PreferenceFragment;

/**
 * Created by shenaolin on 15/4/15.
 */
public class SettingFragment extends PreferenceFragment {
    private Preference prefClearCache;
    private Preference prefFeedback;
    private Preference prefShare;
    private Preference prefAuthor;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        prefClearCache = findPreference(getString(R.string.pref_clear_cache));
        prefAuthor = findPreference(getString(R.string.pref_author));
        prefFeedback = findPreference(getString(R.string.pref_feedback));
        prefShare = findPreference(getString(R.string.pref_share));

        prefClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(AppUtils.sAppContext, "清除成功", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        prefAuthor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), AuthorActivity.class);
                startActivity(i);
                return true;
            }
        });
        prefFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ustcappseed@163.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "反馈意见");
                i.putExtra(Intent.EXTRA_TEXT, "\n\n以下信息为检测错误所需，请不要删除或修改，我们不会索取您的个人信息，谢谢合作！\n" + AppUtils.phoneInfo);
                startActivity(Intent.createChooser(i, "选择邮件客户端"));
                return false;
            }
        });
        prefShare.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "我正在使用一款超赞的社团活动发布应用ClubSeed，快来使用吧~下载地址：http://clubseed.iappseed.com/download/clubseed.apk");
                startActivity(Intent.createChooser(intent, "分享"));
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        v.setBackgroundColor(getResources().getColor(R.color.white));
        return v;
    }
}

