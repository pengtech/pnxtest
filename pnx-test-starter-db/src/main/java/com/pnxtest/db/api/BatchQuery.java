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
package com.pnxtest.db.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

//batch insert or update
public interface BatchQuery {
    BatchQuery values(Iterator<List<?>> values);
    BatchQuery values(Iterable<List<?>> values);

    BatchQuery namedValues(Iterator<Map<String, ?>> values);
    BatchQuery namedValues(Iterable<Map<String, ?>> values);

    int[] execute();

}
