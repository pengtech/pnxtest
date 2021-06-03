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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path {
    private String url;


    Path(String url) {
        this.url = url;
    }

    public void param(Map<String, Object> params) {
        params.forEach((key, value) -> param(key, String.valueOf(value)));
    }

    public void param(String name, String value) {
        Matcher matcher = Pattern.compile("\\{" + name + "\\}").matcher(url);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        if (count == 0) {
            throw new HttpException("Can't find route parameter name \"" + name + "\"");
        }
        this.url = url.replaceAll("\\{" + name + "\\}", HttpUtil.encode(value));
    }

    public void queryString(String name, Collection<?> value){
        for (Object cur : value) {
            queryString(name, cur);
        }
    }

    public void queryString(String name, Object value) {
        StringBuilder queryString = new StringBuilder();
        if (url.contains("?")) {
            queryString.append("&");
        } else {
            queryString.append("?");
        }
        try {
            queryString.append(URLEncoder.encode(name, "UTF-8"));
            if(value != null) {
                queryString.append("=").append(URLEncoder.encode(String.valueOf(value), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new HttpException(e);
        }
        url += queryString.toString();
    }

    public void queryString(Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                queryString(param.getKey(), param.getValue());
            }
        }
    }


    @Override
    public String toString() {
        return url;
    }
}
