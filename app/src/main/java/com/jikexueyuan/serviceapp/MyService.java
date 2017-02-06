package com.jikexueyuan.serviceapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class MyService extends Service {

    private RemoteCallbackList<TimerServiceCallback> callbackList = new RemoteCallbackList<>();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new IAppServiceRemoteBinder.Stub() {

            @Override
            public void setData(String data) throws RemoteException {
                MyService.this.data = data;
            }

            @Override
            public void registCallback(TimerServiceCallback callback) throws RemoteException {
                callbackList.register(callback);
            }

            @Override
            public void unRegistCallback(TimerServiceCallback callback) throws RemoteException {
                callbackList.unregister(callback);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service started");

        new Thread() {
            @Override
            public void run() {
                super.run();
                running = true;

                for(count = 0; running;count++){

                    System.out.println(count);

                    int countNum = callbackList.beginBroadcast();

                    while (countNum-- > 0){

                        try {
                            callbackList.getBroadcastItem(countNum).onTimer(count);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    callbackList.finishBroadcast();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        System.out.println("Service destroyed");
    }

    private String data = "默认值";
    private boolean running = false;
    private int count;
}
