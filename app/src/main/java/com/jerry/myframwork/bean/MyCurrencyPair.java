package com.jerry.myframwork.bean;

import org.greenrobot.greendao.annotation.Entity;

import io.gate.gateapi.models.CurrencyPair;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author Jerry
 * @createDate 2024/4/13
 * @copyright www.axiang.com
 * @description
 */
@Entity
public class MyCurrencyPair extends CurrencyPair {

    @Id()
    public String id;
    public double new1m;
    public double new10s;
    public double new30s;

    public double bonusPercent = 10;

    public double buyPrice;
    public double sellPrice;

    public long buyTime10s;
    public long buyTime30s;
    public long buyTime1m;
    public long sellTime;

    @Generated(hash = 1919873348)
    public MyCurrencyPair(String id, double new1m, double new10s, double new30s,
        double bonusPercent, double buyPrice, double sellPrice, long buyTime10s,
        long buyTime30s, long buyTime1m, long sellTime) {
        this.id = id;
        this.new1m = new1m;
        this.new10s = new10s;
        this.new30s = new30s;
        this.bonusPercent = bonusPercent;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.buyTime10s = buyTime10s;
        this.buyTime30s = buyTime30s;
        this.buyTime1m = buyTime1m;
        this.sellTime = sellTime;
    }

    @Generated(hash = 618054255)
    public MyCurrencyPair() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getNew1m() {
        return this.new1m;
    }

    public void setNew1m(double new1m) {
        this.new1m = new1m;
    }

    public double getNew10s() {
        return this.new10s;
    }

    public void setNew10s(double new10s) {
        this.new10s = new10s;
    }

    public double getNew30s() {
        return this.new30s;
    }

    public void setNew30s(double new30s) {
        this.new30s = new30s;
    }

    public double getBonusPercent() {
        return this.bonusPercent;
    }

    public void setBonusPercent(double bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    public double getBuyPrice() {
        return this.buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return this.sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public long getBuyTime10s() {
        return this.buyTime10s;
    }

    public void setBuyTime10s(long buyTime10s) {
        this.buyTime10s = buyTime10s;
    }

    public long getBuyTime30s() {
        return this.buyTime30s;
    }

    public void setBuyTime30s(long buyTime30s) {
        this.buyTime30s = buyTime30s;
    }

    public long getBuyTime1m() {
        return this.buyTime1m;
    }

    public void setBuyTime1m(long buyTime1m) {
        this.buyTime1m = buyTime1m;
    }

    public long getSellTime() {
        return this.sellTime;
    }

    public void setSellTime(long sellTime) {
        this.sellTime = sellTime;
    }
}
