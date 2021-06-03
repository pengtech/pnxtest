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


public abstract class AbstractArrayAssert<S, A, E> extends AbstractBaseAssert<S, A>{

    public AbstractArrayAssert(A actual, Class<S> myself){
        super(actual, myself);
    }

    public S hasSize(int size){
        super.setKeywords("arrayHasSize");
        assertThat(Matchers.arrayWithSize(size));
        return myself;
    }

    public S isEmpty(){
        super.setKeywords("arrayIsEmpty");
        assertThat(Matchers.emptyArray());
        return myself;
    }

    public S isNotEmpty(){
        super.setKeywords("arrayIsNotEmpty");
        assertThat(Matchers.not(Matchers.emptyArray()));
        return myself;
    }

    @SafeVarargs
    public final S contains(E... elements){
        super.setKeywords("arrayContains");
        assertThat(Matchers.arrayContaining(elements));
        return myself;
    }

    @SafeVarargs
    public final S containsInAnyOrder(E... elements){
        super.setKeywords("arrayContainsInAnyOrder");
        assertThat(Matchers.arrayContainingInAnyOrder(elements));
        return myself;
    }

    public S hasItem(E item){
        super.setKeywords("arrayHasItem");
        assertThat(Matchers.hasItemInArray(item));
        return myself;
    }

    public S doesNotHaveItem(E item){
        super.setKeywords("arrayDoesNotHaveItem");
        assertThat(Matchers.not(Matchers.hasItemInArray(item)));
        return myself;
    }


}
