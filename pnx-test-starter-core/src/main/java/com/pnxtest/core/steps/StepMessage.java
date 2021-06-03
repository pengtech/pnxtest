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

public class StepMessage {
    private final String content;
    private final Type type;

    private StepMessage(String content, Type type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }

    public enum Type{SUCCESS, INFO, WARNING, ERROR};


    public static StepMessage success(String content){
        return new StepMessage(content, Type.SUCCESS);
    }

    public static StepMessage warning(String content){
        return new StepMessage(content, Type.WARNING);
    }

    public static StepMessage error(String content){
        return new StepMessage(content, Type.ERROR);
    }
    public static StepMessage info(String content){
        return new StepMessage(content, Type.INFO);
    }

}
