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

import com.pnxtest.core.exceptions.StepException;
import com.pnxtest.core.api.Repository;

import java.util.stream.Stream;

class StepsInjector {
    private static volatile StepsInjector instance;
    private static final Object mutex = new Object();
    private StepsInjector(){
    }

    public static synchronized StepsInjector injector(){
        StepsInjector result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new StepsInjector();
            }
        }
        return result;
    }

    public void injectStepsInto(Object target, StepsFactory stepsFactory){
        Class<?> clazz = target.getClass();

        StepsField.findFields(clazz).forEach(stepsField -> {
            instantiateStepsField(target, stepsField, stepsFactory);
        });
    }

    void injectNestedStepsInto(final Object stepsFieldValue,
                               final StepsFactory stepsFactory,
                               final Class<?> stepsFieldType
    ) {
        StepsField.findFields(stepsFieldType).forEach(stepsField -> {
            instantiateStepsField(stepsFieldValue, stepsField, stepsFactory);
        });
    }

    private void instantiateStepsField(Object target, StepsField stepsField, StepsFactory stepsFactory){
        if(stepsField.isNotInstantiated(target)){
            ensureThatThisFieldIsNotCyclicOrRecursive(stepsField);

            Class<?> stepsFieldType = stepsField.getFieldClass();
            if(!stepsFieldType.isAnnotationPresent(Repository.class)) return;

            Object stepsFieldValue = stepsFactory.getStepsFieldValueUsingNewInstance(stepsFieldType);
            if(stepsField.isShared()){
                stepsFieldValue = stepsFactory.getSharedStepsFieldValue(stepsFieldType);
            }

            stepsField.setValue(target, stepsFieldValue);

            injectNestedStepsInto(stepsFieldValue, stepsFactory, stepsFieldType);
        }
    }

    private void ensureThatThisFieldIsNotCyclicOrRecursive(StepsField stepsField) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        long levelsOfNesting = Stream.of(stackTrace).filter(element -> element.getMethodName().equals("instantiateStepsField")).count();

        int maxAllowedNesting = 32;
        if (levelsOfNesting > maxAllowedNesting) {
            String message = String.format(
                    "A recursive or cyclic reference was detected for the @Steps-annotated field %s in class %s. " +
                            "You may need to use @Steps(shared=true) to ensure that the same step library instance is used everywhere.",
                    stepsField.getFieldName(), stepsField.getFieldClass().getName());
            throw new StepException(message);
        }
    }


}
