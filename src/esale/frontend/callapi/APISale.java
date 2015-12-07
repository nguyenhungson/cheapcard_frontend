/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.callapi;

import com.google.gson.Gson;
import esale.frontend.common.TGRConfig;
import esale.frontend.common.Utils;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class APISale {

    public static int PAGESIZE = 10;
    private static Logger logger = Logger.getLogger(APISale.class);
    private static Gson gson = new Gson();

    public static String getListGame() throws Exception {
        String url = TGRConfig.gApiInfo.getUrl() + "gamecode_promotion/getGameList";

        String paramKey[] = {};
        String paramValue[] = {};

        logger.info("getListGame >> URL: " + url);
        String result = Utils.callAPIRestObject(url, paramKey, paramValue);
        logger.info(String.format("getListGame End result: %s", result));

        return result;
    }
    
}
