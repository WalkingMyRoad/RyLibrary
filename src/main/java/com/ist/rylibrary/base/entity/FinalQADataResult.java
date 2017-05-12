package com.ist.rylibrary.base.entity;

/**
 * Created by minyuchun on 2017/4/11.
 */

public class FinalQADataResult {
    /**唱歌下载路径*/
    private String downloadUrl;
    /**歌曲源或者天气来源*/
    private String sourceName;
    /**歌手*/
    private String singer;
    /**歌名*/
    private String name;
    /**风向*/
    private String wind;
    /**风等级*/
    private String windLevel;
    /**湿度*/
    private String humidity;
    /**空气质量*/
    private String airQuality;
    /**气温*/
    private String tempRange;
    /**时间*/
    private String dateLong;
    /**天气跟新时间*/
    private String lastUpdateTime;
    /**天气状况*/
    private String weather;
    /**pm25值*/
    private String pm25;
    /**天气日期*/
    private String date;
    /**天气城市*/
    private String city;


    public FinalQADataResult() {
    }

    public FinalQADataResult(String downloadUrl, String sourceName, String singer, String name, String wind, String windLevel, String humidity, String airQuality, String tempRange, String dateLong, String lastUpdateTime, String weather, String pm25, String date, String city) {
        this.downloadUrl = downloadUrl;
        this.sourceName = sourceName;
        this.singer = singer;
        this.name = name;
        this.wind = wind;
        this.windLevel = windLevel;
        this.humidity = humidity;
        this.airQuality = airQuality;
        this.tempRange = tempRange;
        this.dateLong = dateLong;
        this.lastUpdateTime = lastUpdateTime;
        this.weather = weather;
        this.pm25 = pm25;
        this.date = date;
        this.city = city;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(String windLevel) {
        this.windLevel = windLevel;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    public String getTempRange() {
        return tempRange;
    }

    public void setTempRange(String tempRange) {
        this.tempRange = tempRange;
    }

    public String getDateLong() {
        return dateLong;
    }

    public void setDateLong(String dateLong) {
        this.dateLong = dateLong;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "FinalQADataResult{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", singer='" + singer + '\'' +
                ", name='" + name + '\'' +
                ", wind='" + wind + '\'' +
                ", windLevel='" + windLevel + '\'' +
                ", humidity='" + humidity + '\'' +
                ", airQuality='" + airQuality + '\'' +
                ", tempRange='" + tempRange + '\'' +
                ", dateLong='" + dateLong + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", weather='" + weather + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", date='" + date + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
