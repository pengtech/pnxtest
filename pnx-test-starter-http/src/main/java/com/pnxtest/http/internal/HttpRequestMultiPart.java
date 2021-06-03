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
import com.pnxtest.http.api.MultipartBody;
import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

class HttpRequestMultiPart extends BaseRequest<MultipartBody> implements MultipartBody {
    private List<BodyPart> parameters = new ArrayList<>();
    private StringBuilder rawBody = new StringBuilder();

    private MultipartMode mode = MultipartMode.BROWSER_COMPATIBLE;
    private Charset charSet;
    private boolean forceMulti = false;

    HttpRequestMultiPart(HttpRequestImplWithBody httpRequest) {
        super(httpRequest);
        this.charSet = httpRequest.getCharset();
    }

    @Override
    public MultipartBody field(String name, String value) {
        addPart(new ParamPart(name, value));
        return this;
    }

    @Override
    public MultipartBody field(String name, String value, String contentType) {
        addPart(name, value, contentType);
        return this;
    }

    @Override
    public MultipartBody field(String name, Collection<?> collection) {
        for (Object current: collection) {
            addPart(name, current, null);
        }
        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream value, ContentType contentType) {
        addPart(new InputStreamPart(name, value, contentType.toString()));
        return this;
    }

    @Override
    public MultipartBody field(String name, File file) {
        addPart(new FilePart(file, name));
        return this;
    }

    @Override
    public MultipartBody field(String name, File file, String contentType) {
        addPart(new FilePart(file, name, contentType));
        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream stream, ContentType contentType, String fileName) {
        addPart(new InputStreamPart(name, stream, contentType.toString(), fileName));

        return this;
    }

    @Override
    public MultipartBody field(String name, InputStream stream, String fileName) {
        addPart(new InputStreamPart(name, stream, ContentType.APPLICATION_OCTET_STREAM.toString(), fileName));
        return this;
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, ContentType contentType, String fileName) {
        addPart(new ByteArrayPart(name, bytes, contentType, fileName));
        return this;
    }

    @Override
    public MultipartBody field(String name, byte[] bytes, String fileName) {
        addPart(new ByteArrayPart(name, bytes, ContentType.APPLICATION_OCTET_STREAM, fileName));
        return this;
    }

    @Override
    public MultipartBody charset(Charset charset) {
        this.charSet = charset;
        return this;
    }

    @Override
    public MultipartBody contentType(String mimeType) {
        header(HttpHeaders.CONTENT_TYPE, mimeType);
        return this;
    }

    @Override
    public MultipartBody mode(MultipartMode value) {
        this.mode = value;
        return this;
    }


    @Override
    public Charset getCharset() {
        return this.charSet;
    }

    public MultipartBody fields(Map<String, Object> fields) {
        if (fields != null) {
            for (Map.Entry<String, Object> param : fields.entrySet()) {
                if (param.getValue() instanceof File) {
                    field(param.getKey(), (File) param.getValue());
                } else {
                    field(param.getKey(), HttpUtil.nullToEmpty(param.getValue()));
                }
            }
        }
        return this;
    }

    public MultipartBody field(String name, Object value, String contentType) {
        addPart(name, value, contentType);
        return this;
    }

    private void addPart(String name, Object value, String contentType) {
        if(value instanceof InputStream){
            addPart(new InputStreamPart(name, (InputStream)value, contentType));
        } else if (value instanceof File) {
            addPart(new FilePart((File)value, name, contentType));
        } else {
            addPart(new ParamPart(name, HttpUtil.nullToEmpty(value), contentType));
        }
    }

    private void addPart(BodyPart bodyPart) {
        parameters.add(bodyPart);
        Collections.sort(parameters);

        String name = bodyPart.getName();
        Object value = bodyPart.getValue();
        String fileName = bodyPart.getFileName();

        if(value instanceof InputStream){
            rawBody.append(name).append("="). append("***InputStream***").append("\n");
        } else if (value instanceof File) {
            try {
                rawBody.append(name).append("=").append(((File) value).getCanonicalPath()).append("\n");
            }catch (IOException e){
                //ignore
            }
        } else {
            String v = HttpUtil.nullToEmpty(value);
            rawBody.append(name).append("="). append(v).append("\n");
        }
    }

    @Override
    public Optional<Body> getBody() {
        return Optional.of(this);
    }

    @Override
    public boolean isMultiPart() {
        return forceMulti || multiParts().stream().anyMatch(BodyPart::isFile);
    }

    @Override
    public boolean isEntityBody() {
        return false;
    }

    @Override
    public Collection<BodyPart> multiParts() {
        return new ArrayList<>(parameters);
    }

    @Override
    public MultipartMode getMode() {
        return mode;
    }

    MultipartBody forceMultiPart(boolean value) {
        forceMulti = value;
        return this;
    }

    @Override
    public String getRawBody(){
        return rawBody.toString();
    }
}
