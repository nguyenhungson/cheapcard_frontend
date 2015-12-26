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
import org.apache.commons.lang.WordUtils;
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
        TemplateDataDictionary myDic = TemplateDictionary.create();
        String mainContent = Utils.renderTemplate("Template/payment.html", myDic);

        String content = Utils.renderTemplateMasterpage(mainContent, myDic);
        return content;
    }

    private String renderPost(HttpServletRequest req) throws Exception {
        String result = "";

        return result;
    }

    /*private String renderListItem(String pathInfo) throws Exception {
        Map<Integer, Item> mapItem = BusinessProcess.getListItem();
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
                            + "<a class=\"imgcard\" href=\"javascript:;\" onclick=\"chooseCard(this, 1, 0);\"><img src=\"" + TGRConfig.gStaticURL + "/images/cards/" + pair.getValue().getImageFile() + ".jpg\" alt=\"\"> </a>"
                            + "</td>"
                            + "<td class=\"coldecrease\"><a href=\"javascript:;\" onclick=\"decreaseQuantity(this);\"><span class=\"sprt icodecrease\"></span></a></td>"
                            + "<td class=\"colinput\"><input type=\"text\" value=\"0\" placeholder=\"0\" maxlength=\"3\" onblur=\"chooseCard(this, 0, 0);\"></td>"
                            + "<td class=\"colincrease\"><a href=\"javascript:;\" onclick=\"increaseQuantity(this);\"><span class=\"sprt icoincrease\"></span></a></td>"
                            + "<td class=\"colmoney\"><label>0</label> <span>VNĐ</span><input type=\"hidden\" value=\"" + pair.getValue().getUnitPrice() * ((100 - pair.getValue().getDiscountPercent()) / 100) + "\"></td>"
                            + "</tr>";
                }
            }
        }

        return html;
    }*/

}
