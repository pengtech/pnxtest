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
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Run N tasks in parallel
 *
 */
public class TestClassExecutor implements IExecutor{
    private final Collection<TestClassTask> tasks;
    private final ResultCollector resultCollector;

    public TestClassExecutor(Set<TestClassTask> tasks, ResultCollector resultCollector) {
        this.tasks = tasks;
        this.resultCollector = resultCollector;
    }

    //Report the results only when all have completed.
    public void execute2() throws InterruptedException, ExecutionException {
        TestSuite testSuite = this.resultCollector.getTestSuite();
        int threadCount = testSuite.getThreadCount();
        int numThreads = Math.min(tasks.size(), threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<ClassResult>> results = executor.invokeAll(tasks);
        for(Future<ClassResult> result : results){
            ClassResult clazzResult = result.get();
        }

        executor.shutdown(); //always reclaim resources
    }

    //Report the result of each task as it comes in.
    @Override
    public void execute() throws InterruptedException, ExecutionException {
        TestSuite testSuite = this.resultCollector.getTestSuite();
        int threadCount = testSuite.getThreadCount();
        int numThreads = Math.min(tasks.size(), threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CompletionService<ClassResult> compService = new ExecutorCompletionService<>(executor);

        for(TestClassTask task : tasks){
            compService.submit(task);
        }

        long startTime = System.nanoTime();
        for(TestClassTask task : tasks){
            Future<ClassResult> future = compService.take();
            final ClassResult classResult = future.get();

            TestRun testRun = classResult.getTestClass().getTestRun();
            RunResult runResult = resultCollector.getRunsResult().getOrDefault(testRun, new RunResult(testRun));
            resultCollector.getRunsResult().putIfAbsent(testRun, runResult);
            runResult.addResult(classResult.getTestClass(), classResult);

            resultCollector.incrementTotal(classResult.getTotalCount());
            resultCollector.incrementPassed(classResult.getPassedCount());
            resultCollector.incrementFailed(classResult.getFailedCount());
            resultCollector.incrementSkipped(classResult.getSkippedCount());
        }

        resultCollector.incrementDuration(System.nanoTime() - startTime);
        executor.shutdown(); //always reclaim resources
    }


}
