package cn.edu.ustc.appseed.clubseed.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by shenaolin on 15/4/12.
 */
public class ToastManager {
    private Context context;
    private Toast toast = null;
    public ToastManager(Context context) {
        this.context = context;
    }
    public void toastShow(String text,int length) {
        if(toast == null)
        {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        else {
            toast.setText(text);
        }
        toast.show();
    }
}