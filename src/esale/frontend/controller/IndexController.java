/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.controller;

import com.google.gson.Gson;
import esale.frontend.common.Utils;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.io.IOException;
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
            content = this.renderHtml(req, pathInfo);
        }

        return content;
    }

    private String renderHtml(HttpServletRequest req, String pathInfo) throws Exception {
        String mainContent = "";
        TemplateDataDictionary myDic = TemplateDictionary.create();
        
        if(pathInfo.equals("/")){
            mainContent = Utils.renderTemplate("Template/homepage.html", myDic);
        }

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

}
