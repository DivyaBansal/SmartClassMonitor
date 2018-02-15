package com.example.dj.test;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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

import java.lang.ref.WeakReference;

public class MqttService extends Service {
    private String brokerHostName = "";
    MqttAndroidClient client;
    MqttConnectOptions options;
    private NetworkConnectionIntentReceiver netConnReceiver;
    // constants used to notify the Activity UI of received messages
    public static final String MQTT_MSG_RECEIVED_INTENT = "mqtt.MSGRECVD";
    public static final String MQTT_MSG_RECEIVED_TOPIC = "mqtt.MSGRECVD_TOPIC";
    public static final String MQTT_MSG_RECEIVED_MSG = "mqtt.MSGRECVD_MSGBODY";

    // constants used to tell the Activity UI the connection status
    public static final String MQTT_STATUS_INTENT = "mqtt.STATUS";
    public static final String MQTT_STATUS_MSG = "mqtt.STATUS_MSG";

    // constants used by status bar notifications
    public static final int MQTT_NOTIFICATION_UPDATE = 2;

    // constants used to define MQTT connection status
    public enum MQTTConnectionStatus {
        INITIAL,                            // initial status
        CONNECTING,                         // attempting to connect
        CONNECTED,                          // connected
        NOTCONNECTED_USERDISCONNECT         //disconnected
    }

    // status of MQTT client connection
    private MQTTConnectionStatus connectionStatus = MQTTConnectionStatus.INITIAL;

    public class LocalBinder<S> extends Binder {
        private WeakReference<S> mService;

        public LocalBinder(S service) {
            mService = new WeakReference<S>(service);
        }

        public S getService() {
            return mService.get();
        }

        public void close() {
            mService = null;
        }
    }

    private LocalBinder<MqttService> mBinder;

    public MqttService() {

    }

    public void onCreate() {
        super.onCreate();
        connectionStatus = MQTTConnectionStatus.INITIAL;
        mBinder = new LocalBinder<MqttService>(this);
        SharedPreferences settings = getSharedPreferences("MyPref", MODE_PRIVATE);
        brokerHostName = settings.getString("broker", "");
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this, "tcp://" + brokerHostName + ":1883", clientId);
        options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getBaseContext(), "Connection Lost.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                checkTopic(topic, new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        // This is the old onStart method that will be called on the pre-2.0
        // platform.  On 2.0 or later we override onStartCommand() so this
        // method will not be called.

        new Thread(new Runnable() {
            @Override
            public void run() {
                handleStart(intent, startId);
            }
        }, "MQTTservice").start();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handleStart(intent, startId);
            }
        }, "MQTTservice").start();

        // return START_NOT_STICKY - we want this Service to be left running
        //  unless explicitly stopped, and it's process is killed, we want it to
        //  be restarted
        return START_STICKY;
    }

    synchronized void handleStart(Intent intent, int startId) {
        // before we start - check for a couple of reasons why we should stop

        if (client == null) {
            // we were unable to define the MQTT client connection, so we stop
            //  immediately - there is nothing that we can do
            stopSelf();
            return;
        }
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getBaseContext(), "Success! Mqtt Connected.", Toast.LENGTH_SHORT).show();
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getBaseContext(), "Failure! Mqtt Connection Failed.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        rebroadcastStatus();
    }

    public void onDestroy() {
        super.onDestroy();

        // disconnect immediately
        disconnect();

        // inform the app that the app has successfully disconnected
        broadcastServiceStatus("Disconnected");

        if (mBinder != null) {
            mBinder.close();
            mBinder = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void broadcastServiceStatus(String statusDescription) {
        // inform the app (for times when the Activity UI is running /
        //   active) of the current MQTT connection status so that it
        //   can update the UI accordingly
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MQTT_STATUS_INTENT);
        broadcastIntent.putExtra(MQTT_STATUS_MSG, statusDescription);
        sendBroadcast(broadcastIntent);
    }

    private void broadcastReceivedMessage(String topic, String message) {
        // pass a message received from the MQTT server on to the Activity UI
        //   (for times when it is running / active) so that it can be displayed
        //   in the app GUI
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MQTT_MSG_RECEIVED_INTENT);
        broadcastIntent.putExtra(MQTT_MSG_RECEIVED_TOPIC, topic);
        broadcastIntent.putExtra(MQTT_MSG_RECEIVED_MSG, message);
        sendBroadcast(broadcastIntent);
    }

    // methods used to notify the user of what has happened for times when
    //  the app Activity UI isn't running

    private void notifyUser(String alert, String title, String body) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.redcircle).setTicker(alert);
        mBuilder.setContentTitle(title);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        // Add as notification
        nm.notify(MQTT_NOTIFICATION_UPDATE, mBuilder.build());
    }

    public void rebroadcastStatus() {
        String status = "";

        switch (connectionStatus) {
            case INITIAL:
                status = "Please wait";
                break;
            case CONNECTING:
                status = "Connecting...";
                break;
            case CONNECTED:
                status = "Connected";
                break;
            case NOTCONNECTED_USERDISCONNECT:
                status = "Disconnected";
                break;
        }

        //
        // inform the app that the Service has successfully connected
        broadcastServiceStatus(status);
    }

    public void disconnect() {
        disconn();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancelAll();


        // set status
        connectionStatus = MQTTConnectionStatus.NOTCONNECTED_USERDISCONNECT;

        // inform the app that the app has successfully disconnected
        broadcastServiceStatus("Disconnected");

    }

    public void disconn() {
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    client = null;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private boolean isAlreadyConnected() {
        return ((client != null) && (client.isConnected() == true));
    }

    private class NetworkConnectionIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            // we protect against the phone switching off while we're doing this
            //  by requesting a wake lock - we request the minimum possible wake
            //  lock - just enough to keep the CPU running until we've finished
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");
            wl.acquire();

            if (isOnline()) {
                subscribeToTopic();
            }

            // we're finished - if the phone is switched off, it's okay for the CPU
            //  to sleep now
            wl.release();
        }
    }

    private void subscribeToTopic() {
        boolean subscribed = false;

        if (isAlreadyConnected() == false) {
            // quick sanity check - don't try and subscribe if we
            //  don't have a connection

            Log.e("mqtt", "Unable to subscribe as we are not connected");
        } else {
            String topics[] = {"topic/data", "topic/co2", "topic/tem", "topic/hum", "topic/pre", "topic/gas", "topic/lit"};
            int qos[] = {0, 0, 0, 0, 0, 0, 0};
            try {
                client.subscribe(topics, qos);
                subscribed = true;
                broadcastServiceStatus("Subscribed Successfully");
                notifyUser("Subscribed Successfully", "MQTT", "Subscribed Successfully");
            } catch (MqttSecurityException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        if (subscribed == false) {
            broadcastServiceStatus("Unable to subscribe");
            notifyUser("Unable to subscribe", "MQTT", "Unable to subscribe");
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isAvailable() &&
                cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }

        return false;
    }

    public void checkTopic(String t, String m) {
        if (t.equals("topic/data")) {
            broadcastReceivedMessage(m, m);
        } else {
            notifyUser("Alert!!",m, m);
        }
    }
}