package com.jerry.baselib.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author Jerry
 * @createDate 2023/9/22
 * @description
 */
@Entity
public class TestBean {
    @Id(autoincrement = true)
    private Long id;
    private String text;
    @Generated(hash = 279403534)
    public TestBean(Long id, String text) {
        this.id = id;
        this.text = text;
    }
    @Generated(hash = 2087637710)
    public TestBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
