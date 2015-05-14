package cn.edu.ustc.appseed.clubseed.data;

/**
 * Created by Hengruo on 2015/3/5.
 */
public class Club {
    private String clubid;
    private String clubname;
    private String image;
    private String information;
    private String managerName;
    private String email;
    private String phoneNumber;
    private String location;
    private String url;
    private String updatingUrl;

    public Club() {
    }

    public Club(String clubid, String clubname, String image, String information, String managerName, String email, String phoneNumber, String location, String url, String updatingUrl) {
        this.clubid = clubid;
        this.clubname = clubname;
        this.image = image;
        this.information = information;
        this.managerName = managerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.url = url;
        this.updatingUrl = updatingUrl;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdatingUrl() {
        return updatingUrl;
    }

    public void setUpdatingUrl(String updatingUrl) {
        this.updatingUrl = updatingUrl;
    }
}
