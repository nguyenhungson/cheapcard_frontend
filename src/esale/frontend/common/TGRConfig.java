/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.common;

import com.vng.jcore.common.Config;
import esale.frontend.entity.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class TGRConfig {
    
    private static Logger logger_ = Logger.getLogger(Config.class);
    
    public static JettyThreadPool gJettyThreadPool;
    public static AddressService gMemcache;
    public static AddressService gProxy;
    public static String gStaticURL;
    public static String gSiteURL;
    public static String gJSFile;
    public static String gTopup;
    public static String timeDeploy;
    public static String gGoogleValidateLogin;
    public static String secretSplit = ",";
    public static Map gGoogleAccount;
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
            
            gMemcache = new AddressService();
            String memcacheHost = Config.getParam("Memcache", "host");
            String memcachePort = Config.getParam("Memcache", "port");
            gMemcache.setHost(memcacheHost);
            gMemcache.setPort(memcachePort);
            
            gProxy = new AddressService();
            String proxyHost = Config.getParam("proxy", "host");
            String proxyPort = Config.getParam("proxy", "port");
            gProxy.setHost(proxyHost);
            gProxy.setPort(proxyPort);
            
            gGoogleValidateLogin = Config.getParam("google", "url_validate");
            
            String accountAllow = Config.getParam("google", "account");
            String arrAccount[] = accountAllow.split(",");
            gGoogleAccount = new HashMap();
            for(String item : arrAccount){
                String arrItem[] = item.split("-");
                gGoogleAccount.put(arrItem[0], arrItem[1]);
            }
            
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
