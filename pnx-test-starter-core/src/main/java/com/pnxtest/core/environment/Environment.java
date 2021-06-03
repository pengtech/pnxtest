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

import java.util.Set;

final class Environment {
    private static final InheritableThreadLocal<Property> properties = new InheritableThreadLocal<Property>(){
        @Override
        protected Property initialValue() {
            return new Property();
        }
    };

    private static Environment instance;
    private static final Object mutex = new Object();

    private Environment() {}

    public static synchronized Environment instance(){
        Environment result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Environment();
            }
        }
        return result;
    }


    public String getProperty(String key){
        return properties.get().getString(key);
    }

    public Object setProperty(String key, Object value){
        return properties.get().setProperty(key, value);
    }

    public Object removeProperty(String key){
        return properties.get().removeProperty(key);
    }

    public void addBundle(String environmentId){
         properties.get().addBundle(environmentId);
    }

    public void addSystemProperties(){
        properties.get().addSystemProperties();
    }

    public Set<Object> getPropertiesKeySet(){
        return properties.get().keySet();
    }

    public String getString(String key){
        return properties.get().getString(key);
    }

    public String getString(String key, String defaultValue){
        return properties.get().getString(key, defaultValue);
    }

    public boolean getBoolean(String key){
        return properties.get().getBoolean(key);
    }

    public boolean getBoolean(String key, boolean defaultValue){
        return properties.get().getBoolean(key, defaultValue);
    }

    public int getInt(String key){
        return properties.get().getInt(key);
    }

    public int getInt(String key, int defaultValue){
        return properties.get().getInt(key, defaultValue);
    }

    public long getLong(String key){
        return properties.get().getLong(key);
    }

    public long getLong(String key, long defaultValue){
        return properties.get().getLong(key, defaultValue);
    }

    public Object getObject(String key){
        return properties.get().getObject(key);
    }

}
