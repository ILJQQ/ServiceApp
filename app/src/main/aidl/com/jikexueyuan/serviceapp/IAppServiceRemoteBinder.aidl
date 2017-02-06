// IAppServiceRemoteBinder.aidl
package com.jikexueyuan.serviceapp;

// Declare any non-default types here with import statements
import com.jikexueyuan.serviceapp.TimerServiceCallback;

interface IAppServiceRemoteBinder {

    void setData(String data);
    void registCallback(TimerServiceCallback callback);
    void unRegistCallback(TimerServiceCallback callback);
}
