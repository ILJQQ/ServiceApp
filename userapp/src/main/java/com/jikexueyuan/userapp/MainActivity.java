package com.jikexueyuan.userapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jikexueyuan.serviceapp.IAppServiceRemoteBinder;
import com.jikexueyuan.serviceapp.TimerServiceCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private Intent serviceIntent;
    private TextView tvCallbackText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName("com.jikexueyuan.serviceapp","com.jikexueyuan.serviceapp.MyService"));

        tvCallbackText = (TextView) findViewById(R.id.tvCallbackText);

        findViewById(R.id.btnBind).setOnClickListener(this);
        findViewById(R.id.btnUnbind).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBind:
                bindService(serviceIntent,this, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btnUnbind:
                unbindService(this);
                unRegist();
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        System.out.println("bind service");
        System.out.println(service);

        binder =IAppServiceRemoteBinder.Stub.asInterface(service);

        try {
            binder.registCallback(onServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        unRegist();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unRegist();
    }

    private void unRegist(){
        try {
            binder.unRegistCallback(onServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IAppServiceRemoteBinder binder = null;

    private TimerServiceCallback.Stub onServiceCallback = new TimerServiceCallback.Stub() {
        @Override
        public void onTimer(int numIndex) throws RemoteException {
            Message msg = new Message();
            msg.obj = MainActivity.this;
            msg.arg1 = numIndex;
            handler.sendMessage(msg);
        }
    };

    private final MyHandler handler = new MyHandler();
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int index = msg.arg1;
            MainActivity _this = (MainActivity) msg.obj;
            _this.tvCallbackText.setText("这是回调给客户端的数据" + index);
        }
    }
}
