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

package com.pnxtest.core.outputting;

import com.pnxtest.core.executors.MethodResult;
import com.pnxtest.core.executors.RunResult;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TestRunInfo implements Serializable {
    private static final long serialVersionUID = -559041460211896831L;

    private final String title;
    private List<MethodResult> testCases = new LinkedList<>();
    private StatsInfo stats = new StatsInfo();


    public TestRunInfo(String title, RunResult runResult) {
        this.title = title;
        this.stats.setTotalCount(runResult.getTotalCount());
        this.stats.setPassedCount(runResult.getPassedCount());
        this.stats.setFailedCount(runResult.getFailedCount());
        this.stats.setSkippedCount(runResult.getSkippedCount());
    }

    public String getTitle() {
        return title;
    }

    public List<MethodResult> getTestCases() {
        return testCases;
    }

    public void addTestCase(MethodResult methodResult) {
        testCases.add(methodResult);
    }

    public void setTestCases(List<MethodResult> testCases) {
        this.testCases = testCases;
    }

    public StatsInfo getStats() {
        return stats;
    }

    public void setStats(StatsInfo stats) {
        this.stats = stats;
    }
}
