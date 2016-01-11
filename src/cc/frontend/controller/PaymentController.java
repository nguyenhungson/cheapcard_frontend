/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.controller;

import cc.frontend.callapi.APISale;
import cc.frontend.common.TGRConfig;
import com.google.gson.Gson;
import cc.frontend.common.Utils;
import cc.frontend.entity.Bank;
import cc.frontend.entity.OrderCreateResp;
import cc.frontend.entity.OrderDetail;
import cc.frontend.entity.ResponseBank;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.io.IOException;
import java.util.ArrayList;
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
            if (req.getParameter("t") != null) {
                String totalAmount = Utils.formatNumber(Integer.parseInt(req.getParameter("t")));
                myDic.setVariable("total_amount", totalAmount);
            }
            myDic.setVariable("list_bank", this.renderListBank());
            mainContent = Utils.renderTemplate("Template/payment.html", myDic);
        } else if (pathInfo.equals("/hoanthanh")) {
            mainContent = Utils.renderTemplate("Template/payment_finish.html", myDic);
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
            
        }
        else if(pathInfo.equals("/hoanthanh")){
            
        }

        return result;
    }

    private String renderPost(HttpServletRequest req) throws Exception {
        String result = "";

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (pathInfo.equals("/accept_payment")) {
            String data = req.getParameter("data");
            String bankName = req.getParameter("bank");
            String arrParam[] = data.split("\\?|\\&");
            Map mapData = new HashMap();
            for (String item : arrParam) {
                if(!item.equals("")){
                    String arrItem[] = item.split("=");
                    mapData.put(arrItem[0], arrItem[1]);
                }
            }

            int bankId = 0;
            String bankCode = "";
            long totalAmount = Long.parseLong(mapData.get("t").toString());
            String buyer = "";
            List<OrderDetail> listDetail = new ArrayList<OrderDetail>();
            String ip = Utils.getClientIP(req);

            List<Bank> listBank = Utils.getListBank();
            for (Bank item : listBank) {
                if (item.getBankName().equals(bankName)) {
                    bankId = item.getId();
                    bankCode = item.getBankCode();
                    break;
                }
            }

            if (mapData.get("acc") != null) {
                buyer = mapData.get("acc").toString();
                OrderDetail detail = new OrderDetail();
                detail.setItemId(Integer.parseInt(mapData.get("id").toString()));
                detail.setQuantity(Integer.parseInt(mapData.get("q").toString()));
                detail.setAmount(totalAmount);
                listDetail.add(detail);
            }
            else{
                
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
