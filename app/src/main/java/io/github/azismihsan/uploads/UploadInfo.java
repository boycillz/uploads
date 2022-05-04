package io.github.azismihsan.uploads;

public class UploadInfo {

    public String ustad;
    public String location;
    public String title;
    public String description;
    public String masjid;
    public String image;
    public UploadInfo(){

    }
    public UploadInfo(String title,String desc,String mesjid,String location,String ustadz,String url) {
        this.title = title;
        this.description = desc;
        this.location = location;
        this.ustad = ustadz;
        this.masjid = mesjid;
        this.image = url;
    }

    public String getTitle() {
        return title;
    }
    public String getImage() {
        return image;
    }
    public String getUstad(){
        return ustad;
    }
    public String getLocation(){
        return location;
    }
    public String getDescription(){
        return description;
    }
    public String getMasjid(){
        return masjid;
    }
}