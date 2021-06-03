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


import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.pnxtest.core.AppInfo;
import com.pnxtest.core.ApplicationKeys;
import com.pnxtest.core.executors.TestSuite;
import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.core.executors.ClassResult;
import com.pnxtest.core.executors.MethodResult;
import com.pnxtest.core.executors.ResultCollector;
import com.pnxtest.core.executors.RunResult;
import com.pnxtest.core.helper.PnxHelper;
import com.pnxtest.core.api.Status;
import com.pnxtest.core.util.DateUtil;
import com.pnxtest.core.util.StringUtil;

import java.util.List;
import java.util.logging.Logger;

public class ConsoleLog {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ConsoleLog(){}

    public static void printAppInfo(){
        System.out.println("===============================================================================");
        System.out.println(Ansi.colorize("****** PnxTest Framework - "+ AppInfo.version+" - https://pnxtest.com ******", Attribute.BOLD()));
        System.out.println("===============================================================================");
    }

    public static void printTestSuiteOnStart(TestSuite testSuite){
        System.out.println(String.format("<%s> starts running...", testSuite.getName()));
        System.out.println(String.format("Target file: %s", PnxContext.getProperty(ApplicationKeys.PNX_TARGET_FILE)));
        System.out.println(String.format("Configuration location: %s", PnxContext.getProperty(ApplicationKeys.PNX_ENVIRONMENT_PATH)));
        System.out.println(String.format("Environment: %s", PnxContext.getTestEnvironmentId()));
        System.out.println(String.format("Run Mode: %s", runModeFormat(testSuite.getRunMode())));
        System.out.println(String.format("Threads: %s", testSuite.getThreadCount()));
        System.out.println("===============================================================================");
    }

    private static String runModeFormat(TestSuite.RunMode runMode){
        if(runMode == TestSuite.RunMode.testParalleled) {
            return "paralleled(testRun)";
        }

        if(runMode == TestSuite.RunMode.classParalleled) {
            return "paralleled(testClass)";
        }

        return "sequence";
    }

    public static void printTestRunOnStart(RunResult runResult){
        System.out.println(Ansi.colorize(runResult.getTestRun().getName(), Attribute.BLUE_TEXT(), Attribute.BOLD()));
    }

    public static void printTestRunOnFinish(RunResult runResult){
        System.out.println();
    }

    public static void printTestSuiteOnFinish(ResultCollector resultCollector) {
       //System.out.println();
        System.out.println("===============================================================================");
        System.out.println(String.format("Test done within : %s", DateUtil.breakDownDurationToHuman(resultCollector.getDuration())));
        System.out.println(String.format("           Total : %s", resultCollector.getTotalCount()));
        System.out.println(String.format("          Passed : %s", resultCollector.getPassedCount()));
        System.out.println(String.format("          Failed : %s", resultCollector.getFailedCount()));
        System.out.println(String.format("         Skipped : %s", resultCollector.getSkippedCount()));
        System.out.println(String.format("      Outputting : %s", PnxContext.getProperty(ApplicationKeys.PNX_OUTPUTTING_PATH)));
        System.out.println(String.format("     Html report : %s", "index.html"));
        System.out.println(String.format("      Error logs : %s", "pnx-error.log"));
        System.out.println("===============================================================================");
        System.out.println();
    }


    public static void printTestClassOnStart(ClassResult classResult){
        System.out.println(Ansi.colorize(classResult.getTestClass().getClazz().getName(), Attribute.BRIGHT_CYAN_TEXT()));
    }

    public static void printTestClassOnFinish(ClassResult classResult){
        System.out.print('\r');
        List<MethodResult> methodResults = classResult.getResults();
        for(MethodResult methodResult:methodResults){
            String result;
            if(methodResult.getStatus() == Status.PASSED){
                result = Ansi.colorize(String.format("%1$-8s", methodResult.getStatus()), Attribute.GREEN_TEXT());
            }else if(methodResult.getStatus() == Status.FAILED){
                result = Ansi.colorize(String.format("%1$-8s", methodResult.getStatus()), Attribute.RED_TEXT());
            }else if(methodResult.getStatus() == Status.SKIPPED){
                result = Ansi.colorize(String.format("%1$-8s", methodResult.getStatus()), Attribute.YELLOW_TEXT());
            }else{
                result = Ansi.colorize(String.format("%1$-8s", methodResult.getStatus()), Attribute.BRIGHT_CYAN_TEXT());
            }
            String name = StringUtil.rightPad(methodResult.getTestCase().getName(), 60, '.');
            String duration = String.format("[%1$9s]", DateUtil.breakDownDurationToHumanInSecond(methodResult.getDuration()));

            System.out.println("["+result+"] "+name+duration);
        }
    }


    public static void printTestCaseOnStart(MethodResult methodResult){
        System.out.print(Ansi.colorize(methodResult.getTestCase().getName() + " >>> running...", Attribute.WHITE_TEXT()));
    }

    public static void printTestCaseOnFinish(MethodResult methodResult){
        TestSuite.RunMode runMode = PnxContext.getBean(ApplicationKeys.PNX_RUN_MODE, TestSuite.RunMode.class);
        boolean delay = true;
        if(runMode != TestSuite.RunMode.sequence){
            delay = false;
        }

        if(delay) {
            PnxHelper.await(100);
        }
        System.out.print('\r');
        String title = methodResult.getTestCase().getName() + " >>> " + methodResult.getStatus();
        if(methodResult.getStatus() == Status.PASSED){
            System.out.print(Ansi.colorize(title, Attribute.BRIGHT_GREEN_TEXT()));
        }else if(methodResult.getStatus() == Status.FAILED){
            System.out.print(Ansi.colorize(title, Attribute.BRIGHT_RED_TEXT()));
        }else if(methodResult.getStatus() == Status.SKIPPED){
            System.out.print(Ansi.colorize(title, Attribute.BRIGHT_YELLOW_TEXT()));
        }else{
            System.out.print(Ansi.colorize(title, Attribute.BRIGHT_CYAN_TEXT()));
        }
        if(delay) {
            PnxHelper.await(400);
        }
        System.out.print('\r');
    }

}
