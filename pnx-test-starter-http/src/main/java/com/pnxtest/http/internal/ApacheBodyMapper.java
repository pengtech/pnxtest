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
import com.pnxtest.http.api.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

class ApacheBodyMapper {

    private final HttpRequest request;

    ApacheBodyMapper(HttpRequest request){
        this.request = request;
    }


    HttpEntity apply() {
        Optional<Body> body = request.getBody();
        return body.map(this::applyBody).orElseGet(BasicHttpEntity::new);

    }

    private HttpEntity applyBody(Body o) {
        if(o.isEntityBody()){
            return mapToUniBody(o);
        }else {
            return mapToMultipart(o);
        }
    }


    private HttpEntity mapToUniBody(Body b) {
        BodyPart bodyPart = b.bodyPart();
        if(bodyPart == null){
            return new StringEntity("", StandardCharsets.UTF_8);
        } else if(String.class.isAssignableFrom(bodyPart.getPartType())){
            return new StringEntity((String) bodyPart.getValue(), b.getCharset());
        } else {
            return new ByteArrayEntity((byte[])bodyPart.getValue());
        }
    }


    private HttpEntity mapToMultipart(Body body) {
        if (body.isMultiPart()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(body.getCharset());
            builder.setMode(HttpMultipartMode.valueOf(body.getMode().name()));
            for (BodyPart key : body.multiParts()) {
                builder.addPart(key.getName(), apply(key, body));
            }
            return builder.build();
        } else {
            return new UrlEncodedFormEntity(getList(body.multiParts()), body.getCharset());
        }
    }

    private ContentBody apply(BodyPart value, Body body) {
        if (is(value, File.class)) {
            return toFileBody(value, body);
        } else if (is(value, InputStream.class)) {
            return toInputStreamBody(value, body);
        } else if (is(value, byte[].class)) {
            return toByteArrayBody(value);
        } else {
            return toStringBody(value);
        }
    }


    private ContentBody toFileBody(BodyPart value, Body body) {
        File file = (File)value.getValue();
        return new FileBody(file, toApacheType(value.getContentType()));
    }

    private ContentBody toInputStreamBody(BodyPart value, Body body) {
        InputStream part = (InputStream)value.getValue();
        return new StreamBody(part,
                toApacheType(value.getContentType()),
                value.getFileName(),
                value.getName());
    }



    private ContentBody toByteArrayBody(BodyPart value) {
        byte[] part = (byte[])value.getValue();
        return new ByteArrayBody(part,
                toApacheType(value.getContentType()),
                value.getFileName());
    }

    private ContentBody toStringBody(BodyPart value) {
        return new StringBody(String.valueOf(value.getValue()), toApacheType(value.getContentType()));
    }

    private boolean is(BodyPart value, Class<?> cls) {
        return cls.isAssignableFrom(value.getPartType());
    }

    private org.apache.http.entity.ContentType toApacheType(String type) {
        return org.apache.http.entity.ContentType.parse(type);
    }

    static List<NameValuePair> getList(Collection<BodyPart> parameters) {
        List<NameValuePair> result = new ArrayList<>();
        for (BodyPart entry : parameters) {
            result.add(new BasicNameValuePair(entry.getName(), entry.getValue().toString()));
        }
        return result;
    }
}
