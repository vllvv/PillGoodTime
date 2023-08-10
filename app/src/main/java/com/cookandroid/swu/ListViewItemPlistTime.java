package com.cookandroid.swu;

import android.graphics.Bitmap;

public class ListViewItemPlistTime {
    Bitmap plItembitmap;
    String plItemname;
    String plItemDay;
    String plMemo;
    String plTime1, plTime2, plTime3, plTime4, plTime5, plTime6;
    String cPlTime1, cPlTime2, cPlTime3, cPlTime4, cPlTime5, cPlTime6;
    Integer plListCount;

    public void setBitmap(Bitmap bitmap) {
        this.plItembitmap = bitmap;
    }
    public void setName(String name) {
        this.plItemname = name;
    }
    public void setDay(String day) { this.plItemDay = day; }
    public void setMemo(String memo) {this.plMemo = memo;}
    public void setTime1(String time) { this.plTime1 = time; this.cPlTime1 = time; }
    public void setChangedTime1(String time) { this.cPlTime1 = time;}
    public void setTime2(String time) { this.plTime2 = time; }
    public void setChangedTime2(String time) { this.cPlTime2 = time;}
    public void setTime3(String time) { this.plTime3 = time; }
    public void setChangedTime3(String time) { this.cPlTime3 = time;}
    public void setTime4(String time) { this.plTime4 = time; }
    public void setChangedTime4(String time) { this.cPlTime4 = time;}
    public void setTime5(String time) { this.plTime5 = time; }
    public void setChangedTime5(String time) { this.cPlTime5 = time;}
    public void setTime6(String time) { this.plTime6 = time; }
    public void setChangedTime6(String time) { this.cPlTime6 = time;}
    public void setListCount(Integer count) { this.plListCount = count; }


    public Bitmap getBitmap(){
        return this.plItembitmap;
    }
    public String getName(){
        return this.plItemname;
    }
    public String getDay(){
        return this.plItemDay;
    }
    public String getMemo() {return this.plMemo;}
    public String getTime1() { return this.plTime1; }
    public String getChangedTime1() { return this.cPlTime1; }
    public String getTime2() { return this.plTime2; }
    public String getChangedTime2() { return this.cPlTime2; }
    public String getTime3() { return this.plTime3; }
    public String getChangedTime3() { return this.cPlTime3; }
    public String getTime4() { return this.plTime4; }
    public String getChangedTime4() { return this.cPlTime4; }
    public String getTime5() { return this.plTime5; }
    public String getChangedTime5() { return this.cPlTime5; }
    public String getTime6() { return this.plTime6; }
    public String getChangedTime6() { return this.cPlTime6; }
    public Integer getPlListCount() { return this.plListCount; }

}