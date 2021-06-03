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

package com.pnxtest.core;

import java.util.ArrayList;
import java.util.List;

public final class ApplicationKeys {
    public static final String PNX_RUN_MODE = "pnx.run.mode";
    public static final String PNX_THREAD_COUNT = "pnx.thread.count";
    public static final String PNX_TARGET_FILE = "pnx.target.file";
    public static final String PNX_ENVIRONMENT_ID = "pnx.environment.id";
    public static final String PNX_ENVIRONMENT_PATH = "pnx.environment.path";
    public static final String PNX_CRYPTO_IMPL = "pnx.crypto.impl";
    public static final String PNX_OUTPUTTING_PATH = "pnx.outputting.path";
    public static final String PNX_CURRENT_METHOD_NAME = "pnx.current.method.name";
    public static final String PNX_CURRENT_STEP_NAME = "pnx.current.step.name";

    public static final String PNX_MODULE_CONFIG_BEANS = "pnx.module.config.beans";



    public static List<String> listAll(){
        List<String> allKeys = new ArrayList<>();
        allKeys.add(PNX_RUN_MODE);
        allKeys.add(PNX_THREAD_COUNT);
        allKeys.add(PNX_TARGET_FILE);
        allKeys.add(PNX_ENVIRONMENT_ID);
        allKeys.add(PNX_ENVIRONMENT_PATH);
        allKeys.add(PNX_CRYPTO_IMPL);
        allKeys.add(PNX_OUTPUTTING_PATH);
        allKeys.add(PNX_CURRENT_METHOD_NAME);
        allKeys.add(PNX_CURRENT_STEP_NAME);
        allKeys.add(PNX_MODULE_CONFIG_BEANS);

        return allKeys;
    }
}
