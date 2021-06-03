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

package com.pnxtest.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExceptionUtil {
    private ExceptionUtil(){}

    private final static List<String> MASKED_PACKAGES = Arrays.asList(
            "sun.",
            "com.sun",
            "java.",
            "jdk.internal",
            "org.hamcrest",
            "org.apache.http",
            "org.apache.maven.surefire",
            "com.intellij",
            "com.zaxxer",
            "com.pnxtest");

    public static String getExceptionMessage(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        while (throwable != null) {
            if(!StringUtil.isEmpty(throwable.getLocalizedMessage())){
                sb.append(throwable.getLocalizedMessage());
                sb.append("\n");
            }

            throwable = throwable.getCause();
        }

        return sb.toString();

    }

    public static String getRootCauseMessage(Throwable originalTh){
        Throwable th = originalTh;
        if(originalTh.getCause() != null){
            th = originalTh.getCause();
        }

        StackTraceElement[] traceElements = cleanStackTrace(th.getStackTrace());
        if(traceElements.length == 0) return originalTh.getMessage();

        StringBuilder sb = new StringBuilder();
        for(StackTraceElement stackTraceElement: traceElements){
            sb.append(stackTraceElement);
            sb.append("\n");
        }

        return sb.toString();
    }


    private static  StackTraceElement[] cleanStackTrace(StackTraceElement[] stackTrace) {
        List<StackTraceElement> cleanStackTrace = new ArrayList<>();
        for(StackTraceElement element : stackTrace) {
            if (stackTraceIsQualified(element)) {
                cleanStackTrace.add(element);
            }
        }

        if(cleanStackTrace.size()>1){
            cleanStackTrace.remove(cleanStackTrace.size()-1);
        }
        return cleanStackTrace.toArray(new StackTraceElement[0]);
    }

    private static boolean stackTraceIsQualified(StackTraceElement element) {
        if (element.getClassName().contains("$")) {
            return false;
        }
        for(String maskedPackage : MASKED_PACKAGES) {
            if (element.getClassName().startsWith(maskedPackage)) {
                return false;
            }
        }
        return true;
    }

}
