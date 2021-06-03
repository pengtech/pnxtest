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
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

class ApacheResponse extends RawResponseBase {
    private final HttpResponse r;

    public ApacheResponse(HttpResponse r, HttpConfig httpConfig){
        super(httpConfig);
        this.r = r;
    }

    @Override
    public int getStatus(){
        return r.getStatusLine().getStatusCode();
    }

    @Override
    public String getStatusText(){
        return r.getStatusLine().getReasonPhrase();
    }

    @Override
    public Headers getHeaders(){
        Headers h = new Headers();
        Stream.of(r.getAllHeaders()).forEachOrdered(e -> h.add(e.getName(), e.getValue()));
        return h;
    }

    @Override
    public InputStream getContent(){
        try {
            HttpEntity entity = r.getEntity();
            if(entity != null) {
                return entity.getContent();
            }
            return new ByteArrayInputStream(new byte[0]);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    @Override
    public byte[] getContentAsBytes() {
        if(!hasContent()){
            return new byte[0];
        }
        try {
            InputStream is = getContent();
            if (isGzipped(getEncoding())) {
                is = new GZIPInputStream(getContent());
            }
            return getBytes(is);
        } catch (IOException e2) {
            throw new HttpException(e2);
        } finally {
            EntityUtils.consumeQuietly(r.getEntity());
        }
    }

    @Override
    public String getContentAsString() {
        return getContentAsString(null);
    }

    @Override
    public String getContentAsString(String charset) {
        if(!hasContent()){
            return "";
        }
        try {
            String charSet = getCharset(charset);
            return new String(getContentAsBytes(), charSet);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    private String getCharset(String charset) {
        if(charset == null || charset.trim().isEmpty()){
            return getCharSet();
        }
        return charset;
    }

    @Override
    public InputStreamReader getContentReader(){
        return new InputStreamReader(getContent());
    }

    @Override
    public boolean hasContent() {
        return r.getEntity() != null;
    }

    @Override
    public String getContentType() {
        if(hasContent()){
            Header contentType = r.getEntity().getContentType();
            if(contentType != null){
                return contentType.getValue();
            }
        }
        return "";
    }

    @Override
    public String getEncoding() {
        if(hasContent()){
            Header contentType = r.getEntity().getContentEncoding();
            if(contentType != null){
                return contentType.getValue();
            }
        }
        return "";
    }

    private static byte[] getBytes(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
            buf = bos.toByteArray();
        }
        return buf;
    }

    private static boolean isGzipped(String value) {
        return "gzip".equalsIgnoreCase(value.toLowerCase().trim());
    }
}
