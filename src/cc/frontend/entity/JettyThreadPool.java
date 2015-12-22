/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.entity;

/**
 *
 * @author sonnh4
 */
public class JettyThreadPool {
 
    private String minPool;
    private String maxPool;
    private String acceptors;

    public String getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(String acceptors) {
        this.acceptors = acceptors;
    }

    public String getMinPool() {
        return minPool;
    }

    public void setMinPool(String minPool) {
        this.minPool = minPool;
    }

    public String getMaxPool() {
        return maxPool;
    }

    public void setMaxPool(String maxPool) {
        this.maxPool = maxPool;
    }
    
}
