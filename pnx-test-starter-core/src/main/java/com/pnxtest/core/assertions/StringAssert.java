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

import java.util.regex.Pattern;

/**
 * String assertions
 *
 */
public class StringAssert extends AbstractBaseAssert<StringAssert, String>{

    public StringAssert(String actual){
        super(actual, StringAssert.class);
    }

    public StringAssert isEqualToIgnoringCase(String expected){
        setKeywords("stringIsEqualToIgnoringCase");
        assertThat(Matchers.equalToIgnoringCase(expected));
        return this;
    }

    public StringAssert contains(String subString){
        setKeywords("stringContains");
        assertThat(Matchers.containsString(subString));
        return this;
    }

    public StringAssert startsWith(String prefix){
        setKeywords("stringStartsWith");
        assertThat(Matchers.startsWith(prefix));
        return this;
    }

    public StringAssert endsWith(String suffix){
        setKeywords("stringEndsWith");
        assertThat(Matchers.endsWith(suffix));
        return this;
    }

    public StringAssert hasLength(int length){
        setKeywords("stringHasLength");
        assertThat(Matchers.hasLength(length));
        return this;
    }

    public StringAssert matches(String regex){
        setKeywords("stringMatches");
        assertThat(Matchers.matchesPattern(regex));
        return this;
    }

    public StringAssert matches(Pattern pattern){
        setKeywords("stringMatches");
        assertThat(Matchers.matchesPattern(pattern));
        return this;
    }

    public StringAssert doesNotMatch(String regex){
        setKeywords("stringDoesNotMatch");
        assertThat(Matchers.not(Matchers.matchesPattern(regex)));
        return this;
    }

    public StringAssert doesNotMatch(Pattern pattern){
        setKeywords("stringDoesNotMatch");
        assertThat(Matchers.not(Matchers.matchesPattern(pattern)));
        return this;
    }

    public StringAssert doesNotStartWith(String prefix){
        setKeywords("stringDoesNotStartWith");
        assertThat(Matchers.not(Matchers.startsWith(prefix)));
        return this;
    }

    public StringAssert doesNotEndWith(String suffix){
        setKeywords("stringDoesNotEndWith");
        assertThat(Matchers.not(Matchers.endsWith(suffix)));
        return this;
    }

    public StringAssert doesNotContain(String subString){
        setKeywords("stringDoesNotContain");
        assertThat(Matchers.not(Matchers.containsString(subString)));
        return this;
    }


}
