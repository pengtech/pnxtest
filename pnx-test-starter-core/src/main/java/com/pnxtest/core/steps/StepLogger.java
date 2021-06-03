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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

final class StepLogger {
    //private static final ThreadLocal<LogCollector> logCollector = ThreadLocal.withInitial(() ->new LogCollector());
    private static final InheritableThreadLocal<LogCollector> logCollector = new InheritableThreadLocal<LogCollector>(){
        @Override
        protected LogCollector initialValue() {
            return new LogCollector();
        }
    };

    private static StepLogger instance;
    private static final Object mutex = new Object();

    private StepLogger() {}

    public static synchronized StepLogger instance(){
        StepLogger result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new StepLogger();
            }
        }
        return result;
    }

    public void start(String title){
        StepLog stepLog = StepLog.builder(title).build();
        logCollector.get().addStepLog(stepLog);
    }

    public void start(String title, String component, String subComponent){
        StepLog stepLog = StepLog.builder(title).withComponent(component).withSubComponent(subComponent).build();
        logCollector.get().addStepLog(stepLog);
    }


    public void end(){
        logCollector.get().exitCurrentStep();
    }

    public void success(String msg){
        LogCollector logCollector0 = logCollector.get();
        StepLog stepLog = logCollector0.getCurrentStep();
        stepLog.setStepMessage(StepMessage.success(msg));
    }

    public void info(String msg){
        logCollector.get().getCurrentStep().setStepMessage(StepMessage.info(msg));
    }

    public void error(String msg){
        logCollector.get().getCurrentStep().setStepMessage(StepMessage.error(msg));
    }

    public void warning(String msg){
        logCollector.get().getCurrentStep().setStepMessage(StepMessage.warning(msg));
    }


    public void pause(){
        logCollector.get().pause();
    }

    public void resume(){
        logCollector.get().resume();
    }

    public void reset(){
        logCollector.get().reset();
    }

    public List<StepLog> getLogs(){
        return logCollector.get().getStepLogs();
    }
    public StepLog getCurrentStep(){
        return logCollector.get().getCurrentStep();
    }

    public String getCurrentPid(){
        return logCollector.get().getCurrentPid();
    }



    private static class LogCollector{
        private List<StepLog> stepLogs = Collections.synchronizedList(new ArrayList<>());
        private StepLog currentStep;
        private Stack<String> pidIndex = new Stack<>();
        private boolean disabled;
        private int depth = 0;
        private int Max_Cycle_Count = 5;

        LogCollector(){
        }

        public List<StepLog> getStepLogs() {
            return stepLogs;
        }

        public void addStepLog(StepLog stepLog) {
            if(disabled) return;

            //recursion
            if(currentStep != null) {
                StepLog parentStep = currentStep;
                if (stepLog.getpId().equals(parentStep.getId()) && stepLog.getMethodName().equals(parentStep.getMethodName()) ) {
                    if(depth > Max_Cycle_Count) return;
                    if (depth == Max_Cycle_Count) {
                        StepMessage stepMessage = StepMessage.warning("Recursion or cycle method detected, logs were ignored!");
                        //StepLog warningStepLog = StepLog.builder(currentStep.getTitle()).withContent(stepMessage).build();
                        //this.stepLogs.add(warningStepLog);

                        stepLog.setStepMessage(stepMessage);
                        //currentStep = warningStepLog;
                        //pidIndex.push(warningStepLog.getId());
                    }
                    depth++;
                } else {
                    depth=0;
                }
            }

            this.stepLogs.add(stepLog);

            currentStep = stepLog;
            pidIndex.push(stepLog.getId());
        }

        public StepLog getCurrentStep() {
            return currentStep;
        }

        public void exitCurrentStep(){
            if(pidIndex.size() == 0) return;
            pidIndex.pop();
            int logCunt = stepLogs.size();
            if(logCunt>1){
                currentStep = stepLogs.get(stepLogs.size() - 2);
            }else{
                currentStep = null;
            }
        }

        public String getCurrentPid() {
            if(pidIndex.size() == 0) return "0";
            return pidIndex.peek();
        }

        public void reset(){
            stepLogs.clear();
            pidIndex.clear();
            disabled = false;
            depth = 0;
        }

        public void pause(){
            disabled = true;
        }

        public void resume(){
            disabled = false;
        }
    }

}
