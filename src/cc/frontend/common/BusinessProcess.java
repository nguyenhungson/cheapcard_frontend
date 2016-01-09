/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.common;

import com.google.gson.Gson;
import cc.frontend.callapi.APISale;
import cc.frontend.entity.Item;
import cc.frontend.entity.ResponseItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class BusinessProcess {

    private static Logger logger = Logger.getLogger(Utils.class);

    public static Map<Integer,Item> getListItem() throws Exception{
        ResponseItem response = new Gson().fromJson(APISale.getListItem(), ResponseItem.class);
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
