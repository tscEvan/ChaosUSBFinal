package com.example.xo337.try201804;

public class dataFormat{
    private String linkID;
    private String synValue;
    private String usbKey;
    private String usbName;
    private String userName;
    private String webVct;

    public dataFormat(){
    }

    public dataFormat(String linkID, String synValue, String usbKey, String usbName, String userName, String webVct) {
        this.linkID = linkID;
        this.synValue = synValue;
        this.usbKey = usbKey;
        this.usbName = usbName;
        this.userName = userName;
        this.webVct = webVct;
    }

    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String linkID) {
        this.linkID = linkID;
    }

    public String getSynValue() {
        return synValue;
    }

    public void setSynValue(String synValue) {
        this.synValue = synValue;
    }

    public String getUsbKey() {
        return usbKey;
    }

    public void setUsbKey(String usbKey) {
        this.usbKey = usbKey;
    }

    public String getUsbName() {
        return usbName;
    }

    public void setUsbName(String usbName) {
        this.usbName = usbName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWebVct() {
        return webVct;
    }

    public void setWebVct(String webVct) {
        this.webVct = webVct;
    }
}