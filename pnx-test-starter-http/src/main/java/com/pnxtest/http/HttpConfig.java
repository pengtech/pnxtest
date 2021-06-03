
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

package com.pnxtest.http;

import com.pnxtest.http.api.Client;
import com.pnxtest.http.api.ObjectMapper;
import com.pnxtest.http.api.RequestInterceptor;
import com.pnxtest.http.api.ResponseInterceptor;
import com.pnxtest.http.internal.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HttpConfig {
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10000; //10s
    public static final int DEFAULT_SOCKET_TIMEOUT = 60000; //60s
    public static final int DEFAULT_MAX_CONNECTIONS = 200;
    public static final int DEFAULT_MAX_PER_ROUTE = 20;


    private HttpGateway gateway;
    private Headers headers;
    private Proxy proxy;
    private int connectionTimeout;
    private int socketTimeout;
    private int maxConnectionCount;
    private int maxPerRoute;
    private boolean followRedirects;
    private Charset responseEncoding;

    private boolean verifySsl = true;
    private boolean compressionRequest = true;

    private List<RequestInterceptor> requestInterceptors;
    private List<ResponseInterceptor> responseInterceptors;


    private Client client;
    private Function<HttpConfig, Client> clientBuilder = ApacheClient::new;
    private ObjectMapper objectMapper;

    private KeyStore keystore;
    private Supplier<String> keystorePassword = () -> null;


    public static Builder builder(){
        return new Builder();
    }


    private HttpConfig(){}
    private HttpConfig(Builder builder){
        ensureClientNotRunning();
        this.headers = builder.headers;
        this.proxy  = builder.proxy;
        this.connectionTimeout = builder.connectionTimeout;
        this.socketTimeout = builder.socketTimeout;
        this.maxConnectionCount = builder.maxConnectionCount;
        this.maxPerRoute = builder.maxPerRoute;
        this.followRedirects = builder.followRedirects;
        this.verifySsl = builder.verifySsl;
        this.compressionRequest = builder.compressionRequest;
        this.responseEncoding = builder.responseEncoding;
        this.client = builder.client;
        this.objectMapper = builder.objectMapper;
        this.gateway = builder.gateway;
        this.requestInterceptors = builder.requestInterceptors;
        this.responseInterceptors = builder.responseInterceptors;
        this.keystore = builder.keystore;
        this.keystorePassword = builder.keystorePassword;
    }



    public static class Builder{
        private Headers headers; //set default request headers
        private Proxy proxy; // define a http proxy
        private int connectionTimeout;//The timeout until a connection with the server is established (in milliseconds)
        private int socketTimeout;//The timeout to receive data (in milliseconds),Set to zero to disable the timeout
        private int maxConnectionCount; //define the overall connection limit for a connection pool
        private int maxPerRoute; //define a connection limit per one HTTP route (this can be considered a per target host limit)
        private boolean verifySsl;//define if ssl need be verified
        private boolean compressionRequest; // define if compressing request
        private Charset responseEncoding; // response encoding
        private boolean followRedirects; // define if follow redirects

        private Client client; //HttpClient implementation to use for every synchronous request
        private ObjectMapper objectMapper; //Set the ObjectMapper implementation to use for Response to Object binding

        private HttpGateway gateway; //define a http gateway
        private List<RequestInterceptor> requestInterceptors; // set one or multiple request interceptors
        private List<ResponseInterceptor> responseInterceptors; // set one or multiple response interceptors

        private KeyStore keystore; //the keystore to use for a custom ssl context
        private Supplier<String> keystorePassword = () -> null; //

        Builder(){
            this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
            this.socketTimeout = DEFAULT_SOCKET_TIMEOUT;
            this.maxConnectionCount = DEFAULT_MAX_CONNECTIONS;
            this.maxPerRoute = DEFAULT_MAX_PER_ROUTE;

            this.followRedirects = true;
            this.compressionRequest = true;
            this.headers = new Headers();
            this.proxy = null; //no proxy
            this.verifySsl = true;

            this.responseEncoding = StandardCharsets.UTF_8;
            this.objectMapper = new JacksonObjectMapper();

            this.responseInterceptors = new ArrayList<>();
            this.requestInterceptors = new ArrayList<>();
        }

        public Builder connectionTimeout(int connectionTimeoutInMillSeconds){
            this.connectionTimeout = connectionTimeoutInMillSeconds;
            return this;
        }

        public Builder socketTimeout(int socketTimeoutInMillSeconds){
            this.socketTimeout = socketTimeoutInMillSeconds;
            return this;
        }

        public Builder maxConnectionCount(int maxConnectionCount){
            this.maxConnectionCount = maxConnectionCount;
            return this;
        }

        public Builder maxPerRoute(int maxPerRoute){
            this.maxPerRoute = maxPerRoute;
            return this;
        }

        public Builder header(String name, String value){
            header(name, value, false);
            return this;
        }

        public Builder header(String name, String value, boolean replace){
            if(replace) {
                this.headers.replace(name, value);
            }else{
                this.headers.add(name, value);
            }
            return this;
        }

        public Builder verifySsl(boolean isRequired){
            this.verifySsl = isRequired;
            return this;
        }

        public Builder proxy(Proxy proxy){
            this.proxy = proxy;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper){
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder gateway(HttpGateway gateway){
            this.gateway = gateway;
            return this;
        }

        public Builder interceptor(RequestInterceptor requestInterceptor){
            this.requestInterceptors.add(requestInterceptor);
            return this;
        }

        public Builder interceptor(ResponseInterceptor responseInterceptor){
            this.responseInterceptors.add(responseInterceptor);
            return this;
        }

        public Builder responseEncoding(Charset charset){
            this.responseEncoding = charset;
            return this;
        }


        //Set a custom keystore
        public Builder customCertificateStore(KeyStore store, String password) {
            this.keystore = store;
            this.keystorePassword = () -> password;
            return this;
        }


        //set a custom keystore via a file path. Must be a valid PKCS12 file
        public Builder customCertificateStore(String fileLocation, String password) {
            try (InputStream keyStoreStream = HttpUtil.getFileInputStream(fileLocation)) {
                this.keystorePassword = () -> password;
                this.keystore = KeyStore.getInstance("PKCS12");
                this.keystore.load(keyStoreStream, keystorePassword.get().toCharArray());
            } catch (Exception e) {
                throw new HttpConfigException(e);
            }
            return this;
        }



        //===================================
        public Builder client(Client client){
            this.client = client;
            return this;
        }

        public HttpConfig build(){
            return new HttpConfig(this);
        }
    }

    private void ensureClientNotRunning() {
        if(client != null){
            throw new HttpConfigException(
                    "Http Client is already built. Please execute shutdown() before changing settings."
            );
        }
    }

    //getters...
    public HttpGateway getGateway() {
        return gateway;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getMaxConnectionCount() {
        return maxConnectionCount;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public Charset getResponseEncoding() {
        return responseEncoding;
    }

    public boolean isVerifySsl() {
        return verifySsl;
    }

    public boolean isCompressionRequest() {
        return compressionRequest;
    }

    public List<RequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    public List<ResponseInterceptor> getResponseInterceptors() {
        return responseInterceptors;
    }

    public Function<HttpConfig, Client> getClientBuilder() {
        return clientBuilder;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public String getKeystorePassword() {
        return keystorePassword.get();
    }

    public void shutdown(){
        if(client != null){
            List<Exception> ex = client.close().collect(Collectors.toList());
            if(!ex.isEmpty()){
                throw new HttpException(ex);
            }
        }
    }

    public void setVerifySsl(boolean value){
        this.verifySsl = value;
    }



    //get current Client
    public Client getClient(){
        if(client == null){
            this.client = clientBuilder.apply(this);
        }

        return this.client;
    }

}
