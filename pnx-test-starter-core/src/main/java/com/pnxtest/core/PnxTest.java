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

import com.pnxtest.core.api.*;
import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.core.exceptions.CmdArgsParseException;
import com.pnxtest.core.executors.*;
import com.pnxtest.core.outputting.ConsoleLog;
import com.pnxtest.core.outputting.HtmlLog;
import com.pnxtest.core.util.ClassUtil;
import com.pnxtest.core.util.FileUtil;
import com.pnxtest.core.util.StringUtil;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.*;


/**
 * PnxTest engine
 * @author nicolas.chen
 */
public final class PnxTest {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String SuiteDefaultName = "PnxTest Default TestSuite";
    private static final String RunDefaultName = "PnxTest Default TestRun";

    private String target;
    private String environmentId;
    private String configDir;
    private String outputtingDir;
    private Set<Class<?>> controllerClasses = new HashSet<>();
    private Set<IExecutionConfig> gExeConfigBeans = new HashSet<>();
    private static Set<IModConfig> gModuleConfigBeans = new HashSet<>();




    private PnxTest(){}

    public static void run(Class<?> startClass, String[] args){
        PnxTest pnxTest = new PnxTest();
        pnxTest.internalRun(startClass, args);
    }

    private void scanComponentConfiguration(Class<?> bootClass){
        //baseScan
        Set<ICryptoConfig> tCryptoBeans = new HashSet<>();
        try {
            String baseScanPackage = bootClass.getPackage().getName();
            List<Class<?>> classList = ClassUtil.getClasses(baseScanPackage);
            for(Class<?> clazz: classList){
                if(clazz.isAnnotationPresent(Configuration.class)){
                    if(IExecutionConfig.class.isAssignableFrom(clazz)){
                        gExeConfigBeans.add((IExecutionConfig) clazz.newInstance());
                    }
                    else if(ICryptoConfig.class.isAssignableFrom(clazz)){
                        tCryptoBeans.add((ICryptoConfig) clazz.newInstance());
                    }
                    else{
                        gModuleConfigBeans.add((IModConfig) clazz.newInstance());
                    }
                }

                else if(clazz.isAnnotationPresent(Controller.class)){
                    controllerClasses.add(clazz);
                }
            }
        }catch (IOException e){
            //ignore
        } catch (IllegalAccessException | InstantiationException e) {
            //e.printStackTrace();
        }
        if(tCryptoBeans.isEmpty()){
            PnxContext.setProperty(ApplicationKeys.PNX_CRYPTO_IMPL, new DefaultCrypto());
        }else{
            PnxContext.setProperty(ApplicationKeys.PNX_CRYPTO_IMPL, tCryptoBeans.iterator().next());
        }

        PnxContext.setProperty(ApplicationKeys.PNX_MODULE_CONFIG_BEANS, gModuleConfigBeans);
    }

