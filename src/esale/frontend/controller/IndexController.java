/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import esale.frontend.callapi.APISale;
import esale.frontend.common.ReturnCode;
import esale.frontend.common.TGRConfig;
import esale.frontend.common.Utils;
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
            content = this.renderHtml();
        }

        return content;
    }

    private String renderHtml() throws Exception {
        TemplateDataDictionary myDic = TemplateDictionary.create();
        Map<Integer,String> mapHTML = this.renderListItem();//1-the game,2-the dien thoai,3-nap tien game,4-nap tien dien thoai
        
        if(mapHTML.get(2) != null && !mapHTML.get(2).equals("")){
            myDic.setVariable("list_mobile_card", mapHTML.get(2));
            myDic.showSection("MOBILE_CARD");
        }
        
        if(mapHTML.get(1) != null && !mapHTML.get(1).equals("")){
            myDic.setVariable("list_game_card", mapHTML.get(1));
            myDic.showSection("GAME_CARD");
        }
        
        if(mapHTML.get(3) != null && !mapHTML.get(3).equals("")){
            myDic.setVariable("list_topup_game", mapHTML.get(3));
            myDic.showSection("TOPUP_GAME");
        }
        
        if(mapHTML.get(4) != null && !mapHTML.get(4).equals("")){
            myDic.setVariable("list_topup_mobile", mapHTML.get(4));
            myDic.showSection("TOPUP_MOBILE");
        }
        
        String mainContent = Utils.renderTemplate("Template/homepage.html", myDic);
        
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
    
    private Map<Integer,String> renderListItem() throws Exception{
        Map<Integer,String> mapHTML = null;
        JsonObject jsonList = APISale.getListItem();
        if(jsonList.get("code").getAsInt() == ReturnCode.SUCCESS){
            mapHTML = new HashMap<>();
            JsonArray jsonArr = jsonList.get("data").getAsJsonArray();
            Iterator it = jsonArr.iterator();
            while(it.hasNext()){
                JsonObject itemObj = (JsonObject) it.next();
                int typeId = itemObj.get("typeId").getAsInt();
                String curHtml = mapHTML.get(typeId) != null ? mapHTML.get(typeId) : "";
                curHtml += "<li><a href=\"#\"><span class=\"sprtcard logocard " + itemObj.get("imageFile").getAsString() + "\"></span></a></li>";
                mapHTML.put(typeId, curHtml);
            }
        }
        
        return mapHTML;
    }

}
