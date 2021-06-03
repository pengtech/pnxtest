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


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pnxtest.http.api.ObjectMapper;

import java.io.IOException;

public class JacksonObjectMapper implements ObjectMapper {

    private final com.fasterxml.jackson.databind.ObjectMapper om;

    public JacksonObjectMapper() {
        this(new com.fasterxml.jackson.databind.ObjectMapper());
        om.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    }

    public JacksonObjectMapper(com.fasterxml.jackson.databind.ObjectMapper om){
        this.om = om;
    }

    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        try {
            return om.readValue(value, valueType);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    @Override
    public <T> T readValue(String value, GenericType<T> genericType) {
        try {
            return om.readValue(value,  om.constructType(genericType.getType()));
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    @Override
    public String writeValue(Object value) {
        try {
            return om.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new HttpException(e);
        }
    }

}
