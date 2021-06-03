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

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.HashMap;
import java.util.Map;

class StepsFactory {
    private final Map<Class<?>, Object> classIndex = new HashMap();
    private static ThreadLocal<StepsFactory> currentStepsFactory = ThreadLocal.withInitial(() -> new StepsFactory());
    private final Injector injector;



    private StepsFactory(){
        this.injector = Guice.createInjector(new StepsModule());
    }

    public static StepsFactory getFactory() {
        return currentStepsFactory.get();
    }

    public <T> T getSharedStepsFieldValue(final Class<T> stepsClazz) {
        if (isStepsFiledInstantiated(stepsClazz)) {
            return getStepsFieldValueFromCache(stepsClazz);
        } else {
            return getStepsFieldUsingNewInstanceAndCache(stepsClazz);
        }
    }

    public <T> T getStepsFieldValueUsingNewInstance(final Class<T> stepsFieldType) {
        return createNewStepsInstance(stepsFieldType, false);
    }


    public <T> T getStepsFieldUsingNewInstanceAndCache(final Class<T> stepsFieldType) {
        return createNewStepsInstance(stepsFieldType, true);
    }


    public <T> T createNewStepsInstance(Class<T> stepsFieldType, boolean cacheNewInstance) {
        try{
            //T stepsFieldValue = stepsFieldType.getDeclaredConstructor().newInstance();//reference original class
            T stepsFieldValue = injector.getInstance((stepsFieldType));
            if (cacheNewInstance) {
                indexStepsClazz(stepsFieldType, stepsFieldValue);
            }

            instantiateAnyNestedSteps(stepsFieldValue, stepsFieldType);

            return stepsFieldValue;

        }catch (Exception e){
            //ignore
            //throw new RuntimeException(e);
        }

        return null;
    }

    private <T> void instantiateAnyNestedSteps(final T steps, final Class<T> stepsFieldValue) {
        StepsInjector.injector().injectNestedStepsInto(steps, this, stepsFieldValue);
    }



    private boolean isStepsFiledInstantiated(Class<?> stepsFiledType) {
        return classIndex.containsKey(stepsFiledType);
    }

    @SuppressWarnings("unchecked")
    private <T> T getStepsFieldValueFromCache(Class<T> stepsFiledType) {
        return (T) classIndex.get(stepsFiledType);
    }

    private <T> void indexStepsClazz(Class<T> stepsFiledType, T stepsFieldValue) {
        classIndex.put(stepsFiledType, stepsFieldValue);
    }

}
