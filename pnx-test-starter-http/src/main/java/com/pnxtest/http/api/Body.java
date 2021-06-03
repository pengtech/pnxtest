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


import com.pnxtest.http.internal.BodyPart;
import com.pnxtest.http.internal.MultipartMode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;

public interface Body {
    boolean isMultiPart();

    boolean isEntityBody();

    default Charset getCharset(){
        return StandardCharsets.UTF_8;
    }

    default Collection<BodyPart> multiParts(){
        return Collections.emptyList();
    }

    default BodyPart bodyPart(){
        return null;
    }

    default MultipartMode getMode(){
        return MultipartMode.BROWSER_COMPATIBLE;
    }

}
