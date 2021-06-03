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
import com.pnxtest.http.api.RawResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class RawResponseBase implements RawResponse {

    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
    protected HttpConfig httpConfig;

    protected RawResponseBase(HttpConfig httpConfig){
        this.httpConfig = httpConfig;
    }

    protected String getCharSet() {
        String contentType = getContentType();
        String responseCharset = getCharsetFromContentType(contentType);
        if (responseCharset != null && !responseCharset.trim().equals("")) {
            return responseCharset;
        }
        return httpConfig.getResponseEncoding().name();
    }

    private String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }

        Matcher m = CHARSET_PATTERN.matcher(contentType);
        if (m.find()) {
            return m.group(1).trim().toUpperCase();
        }
        return null;
    }

    @Override
    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

}
