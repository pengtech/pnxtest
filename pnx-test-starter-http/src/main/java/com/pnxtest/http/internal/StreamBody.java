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

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class StreamBody extends InputStreamBody {
    private final long length;
    private final String name;
    private final String fileName;

    public StreamBody(InputStream in,
                      ContentType contentType,
                      String fileName,
                      String fieldName) {
        super(in, contentType, fileName);
        this.fileName = fileName;
        try {
            this.name = fieldName;
            this.length = in.available();
        }catch (IOException e){
            throw new HttpException(e);
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        super.writeTo(out);
    }
}