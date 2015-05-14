package cn.edu.ustc.appseed.clubseed.cache;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.edu.ustc.appseed.clubseed.R;
import cn.edu.ustc.appseed.clubseed.utils.AppUtils;

/**
 * Created by shenaolin on 15/4/20.
 */
public class ImageLoader {
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    TextDrawable drawable;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    // 线程池
    ExecutorService executorService;
    int mainColor;
    TextView line1=null;
    TextView line2=null;
    int club_id;
    int event_id;
    Bitmap poster;

    /**
     * stub_id是默认图片的id
     * @param context
     */
    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        drawable=null;

    }
    public ImageLoader(Context context,TextDrawable drawable) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        this.drawable=drawable;

    }

    // 最主要的方法
    public int DisplayImage(Context context,String url,String club_id,String event_id,ImageView imageView,TextView line1,TextView line2) {
        imageViews.put(imageView, url);
        // 先从内存缓存中查找

        this.line1=line1;
        this.line2=line2;
        this.club_id=Integer.parseInt(club_id);
        this.event_id=Integer.parseInt(event_id);
        imageView.setTag(event_id);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);

            drawLines(bitmap);

        }
        else {
            // 若没有的话则开启新线程加载图片
            queuePhoto(url, imageView);


        }



        return mainColor;
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url,imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url){
        File f = fileCache.getFile(url);
        // 先从文件缓存中查找是否有
        Bitmap b = decodeFile(f);
        if (b != null) {
            if (b!=null){
                Palette palette = Palette.generate(b);
                Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                if (swatch!=null){
                    mainColor=swatch.getRgb();
                }
            }
            drawLines(b);
            return b;
        }

        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) imageUrl
                        .openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);

            if (bitmap!=null){
                Palette palette = Palette.generate(bitmap);
                Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                if (swatch!=null){
                    mainColor=swatch.getRgb();
                }
            }
            drawLines(bitmap);
            return bitmap;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // 最后从指定的url中下载图片
    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 200;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            // 更新的操作放在UI线程中
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 防止图片错位
     *
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // 用于在UI线程中更新界面
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageDrawable(drawable);

        }
    }
    private void drawLines(Bitmap bitmap){
        new AsyncTask<Bitmap,Void,Integer>(){
            @Override
            protected Integer doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                if (bitmap!=null) {
                    poster=bitmap;
                    Palette palette = Palette.generate(bitmap);
                    Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                    if (swatch!=null) {
                        Integer integer = swatch.getRgb();
                        AppUtils.colors.put(club_id,integer);
                        return integer;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (line1!=null&&line2!=null){
                    AppUtils.colors.put(club_id,integer);
                    AppUtils.logos.put(club_id,poster);
                    line1.setTextColor(integer);
                    line2.setTextColor(integer);
                }else {
                   AppUtils.graphs.put(event_id,poster);
                }
            }
        }.execute(bitmap);



    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();

    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public int getMainColor(){
        return mainColor;
    }



}
