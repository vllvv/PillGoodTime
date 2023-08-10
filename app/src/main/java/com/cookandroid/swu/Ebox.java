package com.cookandroid.swu;

public class Ebox {
    String Ename;
    String Edate;

    public Ebox(String ename, String edate) {
        this.Ename = ename;
        this.Edate = edate;
    }
    public Ebox(){

    }

    public String getEname() {
        return Ename;
    }

    public void setEname(String ename) {
        this.Ename = ename;
    }

    public String getEdate() {
        return Edate;
    }

    public void setEdate(String edate) {
        this.Edate = edate;
    }
}
