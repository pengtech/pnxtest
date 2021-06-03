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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

abstract class AbstractBaseResult {
    protected AtomicInteger totalCount = new AtomicInteger(0);
    protected AtomicInteger passedCount = new AtomicInteger(0);
    protected AtomicInteger failedCount = new AtomicInteger(0);
    protected AtomicInteger skippedCount = new AtomicInteger(0);
    protected AtomicLong duration = new AtomicLong(0);

    private List<Throwable> errors = new ArrayList<>();

    protected final long startTime; //for display

    AbstractBaseResult() {
        this.startTime = System.currentTimeMillis();
    }

    public int getTotalCount() {
        return totalCount.get();
    }

    public void incrementTotal(){
        this.totalCount.getAndIncrement();
    }
    public void incrementTotal(int delta){
        this.totalCount.getAndAdd(delta);
    }

    public int getPassedCount() {
        return passedCount.get();
    }
    public void incrementPassed(){
        this.passedCount.getAndIncrement();
    }
    public void incrementPassed(int delta){
        this.passedCount.getAndAdd(delta);
    }

    public int getFailedCount() {
        return failedCount.get();
    }

    public void incrementFailed(){
        this.failedCount.getAndIncrement();
    }
    public void incrementFailed(int delta){
        this.failedCount.getAndAdd(delta);
    }


    public int getSkippedCount() {
        return skippedCount.get();
    }

    public void incrementSkipped(){
        this.skippedCount.getAndIncrement();
    }
    public void incrementSkipped(int delta){
        this.skippedCount.getAndAdd(delta);
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration.get();
    }
    public void incrementDuration(long delta){
        this.duration.getAndAdd(delta);
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public void addError(Throwable throwable) {
        this.errors.add(throwable);
    }

}
