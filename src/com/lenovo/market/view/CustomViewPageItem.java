package com.lenovo.market.view;

/**
 * Created by zhouyang on 14-3-14.
 */
public class CustomViewPageItem {
    
    private String name;// item name
    private int imgSrc;// item img

    public CustomViewPageItem(String name, int imgSrc) {
        this.name = name;
        this.imgSrc = imgSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(int imgSrc) {
        this.imgSrc = imgSrc;
    }
}
