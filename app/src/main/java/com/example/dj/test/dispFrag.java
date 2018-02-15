package com.example.dj.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;


public class dispFrag extends Fragment implements View.OnClickListener{
    private MQTTMessageReceiver  messageIntentReceiver;
    MqttAndroidClient client;
    TextView tempVal,occVal,humVal,luminVal,COVal,CODVal,redVal,oxVal;
    TextView temptv,occtv,humtv,lumintv,cotv,codtv,oxtv,redtv;
    EditText ed;
    Button connBut;
    String topic="topic/data";
    String ip;
    MqttConnectOptions options;

    public class MQTTMessageReceiver extends BroadcastReceiver {
        String topic;
        String message;

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle notificationData = intent.getExtras();
            topic = notificationData.getString(MqttService.MQTT_MSG_RECEIVED_TOPIC);
            message = notificationData.getString(MqttService.MQTT_MSG_RECEIVED_MSG);
            assignValues(message);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View windows = inflater.inflate(R.layout.fragment_disp, container, false);
        tempVal=(TextView)windows.findViewById(R.id.tempVal);
        occVal=(TextView)windows.findViewById(R.id.occVal);
        humVal=(TextView)windows.findViewById(R.id.humVal);
        luminVal=(TextView)windows.findViewById(R.id.luminVal);
        COVal=(TextView)windows.findViewById(R.id.COVal);
        CODVal=(TextView)windows.findViewById(R.id.codVal);
        oxVal=(TextView)windows.findViewById(R.id.oxVal);
        redVal=(TextView)windows.findViewById(R.id.redVal);
        temptv=(TextView)windows.findViewById(R.id.tempTV);
        occtv=(TextView)windows.findViewById(R.id.occTV);
        humtv=(TextView)windows.findViewById(R.id.humTV);
        lumintv=(TextView)windows.findViewById(R.id.luminTV);
        codtv=(TextView)windows.findViewById(R.id.codTV);
        cotv=(TextView)windows.findViewById(R.id.COTV);
        oxtv=(TextView)windows.findViewById(R.id.oxTV);
        redtv=(TextView)windows.findViewById(R.id.redTV);

        ed=(EditText)windows.findViewById(R.id.editText);

        //Button vis=(Button)windows.findViewById(R.id.vis);
        connBut=(Button)windows.findViewById(R.id.connectButton);

        connBut.setOnClickListener(this);

        Typeface f = Typeface.createFromAsset(getActivity().getAssets(), "marker.TTF");
        temptv.setTypeface(f);
        occtv.setTypeface(f);
        humtv.setTypeface(f);
        lumintv.setTypeface(f);
        codtv.setTypeface(f);
        cotv.setTypeface(f);
        oxtv.setTypeface(f);
        redtv.setTypeface(f);

        SharedPreferences settings = getActivity().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("broker",ed.getText().toString());
        editor.commit();

        messageIntentReceiver = new MQTTMessageReceiver();
        IntentFilter intentCFilter = new IntentFilter(MqttService.MQTT_MSG_RECEIVED_INTENT);
        getActivity().registerReceiver(messageIntentReceiver, intentCFilter);

        return windows;
    }
    @Override
    public void onResume(){
       super.onResume();
    }
    public void startservice(){
        Intent svc = new Intent(getActivity(), MqttService.class);
        getActivity().startService(svc);
    }

    public void endservice(){
        Intent svc = new Intent(getActivity(), MqttService.class);
        getActivity().unregisterReceiver(messageIntentReceiver);
        getActivity().stopService(svc);
    }

    public void assignValues(String s){
        String val[]=s.split(";");
        tempVal.setText(val[1]+"\u00b0"+"C");
        humVal.setText(val[2]+"%");
        luminVal.setText(val[3]+"lux");
        occVal.setText(val[0]);
        CODVal.setText(val[7]+"ppm");
        COVal.setText(val[4]+"ppm");
        oxVal.setText(val[5]+"KOhms");
        redVal.setText(val[6]+"KOhms");
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.connectButton){
            if(v.getBackground().equals(R.drawable.green2)){
                v.setBackgroundResource(R.drawable.redcircle);
                endservice();
            }
            else{
                v.setBackgroundResource(R.drawable.green2);
                startservice();
            }
        }
      }
}
