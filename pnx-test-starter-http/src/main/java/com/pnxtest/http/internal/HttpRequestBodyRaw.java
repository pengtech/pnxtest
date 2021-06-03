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

import com.pnxtest.http.api.Body;
import com.pnxtest.http.api.RequestBodyEntity;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;


class HttpRequestBodyRaw extends BaseRequest<RequestBodyEntity> implements RequestBodyEntity {

	private BodyPart body;
	private Charset charSet;
	private String rawBody;

	HttpRequestBodyRaw(HttpRequestImplWithBody httpRequest) {
		super(httpRequest);
		this.charSet = httpRequest.getCharset();
	}


	@Override
	public RequestBodyEntity body(String rawText) {
		this.body = new BodyString(rawText, charSet);
		this.rawBody = rawText;
		return this;
	}

	@Override
	public RequestBodyEntity body(byte[] bodyBytes) {
		this.body = new ByteArrayBody(bodyBytes);
		this.rawBody = Arrays.toString(bodyBytes);
		return this;
	}

	@Override
	public RequestBodyEntity charset(Charset charset) {
		this.charSet = charset;
		return this;
	}

	@Override
	public Optional<Body> getBody() {
		return Optional.of(this);
	}

	@Override
	public String getRawBody() {
		return rawBody;
	}

	@Override
	public Charset getCharset() {
		return charSet;
	}

	@Override
	public boolean isMultiPart() {
		return false;
	}

	@Override
	public boolean isEntityBody() {
		return true;
	}

	@Override
	public BodyPart bodyPart() {
		return body;
	}
}
