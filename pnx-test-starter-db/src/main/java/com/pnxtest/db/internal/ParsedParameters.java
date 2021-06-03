/*
 Copyright (c) 2021-2022
 This file is part of PnxTest framework.

 PnxTest is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License version 3 as
 published by the Free Software Foundation

 PnxTest is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.

 For more information, please contact the author at this address:
 chen.baker@gmail.com
 */
package com.pnxtest.db.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ParsedParameters {
    private final boolean positional;
    private final List<String> parameterNames;

    ParsedParameters(boolean positional, List<String> parameterNames) {
        this.positional = positional;
        this.parameterNames = Collections.unmodifiableList(new ArrayList<>(parameterNames));
    }


    public boolean isPositional() {
        return positional;
    }


    public int getParameterCount() {
        return parameterNames.size();
    }


    public List<String> getParameterNames() {
        return parameterNames;
    }



    public static ParsedParameters named(List<String> names) {
        if (names.contains(ParsedSql.POSITIONAL_PARAM)) {
            throw new IllegalArgumentException("Named parameters "
                    + "list must not contain positional parameter "
                    + "\"" + ParsedSql.POSITIONAL_PARAM + "\"");
        }
        return new ParsedParameters(false, names);
    }


    public static ParsedParameters positional(int count) {
        return new ParsedParameters(true, Collections.nCopies(count, "?"));
    }
}
