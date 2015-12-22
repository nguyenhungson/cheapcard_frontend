/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.callapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cc.frontend.common.TGRConfig;
import cc.frontend.common.Utils;
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

    public static String getListItem() throws Exception {
        String url = TGRConfig.gApiCheapCard.getUrl() + "getitemlst";
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String sig = Utils.encryptSHA256(time + TGRConfig.secretSplit + "0" + TGRConfig.secretSplit + TGRConfig.gApiCheapCard.getSecret());

        String paramKey[] = {"time", "type", "sig"};
        String paramValue[] = {time, "0", sig};

        String result = Utils.callAPIRestObject(url, paramKey, paramValue);

        return result;
    }
    
}
