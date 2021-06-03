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

import com.pnxtest.core.exceptions.PnxException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class StepsField {
    private final Field field;

    public StepsField(Field field){
        this.field = field;
    }

    public String getFieldName() {
        return field.getName();
    }

    public Class<?> getFieldClass() {
        return field.getType();
    }

    public void setValue(final Object targetObject, final Object value) {
        try {
            field.setAccessible(true);
            field.set(targetObject, value);
        } catch (IllegalAccessException e) {
            throw new PnxException("Could not access or set field: " + field, e);
        }
    }

    public boolean isInstantiated(final Object targetClass) {
        try {
            field.setAccessible(true);
            Object fieldValue = field.get(targetClass);
            return (fieldValue != null);
        } catch (IllegalAccessException e) {
            //ignore
            //throw new PnxException("Could not access or set @Steps field: " + field, e);
        }

        return false;
    }

    public boolean isNotInstantiated(final Object testCase) {
        return !isInstantiated(testCase);
    }

    public boolean isShared() {
        return field.getAnnotation(Steps.class).shared();
    }



    public static List<StepsField> findFields(Class<?> clazz){
        List<StepsField> ret = new ArrayList<>();
        while (clazz != null){
            for(Field field: clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(Steps.class)){
                    ret.add(new StepsField(field));
                }
            }

            clazz = clazz.getSuperclass();
        }

        return ret;
    }


}
