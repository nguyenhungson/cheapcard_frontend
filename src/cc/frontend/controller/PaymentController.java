/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.controller;

import cc.frontend.callapi.APISale;
import cc.frontend.common.BusinessProcess;
import cc.frontend.common.TGRConfig;
import com.google.gson.Gson;
import cc.frontend.common.Utils;
import cc.frontend.entity.Bank;
import cc.frontend.entity.Item;
import cc.frontend.entity.OrderCreateResp;
import cc.frontend.entity.OrderDetail;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class PaymentController extends HttpServlet {

    private static Logger logger = Logger.getLogger(PaymentController.class);
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String content = this.renderHtml(req);
            if (!content.equals("")) {
                Utils.out(content, resp);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String content = this.renderPost(req);
            if (!content.equals("")) {
                Utils.out(content, resp);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private String renderHtml(HttpServletRequest req) throws Exception {
        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        TemplateDataDictionary myDic = TemplateDictionary.create();
        String mainContent = "";
        if (pathInfo.equals("/banthe") || pathInfo.equals("/naptiengame")) {
            if(this.checkURL(req, pathInfo) == 1){
                if (req.getParameter("t") != null) {
                    String totalAmount = Utils.formatNumber(Integer.parseInt(req.getParameter("t")));
                    myDic.setVariable("total_amount", totalAmount);
                }
                myDic.setVariable("list_bank", this.renderListBank());
                mainContent = Utils.renderTemplate("Template/payment.html", myDic);
            }
            else{
                mainContent = Utils.render404Page(myDic);
            }
        } else if (pathInfo.equals("/hoanthanh")) {
            if(this.checkURL(req, pathInfo) == 1){
                String transId = req.getParameter("transid");
                String amount = req.getParameter("amount");
                String rescode = req.getParameter("rescode");
                String type = req.getParameter("type");
                String status = req.getParameter("status");
                
                if(type.equals("zx")){
                    myDic.setVariable("title", "Nạp tiền ZingXu");
                }
                else{
                    myDic.setVariable("title", "Mua mã thẻ");
                }
                
                String info = "<strong style=\"color:red\">Giao dịch thất bại</strong>";
                if(rescode.equals("1") && status.equals("1")){
                    info = "<strong style=\"color:green\">Giao dịch thành công</strong>";
                    if(type.equals("card")){
                        myDic.showSection("CARD");
                    }
                }
                
                myDic.setVariable("status", info);
                myDic.setVariable("order_no", transId);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                myDic.setVariable("date_trans", dateFormat.format(Calendar.getInstance().getTime()));
                myDic.setVariable("total_amount", Utils.formatNumber(Integer.parseInt(amount)));
                mainContent = Utils.renderTemplate("Template/payment_finish.html", myDic);
            }
            else{
                mainContent = Utils.render404Page(myDic);
            }
        } else {
            mainContent = Utils.render404Page(myDic);
        }

        String content = Utils.renderTemplateMasterpage(mainContent, myDic);
        return content;
    }

    private int checkURL(HttpServletRequest req, String pathInfo) {
        int result = 0;
        if (pathInfo.equals("/naptiengame")) {
            String id = req.getParameter("id");
            String account = req.getParameter("acc");
            String quantity = req.getParameter("q");
            String total = req.getParameter("t");
            String reqSig = req.getParameter("sig");
            String param = String.format("id=%s&acc=%s&q=%s&", id, account, quantity);
            String sig = Utils.encryptMD5(param + "|" + total + "|" + TGRConfig.gApiCheapCard.getSecret());
            if(sig.equals(reqSig)){
                result = 1;
            }
        }
        else if(pathInfo.equals("/banthe")){
            Map<String, String[]> mapParam = req.getParameterMap();
            int cardIndex = 0;
            String cardIdKey = "id0";
            String param = "";
            while(mapParam.get(cardIdKey) != null){
                String quantityKey = "q" + cardIndex;
                String id = mapParam.get(cardIdKey)[0];
                String quantity = mapParam.get(quantityKey)[0];
                param += String.format("%s=%s&%s=%s&", cardIdKey, id, quantityKey, quantity);
                cardIndex++;
                cardIdKey = "id" + cardIndex;
            }
            String totalAmount = req.getParameter("t");
            String reqSig = req.getParameter("sig");
            String sig = Utils.encryptMD5(param + "|" + totalAmount + "|" + TGRConfig.gApiCheapCard.getSecret());
            if(sig.equals(reqSig)){
                result = 1;
            }
        }
        else if(pathInfo.equals("/hoanthanh")){
            String transId = req.getParameter("transid");
            String amount = req.getParameter("amount");
            String rescode = req.getParameter("rescode");
            String type = req.getParameter("type");
            String sig = req.getParameter("secure");
            String sigValidate = Utils.encryptSHA1(transId + "|" + amount + "|" + rescode + "|" + type + "|" + TGRConfig.gApiCheapCard.getSecret());
            if(sig.equals(sigValidate)){
                result = 1;
            }
        }

        return result;
    }

    private String renderPost(HttpServletRequest req) throws Exception {
        String result = "";

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (pathInfo.equals("/accept_payment")) {
            String data = req.getParameter("data");
            String bankCode = req.getParameter("bank");
            String arrParam[] = data.split("\\?|\\&");
            Map mapData = new HashMap();
            for (String item : arrParam) {
                if(!item.equals("")){
                    String arrItem[] = item.split("=");
                    mapData.put(arrItem[0], arrItem[1]);
                }
            }

            int bankId = 0;
            long totalAmount = Long.parseLong(mapData.get("t").toString());
            String buyer = "";
            List<OrderDetail> listDetail = new ArrayList<OrderDetail>();
            String ip = Utils.getClientIP(req);

            if (mapData.get("acc") != null) {
                buyer = mapData.get("acc").toString();
                OrderDetail detail = new OrderDetail();
                detail.setItemId(Integer.parseInt(mapData.get("id").toString()));
                detail.setQuantity(Integer.parseInt(mapData.get("q").toString()));
                detail.setAmount(totalAmount);
                listDetail.add(detail);
            }
            else{
                buyer = "";
                String cardIdKey = "id0";
                int cardIndex = 0;
                Map<Integer,Item> mapItem = BusinessProcess.getListItem();
                while(mapData.get(cardIdKey) != null){
                    OrderDetail detail = new OrderDetail();
                    String quantityKey = "q" + cardIndex;
                    int cardId = Integer.parseInt(mapData.get(cardIdKey).toString());
                    int quantity = Integer.parseInt(mapData.get(quantityKey).toString());
                    int amount = (int) (mapItem.get(cardId).getUnitPrice() * ((100 - mapItem.get(cardId).getDiscountPercent()) / 100) * quantity);
                    
                    detail.setItemId(cardId);
                    detail.setQuantity(quantity);
                    detail.setAmount(amount);
                    listDetail.add(detail);
                    
                    cardIndex++;
                    cardIdKey = "id" + cardIndex;
                }
            }
            
            String strResult = APISale.createOrder(bankId, totalAmount, "", "", "", "", listDetail, bankCode, ip, buyer);
            OrderCreateResp resp = gson.fromJson(strResult, OrderCreateResp.class);
            if(resp.getCode() == 1){
                result = resp.getData().getUrl();
            }
        }

        return result;
    }

    private String renderListBank() throws Exception {
        String html = "";
        List<Bank> listBank = Utils.getListBank();
        for (Bank item : listBank) {
            html += "<li onclick=\"chooseBank(this);\" title=\"" + item.getBankName() + "\" data-bank=\"" + item.getBankCode() + "\">"
                    + "<a href=\"javascript:;\"><span class=\"sprtbank logobank " + item.getImageFile() + "\"></span></a>"
                    + "</li>";
        }

        return html;
    }
}
