/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esale.frontend.genapi;

/**
 *
 * @author sonnh4
 */
public class CampaignDetails {
    private String listAgency;
    private double voucherValue;
    private int voucherQuantity;

    public int getVoucherQuantity() {
        return voucherQuantity;
    }

    public void setVoucherQuantity(int voucherQuantity) {
        this.voucherQuantity = voucherQuantity;
    }

    public String getAgencyCode() {
        return listAgency;
    }

    public void setAgencyCode(String agencyCode) {
        this.listAgency = agencyCode;
    }

    public double getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(double voucherValue) {
        this.voucherValue = voucherValue;
    }
    
    
}
