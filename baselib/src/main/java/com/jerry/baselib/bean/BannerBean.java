package com.jerry.baselib.bean;

/**
 * Created by wzl on 2018/10/24.
 *
 * @Description
 */
public class BannerBean {

    private String cover;
    private String url;
    private String title;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
