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

import com.pnxtest.core.api.IModConfig;
import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.http.api.GetRequest;
import com.pnxtest.http.api.HttpRequestWithBody;
import com.pnxtest.http.api.IHttpConfig;
import com.pnxtest.http.internal.HttpMethod;
import com.pnxtest.http.internal.HttpRequestImplWithBody;
import com.pnxtest.http.internal.HttpRequestImplWithoutBody;

import java.util.Set;

public class PnxHttp {
    private PnxHttp(){}

    public static GetRequest get(String url) {
        return new HttpRequestImplWithoutBody(getCurrentConfig(), HttpMethod.GET, url);
    }

    public static HttpRequestWithBody post(String url) {
        return new HttpRequestImplWithBody(getCurrentConfig(), HttpMethod.POST, url);
    }

    public static HttpRequestWithBody delete(String url) {
        return new HttpRequestImplWithBody(getCurrentConfig(), HttpMethod.DELETE, url);
    }

    public static HttpRequestWithBody put(String url) {
        return new HttpRequestImplWithBody(getCurrentConfig(), HttpMethod.PUT, url);
    }

    public static GetRequest head(String url) {
        return new HttpRequestImplWithoutBody(getCurrentConfig(), HttpMethod.HEAD, url);
    }

    public static GetRequest options(String url) {
        return new HttpRequestImplWithoutBody(getCurrentConfig(), HttpMethod.OPTIONS, url);
    }

    public static HttpRequestWithBody patch(String url) {
        return new HttpRequestImplWithBody(getCurrentConfig(), HttpMethod.PATCH, url);
    }


    private static HttpConfig currentConfig;
    private static final Object mutex = new Object();
    private static synchronized HttpConfig getCurrentConfig(){
        HttpConfig tConfig = currentConfig;
        if (tConfig == null) {
            synchronized (mutex) {
                tConfig = currentConfig;
                if (tConfig == null)
                    currentConfig = tConfig =createCurrentConfig();
            }
        }
        return currentConfig;
    }

    private static HttpConfig createCurrentConfig(){
        HttpConfig tConfig = HttpConfig.builder().build();//default configuration
        Set<IModConfig> beans = PnxContext.getConfigBeans();
        for(IModConfig bean: beans){
            if(bean instanceof IHttpConfig){
                tConfig = ((IHttpConfig) bean).accept();
                break;
            }
        }

        return tConfig;
    }

}
