package com.example.dj.test;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class OxidizingGas extends Activity {
    private String oxApi = "https://api.thingspeak.com/channels/237621/fields/6.json?api_key=JQ4UXUYWCX8SLO55&results=20";
    private LineChart lineChart;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_oxidizing_gas);
        lineChart = (LineChart) findViewById(R.id.ox_chart);
        progressBar = (ProgressBar) findViewById(R.id.ox_progress);
        lineChart.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                oxApi, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();
                try {
                    JSONArray feeds = response.getJSONArray("feeds");
                    ArrayList<Point> temperature_value = new ArrayList<>();
                    for(int i=0; i<feeds.length();i++){
                        JSONObject jo = feeds.getJSONObject(i);
                        String s=jo.getString("created_at");
                        Point p=new Point(new DateToTimestamp(s).getTimestamp(),Float.parseFloat(jo.getString("field6")));
                        temperature_value.add(p);
                        createGraph(temperature_value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
    void createGraph(ArrayList<Point> p){
        List<Entry> entries = new ArrayList<>();
        for(int i = 0 ; i < p.size(); i++){
            entries.add(new Entry(p.get(i).time,p.get(i).val));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Oxidising Gas");
        dataSet.notifyDataSetChanged();
        IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter();
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        dataSet.setDrawFilled(true); // to fill the below area of line in graph
        dataSet.setColors(ColorTemplate.rgb("#FF0749"));
        dataSet.setDrawValues(false);
        dataSet.setHighLightColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleColorHole(Color.RED);
        MyMarkerView myMarkerView= new MyMarkerView(this.getApplicationContext(), R.layout.custom_marker,DateToTimestamp.ref,"KOhms at");
        lineChart.setMarker(myMarkerView);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        progressBar.setVisibility(View.INVISIBLE);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.invalidate();
    }
}
