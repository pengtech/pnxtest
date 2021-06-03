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

package com.pnxtest.core.environment;

import com.pnxtest.core.ApplicationKeys;
import com.pnxtest.core.api.IModConfig;

import java.util.List;
import java.util.Set;

public class PnxContext {
    private PnxContext(){}

    public static String getString(String key){
        return Environment.instance().getString(key);
    }

    public static String getString(String key, String defaultValue){
        return Environment.instance().getString(key, defaultValue);
    }

    public static boolean getBoolean(String key){
        return Environment.instance().getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean defaultValue){
        return Environment.instance().getBoolean(key, defaultValue);
    }

    public static int getInt(String key){
        return Environment.instance().getInt(key);
    }

    public static int getInt(String key, int defaultValue){
        return Environment.instance().getInt(key, defaultValue);
    }

    public static long getLong(String key){
        return Environment.instance().getLong(key);
    }

    public static long getLong(String key, long defaultValue){
        return Environment.instance().getLong(key, defaultValue);
    }

    public static Object getObject(String key){
        return Environment.instance().getObject(key);
    }

    public static String getProperty(String key){
        return Environment.instance().getProperty(key);
    }

    public static Object setProperty(String key, Object value){
        return Environment.instance().setProperty(key, value);
    }

    public static Object removeProperty(String key){
        return Environment.instance().removeProperty(key);
    }

    public static String getTestEnvironmentId() {
        return Environment.instance().getProperty(ApplicationKeys.PNX_ENVIRONMENT_ID);
    }

    public static String getTestResultLocation() {
        return Environment.instance().getProperty(ApplicationKeys.PNX_OUTPUTTING_PATH);
    }

    @SuppressWarnings("unchecked")
    public static Set<IModConfig> getConfigBeans() {
        return (Set<IModConfig>) Environment.instance().getObject(ApplicationKeys.PNX_MODULE_CONFIG_BEANS);
    }

    /**
     * this method should not be used dynamically while running a suite
     */
    public static void setTestEnvironmentId(String environmentId){
        if(environmentId != null && !environmentId.equalsIgnoreCase(getTestEnvironmentId())){
            List<String> protectedKeys = ApplicationKeys.listAll();
            Environment.instance().getPropertiesKeySet().removeIf(skey -> !protectedKeys.contains(String.valueOf(skey)));
            Environment.instance().addSystemProperties();
            Environment.instance().setProperty(ApplicationKeys.PNX_ENVIRONMENT_ID, environmentId);
            Environment.instance().addBundle(environmentId);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName, Class<?> T){
        return (T)Environment.instance().getObject(beanName);
    }

}
