package cn.edu.ustc.appseed.clubseed.data;

import android.graphics.Bitmap;

/**
 * Created by gdshen on 2/6/15.
 *
 * Changed by Hengruo on 3/3/15
 * Change the list.php api result to the universal event infomation.
 */
public class Event {
    private String ID;
    private String PhotoURL;
    private String title;
    private String clubid;
    private String clubname;
    private String summary;
    private String content;
    private String place;
    private String recommend;
    private String tag;
    private String createtime;
    private String activitytime;
    private String AID;
    private String URL;
    private Bitmap bitmap;

    public Event() {
    }

    public Event(String ID, String photoURL, String title, String clubid, String clubname, String summary, String content, String place, String recommend, String tag, String createtime, String activitytime, String AID, String URL, Bitmap bitmap) {
        this.ID = ID;
        PhotoURL = photoURL;
        this.title = title;
        this.clubid = clubid;
        this.clubname = clubname;
        this.summary = summary;
        this.content = content;
        this.place = place;
        this.recommend = recommend;
        this.tag = tag;
        this.createtime = createtime;
        this.activitytime = activitytime;
        this.AID = AID;
        this.URL = URL;
        this.bitmap = bitmap;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClubid() {
        return clubid;
    }

    public void setClubid(String clubid) {
        this.clubid = clubid;
    }

    public String getClubname() {
        return clubname;
    }

    public void setClubname(String clubname) {
        this.clubname = clubname;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getActivitytime() {
        return activitytime;
    }

    public void setActivitytime(String activitytime) {
        this.activitytime = activitytime;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String photoURL) {
        PhotoURL = photoURL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return activitytime.substring(0, 4) + "年" +
                activitytime.substring(4, 6) + "月" +
                activitytime.substring(6, 8) + "日" +
                activitytime.substring(8, 10) + ":" +
                activitytime.substring(10, 12);
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

