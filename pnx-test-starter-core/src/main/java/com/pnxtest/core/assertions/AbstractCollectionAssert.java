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

abstract class AbstractCollectionAssert<S, A> extends AbstractBaseAssert<S, A>{

    AbstractCollectionAssert(A actual, Class<S> selfType) {
        super(actual, selfType);
    }

    public S hasSize(int size){
        setKeywords("collectionHasSize");
        assertThat(Matchers.hasSize(size));
        return myself;
    }

    public S isEmpty(){
        setKeywords("collectionIsEmpty");
        assertThat(Matchers.emptyIterable());
        return myself;
    }

    public S isNotEmpty(){
        setKeywords("collectionIsNotEmpty");
        assertThat(Matchers.not(Matchers.emptyIterable()));
        return myself;
    }


    @SafeVarargs
    public final <E> S contains(E... items){
        setKeywords("collectionContains");
        assertThat(Matchers.hasItems(items));
        return myself;
    }

    @SafeVarargs
    public final <E> S doesNotContain(E... items){
        setKeywords("collectionDoesNotContain");
        assertThat(Matchers.not(Matchers.hasItems(items)));
        return myself;
    }

    //contains exactly
    @SafeVarargs
    public final <E> S consistsOf(E... items){
        setKeywords("collectionConsistsOf");
        assertThat(Matchers.containsInAnyOrder(items));
        return myself;
    }

    @SafeVarargs
    public final <E> S consistsOfOrdered(E... items){
        setKeywords("collectionConsistsOfOrdered");
        assertThat(Matchers.contains(items));
        return myself;
    }

}
