/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.entity;

/**
 *
 * @author sonnh4
 */
public class Prize {
    
    private String prizeName;
    private int quantity;
    private int isRandom;
    private String timeWin;

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIsRandom() {
        return isRandom;
    }

    public void setIsRandom(int isRandom) {
        this.isRandom = isRandom;
    }

    public String getTimeWin() {
        return timeWin;
    }

    public void setTimeWin(String timeWin) {
        this.timeWin = timeWin;
    }
    
    
    
}
