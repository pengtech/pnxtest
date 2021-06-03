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

import com.pnxtest.http.api.ObjectMapper;
import com.pnxtest.http.api.RawResponse;

import java.util.Optional;
import java.util.function.Function;


class ObjectResponse<T> extends BaseResponse<T> {
    private final T body;
    private final ObjectMapper om;

    ObjectResponse(ObjectMapper om, RawResponse response, Class<? extends T> to) {
        super(response);
        this.om = om;
        this.body = readBody(response)
                .map(s -> getBody(s, e -> om.readValue(e, to)))
                .orElse(null);
    }

    ObjectResponse(ObjectMapper om, RawResponse response, GenericType<? extends T> to){
        super(response);
        this.om = om;
        this.body = readBody(response)
                .map(s -> getBody(s, e -> om.readValue(e, to)))
                .orElse(null);
    }

    private Optional<String> readBody(RawResponse response) {
        if(!response.hasContent()){
            return Optional.empty();
        }
        return Optional.of(response.getContentAsString());
    }

    private T getBody(String b, Function<String, T> func){
        try {
            return func.apply(b);
        } catch (RuntimeException e) {
            setParsingException(b, e);
            return null;
        }
    }

    @Override
    public T getBody() {
        return body;
    }
}
