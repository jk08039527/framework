package com.jerry.myframwork.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Jerry
 * @createDate 2025/5/22
 * @copyright www.axiang.com
 * @description
 */
@Entity
public class RecordBean {

    @Id(autoincrement = true)
    private Long id;
    public String cardNum;
    public String cardDate;
    public String cardCvc;
    public String cardAdd;
    public int handleStatus;
    @Generated(hash = 1198292992)
    public RecordBean(Long id, String cardNum, String cardDate, String cardCvc,
            String cardAdd, int handleStatus) {
        this.id = id;
        this.cardNum = cardNum;
        this.cardDate = cardDate;
        this.cardCvc = cardCvc;
        this.cardAdd = cardAdd;
        this.handleStatus = handleStatus;
    }
    @Generated(hash = 96196931)
    public RecordBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCardNum() {
        return this.cardNum;
    }
    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
    public String getCardDate() {
        return this.cardDate;
    }
    public void setCardDate(String cardDate) {
        this.cardDate = cardDate;
    }
    public String getCardCvc() {
        return this.cardCvc;
    }
    public void setCardCvc(String cardCvc) {
        this.cardCvc = cardCvc;
    }
    public String getCardAdd() {
        return this.cardAdd;
    }
    public void setCardAdd(String cardAdd) {
        this.cardAdd = cardAdd;
    }
    public int getHandleStatus() {
        return this.handleStatus;
    }
    public void setHandleStatus(int handleStatus) {
        this.handleStatus = handleStatus;
    }

}
