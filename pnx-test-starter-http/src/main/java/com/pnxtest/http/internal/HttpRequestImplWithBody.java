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
import com.pnxtest.http.api.HttpRequestWithBody;
import com.pnxtest.http.api.MultipartBody;
import com.pnxtest.http.api.RequestBodyEntity;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

public class HttpRequestImplWithBody extends BaseRequest<HttpRequestWithBody> implements HttpRequestWithBody {

	private Charset charSet = StandardCharsets.UTF_8;

	public HttpRequestImplWithBody(HttpConfig httpConfig, HttpMethod method, String url) {
		super(httpConfig, method, url);
	}

	@Override
	public MultipartBody field(String name, Collection<?> value) {
		return new HttpRequestMultiPart(this).field(name, value);
	}

	@Override
	public MultipartBody field(String name, File file) {
		return field(name, file, null);
	}

	@Override
	public MultipartBody field(String name, File file, String contentType) {
		return new HttpRequestMultiPart(this).field(name, file, contentType);
	}

	@Override
	public MultipartBody field(String name, Object value) {
		return field(name, value, null);
	}

	@Override
	public MultipartBody field(String name, Object value, String contentType) {
		return new HttpRequestMultiPart(this).field(name, HttpUtil.nullToEmpty(value), contentType);
	}

	@Override
	public MultipartBody fields(Map<String, Object> parameters) {
		return new HttpRequestMultiPart(this).fields(parameters);
	}

	@Override
	public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
		return new HttpRequestMultiPart(this).field(name, stream, contentType, fileName);
	}

	@Override
	public MultipartBody field(String name, InputStream stream, String fileName) {
		return field(name, stream, ContentType.APPLICATION_OCTET_STREAM, fileName);
	}

	@Override
	public HttpRequestImplWithBody charset(Charset charset) {
		this.charSet = charset;
		return this;
	}


	@Override
	public RequestBodyEntity body(String body) {
		return new HttpRequestBodyRaw(this).body(body);
	}

	@Override
	public RequestBodyEntity body(Object body) {
		return body(httpConfig.getObjectMapper().writeValue(body));
	}

	@Override
	public RequestBodyEntity body(byte[] body) {
		return new HttpRequestBodyRaw(this).body(body);
	}


	@Override
	public Charset getCharset() {
		return charSet;
	}

}
