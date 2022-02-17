package com.zaaach.citypicker.model;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author Bro0cL on 2016/1/26.
 */
public class City {
    private String name;
    private String province;
    private String pinyin;
    private String cityCode;//区号
    private String adCode;//城市编号 410185
    private String lat;//维度
    private String lng;//精度

    public City(String name, String province, String pinyin, String cityCode) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.cityCode = cityCode;
    }

    public City(String name, String province, String pinyin, String cityCode, String adCode, String lat, String lng) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.cityCode = cityCode;
        this.adCode = adCode;
        this.lat = lat;
        this.lng = lng;
    }

    public City() {
    }

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    public String getSection() {
        if (TextUtils.isEmpty(pinyin)) {
            return "#";
        } else {
            String c = pinyin.substring(0, 1);
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(c);
            if (m.matches()) {
                return c.toUpperCase();
            }
            //在添加定位和热门数据时设置的section就是‘定’、’热‘开头
            else if (TextUtils.equals(c, "热"))
                return pinyin;
            else if (TextUtils.equals(c, "定"))
                return "自动定位";
//                return pinyin;
            else
                return "#";
        }
    }

    public String getAdCode() {
        return adCode == null ? "" : adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getLat() {
        return lat == null ? "" : lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng == null ? "" : lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
