/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.entity;

import java.util.List;

/**
 *
 * @author viettd
 */
public class ResponseItem {

    private int code;
    private String msg;
    private List<Item> data;

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

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }

}
