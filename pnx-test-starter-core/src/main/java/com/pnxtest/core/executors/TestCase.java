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

import com.pnxtest.core.*;
import com.pnxtest.core.api.*;
import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.core.exceptions.DataProviderException;
import com.pnxtest.core.exceptions.PnxException;
import com.pnxtest.core.exceptions.PnxModuleException;
import com.pnxtest.core.exceptions.SkipException;
import com.pnxtest.core.outputting.ConsoleLog;
import com.pnxtest.core.steps.PnxSteps;
import com.pnxtest.core.steps.StepMessage;
import com.pnxtest.core.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

/**
 * test method entity
 *
 * @author nicolas.chen
 */

public class TestCase implements Serializable {

    private static final long serialVersionUID = 6739599189213013396L;


    private transient final Method method;
    private transient final TestClass testClass;

    private final String id;
    private final String displayName;
    private final String description;
    private final String methodFullName; //method name
    private final int order;
    private final Levels level;

    private int repeatedCount = 0;
    private float expectedSuccessRate = 100.00f;

    private final boolean hasDataDriven;

    private int repetitionIndex;
    private int recordIndex;
    private int recordCount;


    public TestCase(Method method, TestClass testClass) {
        this.method = method;
        this.testClass = testClass;
        this.methodFullName = method.getDeclaringClass().getName() + "." + method.getName();


        //displayName
        StringBuilder titleBuilder = new StringBuilder();

        String displayName = "";
        if(method.isAnnotationPresent(DisplayName.class)){
            String displayNameValue = method.getAnnotation(DisplayName.class).value();
            if(!StringUtil.isBlank(displayNameValue) && !StringUtil.isEmpty(displayNameValue)){
                displayName = StringUtil.removeHtmlTags(displayNameValue);
            }
        }
        if(StringUtil.isEmpty(displayName)){
            displayName = humanize(withNoIssueNumbers(method.getName()));
        }

        titleBuilder.append(StringUtil.truncate(displayName, 80));


        Issue issueAnnotation = method.getAnnotation(Issue.class);
        if(issueAnnotation != null && issueAnnotation.value().length>0){
            int len = issueAnnotation.value().length;
            titleBuilder.append("(");
            for(int i=0;i<len;i++){
                String issueNo = issueAnnotation.value()[i];
                if(issueNo.matches("#[a-zA-Z0-9\\-_]+$")){
                    titleBuilder.append(issueNo);
                    if(i<len-1){titleBuilder.append(", ");}
                }
                else if(issueNo.matches("[a-zA-Z0-9\\-_]+$")){
                    titleBuilder.append("#").append(issueNo);
                    if(i<len-1){titleBuilder.append(", ");}
                }
            }
            //titleBuilder.append(String.join(", ", issueAnnotation.value()));
            titleBuilder.append(")");
        }

        this.displayName = titleBuilder.toString();

        //description
        this.description = method.isAnnotationPresent(Description.class)?method.getAnnotation(Description.class).value().trim():"";

        //key
        this.id = buildId(method);

        //order
        this.order = method.isAnnotationPresent(Order.class)?method.getAnnotation(Order.class).value():0;

        //level
        this.level = method.isAnnotationPresent(Level.class)?method.getAnnotation(Level.class).value():Levels.L2;

        //repeatedCount
        if(method.isAnnotationPresent(RepeatedCount.class)){
            this.repeatedCount = Math.max(method.getAnnotation(RepeatedCount.class).value(), 1);
            this.expectedSuccessRate = method.getAnnotation(RepeatedCount.class).successPercentage();
        }

        //dataDriven
        this.hasDataDriven = method.isAnnotationPresent(DataDriven.class);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getFullName() {
        return methodFullName;
    }

    public String getName() {
        return method.getName();
    }

    public int getRepeatedCount() {
        return repeatedCount;
    }

    public float getExpectedSuccessRate() {
        return expectedSuccessRate;
    }


    public boolean hasDataDriven() {
        return hasDataDriven;
    }

    public int getOrder() {
        return order;
    }

    public Levels getLevel() {
        return level;
    }

    public Method getMethod() {
        return method;
    }

    public TestClass getTestClass() {
        return testClass;
    }

    public boolean isConfigurationMethod(){
        return method.isAnnotationPresent(BeforeEach.class) || method.isAnnotationPresent(AfterEach.class)
                || method.isAnnotationPresent(BeforeAll.class) || method.isAnnotationPresent(AfterAll.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCase)) return false;
        TestCase that = (TestCase) o;
        return method.equals(that.method) &&
                testClass.equals(that.testClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private String buildId(Method method){
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append(method.getName());

        Parameter[] methodParameters = method.getParameters();
        for (Parameter p : methodParameters) {
            sb.append(ClassUtil.primitive2Wrapper(p.getType()));
        }

        CRC32 crc = new CRC32();
        crc.update(sb.toString().getBytes(StandardCharsets.UTF_8));
        return Long.toHexString(crc.getValue());
    }


    private String humanize(final String name){
        if(StringUtil.isBlank(name) || StringUtil.isEmpty(name)){
            return "";
        }

        String noUnderscores = name.replaceAll("_", " ");
        String splitCamelCase = splitCamelCase(noUnderscores);

        Set<Acronym> acronyms = Acronym.acronymsIn(splitCamelCase);
        //String capitalized = StringUtils.capitalize(splitCamelCase);
        String capitalized = StringUtil.capitalize(splitCamelCase);
        for(Acronym acronym : acronyms) {
            capitalized = acronym.restoreIn(capitalized);
        }
        return capitalized;
    }

    private String splitCamelCase(final String name) {
        List<String> splitWords = new ArrayList<>();

        List<String> phrases = Arrays.stream(name.split("\\s+")).collect(Collectors.toList());

        for(String phrase : phrases) {
            splitWords.addAll(splitWordsIn(phrase));
        }

        String splitPhrase = String.join(" ", splitWords);
        return splitPhrase.trim();
    }

    private List<String> splitWordsIn(String phrase) {

        List<String> splitWords = new ArrayList<>();

        String currentWord = "";
        for (int index = 0; index < phrase.length(); index++) {
            if (onWordBoundary(phrase, index)) {
                splitWords.add(lowercaseOrAcronym(currentWord));
                currentWord = String.valueOf(phrase.charAt(index));
            } else {
                currentWord = currentWord + (phrase.charAt(index));
            }
        }
        splitWords.add(lowercaseOrAcronym(currentWord));

        return splitWords;
    }

    private String lowercaseOrAcronym(String word) {
        if (Acronym.isAnAcronym(word)) {
            return word;
        } else {
            return StringUtil.lowerCase(word);
        }
    }

    private boolean onWordBoundary(String name, int index) {
        return (uppercaseLetterAt(name, index)
                && (lowercaseLetterAt(name, index - 1) || lowercaseLetterAt(name, index + 1)));
    }

    private boolean uppercaseLetterAt(String name, int index) {
        return StringUtil.isAsciiAlphaUpper(name.charAt(index));
    }

    private boolean lowercaseLetterAt(String name, int index) {
        return (index >= 0)
                && (index < name.length())
                && StringUtil.isAsciiAlphaLower(name.charAt(index));
    }

    private String withNoIssueNumbers(final String methodName) {
        if (methodName == null) {
            return null;
        }
        int firstIssueNumberIndex = methodName.indexOf("_(#");
        if (firstIssueNumberIndex == -1) {
            firstIssueNumberIndex = methodName.indexOf("(#");
        }
        if (firstIssueNumberIndex == -1) {
            firstIssueNumberIndex = methodName.indexOf("#");
        }
        if (firstIssueNumberIndex > 0) {
            return methodName.substring(0, firstIssueNumberIndex);
        } else {
            return methodName;
        }
    }

    private void ensureMethodIsQualified(){
        //check if a method is qualified
        if(Modifier.isPrivate(method.getModifiers()) || Modifier.isProtected(method.getModifiers())){
            throw new SkipException("A test method should be public");
        }

        if(Modifier.isStatic(method.getModifiers())){
            throw new SkipException("A test method must be non-static");
        }
    }

    private boolean  checkIfMethodArgumentsAreQualified(){
        Class<?>[] paramTypes = method.getParameterTypes();
        if(method.isAnnotationPresent(DataDriven.class)){
            if(paramTypes.length == 1 && CurrentTestInfo.class.isAssignableFrom(paramTypes[0])) return false;
            return paramTypes.length >= 1;
        }

        if(paramTypes.length == 0) return true;

        return ( paramTypes.length == 1 && CurrentTestInfo.class.isAssignableFrom(paramTypes[0]) );
    }

    private void ensureMethodArgumentsAreQualified(){
        if(isConfigurationMethod()){
            ensureMethodWithoutArguments();
        }else{
            if(!checkIfMethodArgumentsAreQualified()){
                throw new PnxException(String.format("Passing arguments to a test method<%s> is forbidden!", this.getName()));
            }
        }
    }

    private void ensureMethodWithoutArguments(){
        Class<?>[] paramTypes = method.getParameterTypes();
        if(paramTypes.length>0){
            throw new PnxException(String.format("Passing arguments to a test method<%s> is forbidden!", this.getName()));
        }
    }

    private void ensureMethodIsNotSkipped(){
        Disabled disabledMeta = method.getAnnotation(Disabled.class);
        if(disabledMeta != null){
            String disabledReason = disabledMeta.value();
            if(StringUtil.isEmpty(disabledReason)){
                disabledReason = "Skipped due to @Disabled annotated";
            }else{
                disabledReason = String.format("Skipped due to @Disabled annotated <%s>", disabledReason);
            }
            throw new SkipException(disabledReason);
        }

        disabledMeta = method.getClass().getAnnotation(Disabled.class);
        if(disabledMeta != null){
            String disabledReason = disabledMeta.value();
            if(StringUtil.isEmpty(disabledReason)){
                disabledReason = "Skipped due to @Disabled annotated";
            }else{
                disabledReason = String.format("Skipped due to @Disabled annotated <%s>", disabledReason);
            }
            throw new SkipException(disabledReason);
        }
    }

    private void ensureNoErrorsFallen(){
        List<Throwable> errors = this.testClass.getErrors();
        if(errors.size()>0){
            //error happened on class level, all testCases will be marked as skipped
            StringBuilder sb = new StringBuilder();
            for(Throwable th:errors){
                sb.append(th.getLocalizedMessage()).append("\n");
            }

            throw new SkipException(sb.toString());
        }
    }

    //run
    public MethodResult run(){
        MethodResult methodResult = new MethodResult(this);
        ConsoleLog.printTestCaseOnStart(methodResult);
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_METHOD_NAME, this.getName());
        //start executing...
        PnxSteps.reset();
        PnxSteps.start("Test started");
        PnxSteps.end();

        long startTime = System.nanoTime();
        int totalRepetitionsCount = Math.max(this.repeatedCount, 1);
        int successRepetitionsCount = 0;

        for(int currentRepetition=0;currentRepetition<totalRepetitionsCount;currentRepetition++) {
            RepetitionLog eachRepetitionLog = new RepetitionLog();
            eachRepetitionLog.setIndex(currentRepetition);
            this.repetitionIndex = currentRepetition;

            try{
                if(hasDataDriven){
                    executeTestMethodWithDataDriven(eachRepetitionLog);
                }else {
                    executeTestMethod();
                }

                eachRepetitionLog.setStatus(Status.PASSED);
                successRepetitionsCount++;
            }catch (Throwable th){
                if(th instanceof SkipException){
                    eachRepetitionLog.setStatus(Status.SKIPPED);
                    eachRepetitionLog.setRemark(ExceptionUtil.getExceptionMessage(th));
                    methodResult.setStatus(Status.SKIPPED);
                    methodResult.setRemark(ExceptionUtil.getExceptionMessage(th));
                    break;
                }else if(th instanceof AssertionError){
                    eachRepetitionLog.setStatus(Status.FAILED);
                    eachRepetitionLog.setRemark(ExceptionUtil.getExceptionMessage(th));
                }else if(th instanceof PnxModuleException){
                    String remark = ExceptionUtil.getExceptionMessage(th);
                    eachRepetitionLog.setStatus(Status.FAILED);
                    eachRepetitionLog.setRemark(remark);

                    PnxSteps.getCurrentStep().setStepMessage(StepMessage.error(remark+"\n"+ExceptionUtil.getRootCauseMessage(th)));
                    PnxSteps.end();
                }else{
                    String remark = ExceptionUtil.getExceptionMessage(th);
                    eachRepetitionLog.setStatus(Status.FAILED);
                    eachRepetitionLog.setRemark(remark);

                    PnxSteps.start(remark);
                    PnxSteps.error(ExceptionUtil.getRootCauseMessage(th));
                    PnxSteps.end();
                }
            }

            eachRepetitionLog.setDuration(System.nanoTime() - eachRepetitionLog.getStartTime());
            methodResult.getRepetitionLogs().add(eachRepetitionLog);
            if(totalRepetitionsCount>1) {
                PnxSteps.pause();
            }

        }//end repetitions for loop

        if(methodResult.getStatus() == Status.SKIPPED){
            PnxSteps.reset();
            return methodResult;
        }

        methodResult.setDuration(System.nanoTime() - startTime);

        float actualSuccessRate = (float)(successRepetitionsCount * 100) / totalRepetitionsCount;
        if(Float.compare(actualSuccessRate, this.expectedSuccessRate)>=0){
            methodResult.setStatus(Status.PASSED);
        }else{
            methodResult.setStatus(Status.FAILED);
        }

        if(methodResult.getStatus() == Status.PASSED){
            PnxSteps.resume();
            PnxSteps.start("Test finished");
            PnxSteps.end();
        }


        methodResult.setStepLogs(new LinkedList<>(PnxSteps.getLogs()));

        ConsoleLog.printTestCaseOnFinish(methodResult);
        return methodResult;
    }

    private void executeTestMethod() throws Throwable{
        executeBeforeEachConfigurationMethods();
        executeIndividualTestMethod();
        executeAfterEachConfigurationMethods();
    }

    private void executeTestMethodWithDataDriven(RepetitionLog repetitionLog) throws Throwable{
        executeBeforeEachConfigurationMethods();
        executeIndividualDataDrivenTestMethod(repetitionLog);
        executeAfterEachConfigurationMethods();
    }

    private void executeIndividualDataDrivenTestMethod(RepetitionLog eachRepetitionLog) throws Throwable{
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_STEP_NAME, method.getName());
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_METHOD_NAME, method.getName());

