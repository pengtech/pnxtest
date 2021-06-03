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

import com.pnxtest.core.exceptions.PnxException;
import com.pnxtest.core.outputting.ConsoleLog;
import com.pnxtest.core.util.MethodUtil;

public class TestClassTask implements Task<ClassResult> {
    private final TestClass testClass;

    public TestClassTask(TestClass testClass){
        this.testClass = testClass;
    }

    @Override
    public ClassResult run(){
        ClassResult classResult = new ClassResult(testClass);
        ConsoleLog.printTestClassOnStart(classResult);
        //========== BeforeAll Methods ==========
        for (TestCase bfMethod : testClass.getSortedBeforeAllMethods()) {
            try {
                MethodUtil.invokeJvmMethod(testClass.getInstanceObj(),null, bfMethod.getMethod());
            }catch (Throwable throwable){
                PnxException pnxException = new PnxException(String.format("@BeforeAll[%s]", bfMethod.getName()), throwable);
                testClass.getErrors().add(pnxException);
                classResult.addError(pnxException);
                break;
            }
        }

        for(TestCase testCase: testClass.getSortedTestMethods()){
            classResult.addResult( new TestCaseTask(testCase).run() );
        }

        //========== AfterAll Methods ==========
        for (TestCase afMethod : testClass.getSortedAfterAllMethods()) {
            try {
                MethodUtil.invokeJvmMethod(testClass.getInstanceObj(),null, afMethod.getMethod());
            }catch (Throwable throwable){
                PnxException pnxException = new PnxException(String.format("@AfterAll[%s]", afMethod.getName()), throwable);
                testClass.getErrors().add(pnxException);
                classResult.addError(pnxException);
            }
        }

        ConsoleLog.printTestClassOnFinish(classResult);
        return classResult;
    }

    @Override
    public ClassResult call() throws Exception {
        return run();
    }

    public TestClass getTestClass() {
        return testClass;
    }
}
