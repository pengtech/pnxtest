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

import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.http.HttpConfig;
import com.pnxtest.http.api.Header;
import com.pnxtest.http.api.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//build to apache HttpUrlRequest
class PreparedRequest {
    private static final String CONTENT_TYPE = "content-type";
    private static final String ACCEPT_ENCODING_HEADER = "accept-encoding";
    private static final String USER_AGENT_HEADER = "user-agent";
    private static final String USER_AGENT = "PnxHttp";
    private static final Map<HttpMethod, Function<String, HttpRequestBase>> FACTORIES;
    private final HttpRequest request;
    private final boolean async;
    private HttpConfig httpConfig;
    private HttpContext context;


    static {
        FACTORIES = new HashMap<>();
        FACTORIES.put(HttpMethod.GET, HttpGet::new);
        FACTORIES.put(HttpMethod.POST, HttpPost::new);
        FACTORIES.put(HttpMethod.PUT, HttpPut::new);
        FACTORIES.put(HttpMethod.DELETE, ApacheDeleteWithBody::new);
        FACTORIES.put(HttpMethod.PATCH, ApachePatchWithBody::new);
        FACTORIES.put(HttpMethod.OPTIONS, HttpOptions::new);
        FACTORIES.put(HttpMethod.HEAD, HttpHead::new);
    }

    PreparedRequest(HttpRequest request, HttpConfig httpConfig, boolean async, HttpContext context) {
        this.request = request;
        this.httpConfig = httpConfig;
        this.async = async;
        this.context = context;
    }

    HttpRequestBase prepare() {
        HttpRequestBase apacheBaseRequest = buildHttpRequestBase();
        setBody(apacheBaseRequest);
        return apacheBaseRequest;
    }

    private HttpRequestBase buildHttpRequestBase() {
        if (!request.getHeaders().containsKey(USER_AGENT_HEADER)) {
            request.header(USER_AGENT_HEADER, USER_AGENT);
        }
        if (!request.getHeaders().containsKey(ACCEPT_ENCODING_HEADER) && httpConfig.isCompressionRequest()) {
            request.header(ACCEPT_ENCODING_HEADER, "gzip");
        }

        String url = request.getPath().toString();
        if(!HttpUtil.isValidUrl(url)){
            String baseUrl = PnxContext.getString("pnx.http.baseUrl", "http://localhost");
            baseUrl = HttpUtil.trimLast(baseUrl, '/');
            url = baseUrl + url;
        }


        HttpRequestBase apacheBaseRequest = FACTORIES.computeIfAbsent(request.getHttpMethod(), this::register).apply(url);
        request.getHeaders().all().stream().map(this::toEntries).forEach(apacheBaseRequest::addHeader);
        apacheBaseRequest.setConfig(overrideConfig());
        return apacheBaseRequest;
    }

    private RequestConfig overrideConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(request.getConnectTimeout())
                .setSocketTimeout(request.getSocketTimeout())
                .setProxy(RequestOptions.toApacheProxy(request.getProxy()))
                .build();
    }


    private Function<String, HttpRequestBase> register(HttpMethod method) {
        return u -> new ApacheRequestWithBody(method, u);
    }

    private org.apache.http.Header toEntries(Header k) {
        return new BasicHeader(k.getName(), k.getValue());
    }

    private void setBody(HttpRequestBase reqObj) {
        if (request.getBody().isPresent()) {
            ApacheBodyMapper mapper = new ApacheBodyMapper(request);
            HttpEntity entity = mapper.apply();
            if (async) {
                if (reqObj.getHeaders(CONTENT_TYPE) == null || reqObj.getHeaders(CONTENT_TYPE).length == 0) {
                    reqObj.setHeader(entity.getContentType());
                }
                try {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    entity.writeTo(output);
                    NByteArrayEntity en = new NByteArrayEntity(output.toByteArray());
                    ((HttpEntityEnclosingRequestBase) reqObj).setEntity(en);
                } catch (IOException e) {
                    throw new HttpException(e);
                }
            } else {
                ((HttpEntityEnclosingRequestBase) reqObj).setEntity(entity);
            }
        }
    }
}
