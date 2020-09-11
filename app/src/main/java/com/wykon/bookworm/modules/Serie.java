package com.wykon.bookworm.modules;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class Serie {

    // Required
    private Integer mId = -1;
    private String mName = "";
    private Boolean mComplete = Boolean.FALSE;

    public Serie() {}

    public Serie(Integer id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Boolean isCompleted() {
        return mComplete;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setComplete(Boolean complete) {
        this.mComplete = complete;
    }
}
