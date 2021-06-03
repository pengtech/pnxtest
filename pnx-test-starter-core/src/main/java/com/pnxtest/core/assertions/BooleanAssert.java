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

/**
 * Boolean Assertion
 *
 */
public class BooleanAssert extends AbstractBaseAssert<BooleanAssert, Boolean>{

    BooleanAssert(boolean actual){
        super(actual, BooleanAssert.class);
    }

    public BooleanAssert isTrue(){
        setKeywords("isTrue");
        assertThat(Matchers.equalTo(true));
        return this;
    }

    public BooleanAssert isFalse(){
        setKeywords("isFalse");
        assertThat(Matchers.equalTo(false));
        return this;
    }

}
