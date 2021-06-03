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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * testsuite entity
 *
 * @author nicolas.chen
 */
public class TestSuite {
    public enum RunMode{
        sequence, testParalleled, classParalleled;
    }

    private String id;
    private String name;
    private String description;
    private Set<TestRun> testRuns = new LinkedHashSet<>();
    private RunMode runMode = RunMode.sequence;
    private int threadCount;

    public TestSuite() {
    }

    public TestSuite(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public Set<TestRun> getTestRuns() {
        return testRuns;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void addTestRun(TestRun testRun) {
        this.testRuns.add(testRun);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRunMode(RunMode runMode) {
        this.runMode = runMode;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }



    //builder
    public static class Builder{
        private String id;
        private final String name;
        private String description;
        private Set<TestRun> testRuns = new LinkedHashSet<>();
        private RunMode runMode;
        private int threadCount;

        Builder(String name){
            this.name = name;
            this.runMode = RunMode.sequence;
        }

        Builder id(String id){
            this.id = id;
            return this;
        }

        Builder description(String description){
            this.description = description;
            return this;
        }

        Builder runMode(RunMode runMode){
            this.runMode = runMode;
            return this;
        }

        Builder threadCount(int threadCount){
            this.threadCount = threadCount;
            return this;
        }

        Builder addTestRun(TestRun testRun){
            this.testRuns.add(testRun);
            return this;
        }

        TestSuite build(){
            return new TestSuite(this);
        }
    }

    private TestSuite(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.runMode = builder.runMode;
        this.threadCount = builder.threadCount;
        this.testRuns = builder.testRuns;
    }

}
