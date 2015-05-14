package cn.edu.ustc.appseed.clubseed.data;

/**
 * Created by Hengruo on 2015/3/3.
 */
public class ViewActivityPhp {
    private int error;
    private String errormessage;
    private Event data;

    public ViewActivityPhp(){

    }

    public ViewActivityPhp(int error, String errormessage, Event data) {
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

    public Event getData() {
        return data;
    }

    public void setData(Event data) {
        this.data = data;
    }
}
