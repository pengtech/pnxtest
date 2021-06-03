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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class HttpUtil {
    private HttpUtil(){}

    public static String nullToEmpty(Object v) {
        if(v == null){
            return "";
        }
        return String.valueOf(v);
    }

    public static String encode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new HttpException(e);
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String toBasicAuthValue(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public static String toBearAuthValue(String token) {
        return "Bearer " + token;
    }


    public static FileInputStream getFileInputStream(String location){
        try {
            return new FileInputStream(location);
        } catch (FileNotFoundException e) {
            throw new HttpConfigException(e);
        }
    }


    public static String trimLast(String source, char ch) {
        if(source == null) return "";

        String ret = source;
        int len = ret.length();
        if (len > 0 && source.charAt(len - 1) == ch) {
            ret = ret.substring(0, len - 1);
        }
        return ret;
    }

    public static boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException exception) {
            return false;
        }
    }


    public static <T, M extends T> Optional<M> tryCast(T original, Class<M> target) {
        if (original != null && target.isAssignableFrom(original.getClass())) {
            return Optional.of((M) original);
        }
        return Optional.empty();
    }

    @SafeVarargs
    public static Stream<Exception> collectExceptions(Optional<Exception>... ex) {
        return Stream.of(ex).flatMap(HttpUtil::stream);
    }

    public static <T> Stream<T> stream(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }

    public static <T> Optional<Exception> tryDo(T c, ExConsumer<T> consumer) {
        try {
            if (Objects.nonNull(c)) {
                consumer.accept(c);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    @FunctionalInterface
    public interface ExConsumer<T>{
        void accept(T t) throws Exception;
    }

}
