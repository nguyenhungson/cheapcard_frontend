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
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.WordUtils;
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
        String mainContent = Utils.renderTemplate("Template/topupgame.html", myDic);

        String content = Utils.renderTemplateMasterpage(mainContent, myDic);
        return content;
    }

    private String renderPost(HttpServletRequest req) throws Exception {
        String result = "";

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (pathInfo.equals("/choose_card")) {
            Map<Integer, Item> mapItem = BusinessProcess.getListItem();
            String listCard[] = req.getParameter("listCard").split(",");
            int totalAmount = 0;
            String param = "";
            for (int i = 0; i < listCard.length; i++) {
                if (!listCard[i].equals("")) {
                    String arrItem[] = listCard[i].split("-");
                    if (!arrItem[1].equals("0")) {
                        int itemId = Integer.parseInt(arrItem[0]);
                        int quantity = Integer.parseInt(arrItem[1]);
                        totalAmount += (mapItem.get(itemId).getUnitPrice() * ((100 - mapItem.get(itemId).getDiscountPercent()) / 100) * quantity);
                        param += String.format("%s=%s&%s=%s&", "id" + i, itemId, "q" + i, arrItem[1]);
                    }
                }
            }
            String sig = Utils.encryptMD5(param + "|" + totalAmount + "|" + TGRConfig.gApiCheapCard.getSecret());
            result = "/thanhtoan/banthe?" + param + "t=" + totalAmount + "&sig=" + sig;
        }

        return result;
    }

}
