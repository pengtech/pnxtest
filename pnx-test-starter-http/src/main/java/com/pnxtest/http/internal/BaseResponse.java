
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
import com.pnxtest.http.api.HttpResponse;
import com.pnxtest.http.api.RawResponse;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class BaseResponse<T> implements HttpResponse<T> {

    private final Headers headers;
    private final String statusText;
    private final int statusCode;
    private Optional<HttpParsingException> parsedError = Optional.empty();
    private final HttpConfig httpConfig;

    protected BaseResponse(RawResponse response){
        this.headers = response.getHeaders();
        this.statusCode = response.getStatus();
        this.statusText = response.getStatusText();
        this.httpConfig = response.getHttpConfig();
    }

    protected BaseResponse(BaseResponse other){
        this.headers = other.headers;
        this.statusCode = other.statusCode;
        this.statusText = other.statusText;
        this.httpConfig = other.httpConfig;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public abstract T getBody();

    @Override
    public Optional<HttpParsingException> getParsingError() {
        return parsedError;
    }

    @Override
    public <V> V mapBody(Function<T, V> func){
        return func.apply(getBody());
    }

    @Override
    public <V> HttpResponse<V> map(Function<T, V> func) {
        return new MappedResponse(this, mapBody(func));
    }

    protected void setParsingException(String originalBody, RuntimeException e) {
        parsedError = Optional.of(new HttpParsingException(originalBody, e));
    }

    @Override
    public boolean isSuccess() {
        return getStatus() >= 200 && getStatus() < 300 && !getParsingError().isPresent();
    }

    @Override
    public HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer) {
        if(isSuccess()){
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer) {
        if(!isSuccess()){
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public <E> E mapError(Class<? extends E> errorClass) {
        if(!isSuccess()){
            try {
                return httpConfig.getObjectMapper().readValue(getParsingError().get().getOriginalBody(), errorClass);
            }catch (RuntimeException e) {
                setParsingException(getParsingError().get().getOriginalBody(), e);
            }
        }
        return null;
    }

    @Override
    public <E> HttpResponse<T> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer) {
        if(!isSuccess()){
            E error = mapError(errorClass);
            consumer.accept(new SimpleResponse(error, this));
        }
        return this;
    }
}
