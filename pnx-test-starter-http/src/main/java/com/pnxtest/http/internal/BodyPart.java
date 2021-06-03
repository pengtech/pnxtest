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

import java.nio.charset.StandardCharsets;

public abstract class BodyPart<T> implements Comparable { ;
    private final String name;
    private final T value;
    private final String contentType;
    private final Class<?> partType;

    protected BodyPart(T value, String name, String contentType) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
        this.partType = value.getClass();
    }

    public T getValue() {
        return value;
    }

    public Class<?> getPartType(){
        return partType;
    }

    public String getContentType() {
        if(contentType == null){
            if(isFile()){
                return ContentType.APPLICATION_OCTET_STREAM.toString();
            }
            return ContentType.APPLICATION_FORM_URLENCODED.withCharset(StandardCharsets.UTF_8).toString();
        }
        return contentType;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public String getFileName(){
        return null;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof BodyPart){
            return getName().compareTo(((BodyPart)o).getName());
        }
        return 0;
    }


    abstract public boolean isFile();
}
