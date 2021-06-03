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

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.*;

public class TestRunExecutor implements IExecutor{

    private final Collection<TestRunTask> tasks;
    private final ResultCollector resultCollector;

    public TestRunExecutor(Set<TestRunTask> tasks, ResultCollector resultCollector) {
        this.tasks = tasks;
        this.resultCollector = resultCollector;
    }

    //Report the result of each task as it comes in.
    @Override
    public void execute() throws InterruptedException, ExecutionException {
        TestSuite testSuite = this.resultCollector.getTestSuite();
        int threadCount = testSuite.getThreadCount();
        int numThreads = Math.min(tasks.size(), threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CompletionService<RunResult> compService = new ExecutorCompletionService<>(executor);
        for(TestRunTask testRunTask : tasks){
            compService.submit(testRunTask);
        }

        long startTime = System.nanoTime();
        for(TestRunTask task : tasks){
            Future<RunResult> future = compService.take();
            final RunResult runResult = future.get();

            resultCollector.getRunsResult().putIfAbsent(runResult.getTestRun(), runResult);

            resultCollector.incrementTotal(runResult.getTotalCount());
            resultCollector.incrementPassed(runResult.getPassedCount());
            resultCollector.incrementFailed(runResult.getFailedCount());
            resultCollector.incrementSkipped(runResult.getSkippedCount());
        }
        resultCollector.incrementDuration(System.nanoTime() - startTime);
        executor.shutdown(); //always reclaim resources
    }


}
