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

import com.pnxtest.core.steps.PnxSteps;
import com.pnxtest.core.util.StringUtil;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

abstract class AbstractBaseAssert<S, A>{
    protected TextDescription description;
    protected String keywords;
    protected final A actual;
    protected final S myself;

    AbstractBaseAssert(A actual, Class<S> selfType){
        this.actual = actual;
        this.myself = selfType.cast(this);
    }

    public S as(String description){
        this.description = new TextDescription(description);
        return myself;
    }

    public String getDescription(){
       return description!=null?description.value():"";
    }

    protected void assertThat(Matcher matcher){
        boolean isMatched = matcher.matches(actual);
        String descText = getDescriptionFromMatcher(matcher);

        PnxSteps.start(getDescription(), "Assert", getKeywords());
        if (!isMatched) {
            PnxSteps.error(descText);
            PnxSteps.end();
            throw new AssertionError(descText);
        } else {
            PnxSteps.success(descText);
            PnxSteps.end();
        }
    }

    protected <T> void assertThat(T actual, Matcher<? super T> matcher){
        boolean isMatched = matcher.matches(actual);
        String descText = getDescriptionFromMatcher(matcher);

        PnxSteps.start(getDescription(), "Assert", getKeywords());
        if(!isMatched){
            PnxSteps.error(descText);
            PnxSteps.end();
            throw new AssertionError(descText);
        }else{
            PnxSteps.success(descText);
            PnxSteps.end();
        }
    }

    protected String getKeywords() {
        return keywords;
    }

    protected void setKeywords(String keywords) {
        this.keywords = keywords;
    }


    private <T> String getDescriptionFromMatcher(Matcher<T> matcher){
        Description desc = new StringDescription();

        desc.appendText("Expected: ")
                .appendDescriptionOf(matcher)
                .appendText(System.lineSeparator())
                .appendText("Actual: ");

        matcher.describeMismatch(actual, desc);

        String  s = desc.toString();
        if (s.endsWith("Actual: ")) {
            if(actual.getClass().isArray()){
                s = String.format(s + "%s", Arrays.toString((T[])actual));
            }else{
                s = String.format(s + "%s", actual);
            }

        }

        int pos = s.indexOf("Actual:");
        return s.substring(0, pos) + StringUtil.truncate(StringUtil.removeNewLine(s.substring(pos)), 4096);
    }

    //================================
    //Any
    //================================
    public S isNull(){
        this.setKeywords("isNull");
        assertThat(Matchers.nullValue());
        return myself;
    }

    public S isNotNull(){
        this.setKeywords("isNotNull");
        assertThat(Matchers.notNullValue());
        return myself;
    }

    public S isA(Class<?> type){
        this.setKeywords("isA");
        assertThat(Matchers.isA(type));
        return myself;
    }

    public S isEqualTo(A expectedObject){
        this.setKeywords("isEqualTo");
        assertThat(Matchers.equalTo(expectedObject));
        return myself;
    }

    public  S isNotEqualTo(A expectedObject){
        this.setKeywords("isNotEqualTo");
        assertThat(Matchers.not(Matchers.equalTo(expectedObject)));
        return myself;
    }

    public S isSameInstanceAs(A target){
        this.setKeywords("isSameInstanceAs");
        assertThat(Matchers.sameInstance(target));
        return myself;
    }

    public S isNotSameInstanceAs(A target){
        this.setKeywords("isNotSameInstanceAs");
        assertThat(Matchers.sameInstance(target));
        return myself;
    }

    public <E> S isIn(E[] elements){
        this.setKeywords("isIn");
        assertThat(Matchers.in(elements));
        return myself;
    }

    public <E> S isNotIn(E[] elements){
        this.setKeywords("isNotIn");
        assertThat(Matchers.not(Matchers.in(elements)));
        return myself;
    }

    public <E> S isIn(Collection<E> elements){
        this.setKeywords("isIn");
        assertThat(Matchers.in(elements));
        return myself;
    }

    public <E> S isNotIn(List<E> elements){
        this.setKeywords("isNotIn");
        assertThat(Matchers.not(Matchers.in(elements)));
        return myself;
    }

}
