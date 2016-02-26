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
public class IndexController extends HttpServlet {

    private static Logger logger = Logger.getLogger(IndexController.class);
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
        TemplateDataDictionary myDic = TemplateDictionary.create();
        Map<Integer, String> mapHTML = this.renderListItem();//1-the game,2-the dien thoai,3-nap tien game,4-nap tien dien thoai
        
        String mobileTop = "<h1 class=\"msprt mlogo\"><a href=\"#\" title=\"\">Thegiare.vn</a></h1>"
                + "<a class=\"mmenu\" href=\"#\"><span class=\"msprt micomenu\"></span></a>";
        
        if (mapHTML.get(2) != null && !mapHTML.get(2).equals("")) {
            myDic.setVariable("list_mobile_card", mapHTML.get(2));
            myDic.showSection("MOBILE_CARD");
        }

        if (mapHTML.get(1) != null && !mapHTML.get(1).equals("")) {
            myDic.setVariable("list_game_card", mapHTML.get(1));
            myDic.showSection("GAME_CARD");
        }

        if (mapHTML.get(3) != null && !mapHTML.get(3).equals("")) {
            myDic.setVariable("list_topup_game", mapHTML.get(3));
            myDic.showSection("TOPUP_GAME");
        }

        if (mapHTML.get(4) != null && !mapHTML.get(4).equals("")) {
            myDic.setVariable("list_topup_mobile", mapHTML.get(4));
            myDic.showSection("TOPUP_MOBILE");
        }
        
        if(req.getParameter("m") != null){
            try{
                int typeHtml = Integer.parseInt(req.getParameter("m"));
                
                String strTitle = "điện thoại";
                if(typeHtml == 1){
                    strTitle = "game";
                }
                mobileTop = "<a class=\"mmenu\" href=\"/\"><span class=\"msprt micoback\"></span> Mua thẻ " + strTitle + "</a>";
                
                String htmlListCardTouch = this.renderCardHTMLTouch(typeHtml);
                myDic.setVariable("list_card_touch", htmlListCardTouch);
            }
            catch(Exception ex){
                
            }
            myDic.showSection("LIST_CARD_TOUCH");
        }
        else{
            myDic.showSection("LIST_TYPE_TOUCH");
        }

        String mainContent = Utils.renderTemplate("Template/homepage.html", myDic);

        myDic.setVariable("mobile_top", mobileTop);
        
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

    private Map<Integer, String> renderListItem() throws Exception {
        Map<Integer, Item> mapItem = BusinessProcess.getListItem();
        Map<Integer, String> mapHtml = null;
        Map<String, Integer> mapSupplier = new HashMap<>();
        if (mapItem != null) {
            mapHtml = new HashMap<>();
            Iterator it = mapItem.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
                int typeId = pair.getValue().getTypeId();
                String curHtml = mapHtml.get(typeId) != null ? mapHtml.get(typeId) : "";
                String urlSale = "";
                String supplier = pair.getValue().getSupplier();
                if (mapSupplier.get(supplier) == null) {
                    if (typeId == 1 || typeId == 2) {
                        urlSale = "/banthe/" + supplier;
                    } else if (typeId == 3) {
                        urlSale = "/naptiengame/" + supplier;
                    }
                    curHtml += "<li><a href=\"" + urlSale + "\"><span class=\"sprtcard logocard " + pair.getValue().getSupplier() + "\"></span></a></li>";
                    mapHtml.put(typeId, curHtml);
                    mapSupplier.put(supplier, 1);
                }
            }
        }

        return mapHtml;
    }
    
    private String renderCardHTMLTouch(int typeHtml) throws Exception{
        Map<Integer,Item> mapItem = BusinessProcess.getListItem();
        Map<String,String> mapHtml = null; 
        String strHtml = "";
        if(mapItem != null){
            mapHtml = new HashMap<>();
            Iterator it = mapItem.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<Integer,Item> pair = (Map.Entry) it.next();
                String supplier = pair.getValue().getSupplier();
                int typeId = pair.getValue().getTypeId();
                
                String urlSale = "#";
                if (typeId == 1 || typeId == 2) {
                    urlSale = "/banthe/" + supplier;
                } else if (typeId == 3) {
                    urlSale = "/naptiengame/" + supplier;
                }
                if(mapHtml.get(supplier) == null && typeHtml == typeId){
                    strHtml += "<li><a href=\"" + urlSale + "\"><span class=\"msprt micocard list " + pair.getValue().getSupplier() + "\"></span></a></li>";
                    mapHtml.put(supplier, strHtml);
                }
            }
        }
        
        return strHtml;
    }

}
