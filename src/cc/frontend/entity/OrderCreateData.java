/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.entity;

import java.util.List;

/**
 *
 * @author viettd
 */
public class OrderCreateData {
    private String orderNo;
    private String url;
    private List<OrderDetail> lstOFS;

    public List<OrderDetail> getLstOFS() {
        return lstOFS;
    }

    public void setLstOFS(List<OrderDetail> lstOFS) {
        this.lstOFS = lstOFS;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
