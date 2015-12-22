/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cc.frontend.callapi.APISale;
import cc.frontend.entity.Item;
import cc.frontend.entity.ResponseAPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class BusinessProcess {

    private static Logger logger = Logger.getLogger(Utils.class);

    public static String[] onLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        String result[] = {"0",""};
        
        String urlCheck = TGRConfig.gGoogleValidateLogin + req.getParameter("tokenId");
        logger.info(String.format("Check login from Google >> Start url %s", urlCheck));
        String infoFromToken = Utils.sendHTTPGet(urlCheck);
        logger.info(String.format("Check login from Google >> End %s", infoFromToken));
        
        JsonElement jsonElement = new JsonParser().parse(infoFromToken);
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        if(jsonObj.get("email") != null){
            String email = jsonObj.get("email").getAsString();
            String gName = jsonObj.get("given_name").getAsString();
            String gPicture = jsonObj.get("picture") == null ? TGRConfig.gStaticURL + "/img/avatar5.png" : jsonObj.get("picture").getAsString();
            if(TGRConfig.gGoogleAccount.get(email) != null){
                String atHash = jsonObj.get("at_hash").getAsString();
                BusinessProcess.saveLogin(email, atHash, req, resp);
                result[0] = TGRConfig.gGoogleAccount.get(email).toString();
                result[1] = email;
            }
            else{
                result[0] = "-1";
                result[1] = email;
            }
        }
            
        return result;
    }
    
    public static void saveLogin(String email, String at_hash, HttpServletRequest req, HttpServletResponse resp){
        String keyCache = KeyCacheConfig.isLoginReport + email;
        CallMemcache.getInstance().setCache(keyCache, at_hash, 86400);
    }
    
    public static int checkLogin(HttpServletRequest req){
        /*if(SSOCookie.getCookie(req, "p_email") != null && SSOCookie.getCookie(req, "_athash") != null){
            String email = SSOCookie.getCookie(req, "p_email").getValue();
            String hash = SSOCookie.getCookie(req, "_athash").getValue();
            String keyCache = KeyCacheConfig.isLoginReport + email;
            Object cache = CallMemcache.getInstance().getCache(keyCache);
            if(cache != null){
                String cacheHash = cache.toString();
                if(cacheHash.equals(hash)){
                    return 1;
                }
            }
        }*/
        
        return 0;
    }
    
    public static Map<Integer,Item> getListItem() throws Exception{
        ResponseAPI response = new Gson().fromJson(APISale.getListItem(), ResponseAPI.class);
        Map<Integer, Item> mapItem = null;
        if(response.getCode() == 1){
            mapItem = new HashMap<>();
            List<Item> listItem = (List<Item>) response.getData();
            for(Item item : listItem){
                mapItem.put(item.getId(), item);
            }
        }
        return mapItem;
    }

}
