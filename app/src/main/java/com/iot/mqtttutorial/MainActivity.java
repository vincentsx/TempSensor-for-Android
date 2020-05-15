package com.iot.mqtttutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView image_1;
    private ImageView image_2;
    private ImageView image_3;
    private ImageView image_4;
    private ImageView image_5;
    private ImageView image_6;

    private static final String TAG = "MqttTutorial";
    private Button mConnectButton;
    private TextView mTempText;
    private MqttManager mMqttManager;

    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTempText.setText("当前温度: " + new String(msg.getData().getByteArray("PAYLOAD")));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ui_init();  //ui的初始化

        //灯的状态
        image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"该功能暂未开发" , Toast.LENGTH_SHORT).show();
            }
        });
        //风扇的状态
        image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"该功能暂未开发" , Toast.LENGTH_SHORT).show();
            }
        });
        //空调的状态
        image_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"该功能暂未开发" , Toast.LENGTH_SHORT).show();
            }
        });
        //网络的状态
        image_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"该功能暂未开发" , Toast.LENGTH_SHORT).show();
            }
        });
        //温度的状态
        image_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"向设备发送请求信息" , Toast.LENGTH_SHORT).show();
            }
        });
        //时钟的状态
        image_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"该功能暂未开发" , Toast.LENGTH_SHORT).show();
            }
        });

        mConnectButton = findViewById(R.id.connect);
        mTempText = findViewById(R.id.tv_temp);
        mConnectButton.setOnClickListener(new ButtonListener());
        mMqttManager = new MqttManager();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMqttManager.release();
    }
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.connect:
                    mMqttManager.init(MainActivity.this, mUiHandler);
                    break;
            }
        }
    }
    //寻找真正的id与自己定义的id绑定
    private void ui_init() {
        //btn_1 = findViewById(R.id.btn_1);
        image_1 = findViewById(R.id.image_1);
        image_2 = findViewById(R.id.image_2);
        image_3 = findViewById(R.id.image_3);
        image_4 = findViewById(R.id.image_4);
        image_5 = findViewById(R.id.image_5);
        image_6 = findViewById(R.id.image_6);
    }
}
