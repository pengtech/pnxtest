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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    private DateUtil(){}

    public static String breakDownDurationToHumanInSecond(long durationMillis) {
        return String.format("%.6f", durationMillis/1000000000.0);
    }

    public static String breakDownDurationToHuman(long durationMillis) {
        durationMillis = Math.round(durationMillis/1000000.0);
        if(durationMillis < 0) {
            return "0";
            //throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        if(durationMillis<1000) return durationMillis + "ms";

        long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
        durationMillis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        durationMillis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        durationMillis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        durationMillis -= TimeUnit.SECONDS.toMillis(seconds);

        StringBuilder sb = new StringBuilder(64);
        if(days>0) {
            sb.append(days);
            sb.append("d");
        }

        if(hours>0) {
            sb.append(hours);
            sb.append("h");
        }

        if(minutes>0) {
            sb.append(minutes);
            sb.append("m");
        }

        if(seconds>0) {
            sb.append(seconds);
            sb.append("s");
        }

        if(durationMillis>0) {
            sb.append(durationMillis);
            sb.append("ms");
        }

        return sb.toString();
    }


    public static String formatDate(Date date){
        DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SS");
        return dateformat.format(date);
    }

    public static String formatDate(Date date, String dateFormat){
        DateFormat dateformat = new SimpleDateFormat(dateFormat);
        return dateformat.format(date);
    }

    public static String formatDateStripYear(Date date){
        String dateFormat;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate thisDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if(localDate.getYear() == thisDate.getYear()){
            dateFormat = "MM-dd HH:mm:ss.SSS";
        }else{
            dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
        }

        DateFormat dateformat = new SimpleDateFormat(dateFormat);
        return dateformat.format(date);
    }
}