        ensureNoErrorsFallen();
        ensureMethodIsQualified();
        ensureMethodArgumentsAreQualified();
        ensureMethodIsNotSkipped();

        Object[][] argsMatrix = tryToParseDataProvider(this.method);
        int failedRecordCount = 0;
        this.recordCount = argsMatrix.length;

        Class<?>[] paramTypes = method.getParameterTypes();
        for(int currentRecordIndex=0;currentRecordIndex<argsMatrix.length;currentRecordIndex++){
            DataProviderLog eachDpLog = new DataProviderLog();
            eachDpLog.setIndex(currentRecordIndex);
            this.recordIndex = currentRecordIndex;

            try{
                //MethodUtil.invokeJvmMethod(this.testClass.getInstanceObj(), argsMatrix[currentRecordIndex],this.method);
                if(paramTypes.length>1 && CurrentTestInfo.class.isAssignableFrom(paramTypes[paramTypes.length-1])){
                    Object[] oldArgs = argsMatrix[currentRecordIndex];
                    Object[] args = new Object[oldArgs.length+1];
                    for(int i=0;i<oldArgs.length;i++){
                        args[i] = oldArgs[i];
                    }
                    args[oldArgs.length] = buildCurrentTestInfo();
                    //Object[] args = new Object[]{argsMatrix[currentRecordIndex][0], buildCurrentTestInfo()};
                    MethodUtil.invokeJvmMethod(this.testClass.getInstanceObj(), args, this.method);
                }else{
                    MethodUtil.invokeJvmMethod(this.testClass.getInstanceObj(), argsMatrix[currentRecordIndex], this.method);
                }

                eachDpLog.setStatus(Status.PASSED);
            }catch (DataProviderException | PnxException | AssertionError th){
                eachDpLog.setStatus(Status.FAILED);
                eachDpLog.setRemark(ExceptionUtil.getExceptionMessage(th));
                failedRecordCount++;
            } catch (Throwable th){
                eachDpLog.setStatus(Status.FAILED);
                eachDpLog.setRemark(ExceptionUtil.getExceptionMessage(th)+ "\n" + ExceptionUtil.getRootCauseMessage(th));
                failedRecordCount++;
            }

            eachDpLog.setDuration(System.nanoTime() - eachDpLog.getStartTime());
            eachDpLog.setArgs(Arrays.asList(argsMatrix[currentRecordIndex]).toString());
            eachRepetitionLog.getDataProviderLogs().add(eachDpLog);
        }

