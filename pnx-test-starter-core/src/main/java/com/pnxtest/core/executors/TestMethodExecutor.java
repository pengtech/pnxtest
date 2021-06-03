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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestMethodExecutor implements IExecutor{
    private final Collection<TestCaseTask> testMethods;

    public TestMethodExecutor(List<TestCaseTask> testMethods) {
        this.testMethods = testMethods;
    }


    //Report the results only when all have completed.
    @Override
    public void execute() throws InterruptedException, ExecutionException {
        int numThreads = Math.min(testMethods.size(), 100);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<MethodResult>> results = executor.invokeAll(testMethods);
        for(Future<MethodResult> result : results){
            MethodResult methodResult = result.get();
            //System.out.println("Result:" + methodResult.getResult());
        }

        executor.shutdown(); //always reclaim resources
    }


}
