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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class ContentType {
    public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", ISO_8859_1);
    public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", ISO_8859_1);
    public static final ContentType APPLICATION_JSON = create("application/json", StandardCharsets.UTF_8);
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream");
    public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", ISO_8859_1);
    public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", ISO_8859_1);
    public static final ContentType APPLICATION_XML = create("application/xml", ISO_8859_1);
    public static final ContentType IMAGE_BMP = create("image/bmp");
    public static final ContentType IMAGE_GIF = create("image/gif");
    public static final ContentType IMAGE_JPEG = create("image/jpeg");
    public static final ContentType IMAGE_PNG = create("image/png");
    public static final ContentType IMAGE_SVG = create("image/svg+xml");
    public static final ContentType IMAGE_TIFF = create("image/tiff");
    public static final ContentType IMAGE_WEBP = create("image/webp");
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", ISO_8859_1);
    public static final ContentType TEXT_HTML = create("text/html", ISO_8859_1);
    public static final ContentType TEXT_PLAIN = create("text/plain", ISO_8859_1);
    public static final ContentType TEXT_XML = create("text/xml", ISO_8859_1);
    public static final ContentType WILDCARD = create("*/*");
    private final String mimeType;

    private final Charset encoding;


    public static ContentType create(String mimeType) {
        return new ContentType(mimeType, null);
    }

    public static ContentType create(String mimeType, Charset charset) {
        return new ContentType(mimeType, charset);
    }

    ContentType(String mimeType, Charset encoding) {
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(mimeType);
        if(encoding != null){
            sb.append("; charset=").append(encoding);
        }
        return sb.toString();
    }

    public String getMimeType() {
        return mimeType;
    }

    public ContentType withCharset(Charset charset) {
        return new ContentType(mimeType, charset);
    }
}
