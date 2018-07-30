package com.example.asus.carparkfinder;

/**
 * Created by ASUS on 31/12/2017.
 */
public class Carpark {
    public String cp_num, address, cp_type, free_parking;
    public long avail_lots, tot_lots;
    public double empty_percen, lat, lng;
    public boolean night_park;

    Carpark (String cn, String add, String ct, String fp, long al, long tl, double ep, double lt,double lg, String np){
        this.cp_num =cn;
        this.address = add;
        this.cp_type = ct;
        this.free_parking=fp;
        this.avail_lots=al;
        this.tot_lots=tl;
        this.empty_percen=ep;
        this.lat=lt;
        this.lng=lg;

        switch(np){
            case ("YES"): this.night_park=true;
                break;
            case ("NO"): this.night_park=false;
                break;
        }
    }
}