    private void internalRun(Class<?> startClass, String[] args){
        scanComponentConfiguration(startClass);
        tryToParseArgs(startClass, args);
        setupLogger();
        TestSuite testSuite = tryToCreateTestSuite(this.target);
        PnxContext.setProperty(ApplicationKeys.PNX_RUN_MODE, testSuite.getRunMode());
        PnxContext.setProperty(ApplicationKeys.PNX_THREAD_COUNT, testSuite.getThreadCount());
        PnxContext.setTestEnvironmentId(this.environmentId);

        //validate suite
        if(testSuite.getTestRuns().size() == 0){
            System.out.println("------------------------");
            System.out.println("no any test runs found!");
            System.out.println("------------------------");
            System.exit(0);
        }

        //start to execute
        ConsoleLog.printAppInfo();
        //globally configurations when started
        for(IExecutionConfig gClazz: gExeConfigBeans){
            gClazz.onExecuteStart();
        }

        //start running suite
        ConsoleLog.printTestSuiteOnStart(testSuite);
        ResultCollector resultCollector = new ResultCollector(testSuite);//suite result collector
        if(testSuite.getRunMode() == TestSuite.RunMode.classParalleled){
            Set<TestClassTask> tasks = new HashSet<>();
            for (TestRun testRun : testSuite.getTestRuns()) {
                for(TestClass testClass: testRun.getTestClasses()){
                    tasks.add(new TestClassTask(testClass));
                }
            }

            try {
                new TestClassExecutor(tasks, resultCollector).execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(3);
            }
        } else if(testSuite.getRunMode() == TestSuite.RunMode.testParalleled) {
            Set<TestRunTask> tasks = new HashSet<>();
            for (TestRun testRun : testSuite.getTestRuns()) {
                tasks.add(new TestRunTask(testRun));
            }
            try {
                new TestRunExecutor(tasks, resultCollector).execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(3);
            }
        }else{
            //run sequentially
            for (TestRun testRun : testSuite.getTestRuns()) {
                resultCollector.addRunResult(new TestRunTask(testRun).run());
            }
        }

        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(PnxContext.getProperty(ApplicationKeys.PNX_OUTPUTTING_PATH) + File.separator + "index.html"));
            HtmlLog htmlLog = new HtmlLog(printWriter);
            htmlLog.insertHeader(testSuite.getName());
            htmlLog.insertTestSuiteBlock(resultCollector);
            htmlLog.insertTestCaseBlock(resultCollector);
            htmlLog.insertFooter();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }

        ConsoleLog.printTestSuiteOnFinish(resultCollector);

        //globally configurations when finished
        for(IExecutionConfig gClazz: gExeConfigBeans){
            gClazz.onExecuteFinish();
        }

    }

    private void tryToParseArgs(Class<?> startClass, String[] args){
        //parse arguments
        CmdParser cmdParser = new CmdParser();
        try {
            cmdParser.parse(args);

            this.target = cmdParser.getOptionValue("target");
            this.environmentId = cmdParser.getOptionValue("env");
            this.configDir = cmdParser.getOptionValue("config", "./test-config");
            this.outputtingDir = cmdParser.getOptionValue("outputting", "./test-outputting");

            if(StringUtil.isEmpty(this.target)){
                PnxTestApplication pnxTestApplicationOptions = startClass.getAnnotation(PnxTestApplication.class);
                if(pnxTestApplicationOptions !=null && !StringUtil.isEmpty(pnxTestApplicationOptions.suiteFile())){
                    //copy to tmp
                    Path tempPath = Files.createTempFile("pnxtest-",".tmp");
                    File tempSuiteFile = tempPath.toFile();
                    tempSuiteFile.deleteOnExit();
                    Files.copy(getClass().getResourceAsStream("/"+pnxTestApplicationOptions.suiteFile()), tempSuiteFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    this.target = tempSuiteFile.getPath();
                }else{
                    //try to create suite xml
                    Path tempPath = Files.createTempFile("pnxtest-",".tmp");
                    File tempSuiteFile = tempPath.toFile();
                    tempSuiteFile.deleteOnExit();
                    tryToCreateSuiteXmlFile(tempSuiteFile);
                    this.target = tempSuiteFile.getPath();
                }

            }

            if(StringUtil.isEmpty(this.target)){
                System.err.println("[PnxTest] suite xml file not found, pls using -target to specify!");
                cmdParser.printHelper();
                System.exit(2);
                return;
            }


            File suiteFile = new File(this.target);
            if(suiteFile.exists() && suiteFile.isFile()){
                PnxContext.setProperty(ApplicationKeys.PNX_TARGET_FILE, suiteFile.getCanonicalPath());
            }else{
                System.err.println("[PnxTest] suite xml file<"+this.target+"> not found, pls using -target to specify!");
                cmdParser.printHelper();
                System.exit(2);
                return;
            }

            //envID
            if(StringUtil.isEmpty(this.environmentId)){
                PnxTestApplication pnxTestApplicationOptions = startClass.getAnnotation(PnxTestApplication.class);
                if(pnxTestApplicationOptions !=null && !StringUtil.isEmpty(pnxTestApplicationOptions.envID())){
                    this.environmentId = pnxTestApplicationOptions.envID();
                }else{
                    this.environmentId = PnxContext.getTestEnvironmentId();
                }
            }

            if(StringUtil.isEmpty(this.environmentId)){
                this.environmentId = "default";
            }


            File tConfigDir = new File(configDir);
            if("./test-config".equals(configDir)){
                FileUtil.checkAndCreateDir(this.configDir);
                PnxContext.setProperty(ApplicationKeys.PNX_ENVIRONMENT_PATH, tConfigDir.getCanonicalPath());
            }else{
                if(tConfigDir.isDirectory()){
                    PnxContext.setProperty(ApplicationKeys.PNX_ENVIRONMENT_PATH, tConfigDir.getCanonicalPath());
                }else{
                    logger.log(Level.WARNING, this.configDir + " not found!");
                }
            }


            boolean outputtingCreated = FileUtil.checkAndCreateDir(this.outputtingDir);
            if(outputtingCreated){
                PnxContext.setProperty(ApplicationKeys.PNX_OUTPUTTING_PATH, new File(this.outputtingDir).getCanonicalPath());
            }


        }catch (CmdArgsParseException e){
            System.err.println("[PnxTest]"+e.getMessage());
            cmdParser.printHelper();
            System.exit(1);
        }catch (IOException e){
            System.err.println("[PnxTest] specified config or outputting directory error!");
            cmdParser.printHelper();
            System.exit(1);
        }

    }

    private TestSuite tryToCreateTestSuite(String testSuiteXmlFile) {
        //convert xml to suite object
        SuiteXml suiteXml = null;
        try {
            File suiteFile = new File(testSuiteXmlFile);
            JAXBContext jaxbContext = JAXBContext.newInstance(SuiteXml.class);

            //disable dtd validation
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://xml.org/sax/features/validation", false);
            spf.setValidating(false);

            XMLReader xmlReader = spf.newSAXParser().getXMLReader();
            InputSource inputSource = new InputSource(new FileReader(testSuiteXmlFile));
            SAXSource xmlSource = new SAXSource(xmlReader, inputSource);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            //suiteXml = (SuiteXml) jaxbUnmarshaller.unmarshal(suiteFile);
            suiteXml = (SuiteXml) jaxbUnmarshaller.unmarshal(xmlSource);
        }catch (Exception e){
            System.err.println("[PnxTest] suite xml file<"+testSuiteXmlFile+"> parse error, pls check the format and content of suite file!");
            System.exit(2);
        }

        if(suiteXml == null){
            return new TestSuite(SuiteDefaultName);
        }

        String suiteName = suiteXml.getName();
        TestSuite.RunMode runMode = suiteXml.getRunMode();
        int threadCount = suiteXml.getThreadCount();
        threadCount = threadCount>0?threadCount:1;

        if(suiteName == null || suiteName.trim().length() == 0){
            suiteName = SuiteDefaultName;
        }

        if(runMode == null){
            runMode = TestSuite.RunMode.sequence;
        }
        if(runMode == TestSuite.RunMode.sequence){
            threadCount = 1;
        }

        TestSuite testSuite = new TestSuite(suiteName);
        testSuite.setRunMode(runMode);
        testSuite.setThreadCount(threadCount);
        testSuite.setDescription(suiteXml.getDescription()==null?"":suiteXml.getDescription());

        List<String> duplicatedNames = new ArrayList<>();
        for(RunXml runXml : suiteXml.getTests()){
            String testRunName = runXml.getName();
            if(testRunName == null || testRunName.trim().length() == 0){
                testRunName = RunDefaultName;
            }
            //ensure testRunName is not duplicated
            boolean isNotDuplicated = true;
            while(isNotDuplicated){
                for(String tName: duplicatedNames){
                    if(testRunName.equalsIgnoreCase(tName)){
                        testRunName = testRunName + "_duplicated";
                        break;
                    }
                }

                isNotDuplicated = false;
            }
            duplicatedNames.add(testRunName);

            TestRun testRun = new TestRun(testRunName);
            for(ClassXml classXml : runXml.getClasses()){
                if(classXml.getName() == null) continue;

                try{
                    Class<?> clazz = Class.forName(classXml.getName(), true, Thread.currentThread().getContextClassLoader());
                    TestClass testClass = new TestClass(clazz, testRun);

                    if(classXml.getMethods() != null){
                        Set<Method> allTestMethodsInClazz = ClassUtil.getAllMethodsWithAnnotation(testClass.getClazz(), Test.class);
                        testClass.getTestMethods().clear();
                        for(Method testMethod: allTestMethodsInClazz){
                            for(MethodXml methodXml: classXml.getMethods()){
                                String methodName = methodXml.getName();
                                if(testMethod.getName().equals(methodName)){
                                    testClass.addTestMethod(testMethod);
                                }
                            }

                        }
                    }
                    //add to testRun
                    testRun.addTestClass(testClass);
                }catch (ClassNotFoundException cnf){
                    logger.log(Level.WARNING, cnf.getMessage());
                }
            }
            //add to suite
            testSuite.addTestRun(testRun);
        }
        //clear to let gc do its work
        duplicatedNames.clear();
        return testSuite;
    }

    public void tryToCreateSuiteXmlFile(File suiteFile){
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SuiteXml.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE suite SYSTEM \"https://pnxtest.com/pnxtest.xsd\">");

            SuiteXml suiteXml = new SuiteXml();
            suiteXml.setName(PnxTest.SuiteDefaultName);
            suiteXml.setRunMode(TestSuite.RunMode.sequence);
            suiteXml.setThreadCount(1);

            RunXml runXml = new RunXml();
            runXml.setName(PnxTest.RunDefaultName);

            for(Class<?> testClazz: controllerClasses){
                ClassXml classXml = new ClassXml();
                classXml.setName(testClazz.getName());
                runXml.getClasses().add(classXml);
            }

            suiteXml.getTests().add(runXml);

            // Write to System.out
            //marshaller.marshal(suiteXml, System.out);

            // Write to File
            marshaller.marshal(suiteXml, suiteFile);
        }catch (JAXBException e){
            //ignore
            throw new RuntimeException(e);
        }
    }


    @XmlRootElement(name="suite")
    static class SuiteXml{
        private String name;
        private String description;
        private TestSuite.RunMode runMode;
        private int threadCount;
        private List<RunXml> tests = new ArrayList<>();

        public String getName() {
            return name;
        }

        @XmlAttribute
        public void setName(String name) {
            this.name = name;
        }

        public TestSuite.RunMode getRunMode() {
            return runMode;
        }

        public String getDescription() {
            return description;
        }

        @XmlAttribute
        public void setDescription(String description) {
            this.description = description;
        }

        @XmlAttribute
        public void setRunMode(TestSuite.RunMode runMode) {
            this.runMode = runMode;
        }

        public int getThreadCount() {
            return threadCount;
        }

        @XmlAttribute
        public void setThreadCount(int threadCount) {
            this.threadCount = threadCount;
        }

        public List<RunXml> getTests() {
            return tests;
        }

        @XmlElement(name="test")
        public void setTests(List<RunXml> tests) {
            this.tests = tests;
        }
    }

    @XmlRootElement(name="test")
    static class RunXml{
        private String name;
        private List<ClassXml> classes = new ArrayList<>();

        public String getName() {
            return name;
        }

        @XmlAttribute
        public void setName(String name) {
            this.name = name;
        }

        public List<ClassXml> getClasses() {
            return classes;
        }

        @XmlElement(name="class")
        public void setClasses(List<ClassXml> classes) {
            this.classes = classes;
        }
    }

    @XmlRootElement(name="class")
    static class ClassXml{
        private String name;
        private List<MethodXml> methods;

        public String getName() {
            return name;
        }

        @XmlAttribute
        public void setName(String name) {
            this.name = name;
        }

        public List<MethodXml> getMethods() {
            return methods;
        }

        @XmlElement(name="method")
        public void setMethods(List<MethodXml> methods) {
            this.methods = methods;
        }
    }

    @XmlRootElement(name="method")
    static class MethodXml{
        private String name;

        public String getName() {
            return name;
        }

        @XmlAttribute
        public void setName(String name) {
            this.name = name;
        }
    }

    private void setupLogger(){
        try {
            Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            // suppress the logging output to the console
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            if (handlers[0] instanceof ConsoleHandler) {
                rootLogger.removeHandler(handlers[0]);
            }


            logger.setLevel(Level.WARNING);
            // create a TXT formatter
            FileHandler fileTxt = new FileHandler(PnxContext.getProperty(ApplicationKeys.PNX_OUTPUTTING_PATH) + File.separator + "pnx-error.log");
            SimpleFormatter formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);

        }catch (IOException ioe){
            //ignore
        }
    }


    public static void main(String[] args){
        //Thread.currentThread().setName("PnxTest Main");
        //PnxTest.run(PnxTest.class, args);
        //new PnxTest().tryToCreateSuiteXmlFile("pnxtest2020.xml");
    }

}
