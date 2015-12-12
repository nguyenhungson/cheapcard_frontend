/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.callapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import esale.frontend.common.TGRConfig;
import esale.frontend.common.Utils;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class APISale {

    public static int PAGESIZE = 10;
    private static Logger logger = Logger.getLogger(APISale.class);
    private static Gson gson = new Gson();

    public static JsonObject getListItem() throws Exception {
        String url = TGRConfig.gApiCheapCard.getUrl() + "getitemlst";
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String sig = Utils.encryptSHA256(time + TGRConfig.secretSplit + TGRConfig.gApiCheapCard.getSecret());

        String paramKey[] = {"time", "sig"};
        String paramValue[] = {time, sig};

        JsonObject result = Utils.callAPIRestJsonObject(url, paramKey, paramValue);

        return result;
    }
    
}
