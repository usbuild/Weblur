package com.lecoding.data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: usbuild
 * DateTime: 13-4-13 上午9:49
 */
public class Visible implements Serializable{
    int type;
    long listId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }
}
