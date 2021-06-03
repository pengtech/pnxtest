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
 * comparable assertions
 * @param <S>
 * @param <A>
 */
public abstract class AbstractComparableAssert<S, A extends Comparable<A>> extends AbstractBaseAssert<S, A>{
    public AbstractComparableAssert(A actual, Class<S> myself){
        super(actual, myself);
    }


    public S isGreaterThan(A other){
        setKeywords("isGreaterThan");
        assertThat(Matchers.greaterThan(other));
        return myself;
    }

    public S isGreaterThanOrEqualTo(A other){
        setKeywords("isGreaterThanOrEqualTo");
        assertThat(Matchers.greaterThanOrEqualTo(other));
        return myself;
    }

    public S isLessThan(A other){
        setKeywords("isLessThan");
        assertThat(Matchers.lessThan(other));
        return myself;
    }

    public S isLessThanOrEqualTo(A other){
        setKeywords("lessThanOrEqualTo");
        assertThat(Matchers.lessThanOrEqualTo(other));
        return myself;
    }

}
