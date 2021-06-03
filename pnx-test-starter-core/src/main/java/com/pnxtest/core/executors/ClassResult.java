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

import com.pnxtest.core.api.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Result DTO of class level
 * @author nicolas.chen
 */
public class ClassResult extends AbstractBaseResult {

    private final List<MethodResult> results = new ArrayList<>();
    private final TestClass testClass;


    public ClassResult(TestClass testClass) {
        super();
        this.testClass = testClass;
    }

    public List<MethodResult> getResults() {
        return results;
    }

    public TestClass getTestClass() {
        return testClass;
    }

    public void addResult(MethodResult methodResult) {
        this.results.add(methodResult);
        this.incrementTotal();
        this.incrementDuration(methodResult.getDuration());
        if(methodResult.getStatus() == Status.PASSED){
            this.incrementPassed();
        }

        if(methodResult.getStatus() == Status.FAILED){
            this.incrementFailed();
        }

        if(methodResult.getStatus() == Status.SKIPPED){
            this.incrementSkipped();
        }
    }
}
