package com.cookandroid.swu;

import android.graphics.Bitmap;


public class ListViewItem {
    private Bitmap iconDrawable ;
    private String titleStr ;
    private String descStr ;
    private String dateStr;
    private String memoStr ;

    public ListViewItem(){

    }

    public void setIcon(Bitmap icon) {
        iconDrawable = icon ;
    }

    public void setTitle(String title) {
        titleStr = title ;
    }


    public void setDesc(String desc) { descStr = desc ; }
    public void setDate(String date) { dateStr = date ; }
    public void setMemo(String memo) { memoStr = memo ; }

    public Bitmap getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public String getDate() {
        return this.dateStr ;
    }
    public String getMemo() {
        return this.memoStr ;
    }
}