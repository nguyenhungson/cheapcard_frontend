/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.entity;

/**
 *
 * @author viettd
 */
public class ResponseCheckStatus {

    private int code;
    private String msg;
    private OrderStatusResp data;

    public OrderStatusResp getData() {
        return data;
    }

    public void setData(OrderStatusResp data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

      public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
