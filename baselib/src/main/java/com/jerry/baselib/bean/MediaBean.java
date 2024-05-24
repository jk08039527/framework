package com.jerry.baselib.bean;

/**
 * @author Jerry
 * @createDate 2019-08-21
 * @description
 */
public class MediaBean implements Comparable<MediaBean> {

    /**
     * 0：图片，1：视频
     */
    private int type;
    private String url;
    private String remoteImg;
    private String modified;
    private boolean hide;
    private boolean selected;

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getRemoteImg() {
        return remoteImg;
    }

    public void setRemoteImg(final String remoteImg) {
        this.remoteImg = remoteImg;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(final String modified) {
        this.modified = modified;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(final boolean hide) {
        this.hide = hide;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    @Override
    public int compareTo(final MediaBean o) {
        return o.getModified().compareTo(modified);
    }
}
