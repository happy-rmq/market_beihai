package com.lenovo.market.common;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

public class CustomAndroidHttpTransport extends HttpTransportSE {

    private int timeout = 5000; // 默认超时时间为30s

    public CustomAndroidHttpTransport(String url) {
        super(url);
    }

    public CustomAndroidHttpTransport(String url, int timeout) {
        super(url);
        this.timeout = timeout;
    }

    protected ServiceConnection getServiceConnection(String url) throws IOException {
        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url);
        serviceConnection.setConnectionTimeOut(timeout);
        return new ServiceConnectionSE(url);
    }
}
