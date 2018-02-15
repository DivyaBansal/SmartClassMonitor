package com.example.dj.test;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by DivyaB on 07-04-2017.
 */
public class DateToTimestamp {
    Date d;
    float timestamp;
    static long ref=1483248661000L;
    public DateToTimestamp(String Dt) throws ParseException {
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        d=utcFormat.parse(Dt);
        DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        pstFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        String i=pstFormat.format(d);
        Date dIST=pstFormat.parse(i);
        long origin=dIST.getTime();
        long conv=origin-ref;
        timestamp=(float)conv;
    }
    public float getTimestamp(){
        return timestamp;
    }
}