        if(failedRecordCount>0){
            throw new DataProviderException(String.format("%s records failed!", failedRecordCount));
        }

    }

    private void executeIndividualTestMethod() throws Throwable{
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_STEP_NAME, method.getName());
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_METHOD_NAME, method.getName());

        ensureNoErrorsFallen();
        ensureMethodIsQualified();
        ensureMethodArgumentsAreQualified();
        ensureMethodIsNotSkipped();

        Class<?>[] paramTypes = method.getParameterTypes();
        if(paramTypes.length == 1 && CurrentTestInfo.class.isAssignableFrom(paramTypes[0])){
            Object[] args = new Object[1];
            args[0] = buildCurrentTestInfo();
            MethodUtil.invokeJvmMethod(this.testClass.getInstanceObj(), args, this.method);
        }else{
            MethodUtil.invokeJvmMethod(this.testClass.getInstanceObj(), null, this.method);
        }
    }

    private void executeIndividualConfigurationMethod() throws Throwable{
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_STEP_NAME, method.getName());
        PnxContext.setProperty(ApplicationKeys.PNX_CURRENT_METHOD_NAME, method.getName());

        ensureNoErrorsFallen();
        ensureMethodIsQualified();
        ensureMethodArgumentsAreQualified();
        ensureMethodIsNotSkipped();

        MethodUtil.invokeJvmMethod(this.testClass.getInstanceObj(), null, this.method);
    }

    private void executeBeforeEachConfigurationMethods() throws Throwable{
        for (TestCase bfMethod : this.testClass.getSortedBeforeEachMethods()) {
            bfMethod.executeIndividualConfigurationMethod();
        }
    }

    private void executeAfterEachConfigurationMethods() throws Throwable{
        for (TestCase afMethod : this.testClass.getSortedAfterEachMethods()) {
            afMethod.executeIndividualConfigurationMethod();
        }
    }


    private Object[][] tryToParseDataProvider(Method testMethod){
        DataDriven dataDriven = testMethod.getAnnotation(DataDriven.class);
        if(!"FILE".equalsIgnoreCase(dataDriven.type())) {
            throw new DataProviderException("Error: @DataDriven only supports <file> currently!");
        }

        if(!dataDriven.value().endsWith("csv") && !dataDriven.value().endsWith("txt")) {
            throw new DataProviderException("Error: @DataDriven only supports csv and txt files currently!");
        }

        Class<?>[] paramTypes = testMethod.getParameterTypes();
        if(paramTypes.length == 0){
            throw new DataProviderException("Error: @DataDriven must have one argument at least!");
        }

        if(paramTypes.length == 1 && CurrentTestInfo.class.isAssignableFrom(paramTypes[0])){
            throw new DataProviderException("Error: @DataDriven must have one argument at least except 'CurrentTestInfo'!");
        }

        if(paramTypes.length == 1 && !Map.class.isAssignableFrom(paramTypes[0])){
            throw new DataProviderException("Error: @DataDriven wrong number of arguments");
        }

        if(paramTypes.length == 2 && Map.class.isAssignableFrom(paramTypes[0]) && !CurrentTestInfo.class.isAssignableFrom(paramTypes[1])){
            throw new DataProviderException("Error: @DataDriven wrong number of arguments");
        }


        try {
            File tDataFile = new File(PnxContext.getProperty(ApplicationKeys.PNX_ENVIRONMENT_PATH) + File.separator + dataDriven.value());
            if(!tDataFile.isFile() || !tDataFile.canRead()){
                throw new DataProviderException(String.format("Error: @DataDriven file <%s> not found or read error!", tDataFile.getCanonicalPath()));
            }

            CsvReader csvReader = new CsvReader(new FileInputStream(tDataFile), dataDriven.separator(), true);
            List<Object[]> retRows = new ArrayList<>();
            List<String> colNames = new ArrayList<>();

            int tempParamsCount = paramTypes.length;
            if(CurrentTestInfo.class.isAssignableFrom(paramTypes[paramTypes.length-1])){
                tempParamsCount--;
            }

            //first retrieve columns
            while (csvReader.hasNext()) {
                List<String> rawRowData = new ArrayList<>(csvReader.next());
                if(rawRowData.size() == 0) continue;
                if(dataDriven.withHeader()){
                    for(int i=0;i<rawRowData.size();i++){
                        if(rawRowData.get(i) == null || rawRowData.get(i).isEmpty()){
                            colNames.add("col_"+i);
                        }else {
                            colNames.add(rawRowData.get(i).trim());
                        }
                    }
                }else{
                    for(int i=0;i<rawRowData.size();i++){
                        colNames.add("col_"+i);
                    }

                    if( (paramTypes.length == 1 && Map.class.isAssignableFrom(paramTypes[0]))
                            || (paramTypes.length == 2 && Map.class.isAssignableFrom(paramTypes[0]) && CurrentTestInfo.class.isAssignableFrom(paramTypes[1]))
                    ){
                        Map<String, Object> tMap = new LinkedHashMap<String, Object>();
                        for(int j=0;j<colNames.size();j++){
                            tMap.put(colNames.get(j), rawRowData.get(j));
                        }
                        retRows.add(new Object[] { tMap });
                    }else{
                        Object[] tRow = new Object[tempParamsCount];
                        for(int j=0;j<tempParamsCount;j++){
                            tRow[j] = ClassUtil.convertValueByType(paramTypes[j], rawRowData.get(j));
                        }

                        retRows.add(tRow);
                    }
                }
                break;
            }



            if(colNames.size() < tempParamsCount){
                throw new DataProviderException(String.format("Error: @DataDriven the number of arguments not matched on method <%s>!", testMethod.getName()));
            }


            while (csvReader.hasNext()) {
                List<String> rawRowData = new ArrayList<>(csvReader.next());

                if( (paramTypes.length == 1 && Map.class.isAssignableFrom(paramTypes[0]))
                    || (paramTypes.length == 2 && Map.class.isAssignableFrom(paramTypes[0]) && CurrentTestInfo.class.isAssignableFrom(paramTypes[1]))
                ){
                    Map<String, Object> tMap = new LinkedHashMap<String, Object>();
                    for(int j=0;j<colNames.size();j++){
                        tMap.put(colNames.get(j), rawRowData.get(j));
                    }
                    retRows.add(new Object[] { tMap });
                }else{
                    Object[] tRow = new Object[tempParamsCount];
                    for(int j=0;j<tempParamsCount;j++){
                        tRow[j] = ClassUtil.convertValueByType(paramTypes[j], rawRowData.get(j));
                    }

                    retRows.add(tRow);
                }

            }//end while loop


            Object[][] ret = retRows.toArray(new Object[][]{});
            if(ret.length == 0) throw new SkipException("Marked as Skip due to no data provided");

            return ret;

        }catch (IOException e){
            throw new PnxException(e);
        }

    }

    private CurrentTestInfo buildCurrentTestInfo(){
        CurrentTestInfo currentTestInfo = new CurrentTestInfo();

        currentTestInfo.setDisplayName(this.displayName);
        currentTestInfo.setMethodName(this.methodFullName);
        currentTestInfo.setClassName(this.testClass.getClazz().getName());
        currentTestInfo.setOrder(this.order);
        currentTestInfo.setLevel(this.level);
        currentTestInfo.setRepetitionIndex(this.repetitionIndex);
        currentTestInfo.setRepetitionCount(this.repeatedCount);
        currentTestInfo.setRecordIndex(this.recordIndex);
        currentTestInfo.setRecordCount(this.recordCount);
        return currentTestInfo;
    }




}
