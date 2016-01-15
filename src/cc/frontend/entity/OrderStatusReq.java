/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.entity;

/**
 *
 * @author sonnh4
 */
public class OrderStatusReq {
    
    private String orderNo;
    private int isGetCard;
    private long time;
    private String sig;

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getIsGetCard() {
        return isGetCard;
    }

    public void setIsGetCard(int isGetCard) {
        this.isGetCard = isGetCard;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    
}
