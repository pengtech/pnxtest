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

package com.pnxtest.http.api;


import com.pnxtest.http.internal.HttpParsingException;
import com.pnxtest.http.internal.Headers;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;


public interface HttpResponse<T> {

    int getStatus();

    String getStatusText();

    Headers getHeaders();

    T getBody();

    Optional<HttpParsingException> getParsingError();

    <V> V mapBody(Function<T, V> func);


    <V> HttpResponse<V> map(Function<T, V> func);


    HttpResponse<T> ifSuccess(Consumer<HttpResponse<T>> consumer);

    HttpResponse<T> ifFailure(Consumer<HttpResponse<T>> consumer);

    <E> HttpResponse<T> ifFailure(Class<? extends E> errorClass, Consumer<HttpResponse<E>> consumer);

    boolean isSuccess();

    <E> E mapError(Class<? extends E> errorClass);
}
