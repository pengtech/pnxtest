
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
import com.pnxtest.http.api.HttpRequest;
import com.pnxtest.http.api.HttpResponse;
import com.pnxtest.http.api.ObjectMapper;
import com.pnxtest.http.api.RawResponse;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class BaseRequest<R extends HttpRequest> implements HttpRequest<R> {

    protected Headers headers = new Headers();
    protected final HttpConfig httpConfig;
    protected HttpMethod method;
    protected Path path;

    private Optional<ObjectMapper> objectMapper = Optional.empty();
    private int connectTimeout;
    private int socketTimeout;
    private Proxy proxy;

    BaseRequest(BaseRequest httpRequest) {
        this.httpConfig = httpRequest.httpConfig;
        this.method = httpRequest.method;
        this.path = httpRequest.path;
        this.headers.putAll(httpRequest.headers);

        this.objectMapper = httpRequest.objectMapper;
        this.connectTimeout = httpRequest.connectTimeout;
        this.socketTimeout = httpRequest.socketTimeout;
        this.proxy = httpRequest.proxy;

    }

    BaseRequest(HttpConfig httpConfig, HttpMethod method, String url) {
        this.httpConfig = httpConfig;
        this.method = method;
        this.path = new Path(url);
        this.headers.putAll(httpConfig.getHeaders());
    }


    @Override
    public R routeParam(String name, String value) {
        path.param(name, value);
        return (R)this;
    }

    @Override
    public R routeParam(Map<String, Object> params) {
        path.param(params);
        return (R)this;
    }

    @Override
    public R basicAuth(String username, String password) {
        this.headers.replace("Authorization", HttpUtil.toBasicAuthValue(username, password));
        return (R)this;
    }

    @Override
    public R bearAuth(String token) {
        this.headers.replace("Authorization", HttpUtil.toBearAuthValue(token));
        return (R)this;
    }



    @Override
    public R accept(String value) {
        return header(HeaderNames.ACCEPT, value);
    }

    @Override
    public R header(String name, String value) {
        this.headers.add(name.trim(), value);
        return (R)this;
    }

    @Override
    public R headerReplace(String name, String value) {
        this.headers.replace(name, value);
        return (R)this;
    }

    @Override
    public R headers(Map<String, String> headerMap) {
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
        }
        return (R)this;
    }

    @Override
    public R queryString(String name, Collection<?> value) {
        path.queryString(name, value);
        return (R)this;
    }

    @Override
    public R queryString(String name, Object value) {
        path.queryString(name, value);
        return (R)this;
    }

    @Override
    public R queryString(Map<String, Object> parameters) {
        path.queryString(parameters);
        return (R)this;
    }

    @Override
    public R usingObjectMapper(ObjectMapper mapper) {
        if(mapper !=null){
            this.objectMapper = Optional.of(mapper);
        }
        return (R)this;
    }

    @Override
    public R usingProxy(Proxy proxy) {
        this.proxy = proxy;
        return (R)this;
    }

    @Override
    public R connectTimeout(int millSeconds) {
        this.connectTimeout = millSeconds;
        return (R)this;
    }

    @Override
    public R socketTimeout(int millSeconds) {
        this.socketTimeout = millSeconds;
        return (R)this;
    }


    @Override
    public R verifySSL(boolean required){
        this.httpConfig.setVerifySsl(required);
        return (R)this;
    }

    @Override
    public HttpResponse asEmpty() {
        return httpConfig.getClient().request(this, EmptyResponse::new);
    }



    @Override
    public HttpResponse<String> asString() throws HttpException {
        return httpConfig.getClient().request(this, r -> new StringResponse(r, httpConfig.getResponseEncoding().name()));
    }

    @Override
    public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) throws HttpException {
        return httpConfig.getClient().request(this, r -> new ObjectResponse<T>(httpConfig.getObjectMapper(), r, responseClass));
    }

    @Override
    public <T> HttpResponse<T> asObject(GenericType<T> genericType) throws HttpException {
        return httpConfig.getClient().request(this, r -> new ObjectResponse<T>(httpConfig.getObjectMapper(), r, genericType));
    }


    @Override
    public <T> HttpResponse<T> asObject(Function<RawResponse, T> function) {
        return httpConfig.getClient().request(this, funcResponse(function));
    }



    private <T> Function<RawResponse, HttpResponse<T>> funcResponse(Function<RawResponse, T> function) {
        return r -> new BasicResponse<>(r, function.apply(r));
    }


    @Override
    public HttpResponse<File> asFile(String path) {
        return httpConfig.getClient().request(this, r -> new FileResponse(r, path));
    }



    @Override
    public HttpMethod getHttpMethod() {
        return method;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper.orElseGet(httpConfig::getObjectMapper);
    }

    @Override
    public int getConnectTimeout() {
        if(connectTimeout>0) return connectTimeout;
        return httpConfig.getConnectionTimeout();
    }

    @Override
    public int getSocketTimeout() {
        if(socketTimeout>0) return socketTimeout;
        return httpConfig.getSocketTimeout();
    }


    @Override
    public Proxy getProxy() {
        return valueOr(proxy, httpConfig::getProxy);
    }

    private <T> T valueOr(T x, Supplier<T> o){
        if(x != null){
            return x;
        }
        return o.get();
    }


}
