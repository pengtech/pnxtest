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

package com.pnxtest.core.steps;

import com.pnxtest.core.ApplicationKeys;
import com.pnxtest.core.exceptions.PnxException;
import com.pnxtest.core.exceptions.StepException;
import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.core.util.StringUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.text.MessageFormat;
import java.util.concurrent.*;

class StepInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String methodClass = methodInvocation.getMethod().getDeclaringClass().getName();
        String methodName = methodInvocation.getMethod().getName();

        String stepName = methodClass + " :: " + methodName;
        Step step = methodInvocation.getMethod().getAnnotation(Step.class);
        int timeThreshold = 0;
        if(step !=null){
            if(!StringUtil.isEmpty(step.value())) {
                stepName = new MessageFormat(step.value()).format(methodInvocation.getArguments());
            }
            timeThreshold = step.timeThreshold();
        }

        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_STEP_NAME, stepName);
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_METHOD_NAME, methodName);


        if(timeThreshold>0){
            PnxSteps.start(stepName);

            ExecutorService executor = Executors.newCachedThreadPool();
            Future<Object> future = executor.submit(() -> {
                while(true){
                    if(Thread.interrupted()){
                        throw new InterruptedException();
                    }

                    try{
                        return methodInvocation.proceed();
                    }catch (Throwable th){
                        throw new StepException(th);
                    }
                }
            });

            try {
                PnxSteps.start(stepName);
                Object result = future.get(timeThreshold, TimeUnit.SECONDS);
                PnxSteps.end();
                return result;
            } catch (TimeoutException | InterruptedException | ExecutionException ex) {
                throw new StepException(String.format("Step running timeout, timeThreshold=%ds", timeThreshold), ex);
            }catch (Exception e){
                throw new PnxException(e);
            } finally {
                future.cancel(true);
                executor.shutdownNow();
            }
        }else {
            try {
                PnxSteps.start(stepName);
                Object result = methodInvocation.proceed();
                PnxSteps.end();
                return result;
            }catch (Throwable throwable){
                throw new StepException(throwable);
            }

        }
    }
}
