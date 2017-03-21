package com.mirrket.tod_app.behaviour;

import android.app.Application;

/**
 * Created by yy on 15.03.2017.
 */

public class ConnectionApplication extends Application {
    private static ConnectionApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized ConnectionApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
