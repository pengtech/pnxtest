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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class RunResult extends AbstractBaseResult {
    private final Map<TestClass, ClassResult> results = Collections.synchronizedMap(new LinkedHashMap<>());
    private final TestRun testRun;


    public RunResult(TestRun testRun) {
        super();
        this.testRun = testRun;
    }

    public Map<TestClass, ClassResult> getResults() {
        return results;
    }

    public void addResult(TestClass testClass, ClassResult classResult){
        this.results.put(testClass, classResult);
        this.duration.getAndAdd(classResult.getDuration());
        this.totalCount.getAndAdd(classResult.getTotalCount());
        this.passedCount.getAndAdd(classResult.getPassedCount());
        this.failedCount.getAndAdd(classResult.getFailedCount());
        this.skippedCount.getAndAdd(classResult.getSkippedCount());
    }

    public TestRun getTestRun() {
        return testRun;
    }

    @Override
    public String toString() {
        return "RunResult{" +
                ", result='" + totalCount + '\'' +
                ", duration=" + duration +
                '}';
    }
}
