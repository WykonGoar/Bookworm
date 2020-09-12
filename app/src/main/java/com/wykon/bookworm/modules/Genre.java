package com.wykon.bookworm.modules;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class Genre {

    // Required
    private Integer mId = -1;
    private String mName = "";

    public Genre() {}

    public Genre(Integer id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
