/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.callapi;

import com.google.gson.Gson;
import cc.frontend.common.TGRConfig;
import cc.frontend.common.Utils;
import cc.frontend.entity.OrderCreateReq;
import cc.frontend.entity.OrderDetail;
import cc.frontend.entity.OrderStatusReq;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class APISale {

    public static int PAGESIZE = 10;
    private static Logger logger = Logger.getLogger(APISale.class);
    private static Gson gson = new Gson();

    public static String getListItem() throws Exception {
        String url = TGRConfig.gApiCheapCard.getUrl() + "getitemlst";
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String sig = Utils.encryptSHA256(time + TGRConfig.secretSplit + "0" + TGRConfig.secretSplit + TGRConfig.gApiCheapCard.getSecret());

        String paramKey[] = {"time", "type", "sig"};
        String paramValue[] = {time, "0", sig};

        String result = Utils.callAPIRestObject(url, paramKey, paramValue);

        return result;
    }

    public static String getListBank() throws Exception {
        String url = TGRConfig.gApiCheapCard.getUrl() + "getbanklst";
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String sig = Utils.encryptSHA256(time + TGRConfig.secretSplit + TGRConfig.gApiCheapCard.getSecret());

        String paramKey[] = {"time", "type", "sig"};
        String paramValue[] = {time, "0", sig};

        String result = Utils.callAPIRestObject(url, paramKey, paramValue);

        return result;
    }

    public static String createOrder(int bankId, long totalAmount, String email, String fullName, String idNumber,
            String mobile, List<OrderDetail> detail, String bankCode, String ip, String buyer) throws Exception {
        String url = TGRConfig.gApiCheapCard.getUrl() + "createorder";
        long time = Calendar.getInstance().getTimeInMillis();
        String sig = Utils.encryptSHA256(String.valueOf(bankId) + TGRConfig.secretSplit + String.valueOf(totalAmount)
                + TGRConfig.secretSplit + time + TGRConfig.secretSplit + ip + TGRConfig.secretSplit
                + bankCode + TGRConfig.secretSplit + TGRConfig.gApiCheapCard.getSecret());

        OrderCreateReq req = new OrderCreateReq();
        req.setBankCode(bankCode);
        req.setBankId(bankId);
        req.setBuyer(buyer);
        req.setDetails(detail);
        req.setEmail(email);
        req.setFullName(fullName);
        req.setIdNumber(idNumber);
        req.setIp(ip);
        req.setMobile(mobile);
        req.setSig(sig);
        req.setTime(time);
        req.setTotalAmount(totalAmount);
        String result = Utils.callAPIRestObject(url, req);

        return result;
    }

    public static String checkStatus(String orderNo, int isGetCard) throws Exception{
        String url = TGRConfig.gApiCheapCard.getUrl() + "checkorderstatus";
        long time = Calendar.getInstance().getTimeInMillis();
        String sig = Utils.encryptSHA256(orderNo + TGRConfig.secretSplit + isGetCard + TGRConfig.secretSplit + time 
                + TGRConfig.secretSplit + TGRConfig.gApiCheapCard.getSecret());
        
        OrderStatusReq req = new OrderStatusReq();
        req.setOrderNo(orderNo);
        req.setIsGetCard(isGetCard);
        req.setTime(time);
        req.setSig(sig);
        
        String result = Utils.callAPIRestObject(url, req);

        return result;
    }

}
