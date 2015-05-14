package cn.edu.ustc.appseed.clubseed.cache;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shenaolin on 15/4/20.
 */
public class MemoryCache {
    private static final String TAG = "MemoryCache";
    //放入缓存是同步操作
    //LinkedHashMap构造方法的最后一个参数true代表这个map里的元素将按照LRU排列
    private Map<String,Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10,1.5f,true));

    //缓存中图片所占的字节,通过这个变量严格控制缓存所占的堆内存
    private long size = 0;
    //缓存只能占用的最大堆内存,设置为1M
    private long limit = 1000000;
    public MemoryCache() {
        // use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void setLimit(long new_limit) {
        limit = new_limit;
        Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
    }

    public Bitmap get(String id) {
        try {
            if (!cache.containsKey(id))
                return null;
            return cache.get(id);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void put(String id, Bitmap bitmap) {
        try {
            if (cache.containsKey(id))
                size -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * 严格控制堆内存，如果超过将首先替换最近最少使用的那个图片缓存
     *
     */
    private void checkSize() {
        Log.i(TAG, "cache size=" + size + " length=" + cache.size());
        if (size > limit) {
            // 先遍历最近最少使用的元素
            Iterator<Map.Entry<String, Bitmap>> iter = cache.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size " + cache.size());
        }
    }
    public void clear() {
        cache.clear();
    }
    /**
     * 图片占用的内存
     *
     * @param bitmap
     * @return
     */
    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }




}
