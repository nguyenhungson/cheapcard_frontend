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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class BusinessProcess {

    private static Logger logger = Logger.getLogger(Utils.class);

    public static Map<Integer, Item> getListItem() throws Exception {
        ResponseItem response = new Gson().fromJson(APISale.getListItem(), ResponseItem.class);
        Map<Integer, Item> mapItem = null;
        if (response.getCode() == 1) {
            mapItem = new HashMap<>();
            List<Item> listItem = (List<Item>) response.getData();
            for (Item item : listItem) {
                mapItem.put(item.getId(), item);
            }
        }
        return mapItem;
    }

    public static String renderListPriceCard(Map<Integer, Item> mapListItem, int typeId, String supplier) throws Exception {
        Map<String, String> mapListPrice = BusinessProcess.addListPrice(mapListItem, typeId, supplier);
        Iterator it = mapListPrice.entrySet().iterator();
        String html = "";
        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry) it.next();
            String styleDisplay = "display:none";
            if (pair.getKey().equals(supplier)) {
                styleDisplay = "display:table-row";
            }
            html += "<tr class=\"txtprotbl\" data-type=\"" + pair.getKey() + "\" style=\"" + styleDisplay + "\">"
                    + "<td colspan=\"2\">Thẻ <strong>" + pair.getKey().toUpperCase() + "</strong></td>"
                    + "</tr>"
                    + "<tr class=\"titletbl\" data-type=\"" + pair.getKey() + "\" style=\"" + styleDisplay + "\">"
                    + "<td>Mệnh giá</td>"
                    + "<td>Giá bán</td>"
                    + "</tr>"
                    + pair.getValue();
        }

        return html;
    }

    public static Map<String, String> addListPrice(Map<Integer, Item> mapListItem, int typeId, String supplier) throws Exception {
        if(supplier.equals("")){
            supplier = "zxu";
        }
        
        Iterator it = mapListItem.entrySet().iterator();
        Map<String, String> mapListPrice = new HashMap<>();
        Map<String, Integer> mapIndex = new HashMap<>();
        while (it.hasNext()) {
            Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
            if (pair.getValue().getTypeId() != typeId) {
                continue;
            }

            String styleDisplay = "display:none";
            if (pair.getValue().getSupplier().equals(supplier)) {
                styleDisplay = "display:table-row";
            }

            String htmlItem = mapListPrice.get(pair.getValue().getSupplier()) == null ? "" : mapListPrice.get(pair.getValue().getSupplier());
            int indexNo = mapIndex.get(pair.getValue().getSupplier()) == null ? 0 : mapIndex.get(pair.getValue().getSupplier());
            String className = " class=\"bggreytbl\"";
            if (indexNo % 2 == 0) {
                className = " class=\"bgwhitetbl\"";
            }

            htmlItem += "<tr" + className + " data-type=\"" + pair.getValue().getSupplier() + "\" style=\"" + styleDisplay + "\">"
                    + "<td>" + Utils.formatNumber(pair.getValue().getUnitPrice()) + "</td>"
                    + "<td>" + Utils.formatNumber(pair.getValue().getDiscountAmount()) + "</td>"
                    + "</tr>";

            mapListPrice.put(pair.getValue().getSupplier(), htmlItem);
            mapIndex.put(pair.getValue().getSupplier(), ++indexNo);
        }

        return mapListPrice;
    }
    
    public static String renderListPriceZX() throws Exception {
        String html = "";
        int indexNo = 0;
        Map<Integer, Item> mapListItem = BusinessProcess.getListItem();
        Iterator it = mapListItem.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Item> pair = (Map.Entry) it.next();
            if (pair.getValue().getSupplier().equals("zxu")) {
                String className = " class=\"bggreytbl\"";
                if (indexNo % 2 == 0) {
                    className = " class=\"bgwhitetbl\"";
                }

                html += "<tr" + className + ">"
                        + "<td>" + Utils.formatNumber(pair.getValue().getUnitPrice()) + "</td>"
                        + "<td>" + Utils.formatNumber(pair.getValue().getDiscountAmount()) + "</td>"
                        + "</tr>";
                indexNo++;
            }
        }

        return html;
    }

}
