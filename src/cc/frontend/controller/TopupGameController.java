/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.controller;

import com.google.gson.Gson;
import cc.frontend.common.BusinessProcess;
import cc.frontend.common.TGRConfig;
import cc.frontend.common.Utils;
import cc.frontend.entity.Item;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
public class TopupGameController extends HttpServlet {

    private static Logger logger = Logger.getLogger(TopupGameController.class);
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String content = this.renderHtml(req, resp);
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

    private String renderHtml(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        TemplateDataDictionary myDic = TemplateDictionary.create();
        List<Item> listItem = this.getZXInfo();
        //myDic.setVariable("zx_price", String.valueOf(itemZx.getDiscountPercent()));
        myDic.setVariable("list_price_zx", BusinessProcess.renderListPriceZX());
        myDic.setVariable("total_amount", Utils.formatNumber(listItem.get(0).getDiscountAmount()));
        myDic.setVariable("list_zx", this.renderListZX(listItem));
        String mainContent = Utils.renderTemplate("Template/topupgame.html", myDic);

        String mobileTop = "<a class=\"mmenu\" href=\"/\"><span class=\"msprt micoback\"></span> Nạp tiền game</a>";
        myDic.setVariable("mobile_top", mobileTop);

        String content = Utils.renderTemplateMasterpage(mainContent, myDic);
        return content;
    }

    private String renderPost(HttpServletRequest req) throws Exception {
        String result = "";

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (pathInfo.equals("/accept_topup")) {
            String accountName = req.getParameter("accountName");
            String totalAmount = req.getParameter("amount");
            String id = req.getParameter("id");
            String param = String.format("id=%s&acc=%s&", id, accountName);
            String sig = Utils.encryptMD5(param + "|" + totalAmount + "|" + TGRConfig.gApiCheapCard.getSecret());
            result = "/thanhtoan/naptiengame?" + param + "t=" + totalAmount + "&sig=" + sig;
        }

        return result;
    }

    private List<Item> getZXInfo() throws Exception {
        Map<Integer, Item> mapItem = BusinessProcess.getListItem();
        List<Item> item = new ArrayList<Item>();
        if (mapItem != null) {
            Iterator it = mapItem.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
                if (pair.getValue().getSupplier().equals("zxu")) {
                    item.add(mapItem.get(pair.getKey()));
                }
            }
        }

        return item;
    }

    private String renderListZX(List<Item> listItem) {
        String html = "";

        for (int i = 0; i < listItem.size(); i++) {
            html += "<li onclick=\"choosePriceRadio(this);\">"
                    + "<span class=\"zpsprt icncheck\"></span>"
                    + "<input type=\"radio\" name=\"radiolist\" id=\"r" + i + "\" value=\"" + listItem.get(i).getDiscountAmount() + "|" + listItem.get(i).getId() + "\">"
                    + "<label for=\"r" + i + "\"><span class=\"radio\"></span>" + Utils.formatNumber(listItem.get(i).getUnitPrice()) + "</label></li>";
        }

        return html;
    }

}
