/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.controller;

import com.google.gson.Gson;
import cc.frontend.common.BusinessProcess;
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
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class InfoController extends HttpServlet {

    private static Logger logger = Logger.getLogger(InfoController.class);
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String content = this.renderContent(req, resp);
            if (!content.equals("")) {
                Utils.out(content, resp);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String renderContent(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String pathInfo = (req.getPathInfo() == null || req.getPathInfo().equals("")) ? "/banggia.html" : req.getPathInfo();
        TemplateDataDictionary myDic = TemplateDictionary.create();
        String infoTemplate = "";
        if (pathInfo.equals("/banggia.html")) {
            Map<Integer, Item> mapListItem = BusinessProcess.getListItem();
            Map<String, String> mapHtmlPrice = this.renderHtmlPriceList(mapListItem);
            //game list
            myDic.setVariable("vtc_list", mapHtmlPrice.get("vtc").toString());
            myDic.setVariable("zing_list", mapHtmlPrice.get("zing").toString());
            myDic.setVariable("gate_list", mapHtmlPrice.get("gate").toString());
            myDic.setVariable("on_list", mapHtmlPrice.get("ongame").toString());
            myDic.setVariable("bit_list", mapHtmlPrice.get("bit").toString());
            myDic.setVariable("garena_list", mapHtmlPrice.get("garena").toString());
            myDic.setVariable("mobay_list", mapHtmlPrice.get("mobay").toString());
            
            //zingxu
            myDic.setVariable("zingxu_list", mapHtmlPrice.get("zxu").toString());
            
            //mobile
            myDic.setVariable("mobifone_list", mapHtmlPrice.get("mobifone").toString());
            myDic.setVariable("vinaphone_list", mapHtmlPrice.get("vinaphone").toString());
            myDic.setVariable("viettel_list", mapHtmlPrice.get("viettel").toString());
            myDic.setVariable("vietnamobile_list", mapHtmlPrice.get("vietnamobile").toString());
            myDic.setVariable("gmobile_list", mapHtmlPrice.get("gmobile").toString());
            
            infoTemplate = Utils.renderTemplate("Template/pricelist.html", myDic);
        } else {
            infoTemplate = Utils.render404Page(myDic);
        }

        String content = Utils.renderTemplateMasterpage(infoTemplate, myDic);

        return content;
    }
    
    private Map<String, String> renderHtmlPriceList(Map<Integer, Item> mapListItem) throws Exception {
        Iterator it = mapListItem.entrySet().iterator();
        Map<String, String> mapListPrice = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry<Integer, Item> pair = (Map.Entry) it.next();

            String htmlItem = mapListPrice.get(pair.getValue().getSupplier()) == null ? "" : mapListPrice.get(pair.getValue().getSupplier());
            htmlItem += "<tr>"
                    + "<td>" + Utils.formatNumber(pair.getValue().getUnitPrice()) + "</td>"
                    + "<td>" + Utils.formatNumber(pair.getValue().getDiscountAmount()) + "</td>"
                    + "</tr>";

            mapListPrice.put(pair.getValue().getSupplier(), htmlItem);
        }

        return mapListPrice;
    }

}
