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

package com.pnxtest.core.executors;

import java.io.Serializable;
import java.util.*;

/**
 * testrun entity
 *
 * @author nicolas.chen
 */
public class TestRun implements Serializable {
    private static final long serialVersionUID = 4347746635234837084L;
    private String id;
    private final String name;
    private String description;
    private Set<TestClass> testClasses = new LinkedHashSet<>();


    public TestRun(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<TestClass> getTestClasses() {
        return testClasses;
    }

    public void addTestClass(TestClass testClass){
        testClasses.add(testClass);
    }


    //builder
    public static class Builder{
        private final String name; //required

        private String id;
        private String description;
        private Set<TestClass> testClasses = new LinkedHashSet<>();

        public Builder(String name){
            this.name = name;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder description(String description){
            this.description = description;
            return this;
        }

        public Builder addTestClass(TestClass testClass){
            testClasses.add(testClass);
            return this;
        }

        public TestRun build() {
            return new TestRun(this);
        }
    }

    private TestRun(Builder builder){
        id = builder.id;
        name = builder.name;
        description = builder.description;
        testClasses = builder.testClasses;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestRun)) return false;
        TestRun testRun = (TestRun) o;
        return name.equals(testRun.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
