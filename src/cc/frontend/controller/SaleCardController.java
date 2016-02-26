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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class SaleCardController extends HttpServlet {

    private static Logger logger = Logger.getLogger(SaleCardController.class);
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
        String pathInfo = req.getPathInfo() != null ? req.getPathInfo() : "";
        String supplier = pathInfo.substring(1);
        Cookie cookie = new Cookie("supplier", supplier);
        cookie.setMaxAge(60 * 20);
        cookie.setPath("/");
        resp.addCookie(cookie);

        Map<Integer, Item> mapListItem = BusinessProcess.getListItem();
        Iterator it = mapListItem.entrySet().iterator();
        int typeId = 0;
        while (it.hasNext()) {
            Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
            if (pair.getValue().getSupplier().equals(supplier)) {
                typeId = pair.getValue().getTypeId();
                break;
            }
        }

        TemplateDataDictionary myDic = TemplateDictionary.create();
        myDic.setVariable("card_name", WordUtils.capitalize(pathInfo.substring(1)));
        myDic.setVariable("list_card", this.renderListItem(pathInfo, mapListItem));
        myDic.setVariable("list_card_mobile", this.renderHtmlMobile(pathInfo, mapListItem));
        myDic.setVariable("list_price_card", BusinessProcess.renderListPriceCard(mapListItem, typeId, supplier));
        if (typeId == 2) {
            myDic.showSection("CARD_PRICE");
        } else {
            myDic.showSection("GAME_PRICE");
        }
        myDic.setVariable(supplier + "_current", "class=\"current\"");
        String mainContent = Utils.renderTemplate("Template/salecard.html", myDic);

        String linkTitle = "/?m=" + typeId;
        String strTitle = "điện thoại";
        if (typeId == 1) {
            strTitle = "game";
        }
        String mobileTop = "<a class=\"mmenu\" href=\"" + linkTitle + "\"><span class=\"msprt micoback\"></span> Mua thẻ " + strTitle + "</a>";
        myDic.setVariable("mobile_top", mobileTop);

        String content = Utils.renderTemplateMasterpage(mainContent, myDic);
        return content;
    }

    private String renderHtmlMobile(String pathInfo, Map<Integer, Item> mapItem) {
        String html = "";
        if (mapItem != null) {
            Iterator it = mapItem.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
                String cardType = pathInfo.substring(1);
                if (cardType.equals(pair.getValue().getSupplier())) {
                    html += "<div class=\"rowcard clearfix\">"
                            + "<input type=\"hidden\" value=\"" + pair.getValue().getId() + "\" />"
                            + "<div class=\"boxcard fl\" style=\"display: block\">"
                            + "<a class=\"imgcard card" + pair.getValue().getUnitPrice() / 1000 + "k\" href=\"javascript:;\" onclick=\"chooseCard(this, 1, 0, 'mobile');\">"
                            + "<span class=\"msprt micocard list " + pair.getValue().getSupplier() + "\"></span>"
                            + "<em>" + Utils.formatNumber(pair.getValue().getUnitPrice()) + " <i>VNĐ</i></em>"
                            + "</a>"
                            + "</div>"
                            + "<div class=\"coldecrease fl\">"
                            + "<a href=\"javascript:;\" onclick=\"decreaseQuantity(this, 'mobile');\"><span class=\"sprt icodecrease\"></span></a>"
                            + "</div>"
                            + "<div class=\"colinput fl\"><input type=\"text\" value=\"0\" placeholder=\"0\" onblur=\"chooseCard(this, 0, 0, 'mobile');\"></div>"
                            + "<div class=\"colincrease fl\"><a href=\"javascript:;\" onclick=\"increaseQuantity(this, 'mobile');\"><span class=\"sprt icoincrease\"></span></a></div>"
                            + "<div class=\"colmoney fl\"><label>0</label> <span>VNĐ</span><input type=\"hidden\" value=\"" + pair.getValue().getDiscountAmount() + "\"></div>"
                            + "</div>";
                    /*"<tr>"
                            + "<td class=\"boxcard\">"
                            + "<input type=\"hidden\" value=\"" + pair.getValue().getId() + "\" />"
                            + "<a class=\"imgcard card" + pair.getValue().getUnitPrice() / 1000 + "k\" href=\"javascript:;\" onclick=\"chooseCard(this, 1, 0);\">"
                            + "<span class=\"sprtcard logocard " + pair.getValue().getSupplier() + "\"></span>"
                            + "<em>" + Utils.formatNumber(pair.getValue().getUnitPrice()) + " <i>VNĐ</i></em>"
                            + "</a>"
                            + "</td>"
                            + "<td class=\"coldecrease\"><a href=\"javascript:;\" onclick=\"decreaseQuantity(this);\"><span class=\"sprt icodecrease\"></span></a></td>"
                            + "<td class=\"colinput\"><input type=\"text\" value=\"0\" placeholder=\"0\" maxlength=\"3\" onblur=\"chooseCard(this, 0, 0);\"></td>"
                            + "<td class=\"colincrease\"><a href=\"javascript:;\" onclick=\"increaseQuantity(this);\"><span class=\"sprt icoincrease\"></span></a></td>"
                            + "<td class=\"colmoney\"><label>0</label> <span>VNĐ</span><input type=\"hidden\" value=\"" + pair.getValue().getDiscountAmount() + "\"></td>"
                            + "</tr>";*/
                }
            }
        }

        return html;
    }

    private String renderPost(HttpServletRequest req) throws Exception {
        String result = "";

        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        if (pathInfo.equals("/choose_card")) {
            Map<Integer, Item> mapItem = BusinessProcess.getListItem();
            String listCard[] = req.getParameter("listCard").split(",");
            int totalAmount = 0;
            String param = "";
            int cardIndex = 0;
            for (int i = 0; i < listCard.length; i++) {
                if (!listCard[i].equals("")) {
                    String arrItem[] = listCard[i].split("-");
                    if (!arrItem[1].equals("0")) {
                        int itemId = Integer.parseInt(arrItem[0]);
                        int quantity = Integer.parseInt(arrItem[1]);
                        totalAmount += mapItem.get(itemId).getDiscountAmount() * quantity;
                        param += String.format("%s=%s&%s=%s&", "id" + cardIndex, itemId, "q" + cardIndex, arrItem[1]);
                        cardIndex++;
                    }
                }
            }
            String sig = Utils.encryptMD5(param + "|" + totalAmount + "|" + TGRConfig.gApiCheapCard.getSecret());
            result = "/thanhtoan/banthe?" + param + "t=" + totalAmount + "&sig=" + sig;
        }

        return result;
    }

    private String renderListItem(String pathInfo, Map<Integer, Item> mapItem) throws Exception {
        String html = "";
        if (mapItem != null) {
            Iterator it = mapItem.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
                String cardType = pathInfo.substring(1);
                if (cardType.equals(pair.getValue().getSupplier())) {
                    html += "<tr>"
                            + "<td class=\"boxcard\">"
                            + "<input type=\"hidden\" value=\"" + pair.getValue().getId() + "\" />"
                            + "<a class=\"imgcard card" + pair.getValue().getUnitPrice() / 1000 + "k\" href=\"javascript:;\" onclick=\"chooseCard(this, 1, 0);\">"
                            + "<span class=\"sprtcard logocard " + pair.getValue().getSupplier() + "\"></span>"
                            + "<em>" + Utils.formatNumber(pair.getValue().getUnitPrice()) + " <i>VNĐ</i></em>"
                            + "</a>"
                            + "</td>"
                            + "<td class=\"coldecrease\"><a href=\"javascript:;\" onclick=\"decreaseQuantity(this);\"><span class=\"sprt icodecrease\"></span></a></td>"
                            + "<td class=\"colinput\"><input type=\"text\" value=\"0\" placeholder=\"0\" maxlength=\"3\" onblur=\"chooseCard(this, 0, 0);\"></td>"
                            + "<td class=\"colincrease\"><a href=\"javascript:;\" onclick=\"increaseQuantity(this);\"><span class=\"sprt icoincrease\"></span></a></td>"
                            + "<td class=\"colmoney\"><label>0</label> <span>VNĐ</span><input type=\"hidden\" value=\"" + pair.getValue().getDiscountAmount() + "\"></td>"
                            + "</tr>";
                }
            }
        }

        return html;
    }

}
