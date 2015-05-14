package cn.edu.ustc.appseed.clubseed.data;

import java.util.LinkedList;

/**
 * Created by gdshen on 2/6/15.
 *
 * Changed by Hengruo on 3/3/15
 * Combine the ListPhp and ViewActivityPhp jsondata
 */
public class ListPhp {
    private int error;
    private String errormessage;
    private LinkedList<Event> data;

    public ListPhp() {
    }

    public ListPhp(int error, String errormessage, LinkedList<Event> data) {
        this.error = error;
        this.errormessage = errormessage;
        this.data = data;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public LinkedList<Event> getData() {
        return data;
    }

    public void setData(LinkedList<Event> data) {
        this.data = data;
    }

    public void appendData(LinkedList<Event> data) {
        this.data.addAll(data);
    }

    public void resetData(LinkedList<Event> data){
        this.data.clear();
        this.data = data;
    }
}
