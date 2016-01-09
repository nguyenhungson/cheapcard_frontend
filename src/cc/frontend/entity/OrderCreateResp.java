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
public class OrderCreateResp {

        private int code;
        private String msg;
        private OrderCreateData data;

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

        public OrderCreateData getData() {
            return data;
        }

        public void setData(OrderCreateData data) {
            this.data = data;
        }

}
