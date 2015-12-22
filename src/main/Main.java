/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.vng.jcore.common.Config;
import com.vng.jcore.common.LogUtil;
import cc.frontend.common.TGRConfig;
import httpservice.WebServer;
import java.io.File;
import org.apache.log4j.Logger;
import ucom.upmonitor.main.UcomClient;

/**
 *
 * @author sonnh4
 */
public class Main {
    
    private static Logger logger_ = Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
        
        LogUtil.init();
        TGRConfig.loadConfig();
        String pidFile = System.getProperty("pidfile");

        try {

            if (pidFile != null) {
                new File(pidFile).deleteOnExit();
            }
            
            initUpMonitor();
            
            if (System.getProperty("foreground") == null) {
                System.out.close();
                System.err.close();
            }

            // Start REST service
            WebServer webserver = new WebServer();
            webserver.start();

        } catch (Throwable e) {
            logger_.error("Exception at startup: " + e.getMessage());
            System.exit(3);
        }
        
    }
    
    private static void initUpMonitor() {
        try {
            String upmonitor = System.getProperty("upmonitor"); // Khi deploy lên LIVE SO sẽ tự động add thêm VM option này vô.
            if (upmonitor != null) {
                int isEnableUpmonitor = Integer.valueOf(upmonitor);
                if (isEnableUpmonitor > 0) {
                    String zkaddress = Config.getParam("upmonitor", "address");
                    String zname = System.getProperty("zname"); //zname define trong runserver/service.in.sh (theo format /$PRODUCT-CHÍNH/$GROUP-SERVICE(nếu cần)/$SUB-GROUP-SERVICE(Nếu cần)/$SERICE( là APPNAME/ZNAME)
                    UcomClient ucomClient = new UcomClient(zname, zkaddress);
                    new Thread((Runnable) ucomClient);
                }
            }
        } catch (Exception ex) {
            logger_.error("initUpMonitor >> error:", ex);
        }
    }
    
    /*public static void setHttpProxy(boolean isNeedProxy) {
        logger_.info("setHttpProxy >> " + isNeedProxy);
        if (isNeedProxy) {
            System.setProperty("https.proxySet", "true");
            System.setProperty("https.proxyHost", TGRConfig.gProxy.getHost());
            System.setProperty("https.proxyPort", TGRConfig.gProxy.getPort());
        } else {
            System.clearProperty("https.proxySet");
            System.clearProperty("https.proxyHost");
            System.clearProperty("https.proxyPort");
        }
    }*/
    
}
