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

package com.pnxtest.core.helper;

import com.pnxtest.core.exceptions.PnxException;
import com.pnxtest.core.steps.Step;

import java.util.concurrent.TimeUnit;

/**
 * general helpers
 * author: nicolas.chen
 */
public class PnxHelper {
    private PnxHelper(){}

    public static void await(long timeOut){
        await(timeOut, TimeUnit.MILLISECONDS);
    }

    @Step("程序等待{0} {1}")
    public static void await(long timeOut, TimeUnit timeUnit){
        try {
            if (timeUnit == TimeUnit.DAYS) {
                TimeUnit.DAYS.sleep(timeOut);
            }

            if (timeUnit == TimeUnit.HOURS) {
                TimeUnit.HOURS.sleep(timeOut);
            }

            if(timeUnit == TimeUnit.MINUTES){
                TimeUnit.MINUTES.sleep(timeOut);
            }
            if(timeUnit == TimeUnit.SECONDS){
                TimeUnit.SECONDS.sleep(timeOut);
            }

            if(timeUnit == TimeUnit.MILLISECONDS){
                TimeUnit.MILLISECONDS.sleep(timeOut);
            }

            if(timeUnit == TimeUnit.MICROSECONDS){
                TimeUnit.MICROSECONDS.sleep(timeOut);
            }

        }catch (InterruptedException e){
            throw new PnxException(String.format("%d %s await timeout", timeOut, timeUnit.name()), e);
        }
    }

}
