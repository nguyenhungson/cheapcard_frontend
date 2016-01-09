/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.common;

import com.vng.jcore.common.Config;
import cc.frontend.entity.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class TGRConfig {
    
    private static Logger logger_ = Logger.getLogger(Config.class);
    
    public static JettyThreadPool gJettyThreadPool;
    public static String gStaticURL;
    public static String gSiteURL;
    public static String gJSFile;
    public static String gTopup;
    public static String timeDeploy;
    public static String secretSplit = ",";
    public static APIEntity gApiCheapCard;
    public static SimpleDateFormat gWheelDateFormat;
    
    //permission
    public final static int CONFIG = 1;
    public final static int REPORT = 2;
    public final static int RETRYSMS = 4;
    
    public static void loadConfig(){
        
        try{
        
            gJettyThreadPool = new JettyThreadPool();
            String jettyMinPool = Config.getParam("jetty_threadpool", "minthread");
            String jettyMaxPool = Config.getParam("jetty_threadpool", "maxthread");
            String acceptors = Config.getParam("jetty_threadpool", "acceptors");
            gJettyThreadPool.setMaxPool(jettyMaxPool);
            gJettyThreadPool.setMinPool(jettyMinPool);
            gJettyThreadPool.setAcceptors(acceptors);

            gStaticURL = Config.getParam("site", "static");
            gSiteURL = Config.getParam("site", "siteUrl");
            gJSFile = Config.getParam("site", "jsfile");
            
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            timeDeploy = dateFormat.format(cal.getTime());
            
            gApiCheapCard = new APIEntity();
            String apiUrl = Config.getParam("api_cheapcard", "url");
            String apiSecret = Config.getParam("api_cheapcard", "secret");
            gApiCheapCard.setSecret(apiSecret);
            gApiCheapCard.setUrl(apiUrl);
            
            gWheelDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        }
        catch(Exception ex){
            logger_.error("Read config file fail", ex);
            System.exit(3);
        }
        
    }
    
}
