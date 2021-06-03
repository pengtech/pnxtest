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


import com.pnxtest.http.internal.ContentType;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

public interface HttpRequestWithBody extends HttpRequest<HttpRequestWithBody> {
    HttpRequestWithBody charset(Charset charset);

    MultipartBody field(String name, Collection<?> value);

    MultipartBody field(String name, File file);

    MultipartBody field(String name, File file, String contentType);

    MultipartBody field(String name, Object value);

    MultipartBody field(String name, Object value, String contentType);

    MultipartBody fields(Map<String, Object> parameters);

    MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName);

    MultipartBody field(String name, InputStream stream, String fileName);

    RequestBodyEntity body(String body);

    RequestBodyEntity body(Object body);

    RequestBodyEntity body(byte[] body);

    Charset getCharset();
}
