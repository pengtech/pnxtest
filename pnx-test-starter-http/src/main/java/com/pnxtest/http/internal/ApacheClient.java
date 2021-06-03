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

import com.pnxtest.core.steps.PnxSteps;
import com.pnxtest.core.util.ExceptionUtil;
import com.pnxtest.http.api.Client;
import com.pnxtest.http.api.HttpRequest;
import com.pnxtest.http.api.HttpResponse;
import com.pnxtest.http.api.RawResponse;
import com.pnxtest.http.HttpConfig;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.Closeable;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class ApacheClient extends BaseApacheClient implements Client {
    private final HttpClient client;
    private final HttpConfig httpConfig;
    private final PoolingHttpClientConnectionManager manager;
    private final SyncIdleConnectionMonitorThread syncMonitor;
    private final SecurityConfig security;
    private boolean hookIsSet;


    public ApacheClient(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        security = new SecurityConfig(httpConfig);
        manager = security.createManager();
        syncMonitor = new SyncIdleConnectionMonitorThread(manager);
        syncMonitor.start();

        HttpClientBuilder cb = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestOptions.toRequestConfig(httpConfig))
                .setDefaultCredentialsProvider(super.toApacheCreds(httpConfig.getProxy()))
                .setConnectionManager(manager)
                .useSystemProperties();

        setOptions(cb);

        client = cb.build();
    }

    public ApacheClient(HttpClient httpClient, HttpConfig httpConfig, PoolingHttpClientConnectionManager clientManager, SyncIdleConnectionMonitorThread connMonitor) {
        this.client = httpClient;
        this.httpConfig = httpConfig;
        this.manager = clientManager;
        this.syncMonitor = connMonitor;
        this.security = new SecurityConfig(httpConfig);
    }

    private void setOptions(HttpClientBuilder cb) {
        security.configureSecurity(cb);
        if (!httpConfig.isCompressionRequest()) {
            cb.disableContentCompression();
        }

        if (!httpConfig.isFollowRedirects()) {
            cb.disableRedirectHandling();
        }

        httpConfig.getRequestInterceptors().forEach(cb::addInterceptorFirst);
        httpConfig.getResponseInterceptors().forEach(cb::addInterceptorFirst);

        if(httpConfig.getGateway() != null){
            cb.addInterceptorLast((HttpRequestInterceptor) httpConfig.getGateway());
            cb.addInterceptorLast((HttpResponseInterceptor) httpConfig.getGateway());
        }

        registerShutdownHook();
    }

    @Override
    public void registerShutdownHook() {
        if(!hookIsSet) {
            hookIsSet = true;
            Runtime.getRuntime().addShutdownHook(new Thread(this::close, "PnxHttp Client Shutdown!"));
        }
    }


    @Override
    public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer) {
        HttpContext context = new BasicHttpContext();
        HttpRequestBase requestObj = new PreparedRequest(request, httpConfig, false, context).prepare();
        try {
            org.apache.http.HttpResponse apacheResponse = client.execute(requestObj, context);
            HttpResponse<T> httpResponse = transformer.apply(new ApacheResponse(apacheResponse, httpConfig)); //map to httpResponse
            requestObj.releaseConnection();

            pnxTracker(context, request, requestObj, httpResponse);

            return httpResponse;
        } catch (Exception e) {
            pnxTracker(context, request, requestObj, e);
            throw new HttpException(e);
        } finally {
            try{
                requestObj.releaseConnection();
            }catch (Exception e){
                //ignore
            }

        }
    }

    @Override
    public HttpClient getClient() {
        return client;
    }

    public PoolingHttpClientConnectionManager getManager() {
        return manager;
    }

    public SyncIdleConnectionMonitorThread getSyncMonitor() {
        return syncMonitor;
    }

    @Override
    public Stream<Exception> close() {
        return HttpUtil.collectExceptions(HttpUtil.tryCast(client, CloseableHttpClient.class)
                        .map(c -> HttpUtil.tryDo(c, Closeable::close))
                        .filter(Optional::isPresent)
                        .map(Optional::get),
                HttpUtil.tryDo(manager, PoolingHttpClientConnectionManager::close),
                HttpUtil.tryDo(syncMonitor, Thread::interrupt)
        );
    }

    private String buildRequestLine(HttpRequest request,  HttpRequestBase requestObj){
        String reqLine = "";
        try {
            reqLine = URLDecoder.decode(requestObj.getRequestLine().toString(), StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e){
            //ignore
            reqLine = request.getPath().toString();
        }

        return reqLine;
    }

    private StringBuilder buildRequestLogs(HttpContext context, HttpRequest request,  HttpRequestBase requestObj){
        StringBuilder pnxLogs = new StringBuilder();
        pnxLogs.append(buildRequestLine(request, requestObj)).append("\n");

        //headers
        org.apache.http.Header[] reqHeaders = requestObj.getAllHeaders();
        try {
            HttpRequestWrapper requestWrapper = ((HttpRequestWrapper) context.getAttribute(HttpClientContext.HTTP_REQUEST));
            if (requestWrapper != null) {
                reqHeaders = requestWrapper.getAllHeaders();
            }
        }catch (ClassCastException e){
            //ignore
        }

        for(org.apache.http.Header header: reqHeaders){
            pnxLogs.append(header.getName()).append(": ").append(header.getValue()).append("\n");
        }
        //body
        pnxLogs.append( request.getRawBody()).append("\n");

        return pnxLogs;
    }


    private <T> void pnxTracker(HttpContext context, HttpRequest request,  HttpRequestBase requestObj, HttpResponse<T> httpResponse){
        PnxSteps.start(request.getPath().toString(), "HTTP", requestObj.getMethod());
        StringBuilder pnxLogs = buildRequestLogs(context, request, requestObj);

        //===============response===============
        pnxLogs.append("----------response----------").append("\n");
        pnxLogs.append(httpResponse.getStatus()).append(" ").append(httpResponse.getStatusText()).append("\n");
        httpResponse.getHeaders().all().forEach(header -> {
            pnxLogs.append(header.getName()).append(": ").append(header.getValue()).append("\n");
        });

        T resBody = httpResponse.getBody();
        if (resBody != null) {
            if(resBody instanceof File){
                pnxLogs.append(((File) resBody).getName()).append("\n");
            }else{
                String tResBody = resBody.toString();
                if(tResBody.length()>5*1024) {
                    tResBody = "***Response content is too large, not displayed!***";
                }
                pnxLogs.append(tResBody).append("\n");
            }
        }

        if(httpResponse.getParsingError().isPresent()){
            HttpParsingException pe = httpResponse.getParsingError().get();
            pnxLogs.append("----------response parsing error----------").append("\n");
            pnxLogs.append(ExceptionUtil.getRootCauseMessage(pe)).append("\n");
            PnxSteps.error(pnxLogs.toString());
        }else{
            PnxSteps.success(pnxLogs.toString());
        }

        PnxSteps.end();
    }

    private void pnxTracker(HttpContext context, HttpRequest request,  HttpRequestBase requestObj, Exception error){
        //===============request===============
        PnxSteps.start(request.getPath().toString(), "HTTP", requestObj.getMethod());
        StringBuilder pnxLogs = buildRequestLogs(context, request, requestObj);
        pnxLogs.append("----------request interrupted----------").append("\n");
        pnxLogs.append(error.getMessage()).append("\n");
        PnxSteps.error(pnxLogs.toString());
        PnxSteps.end();
    }

}
