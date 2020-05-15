package com.iot.mqtttutorial;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttManager {
    public final String TAG = MqttManager.class.getSimpleName();
    private MqttAndroidClient mMqttClient;
    private MqttConnectOptions mConnectOptions;
    public String HOST = "tcp://120.131.1.163:1883";
    public String USERNAME = "admin";//用户名
    public String PASSWORD = "";//密码
    public static String PUBLISH_TOPIC = "TASTEK";//发布主题
    public static String RESPONSE_TOPIC = "message_arrived";//响应主题
    public String CLIENTID = "Android_0123";
    private Context mContext;
    private Handler mHandler;

    public void init(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        mMqttClient = new MqttAndroidClient(mContext, serverURI, CLIENTID);
        mMqttClient.setCallback(mqttCallback); //设置监听订阅消息的回调
        mConnectOptions = new MqttConnectOptions();
        mConnectOptions.setCleanSession(true); //设置是否清除缓存
        mConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mConnectOptions.setKeepAliveInterval(20); //设置心跳包发送间隔，单位：秒
        mConnectOptions.setUserName(USERNAME); //设置用户名
        mConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
        Integer qos = 2;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            doClientConnection();
        }
    }

    private void doClientConnection() {
        if (!mMqttClient.isConnected() && isConnectIsNomarl()) {
            try {
                mMqttClient.connect(mConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }

    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                mMqttClient.subscribe(PUBLISH_TOPIC, 2);//订阅主题，参数：主题、服务质量
                Toast.makeText(mContext, "连接成功，订阅主题：" + PUBLISH_TOPIC, Toast.LENGTH_LONG).show();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： " + new String(message.getPayload()));
            Bundle data = new Bundle();
            data.putByteArray("PAYLOAD", message.getPayload());
            Message msg = new Message();
            msg.setData(data);
            mHandler.sendMessage(msg);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开 ");
            doClientConnection();//连接断开，重连
        }
    };

    public void release() {
        try {
            mMqttClient.unregisterResources();
            mMqttClient.close();
            mMqttClient.disconnect(); //断开连接
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
