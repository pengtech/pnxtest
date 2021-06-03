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

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SelectQuery extends Query<SelectQuery>{
    Optional<Map<String, Object>> asOne();
    List<Map<String, Object>> asList();

    <T> Optional<T> asOne(Class<T> mapToClass);
    <T> List<T> asList(Class<T> mapToClass);

}
