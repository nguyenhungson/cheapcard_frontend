/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.entity;

/**
 *
 * @author sonnh4
 */
public class LoginConfig {
    
    private String urlSSO;
    private String urlSuccess;
    private String urlFail;
    private String apiKey;
    private String pid;
    private String forgetPassword;
    private String changePassword;

    public String getForgetPassword() {
        return forgetPassword;
    }

    public void setForgetPassword(String forgetPassword) {
        this.forgetPassword = forgetPassword;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

    public String getUrlSSO() {
        return urlSSO;
    }

    public void setUrlSSO(String urlSSO) {
        this.urlSSO = urlSSO;
    }

    public String getUrlSuccess() {
        return urlSuccess;
    }

    public void setUrlSuccess(String urlSuccess) {
        this.urlSuccess = urlSuccess;
    }

    public String getUrlFail() {
        return urlFail;
    }

    public void setUrlFail(String urlFail) {
        this.urlFail = urlFail;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    
}
