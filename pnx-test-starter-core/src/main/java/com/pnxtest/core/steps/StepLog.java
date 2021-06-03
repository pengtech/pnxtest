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
import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.core.util.StringUtil;

/**
 * Single step log entry
 */
public class StepLog {
    private final String id;
    private final String pId;
    private final long startTime;
    private final long threadId;
    private final String methodName;

    private StepMessage content;
    private String component;
    private String subComponent;
    private String title;

    public static Builder builder(String title){
        return new Builder(title);
    }

    public static class Builder {
        private StepMessage content;
        private String component;
        private String subComponent;
        private final String title;

        public Builder(String title) {
            this.title = StringUtil.truncate(title, 80);
        }

        public Builder withComponent(String component){
            this.component = component;
            return this;
        }

        public Builder withSubComponent(String subComponent){
            this.subComponent = subComponent;
            return this;
        }

        public Builder withContent(StepMessage content){
            this.content = content;
            return this;
        }

        public StepLog build(){
            StepLog stepLog = new StepLog();
            stepLog.title = this.title;
            stepLog.component = this.component;
            stepLog.subComponent = this.subComponent;
            stepLog.content = this.content;
            return stepLog;
        }

    }


    private StepLog(){
        this.startTime = System.currentTimeMillis();
        this.threadId = Thread.currentThread().getId();
        this.id = Thread.currentThread().getId() + "-" + System.nanoTime() + "_" + StepLogger.instance().getLogs().size();
        this.pId = StepLogger.instance().getCurrentPid();
        this.methodName = PnxContext.getString(ApplicationKeys.PNX_CURRENT_METHOD_NAME);
    }


    public String getId() {
        return id;
    }

    public String getpId() {
        return pId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StepMessage getContent() {
        return content;
    }

    public void setStepMessage(StepMessage content) {
        this.content = content;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getSubComponent() {
        return subComponent;
    }

    public void setSubComponent(String subComponent) {
        this.subComponent = subComponent;
    }

}
