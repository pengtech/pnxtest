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

package com.pnxtest.core.executors;

import com.pnxtest.core.exceptions.PnxException;
import com.pnxtest.core.exceptions.SkipException;
import com.pnxtest.core.api.*;
import com.pnxtest.core.steps.PnxSteps;
import com.pnxtest.core.util.StringUtil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * test Class including some test methods
 *
 */
public class TestClass implements Serializable {

    private static final long serialVersionUID = 5648381264482338837L;
    private final TestRun testRun;
    private final String module;
    private final String maintainer;
    private final Class<?> clazz;
    private Object instanceObj;
    
    private Set<TestCase> testMethods = new HashSet<>();
    private final Set<TestCase> beforeEachMethods = new HashSet<>();
    private final Set<TestCase> afterEachMethods = new HashSet<>();
    private final Set<TestCase> beforeAllMethods = new HashSet<>();
    private final Set<TestCase> afterAllMethods = new HashSet<>();

    private List<Throwable> errors = new ArrayList<>();


    public TestClass(Class<?> clazz, TestRun testRun){
        this.clazz = clazz;
        this.testRun = testRun;
        try {
            this.instanceObj = buildInstance();
            PnxSteps.injectStepsInto(this.instanceObj);
        }catch (Exception e){
            this.errors.add(e);
        }

        //module
        if(this.clazz.isAnnotationPresent(Controller.class) && clazz.getAnnotation(Controller.class).module().trim().length()>0){
            this.module = StringUtil.removeHtmlTags(clazz.getAnnotation(Controller.class).module().trim());
        }else{
            this.module = "Uncategorized";
        }

        //maintainer
        if(this.clazz.isAnnotationPresent(Controller.class) && clazz.getAnnotation(Controller.class).maintainer().trim().length()>0){
            this.maintainer = StringUtil.removeHtmlTags(clazz.getAnnotation(Controller.class).maintainer().trim());
        }else{
            this.maintainer = System.getProperty("user.name", "PNX");
        }

        buildTestMethods();

        //disabled
        if(clazz.getAnnotation(Disabled.class) != null){
            String message = clazz.getAnnotation(Disabled.class).value();
            if(message.length() == 0){
                message = "Class or Module is disabled!";
            }
            this.errors.add(new SkipException(message));
        }
    }

    private Object buildInstance(){
        try{
            return clazz.newInstance();
        }catch (InstantiationException | IllegalAccessException e){
            throw new PnxException("Class Configuration method failed: " + e.getMessage());
        }
    }

    private void buildTestMethods(){
        buildTestMethods(true);
    }

    private void buildTestMethods(boolean includingTestMethods){
        Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
            if(method.isAnnotationPresent(BeforeEach.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    beforeEachMethods.add(new TestCase(method, this));
                }
            }else if(method.isAnnotationPresent(AfterEach.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    afterEachMethods.add(new TestCase(method, this));
                }
            }
            else if(method.isAnnotationPresent(BeforeAll.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    beforeAllMethods.add(new TestCase(method, this));
                }
            }
            else if(method.isAnnotationPresent(AfterAll.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    afterAllMethods.add(new TestCase(method, this));
                }
            }
            else if(includingTestMethods && method.isAnnotationPresent(Test.class)){
                testMethods.add(new TestCase(method, this));
            }
        });
        Arrays.stream(clazz.getMethods()).forEach(method -> {
            if(method.isAnnotationPresent(BeforeEach.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    beforeEachMethods.add(new TestCase(method, this));
                }
            }else if(method.isAnnotationPresent(AfterEach.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    afterEachMethods.add(new TestCase(method, this));
                }
            }
            else if(method.isAnnotationPresent(BeforeAll.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    beforeAllMethods.add(new TestCase(method, this));
                }
            }
            else if(method.isAnnotationPresent(AfterAll.class)){
                if(!method.isAnnotationPresent(Disabled.class) & !method.isAnnotationPresent(DataDriven.class)){
                    afterAllMethods.add(new TestCase(method, this));
                }
            }
            else if(includingTestMethods && method.isAnnotationPresent(Test.class)){
                testMethods.add(new TestCase(method, this));
            }
        });

    }

    public TestRun getTestRun() {
        return testRun;
    }

    public String getModule() {
        return module;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public Class<?> getClazz() {
        return clazz;
    }


    public Object getInstanceObj() {
        return instanceObj;
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public Set<TestCase> getTestMethods() {
        return testMethods;
    }

    public void addTestMethod(String methodName){
        try{
            Method m = clazz.getMethod(methodName);
            if(m.isAnnotationPresent(Test.class)
                    && !m.isAnnotationPresent(BeforeAll.class)
                    && !m.isAnnotationPresent(AfterAll.class)
                    && !m.isAnnotationPresent(BeforeEach.class)
                    && !m.isAnnotationPresent(AfterEach.class)
            ){
                this.testMethods.add(new TestCase(m, this));
            }
        }catch (NoSuchMethodException e){
            //logger...
        }
    }

    public void addTestMethod(Method m){
        if(m.isAnnotationPresent(Test.class)
                && !m.isAnnotationPresent(BeforeAll.class)
                && !m.isAnnotationPresent(AfterAll.class)
                && !m.isAnnotationPresent(BeforeEach.class)
                && !m.isAnnotationPresent(AfterEach.class)
        ){
            this.testMethods.add(new TestCase(m, this));
        }
    }


    /**
     * sorted methods list
     * @return
     */
    public List<TestCase> getSortedTestMethods() {
        return testMethods.stream()
                .sorted(Comparator.comparing(TestCase::getName))
                .sorted(Comparator.comparingInt(TestCase::getOrder))
                .collect(Collectors.toList());
    }

    public List<TestCase> getSortedBeforeAllMethods() {
        return beforeAllMethods.stream()
                .sorted(Comparator.comparing(TestCase::getName))
                .sorted(Comparator.comparingInt(TestCase::getOrder))
                .collect(Collectors.toList());
    }

    public List<TestCase> getSortedAfterAllMethods() {
        return afterAllMethods.stream()
                .sorted(Comparator.comparing(TestCase::getName).reversed())
                .sorted(Comparator.comparingInt(TestCase::getOrder))
                .collect(Collectors.toList());
    }

    public List<TestCase> getSortedBeforeEachMethods() {
        return beforeEachMethods.stream()
                .sorted(Comparator.comparing(TestCase::getName))
                .sorted(Comparator.comparingInt(TestCase::getOrder))
                .collect(Collectors.toList());
    }

    public List<TestCase> getSortedAfterEachMethods() {
        return afterEachMethods.stream()
                .sorted(Comparator.comparing(TestCase::getName).reversed())
                .sorted(Comparator.comparingInt(TestCase::getOrder))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestClass)) return false;
        TestClass other = (TestClass) o;
        //return this.clazz.getName().equals(other.clazz.getName()) && testRunName.equals(other.testRunName);
        return this.clazz.getName().equals(other.clazz.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, testRun);
    }

}
