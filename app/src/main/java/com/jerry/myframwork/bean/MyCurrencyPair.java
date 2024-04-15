package com.jerry.myframwork.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import io.gate.gateapi.models.CurrencyPair;
import org.greenrobot.greendao.annotation.Generated;

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
    public double newPrice;
    public double bonusPercent = 10;
    public double buyPrice;
    public double sellPrice;
    public long buyTime;
    public long sellTime;
    @Generated(hash = 1796931030)
    public MyCurrencyPair(String id, double newPrice, double bonusPercent,
            double buyPrice, double sellPrice, long buyTime, long sellTime) {
        this.id = id;
        this.newPrice = newPrice;
        this.bonusPercent = bonusPercent;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.buyTime = buyTime;
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
    public double getNewPrice() {
        return this.newPrice;
    }
    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
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
    public long getBuyTime() {
        return this.buyTime;
    }
    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }
    public long getSellTime() {
        return this.sellTime;
    }
    public void setSellTime(long sellTime) {
        this.sellTime = sellTime;
    }
}
