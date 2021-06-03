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

import com.pnxtest.http.api.RawResponse;

class BasicResponse<T> extends BaseResponse<T> {
    private final T body;

    public BasicResponse(RawResponse httpResponse, T body) {
        super(httpResponse);
        this.body = body;
    }

    public BasicResponse(RawResponse httpResponse, String ogBody, RuntimeException ex) {
        this(httpResponse, null);
        setParsingException(ogBody, ex);
    }

    @Override
    public T getBody() {
        return body;
    }
}
