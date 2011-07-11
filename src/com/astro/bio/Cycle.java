package com.astro.bio;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public enum Cycle {
    

    Emotional, Intellectual, Physical;

    public int duration() {
        int ret = 1;
        if (Emotional.equals(this)) {
            ret = 28;
        }
        if (Intellectual.equals(this)) {
            ret = 33;
        } else if (Physical.equals(this)) {
            ret = 23;
        }
        return ret;
    }
    /**
     * gets the percentage of cycle
     * @param days
     * @return 
     */
    public double percent(long days) {
        double val = days % duration() * 100;
        double d = val / duration();
        d=Math.ceil(d);
        
         double ret=0l;
        if(d<=25){
            ret= d*4;
        }
        if(d>25 && d<50){
            d=d-25;
            ret= 100-(d*4);
        }
        if(d>=50 && d<=75){
            d=d-50;
            ret = -(d*4);
        }
        if(d>75 && d<=100){
            d=d-75;
            ret = -(100-(d*4));
        }
        //System.out.println(""+d+" : "+ret);
     
        
        return ret;
    }

    public static double overAll(long days) {
        return (Emotional.percent(days) + Intellectual.percent(days) + Physical.percent(days)) / 3;
    }
    int duration = 8;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yy");
    public Map<String,Double> getDetails(Date d1, Date d2){
        Map<String,Double> map = new HashMap<String,Double>();
        long days = DateUtils.getDaysInBetweenDates(d1,d2);
        for(int i=1;i<duration;i++){
            Date tempDate = DateUtils.getNextWeekDay(TimeZone.getDefault(), d2, i);
            map.put(sdf.format(tempDate),percent(DateUtils.getDaysInBetweenDates(d1,tempDate)%duration()));
            d2=tempDate;
        }
        return map;
    }
   
}
