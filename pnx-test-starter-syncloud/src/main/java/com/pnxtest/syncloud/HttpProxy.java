package com.pnxtest.syncloud;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class HttpProxy {

    public static Proxy fromSystemProperties() {
        try {
            String proxyHost = System.getProperty("https.proxyHost");
            String proxyPort = System.getProperty("https.proxyPort");
            if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null && !proxyPort.isEmpty()) {
                return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
            }
        } catch (Throwable var2) {
            //LOGGER.info("Unable to read HTTP proxy information from system properties", var2);
        }

        return Proxy.NO_PROXY;
    }

    private HttpProxy() {
    }
}
