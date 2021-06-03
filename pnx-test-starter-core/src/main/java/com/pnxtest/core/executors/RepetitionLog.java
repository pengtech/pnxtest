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


import com.pnxtest.core.api.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * repetition log entity
 * author: nicolas.chen
 */
public final class RepetitionLog {
    private int index;
    private long startTime;
    private long duration;
    private Status status;
    private String remark;
    private List<DataProviderLog> dataProviderLogs = new ArrayList<>();

    RepetitionLog(){
        this.startTime = System.nanoTime();
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<DataProviderLog> getDataProviderLogs() {
        return dataProviderLogs;
    }

    public void setDataProviderLogs(List<DataProviderLog> dataProviderLogs) {
        this.dataProviderLogs = dataProviderLogs;
    }

    public void addDataProviderLogs(List<DataProviderLog> dataProviderLogs){
        this.dataProviderLogs.addAll(dataProviderLogs);
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
