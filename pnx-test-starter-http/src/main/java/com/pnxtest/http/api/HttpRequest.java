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

package com.pnxtest.http.api;

import com.pnxtest.http.internal.GenericType;
import com.pnxtest.http.internal.HttpMethod;
import com.pnxtest.http.internal.Path;
import com.pnxtest.http.internal.Proxy;
import com.pnxtest.http.internal.Headers;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface HttpRequest<R extends HttpRequest> {

    R routeParam(String name, String value);

    R routeParam(Map<String, Object> params);

    R basicAuth(String username, String password);
    R bearAuth(String token);

    R accept(String value);

    R header(String name, String value);

    R headerReplace(String name, String value);

    R headers(Map<String, String> headerMap);

    R queryString(String name, Object value);

    R queryString(String name, Collection<?> value);

    R queryString(Map<String, Object> parameters);

    R usingObjectMapper(ObjectMapper mapper);
    R usingProxy(Proxy proxy);
    R connectTimeout(int millSeconds);
    R socketTimeout(int millSeconds);
    R verifySSL(boolean required);


    HttpResponse<String> asString();

    <T> HttpResponse<T> asObject(Class<? extends T> responseClass);
    <T> HttpResponse<T> asObject(GenericType<T> genericType);
    <T> HttpResponse<T> asObject(Function<RawResponse, T> function);


    HttpResponse<File> asFile(String fileSavePath);


    HttpResponse asEmpty();

    Path getPath();
    HttpMethod getHttpMethod();
    Headers getHeaders();

    default Optional<Body> getBody(){
        return Optional.empty();
    }
    default String getRawBody() { return "";}

    int getConnectTimeout();
    int getSocketTimeout();
    Proxy getProxy();
    ObjectMapper getObjectMapper();

}
