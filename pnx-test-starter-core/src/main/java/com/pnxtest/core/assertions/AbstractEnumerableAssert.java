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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Map;

public abstract class AbstractEnumerableAssert<S, A> extends AbstractBaseAssert<S, A>{
    protected final S myself;
    protected final A actual;

    AbstractEnumerableAssert(A actual, Class<S> myself) {
        super(actual, myself);
        this.actual = actual;
        this.myself = myself.cast(this);
    }


    public S isNullOrEmpty() {
        setKeywords("mapIsNullOrEmpty");
        assertThat(Matchers.anyOf(Matchers.anEmptyMap(), Matchers.nullValue()));
        return myself;
    }

    public S isEmpty() {
        setKeywords("mapIsEmpty");
        assertThat(Matchers.anEmptyMap());
        return myself;
    }

    public S isNotEmpty() {
        setKeywords("mapIsNotEmpty");
        assertThat(Matchers.not(Matchers.anEmptyMap()));
        return myself;
    }


    public S hasSize(int size){
        setKeywords("mapHasSize");
        assertThat(Matchers.aMapWithSize(size));
        return myself;
    }

    public <K,V> S contains(K key, V value){
        setKeywords("mapHasEntry");
        assertThat(Matchers.hasEntry(key, value));
        return myself;
    }

    public <K,V> S contains(Map.Entry<K,V> entry){
        setKeywords("mapHasEntry");
        assertThat(Matchers.hasEntry(entry.getKey(), entry.getValue()));
        return myself;
    }

    public <K,V> S doesNotContain(K key, V value){
        setKeywords("mapDoesNotHasEntry");
        assertThat(Matchers.not(Matchers.hasEntry(key, value)));
        return myself;
    }

    public <K,V> S doesNotContain(Map.Entry<K,V> entry){
        setKeywords("mapDoesNotHasEntry");
        assertThat(Matchers.not(Matchers.hasEntry(entry.getKey(), entry.getValue())));
        return myself;
    }


    public <K> S containsKey(K key){
        setKeywords("mapContainsKey");
        assertThat(Matchers.hasKey(key));
        return myself;
    }

    public <K> S doesNotContainKey(K key){
        setKeywords("mapDoesNotContainKey");
        assertThat(Matchers.not(Matchers.hasKey(key)));
        return myself;
    }

    public <V> S doesNotContainValue(V value){
        setKeywords("mapDoesNotContainValue");
        assertThat(Matchers.not(Matchers.hasValue(value)));
        return myself;
    }

    @SafeVarargs
    public final <K> S containsKeys(K... keys){
        setKeywords("mapContainsKeys");
        Matcher[] matchers = new Matcher[keys.length];
        for(int i=0;i<keys.length;i++){
            matchers[i] = Matchers.hasKey(keys[i]);
        }
        assertThat(Matchers.allOf(matchers));
        return myself;
    }

}
