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

import com.pnxtest.core.steps.StepLog;
import com.pnxtest.core.api.Status;
import com.pnxtest.core.util.ClassUtil;

import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Implementation result of method level
 * @author nicolas.chen
 */
public class MethodResult {
    private final TestCase testCase;
    private final String id;
    private Status status = Status.NT;
    private long duration; //nano
    private String remark;

    private String operator;
    private Date operatedAt;
    private final long threadId;
    private List<StepLog> stepLogs = new LinkedList<>();

    private List<RepetitionLog> repetitionLogs = new LinkedList<>();


    public MethodResult(TestCase testCase) {
        this.testCase = testCase;
        this.operatedAt = new Date();
        this.operator = System.getProperty("user.name", "PnxTest");
        this.id = buildId();
        this.threadId = Thread.currentThread().getId();
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperatedAt() {
        return operatedAt;
    }

    public void setOperatedAt(Date operatedAt) {
        this.operatedAt = operatedAt;
    }

    public long getThreadId() {
        return threadId;
    }

    public List<StepLog> getStepLogs() {
        return stepLogs;
    }

    public void setStepLogs(List<StepLog> stepLogs) {
        this.stepLogs = stepLogs;
    }

    public List<RepetitionLog> getRepetitionLogs() {
        return repetitionLogs;
    }

    public void setRepetitionLogs(List<RepetitionLog> repetitionLogs) {
        this.repetitionLogs = repetitionLogs;
    }

    private String buildId(){
        StringBuilder sb = new StringBuilder();
        sb.append(testCase.getTestClass().getTestRun().getName());
        sb.append(testCase.getTestClass().getClazz().getName());
        sb.append(testCase.getMethod().getName());

        Parameter[] methodParameters = testCase.getMethod().getParameters();
        for (Parameter p : methodParameters) {
            sb.append(ClassUtil.primitive2Wrapper(p.getType()));
        }

        sb.append(this.getThreadId());
        sb.append(this.getOperatedAt().getTime());

        CRC32 crc = new CRC32();
        crc.update(sb.toString().getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(crc.getValue());
    }
}
