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

public class CharAssert extends AbstractBaseAssert<CharAssert, Character>{
    private char actual;

    public CharAssert(char actual){
        super(actual, CharAssert.class);
        this.actual = actual;
    }

    public CharAssert isUpperCase(){
        setKeywords("isUpperCase");
        assertThat(Character.isUpperCase(actual), Matchers.equalTo(true));
        return this;
    }

    public CharAssert isLowerCase(){
        setKeywords("isLowerCase");
        assertThat(Character.isLowerCase(actual), Matchers.equalTo(true));
        return this;
    }
}