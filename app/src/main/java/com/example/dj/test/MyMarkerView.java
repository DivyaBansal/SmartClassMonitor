package com.example.dj.test;

/**
 * Created by DivyaB on 10-04-2017.
 */
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private long referenceTimestamp;  // minimum timestamp in your data set
    private String t;

    public MyMarkerView (Context context, int layoutResource, long referenceTimestamp,String t) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.referenceTimestamp = referenceTimestamp;
        this.t=t;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long currentTimestamp = (int)e.getX() + referenceTimestamp;
        Date d=new Date(currentTimestamp);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String dt=format.format(d);
        tvContent.setText(e.getY() +t+ dt); // set the entry-value as the display text
        super.refreshContent(e, highlight);
    }
    @Override
    public MPPointF getOffset() {
        MPPointF mOffset = new MPPointF();
        mOffset.x=-(getWidth() / 2);
        mOffset.y=-getHeight();
        return mOffset;
    }

}