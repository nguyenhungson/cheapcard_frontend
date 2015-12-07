/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import esale.frontend.callapi.APISale;
import esale.frontend.common.BusinessProcess;
import esale.frontend.common.TGRConfig;
import esale.frontend.common.Utils;
import esale.frontend.entity.HTMLRender;
import esale.frontend.genapi.CampaignDetails;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

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
            
        } else if (pathInfo.equals("/")) {
            if (req.getParameter("reqType") == null) {
                
            } else {
                if (req.getParameter("respType").equals("html")) {
                    content = this.renderHtml(req);
                } else if (req.getParameter("respType").equals("json")) {
                    content = this.actionResultAjax(req);
                }
            }
        }

        return content;
    }

    private String renderHtml(HttpServletRequest req) throws Exception {
        

        return "test";
    }

    private String actionResultAjax(HttpServletRequest req) throws Exception {
        String result = "";

        String reqFunc = req.getParameter("reqFunc");
        if (reqFunc.equals("report_general")) {
            
        } 

        return result;
    }

}
