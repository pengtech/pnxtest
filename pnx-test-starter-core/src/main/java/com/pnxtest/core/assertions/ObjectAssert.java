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

import org.hamcrest.Matchers;

public class ObjectAssert extends AbstractBaseAssert<ObjectAssert, Object>{

    ObjectAssert(Object actual) {
        super(actual, ObjectAssert.class);
    }

    public ObjectAssert hasProperty(String propertyName){
        setKeywords("objectHasProperty");
        assertThat(Matchers.hasProperty(propertyName));
        return this;
    }

    public <E> ObjectAssert hasPropertyWithValue(String propertyName, E value){
        setKeywords("objectHasPropertyWithValue");
        assertThat(Matchers.hasProperty(propertyName, Matchers.equalTo(value)));
        return this;
    }



}
