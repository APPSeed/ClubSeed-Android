package cn.edu.ustc.appseed.clubseed.data;

import java.util.LinkedList;

/**
 * Created by Hengruo on 2015/3/5.
 */
public class ClubPhp {
    private int error;
    private String errormessage;
    private LinkedList<Club> data;
    public ClubPhp(){

    }
    public ClubPhp(int error, String errormessage, LinkedList<Club> data) {
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

    public LinkedList<Club> getData() {
        return data;
    }

    public void setData(LinkedList<Club> data) {
        this.data = data;
    }
}
