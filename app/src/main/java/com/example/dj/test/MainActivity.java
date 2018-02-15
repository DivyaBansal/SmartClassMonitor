package com.example.dj.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private MQTTMessageReceiver  messageIntentReceiver;
    MqttAndroidClient client;
    TextView tempVal,occVal,humVal,luminVal,COVal,CODVal,redVal,oxVal;
    TextView temptv,occtv,humtv,lumintv,cotv,codtv;
    EditText ed;
    Button connBut;
    String topic="topic/test";
    String ip;
    MqttConnectOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        tempVal=(TextView)findViewById(R.id.tempVal);
        occVal=(TextView)findViewById(R.id.occVal);
        humVal=(TextView)findViewById(R.id.humVal);
        luminVal=(TextView)findViewById(R.id.luminVal);
        COVal=(TextView)findViewById(R.id.COVal);
        CODVal=(TextView)findViewById(R.id.codVal);
        oxVal=(TextView)findViewById(R.id.oxVal);
        redVal=(TextView)findViewById(R.id.redVal);
        temptv=(TextView)findViewById(R.id.tempTV);
        occtv=(TextView)findViewById(R.id.occTV);
        humtv=(TextView)findViewById(R.id.humTV);
        lumintv=(TextView)findViewById(R.id.luminTV);
        codtv=(TextView)findViewById(R.id.codTV);
        cotv=(TextView)findViewById(R.id.COTV);

        ed=(EditText)findViewById(R.id.editText);

        Button vis=(Button) findViewById(R.id.vis);
        connBut=(Button) findViewById(R.id.connectButton);

        Typeface f = Typeface.createFromAsset(getAssets(), "chawp.ttf");
        temptv.setTypeface(f);
        occtv.setTypeface(f);
        humtv.setTypeface(f);
        lumintv.setTypeface(f);
        codtv.setTypeface(f);
        cotv.setTypeface(f);

        SharedPreferences settings = getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("broker",ed.getText().toString());
        editor.commit();


    }
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

    public void startservice(View view){
        Intent svc = new Intent(this, MqttService.class);
        startService(svc);
        messageIntentReceiver = new MQTTMessageReceiver();
        IntentFilter intentCFilter = new IntentFilter(MqttService.MQTT_MSG_RECEIVED_INTENT);
        registerReceiver(messageIntentReceiver, intentCFilter);
    }

    public void endservice(View view){
        Intent svc = new Intent(this, MqttService.class);
        unregisterReceiver(messageIntentReceiver);
        stopService(svc);
    }

    private void setSubscription(){
        try{
            client.subscribe(topic,0);
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void visualizeClicked(View view){
        Intent intent = new Intent(this,Chart.class);
        startActivity(intent);
    }
    public void connect(View v){
        if(connBut.getBackground().equals(R.drawable.green)){
            disconn(v);
        }
        else {
            String clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(this, "tcp://" + ed.getText() + ":1883", clientId);
            options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(getBaseContext(), "Connection Lost.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    assignValues(new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        connBut.setBackgroundResource(R.drawable.green);
                        Toast.makeText(getBaseContext(), "Success! Mqtt Connected.", Toast.LENGTH_SHORT).show();
                        setSubscription();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(getBaseContext(), "Failure! Mqtt Connection Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
    public void disconn(View v){
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getBaseContext(), "Success! Mqtt Disconnected.", Toast.LENGTH_SHORT).show();
                    connBut.setBackgroundResource(R.drawable.redcircle);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getBaseContext(), "Failure! Could not disconnect.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void assignValues(String s){
        String val[]=s.split(";");
        tempVal.setText(val[1]);
        humVal.setText(val[2]);
        luminVal.setText(val[3]);
        occVal.setText(val[0]);
        CODVal.setText(val[7]);
        COVal.setText(val[4]);
        oxVal.setText(val[5]);
        redVal.setText(val[6]);
    }

}