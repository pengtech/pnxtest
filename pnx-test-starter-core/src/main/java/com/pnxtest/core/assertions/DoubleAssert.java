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

public class DoubleAssert extends AbstractComparableAssert<DoubleAssert, Double>{

    public DoubleAssert(Double actual) {
        super(actual, DoubleAssert.class);
    }


    public DoubleAssert isCloseTo(double expected, double delta){
        setKeywords("doubleIsCloseTo");
        assertThat(Matchers.closeTo(expected, delta));
        return this;
    }

    public DoubleAssert isCloseTo(Double expected, Double delta){
        setKeywords("doubleIsCloseTo");
        assertThat(Matchers.closeTo(expected, delta));
        return this;
    }

    public DoubleAssert isNotANumber(){
        setKeywords("doubleIsNotANumber");
        assertThat(Matchers.notANumber());
        return this;
    }

}
