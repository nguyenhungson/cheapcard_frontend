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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
            //if (BusinessProcess.checkLogin(req) == 1) {
            String content = this.renderContent(req, resp);
            if (!content.equals("")) {
                Utils.out(content, resp);
            }
            /*} else {
             Utils.urlRedirect(resp, "/logout");
             }*/
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String renderContent(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String pathInfo = req.getPathInfo() == null ? "/" : req.getPathInfo();
        String content = "test";

        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (isMultipart == true) {

        } else if (req.getParameter("respType") != null && req.getParameter("respType").equals("json")) {
            content = this.actionResultAjax(req);
        } else {
            content = this.renderHtml(req);
        }

        return content;
    }

    private String renderHtml(HttpServletRequest req) throws Exception {
        String pathInfo = req.getPathInfo() != null ? req.getPathInfo() : "";
        TemplateDataDictionary myDic = TemplateDictionary.create();

        myDic.setVariable("card_name", WordUtils.capitalize(pathInfo.substring(1)));
        myDic.setVariable("list_card", this.renderListItem(pathInfo));
        String mainContent = Utils.renderTemplate("Template/salecard.html", myDic);

        String content = Utils.renderTemplateMasterpage(mainContent, myDic);
        return content;
    }

    private String actionResultAjax(HttpServletRequest req) throws Exception {
        String result = "";

        String reqFunc = req.getParameter("reqFunc");
        if (reqFunc.equals("report_general")) {

        }

        return result;
    }

    private String renderListItem(String pathInfo) throws Exception {
        Map<Integer, Item> mapItem = BusinessProcess.getListItem();
        String html = "";
        if (mapItem != null) {
            Iterator it = mapItem.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
                String cardType = pathInfo.substring(1);
                if(cardType.equals(pair.getValue().getSupplier())){
                    html += "<tr>"
                            + "<td class=\"boxcard\">"
                            + "<a class=\"imgcard\" href=\"#\"><img src=\"" + TGRConfig.gStaticURL + "/images/cards/" + pair.getValue().getImageFile() + ".jpg\" alt=\"\"> </a>"
                            + "</td>"
                            + "<td class=\"coldecrease\"><a href=\"#\"><span class=\"sprt icodecrease\"></span></a></td>"
                            + "<td class=\"colinput\"><input type=\"text\" value=\"0\" placeholder=\"0\" maxlength=\"3\"></td>"
                            + "<td class=\"colincrease\"><a href=\"#\"><span class=\"sprt icoincrease\"></span></a></td>"
                            + "<td class=\"colmoney\">0 <span>VNĐ</span></td>"
                            + "</tr>";
                }
            }
        }

        return html;
    }

}
