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
import java.util.Iterator;
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
        TemplateDataDictionary myDic = TemplateDictionary.create();
        Item itemZx = this.getZXInfo();
        myDic.setVariable("zx_price", String.valueOf(itemZx.getDiscountPercent()));
        myDic.setVariable("total_amount", Utils.formatNumber((100 - itemZx.getDiscountPercent()) * 500));
        myDic.setVariable("list_price_zx", this.renderListPriceZX());
        String mainContent = Utils.renderTemplate("Template/topupgame.html", myDic);

        String content = Utils.renderTemplateMasterpage(mainContent, myDic);
        return content;
    }

    private String renderListPriceZX() throws Exception {
        String html = "";
        int indexNo = 0;
        Map<Integer, Item> mapListItem = BusinessProcess.getListItem();
        Iterator it = mapListItem.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
            if (pair.getValue().getSupplier().equals("zxu")) {
                String className = " class=\"bggreytbl\"";
                if (indexNo % 2 == 0) {
                    className = " class=\"bgwhitetbl\"";
                }

                html += "<tr" + className + ">"
                        + "<td>" + Utils.formatNumber(pair.getValue().getUnitPrice()) + "</td>"
                        + "<td>" + Utils.formatNumber(pair.getValue().getUnitPrice() * (100 - pair.getValue().getDiscountPercent()) / 100) + "</td>"
                        + "</tr>";
                indexNo++;
            }
        }

        return html;
    }

    private String renderPost(HttpServletRequest req) throws Exception {
        String result = "";

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (pathInfo.equals("/accept_topup")) {
            Item itemZx = this.getZXInfo();
            String accountName = req.getParameter("accountName");
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            int totalAmount = (int) (quantity * (100 - itemZx.getDiscountPercent()));
            String param = String.format("id=%s&acc=%s&q=%s&", itemZx.getId(), accountName, quantity);
            String sig = Utils.encryptMD5(param + "|" + totalAmount + "|" + TGRConfig.gApiCheapCard.getSecret());
            result = "/thanhtoan/naptiengame?" + param + "t=" + totalAmount + "&sig=" + sig;
        }

        return result;
    }

    private Item getZXInfo() throws Exception {
        Map<Integer, Item> mapItem = BusinessProcess.getListItem();
        Item item = null;
        if (mapItem != null) {
            Iterator it = mapItem.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
                if (pair.getValue().getSupplier().equals("zxu")) {
                    item = mapItem.get(pair.getKey());
                    break;
                }
            }
        }

        return item;
    }

}
