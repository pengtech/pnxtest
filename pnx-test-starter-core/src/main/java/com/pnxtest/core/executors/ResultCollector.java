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

public class ResultCollector extends AbstractBaseResult {
    private Map<TestRun, RunResult> runsResult = Collections.synchronizedMap(new LinkedHashMap<>());
    private final TestSuite testSuite;

    public ResultCollector(TestSuite testSuite){
        super();
        this.testSuite = testSuite;
    }

    public Map<TestRun, RunResult> getRunsResult() {
        return runsResult;
    }

    public void addRunResult(RunResult runResult) {
        this.runsResult.put(runResult.getTestRun(), runResult );

        this.duration.getAndAdd(runResult.getDuration());
        this.totalCount.getAndAdd(runResult.getTotalCount());
        this.passedCount.getAndAdd(runResult.getPassedCount());
        this.failedCount.getAndAdd(runResult.getFailedCount());
        this.skippedCount.getAndAdd(runResult.getSkippedCount());
    }

    public TestSuite getTestSuite() {
        return testSuite;
    }
}
