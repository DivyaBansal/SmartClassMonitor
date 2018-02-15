package com.example.dj.test;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by DivyaB on 07-04-2017.
 */
public class HourAxisValueFormatter implements IAxisValueFormatter{
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long Timestamp = (long) value+DateToTimestamp.ref;
        Date d=new Date(Timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return hours+":"+minutes;
    }
}
