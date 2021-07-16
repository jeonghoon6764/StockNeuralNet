package com.jlee3688gatech;

import java.io.Serializable;
import java.util.Calendar;

public class NetworkObject implements Serializable{

    private String messageFrom;
    private String messageTo;
    private Calendar creadtedTime;
    private Object object;
    private String str;

    public NetworkObject(String msgFrom, String msgTo, Object obj, String str) {
        this.messageFrom = msgFrom;
        this.messageTo = msgTo;
        this.object = obj;
        this.creadtedTime = Calendar.getInstance();
        this.str = str;
    }
    
    public String getMessageFrom() {
        return this.messageFrom;
    }

    public String getMessageTo() {
        return this.messageTo;
    }

    public Calendar getCreatedTime() {
        return this.creadtedTime;
    }

    public <T> T getObject(Class<T> type) {
        return type.cast(this.object);
    }

    public String getString() {
        return this.str;
    }
}
