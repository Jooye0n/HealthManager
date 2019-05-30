package com.example.bbbb.healthmanager;

import android.graphics.Bitmap;

public class ListViewItem {

    private String itemTitle;
    private String itemContext;
    private Bitmap itemImg;

    public ListViewItem() {
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemContext() {
        return itemContext;
    }

    public void setItemContext(String itemContext) {
        this.itemContext = itemContext;
    }

    public Bitmap getItemImg() {
        return itemImg;
    }

    public void setItemImg(Bitmap itemImg) {
        this.itemImg = itemImg;
    }


}
