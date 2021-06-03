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

import java.util.List;

/**
 * public steps api
 */
public class PnxSteps {

    private PnxSteps(){

    }

    public static void start(String title, String component, String subComponent){
        StepLogger.instance().start(title, component, subComponent);
    }

    public static void start(String title){
        StepLogger.instance().start(title);
    }

    public static void end(){
        StepLogger.instance().end();
    }

    public static void info(String msg){
        StepLogger.instance().info(msg);
    }

    public static void error(String msg){
        StepLogger.instance().error(msg);
    }

    public static void waring(String msg){
        StepLogger.instance().warning(msg);
    }

    public static void success(String msg){
        StepLogger.instance().success(msg);
    }

    public static void reset(){
        StepLogger.instance().reset();
    }

    public static void pause(){
        StepLogger.instance().pause();
    }

    public static void resume(){
        StepLogger.instance().resume();
    }



    public static List<StepLog> getLogs(){
        return StepLogger.instance().getLogs();
    }


    public static StepLog getCurrentStep(){
        return StepLogger.instance().getCurrentStep();
    }

    public static void injectStepsInto(Object target){
        StepsInjector.injector().injectStepsInto(target, StepsFactory.getFactory());
    }

}
