/*
 *  Copyright (c) 2020-2021
 *  This file is part of PnxTest framework.
 *
 *  PnxTest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero Public License version 3 as
 *  published by the Free Software Foundation
 *
 *  PnxTest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero Public License for more details.
 *
 *  You should have received a copy of the GNU Affero Public License
 *  along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For more information, please contact the author at this address:
 *  chen.baker@gmail.com
 *
 */

package com.pnxtest.http.internal;

import com.pnxtest.http.HttpConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

class SecurityConfig {
    private final HttpConfig httpConfig;
    private SSLContext sslContext;
    private SSLConnectionSocketFactory sslSocketFactory;


    public SecurityConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    public PoolingHttpClientConnectionManager createManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(buildSocketFactory(),
                null, null, null,
                1, TimeUnit.MILLISECONDS);

        manager.setMaxTotal(httpConfig.getMaxConnectionCount());
        manager.setDefaultMaxPerRoute(httpConfig.getMaxPerRoute());
        return manager;
    }

    private Registry<ConnectionSocketFactory> buildSocketFactory() {
        try {
            if (!httpConfig.isVerifySsl()) {
                return createDisabledSSLContext();
            } else if (httpConfig.getKeystore() != null) {
                return createCustomSslContext();
            } else {
                return createDefaultRegistry();
            }
        } catch (Exception e) {
            throw new HttpConfigException(e);
        }
    }

    private Registry<ConnectionSocketFactory> createDefaultRegistry() {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
    }

    private Registry<ConnectionSocketFactory> createCustomSslContext() {
        SSLConnectionSocketFactory socketFactory = getSocketFactory();
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory)
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();
    }

    private Registry<ConnectionSocketFactory> createDisabledSSLContext() throws Exception {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(new SSLContextBuilder()
                        .loadTrustMaterial(null, (x509CertChain, authType) -> true)
                        .build(),
                        NoopHostnameVerifier.INSTANCE))
                .build();
    }

    private SSLConnectionSocketFactory getSocketFactory() {
        if(sslSocketFactory == null) {
            sslSocketFactory = new SSLConnectionSocketFactory(createSslContext(), new NoopHostnameVerifier());
        }
        return sslSocketFactory;
    }
    private SSLContext createSslContext() {
        if(sslContext == null) {
            try {
                char[] pass = Optional.ofNullable(httpConfig.getKeystorePassword())
                        .map(String::toCharArray)
                        .orElse(null);
                sslContext = SSLContexts.custom()
                        .loadKeyMaterial(httpConfig.getKeystore(), pass)
                        .build();
            } catch (Exception e) {
                throw new HttpConfigException(e);
            }
        }
        return sslContext;
    }

    public void configureSecurity(HttpClientBuilder cb) {
        if(httpConfig.getKeystore() != null){
            cb.setSSLContext(createSslContext());
            cb.setSSLSocketFactory(getSocketFactory());
        }
        if (!httpConfig.isVerifySsl()) {
            disableSsl(cb);
        }
    }

    private void disableSsl(HttpClientBuilder cb) {
        try {
            cb.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
            cb.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true).build());
        } catch (Exception e) {
            throw new HttpConfigException(e);
        }
    }
}
