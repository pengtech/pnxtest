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

package com.pnxtest.core.assertions;

import java.util.Objects;

public final class TextDescription {
    private final String value;
    private final Object[] args;

    public TextDescription(String value, Object... args) {
        this.value = value == null ? "" : value;
        this.args = (args == null || args.length == 0) ? null : (Object[])args.clone();
    }

    public String value() {
        if(value == null || value.isEmpty()) return null;
        if(args == null || args.length == 0){
            return (value.replace("%", "%%")).replace("%%n", "%n"); //escape percent
        }else{
            return String.format(this.value, this.args);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.value, this.args});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            TextDescription other = (TextDescription)obj;
            return Objects.deepEquals(this.value, other.value) && java.util.Arrays.deepEquals(this.args, other.args);
        }
    }
}
