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

package com.pnxtest.core.outputting;

import com.pnxtest.core.*;
import com.pnxtest.core.executors.*;
import com.pnxtest.core.steps.StepLog;
import com.pnxtest.core.steps.StepMessage;
import com.pnxtest.core.util.DateUtil;
import com.pnxtest.core.util.StringUtil;
import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.core.api.Status;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HtmlLog {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private PrintWriter printWriter;
    public HtmlLog(PrintWriter printWriter){
        this.printWriter = printWriter;
    }

    public void insertHeader(String title){
        printWriter.println("<html>");
        printWriter.println("<head>");
        printWriter.println("<meta charset=\"UTF-8\">");
        printWriter.println("<link rel=\"icon\" type=\"image/x-icon\" href=\"https://pnxtest.com/favicon.ico?v=1.0\">");
        printWriter.println("<title>"+title+" - PnxTest</title>");
        printWriter.println("<style type=\"text/css\">body{margin:0 auto;padding:0;background:#dedede;font-family:\"Microsoft YaHei\",\"微软雅黑\",Arial,sans-serif;}.pnx-card {max-width: 1200px;min-height: 350px;margin:32px auto 0 auto;text-align: center;position:relative;padding: 28px 32px;border-radius: 4px;overflow: hidden;background-color: #FFF;color: #303133;border: 1px solid #EBEEF5;box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);-webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);}.pnx-suite-header{text-align:center;margin:0 0 10px 0;padding:0;overflow:hidden;}.pnx-suite-header>h1{font-size:24px;margin:0;padding:0;}.pnx-suite-header>p.desc{color:#999999;font-size:10px;margin:5px 0;padding:0;line-height:10px;}.pnx-appInfo{text-align: center;font-size: 12px;color: #696969;margin-top: 10px;margin-bottom: 20px;}.pnx-appInfo>.version{margin-left: 10px;}.pnx-message {color: #989898;background: #f5f6f7;border-bottom: 1px solid #cacaca;font-size: 11px;padding: 5px 5px;margin: 0;}.pnx-tag{padding: 2px;border-radius: 4px;box-sizing: border-box;white-space: nowrap;font-size: 9px;border: 1px solid #b3d8ff;color: #409eff;margin-right: 6px;}.pnx-tag-dp{border: 1px solid #783887;color:#6f42c1;}.stats-passed {color: #00AA00;}.status-passed {background: #00AA00;color: #ffffff;}.stats-failed {color: #DD0000;}.status-failed {background: #DD0000;color: #ffffff;}.stats-skipped {color: #FF66FF;}.status-skipped {background: #FF66FF;color: #ffffff;}.stats-total {color: #002060;}.status-nt {background: #dedede;color: #ffffff;}.pnx-table-container {}.pnx-table {color: #606266;font-size: 12px;width: 100%;table-layout: fixed;border-collapse: separate;border-spacing: 0;}.pnx-table>tbody>tr.record:hover {background: #f2f8fe;}.cell {text-align: left;cursor: default;padding: 2px 10px;box-sizing: border-box;word-break: break-all;white-space: normal;line-height: 23px;text-overflow: ellipsis;overflow: hidden;border-top: 1px solid #EBEEF5;}.pnx-table>thead>tr>th.cell {border-top: 1px solid #ddd;}.pnx-table tr>.cell:last-child {border-right: none;}.cell-stats {width: 110px;text-align: center;}.cell-date {width: 250px;}.cell-total-duration {width: 150px;}.cell-env {width: 150px;text-align: center;}.cell-expanded{width: 30px;}.cell-cate {width: 220px;white-space: nowrap;}.cell-runName {width: 250px;}.cell-subject {width: auto;white-space: nowrap;}.cell-subject>span.desc{color:#898989;margin-left:8px;}.cell-level {width: 110px;text-align: center;}.cell-operator {width: 110px;text-align: center;}.cell-duration {width: 110px;text-align: center;}.cell-status {width: 110px;text-align: center;}.pnx-table>tbody>tr:first-child>td {border-top: 1px solid #ddd;}.pnx-table>tbody>tr:last-child>td {border-bottom: 1px solid #EBEEF5;}.test-suite-table>tbody>tr:last-child>td{border-bottom: 1px solid #ccc;}.test-runs-table>tbody>tr:last-child>td{border-bottom: 1px solid #ccc;}.caret-wrapper {display: inline-flex;flex-direction: column;align-items: center;height: 24px;width: 24px;vertical-align: middle;cursor: pointer;overflow: initial;position: relative;}.sort-caret {width: 0;height: 0;border: 5px solid transparent;position: absolute;left: 7px;}.sort-caret.ascending {border-bottom-color: #c0c4cc;top: 0;}.sort-caret.descending {border-top-color: #c0c4cc;bottom: 2px;}.order-asc .sort-caret.ascending {border-bottom-color: #409eff;}.order-desc .sort-caret.descending {border-top-color: #409eff;}.pnx-form-container{margin:32px;}.pnx-form-item{margin-right: 0;margin-bottom: 0;width: 100%;height: auto;display: inline-block;vertical-align: top;}.pnx-form-item:after, .pnx-form-item:before {display: table;content: \"\";}.pnx-form-item label{float: left;display: inline-block;width: 90px;color: #99a9bf;text-align: left;line-height: 26px;padding: 0 12px 0 0;box-sizing: border-box;}.pnx-form-item div.content{float:none;vertical-align: top;line-height: 26px;position: relative;width: auto;overflow:hidden;}.pnx-form-item-inline{display: flex;}.pnx-status-container{margin-top: 5px;padding: 5px 0;border-top:1px solid #fafafa;}.pnx-item-inline{margin-right: 30px;}.pnx-expanded-icon{position: relative;cursor: pointer;color: #666;transition: transform .2s ease-in-out;height: 10px;}.pnx-expanded-icon.expanded{transform: rotate(90deg);}.pnx-expanded-icon>span.icon{position: absolute;left: 50%;top: 0;margin-left: -5px;margin-top: -5px;}.pnx-expanded-icon>span.icon:before {content: \">\";}.pnx-logs-viewer{position: relative;padding:10px 0;max-height: 600px;overflow: auto;}.pnx-tree-item {position: relative;padding-bottom: 20px;}.pnx-tree-item:last-child{padding-bottom:0;}.pnx-tree-item:last-child>div.pnx-tree-item-tail {display: none;}.pnx-tree-item-tail {position: absolute;left: 4px;height: 100%;border-left: 2px solid #e4e7ed;}.pnx-tree-item-node {position: absolute;background-color: #99A9BE;border-radius: 50%;display: flex;justify-content: center;align-items: center;box-sizing: border-box;width: 10px;height: 10px;z-index:1;}.pnx-tree-item-node:hover{cursor:pointer;}.pnx-tree-item.expanded>.pnx-tree-item-node{background-color: white;border: 2px solid #99A9BE;}.pnx-tree-item.expanded>.pnx-svg-box{display: block;}.pnx-tree-item.expanded>.children{display: block;}.pnx-tree-item .children{display: none;padding-left: 24px;margin: 0;}.pnx-svg-box{display: none;height:30px;position:relative;top: -3px;}.pnx-tree-item-content-wrapper {position: relative;padding-left: 28px;top: -3px;}.pnx-tree-item-content-wrapper >div.desc{padding: 0 10px;position: relative;min-width:300px;}.pnx-tree-item-content-wrapper >div.desc:hover{background:#f5f6f7;}.pnx-tree-item-content-wrapper>div.desc>div.title{padding-top: 4px;line-height: 1;font-weight: bold;}.pnx-tree-item-content-wrapper>div.desc>div.content{line-height:24px;color: #909399;}.pnx-tree-item-content-wrapper:last-child .content{padding-bottom:0;}.warning {padding: 2px 8px;background-color: #fff6f7;border-left: 3px solid #fe6c6f;margin: 8px 0;color: red;}span.step-tag{padding:2px 4px;border-radius:2px;margin-right:10px;}span.step-tag-type{background: #409eff;color:#ffffff;}span.step-tag-subtype{background: #909399;color:#ffffff;}.pnx-repetition-table,.pnx-repetition-summary-table{table-layout: fixed;width:100%;border-collapse: separate;border-spacing: 1px;background:#585858;}.pnx-repetition-summary-table>thead>tr>th,.pnx-repetition-table>thead>tr>th{border-top:1px solid #99A9BD;}.pnx-repetition-summary-table>thead>tr>th,.pnx-repetition-summary-table>tbody>tr>td{border-bottom:1px solid #ffffff;font-size: 10px;color:#99A9BD;padding:0 5px;background: #fafafa;}.pnx-repetition-summary-table .total, .pnx-repetition-summary-table .max, .pnx-repetition-summary-table .min, .pnx-repetition-summary-table .avg, .pnx-repetition-summary-table .successRate{width: 120px;text-align: center;}.pnx-repetition-table>thead>tr>th,.pnx-repetition-table>tbody>tr>td{border-bottom:1px solid #ffffff;font-size: 10px;color:#99A9BD;padding:0 5px;background: #fafafa;}.pnx-repetition-table tr>th.no,.pnx-repetition-table tr>th.duration,.pnx-repetition-table tr>th.status, .pnx-repetition-table tr>td.no,.pnx-repetition-table tr>td.duration,.pnx-repetition-table tr>td.status {width:120px;text-align: center;}.pnx-repetition-table tr>th.remark,.pnx-repetition-table tr>td.remark{text-align: left;}.pnx-repetition-table tr>th.args,.pnx-repetition-table tr>td.args{text-align: left;max-width: 300px;}.pnx-repetition-table tr>td.remark{color:red}.pass-tick{color:#00AA00}.fail-fork{color:red}.msg-success {padding: 2px 8px;margin: 8px 0;border-left: 3px solid #67c23a;background-color: #f0f9eb;color: #67c23a;}.msg-error {padding: 2px 8px;margin: 8px 0;border-left: 3px solid #fe6c6f;background-color: #fff6f7;color: red;}.msg-warning {padding: 2px 8px;margin: 8px 0;background-color: #fdf6ec;border-left: 3px solid #e6a23c;color: #e6a23c;}.msg-info {padding: 2px 8px;margin: 8px 0;background-color: #f4f4f5;border-left: 3px solid #909399;color: #909399;}</style>");
        printWriter.println("</head>");
        printWriter.println("<body><div class=\"pnx-card\">");
        printWriter.flush();
    }

    public void insertFooter(){
        printWriter.println("</div>");
        printWriter.println("<div class=\"pnx-appInfo\">Powered by <a href=\""+ AppInfo.url+"\">"+AppInfo.name+"</a><span class=\"version\">V"+ AppInfo.version +"</span></div>");
        printWriter.println("<script type=\"text/javascript\">!function(){\"use strict\";function e(e,t,r){Object.keys(e).forEach(function(n){e[n].addEventListener(t,r,!1)})}function t(e){const t=[];for(var r=0,n=e.length;n>r;r++)for(var o=0,c=e[r].cells.length;c>o;o++)\"undefined\"==typeof t[r]&&(t[r]={},t[r].key=r),t[r][o]=e[r].cells[o].innerText;return t}function r(r){const o=\"order-asc\",c=\"order-desc\",a=function(e){if(\"TH\"===e.tagName.toUpperCase())return e;var t=e.parentNode;return\"TH\"===t.tagName.toUpperCase()?t:a(t)},l=a(r);if(!l.classList.contains(\"sortable\"))return!1;const s=function(e){var t=e.parentNode;return\"TABLE\"===t.tagName.toUpperCase()?t:s(t)},d=s(r);if(!d)return!1;const i=l.cellIndex;var u=t(d.querySelectorAll(\"tr.record\"));const f=l.classList.contains(o)?-1:1;u.sort(function(e,t){return e[i]<t[i]?-1*f:e[i]>t[i]?f:0});var p=\"\";u.forEach(function(e){p+=d.querySelectorAll(\"tbody>tr.record\")[e.key].outerHTML}),d.querySelector(\"tbody\").innerHTML=p;const y=d.querySelectorAll(\"thead th\");Object.keys(y).forEach(function(e){y[e].classList.remove(c),y[e].classList.remove(o)}),1===f?l.classList.add(o):l.classList.add(c),d.querySelectorAll(\"tbody>tr.record>td.cell-expanded>div.pnx-expanded-icon\").forEach(function(e){e.classList.remove(\"expanded\")});const m=document.querySelectorAll(\"table.sortable tbody>tr>td.cell-expanded\");e(m,\"click\",function(e){n(e.target)})}function n(t){const r=function(e){if(\"TD\"===e.tagName.toUpperCase())return e;var t=e.parentNode;return\"TD\"===t.tagName.toUpperCase()?t:r(t)},n=r(t),c=n.parentNode,a=n.firstElementChild,l=n.getAttribute(\"rel\");if(null!==l)if(a.classList.contains(\"expanded\"))a.classList.remove(\"expanded\"),c.parentNode.removeChild(c.parentNode.querySelector(\"tr[rel='\"+l+\"']\"));else{a.classList.add(\"expanded\");var s=document.getElementById(\"data\").querySelector(\"tr[rel='\"+l+\"']\").cloneNode(!0);c.insertAdjacentHTML(\"afterend\",s.outerHTML);const d=document.querySelectorAll(\"table.sortable>tbody .pnx-tree-item>.pnx-tree-item-node\");e(d,\"click\",function(e){o(e.target)})}}function o(e){var t=e.parentNode;t.classList.contains(\"expanded\")?t.classList.remove(\"expanded\"):t.classList.add(\"expanded\")}window.addEventListener(\"load\",function(){const t=document.querySelectorAll(\"table.sortable thead th\");e(t,\"click\",function(e){r(e.target)});const c=document.querySelectorAll(\"table.sortable tbody>tr>td.cell-expanded\");e(c,\"click\",function(e){n(e.target)});const a=document.querySelectorAll(\"table.sortable>tbody .pnx-tree-item>.pnx-tree-item-node\");e(a,\"click\",function(e){o(e.target)})},!1)}();</script>");
        printWriter.println("</body>");
        printWriter.println("</html>");
        printWriter.flush();
        printWriter.close();
    }


    public void insertTestSuiteBlock(ResultCollector resultCollector){
        String testSuiteBlock = "<div class=\"pnx-suite-header\"><h1>"+resultCollector.getTestSuite().getName()+"</h1><p class=\"desc\">"+resultCollector.getTestSuite().getDescription()+"</p></div>";
        testSuiteBlock += "<table class=\"pnx-table test-suite-table\">";
        testSuiteBlock += "<thead><tr>" +
                "<th class=\"cell cell-date\">执行日期</th>" +
                "<th class=\"cell cell-total-duration\">总耗时</th>" +
                "<th class=\"cell cell-subject\">执行参数</th>" +
                "<th class=\"cell cell-stats\">总数</th>" +
                "<th class=\"cell cell-stats\">成功</th>" +
                "<th class=\"cell cell-stats\">失败</th>" +
                "<th class=\"cell cell-stats\">跳过</th>" +
                "</tr></thead>";
        testSuiteBlock += "<tbody><tr>" +
                "<td class=\"cell\"><b>"+ DateUtil.formatDate(new Date(resultCollector.getStartTime()),"yyyy-MM-dd HH:mm")+"</b></td>" +
                "<td class=\"cell cell-total-duration\"><b>"+DateUtil.breakDownDurationToHuman(resultCollector.getDuration())+"</b></td>" +
                "<td class=\"cell cell-subject\"><b>env: "+ PnxContext.getTestEnvironmentId() +" | mode: "+resultCollector.getTestSuite().getRunMode()+" | threads: "+PnxContext.getInt(ApplicationKeys.PNX_THREAD_COUNT)+"</b></td>" +
                "<td class=\"cell cell-stats stats-total\"><b>"+resultCollector.getTotalCount()+"</b></td>" +
                "<td class=\"cell cell-stats stats-passed\"><b>"+resultCollector.getPassedCount()+"</b></td>" +
                "<td class=\"cell cell-stats stats-failed\"><b>"+resultCollector.getFailedCount()+"</b></td>" +
                "<td class=\"cell cell-stats stats-skipped\"><b>"+resultCollector.getSkippedCount()+"</b></td>" +
                "</tr></tbody></table>";
        printWriter.println(testSuiteBlock);
        printWriter.flush();
    }

    public void insertTestRunBlock(){
        printWriter.println("<h1>TestRun Information</h1>");
        printWriter.flush();
    }

    public void insertTestCaseBlock(ResultCollector resultCollector){
        String testRunBlock = "";
        String testCaseBlock = "";
        String tExpandedRow = "<table id=\"data\" style=\"display:none;\"><tbody>";
        testCaseBlock += "<table class=\"pnx-table sortable\">";
        testCaseBlock += "<thead><tr>" +
                "<th class=\"cell cell-expanded\"><div class=\"pnx-expanded-icon\"><span class=\"icon\"></span></div></div>"+
                "<th class=\"cell cell-cate sortable\">类别<span class=\"caret-wrapper\"><i class=\"sort-caret ascending\"></i><i class=\"sort-caret descending\"></i></span></th>" +
                "<th class=\"cell cell-subject sortable\">主题<span class=\"caret-wrapper\"><i class=\"sort-caret ascending\"></i><i class=\"sort-caret descending\"></i></span></th>" +
                "<th class=\"cell cell-level sortable\">级别<span class=\"caret-wrapper\"><i class=\"sort-caret ascending\"></i><i class=\"sort-caret descending\"></i></span></th>" +
                "<th class=\"cell cell-duration sortable\">耗时(s)<span class=\"caret-wrapper\"><i class=\"sort-caret ascending\"></i><i class=\"sort-caret descending\"></i></span></th>" +
                "<th class=\"cell cell-operator sortable\">维护人<span class=\"caret-wrapper\"><i class=\"sort-caret ascending\"></i><i class=\"sort-caret descending\"></i></span></th>" +
                "<th class=\"cell cell-status sortable\">状态<span class=\"caret-wrapper\"><i class=\"sort-caret ascending\"></i><i class=\"sort-caret descending\"></i></span></th>" +
                "</tr></thead>";
        testCaseBlock += "<tbody>";


        List<Map.Entry<TestRun, RunResult>> runResultEntries = resultCollector.getRunsResult().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(TestRun::getName)))
                .collect(Collectors.toList());
        boolean showRunName = runResultEntries.size()>1;
        testRunBlock += "<table class=\"pnx-table test-runs-table\"><tbody>";
        for(Map.Entry<TestRun, RunResult> runResultEntry: runResultEntries){
            TestRun testRun = runResultEntry.getKey();
            RunResult eachRunResult = runResultEntry.getValue();
            String runNameInRow = showRunName?StringUtil.truncate(testRun.getName(),8)+" - ":"";
            testRunBlock += "<tr>" +
                    "<td class=\"cell cell-runName\">"+testRun.getName()+"</td>" +
                    "<td class=\"cell cell-total-duration\">"+DateUtil.breakDownDurationToHuman(eachRunResult.getDuration())+"</td>" +
                    "<td class=\"cell cell-subject\"></td>" +
                    "<td class=\"cell cell-stats stats-total\">"+eachRunResult.getTotalCount()+"</td>"+
                    "<td class=\"cell cell-stats stats-passed\">"+eachRunResult.getPassedCount()+"</td>"+
                    "<td class=\"cell cell-stats stats-failed\">"+eachRunResult.getFailedCount()+"</td>"+
                    "<td class=\"cell cell-stats stats-skipped\">"+eachRunResult.getSkippedCount()+"</td>"+
                    "</tr>";


            Set<Map.Entry<TestClass, ClassResult>> classResultEntries = eachRunResult.getResults().entrySet();
            List<Map.Entry<TestClass, ClassResult>> sortedClassResultEntries = classResultEntries.stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(TestClass::getModule).reversed()))
                    .collect(Collectors.toList());


            for(Map.Entry<TestClass, ClassResult> classResultEntry: sortedClassResultEntries){
                ClassResult eachClassResult = classResultEntry.getValue();

                List<MethodResult> methodResults = eachClassResult.getResults();

                String clazzError = "";
                if(eachClassResult.getErrors().size()>0){
                    for(Throwable throwable:eachClassResult.getErrors()){
                        clazzError += throwable.getMessage() + "\n";
                    }
                }

                for(MethodResult methodResult: methodResults){
                    TestCase eachTestCase = methodResult.getTestCase();
                    String tSubjectTag = "";
                    if(eachTestCase.getRepeatedCount()>1){
                        tSubjectTag += "<span class=\"pnx-tag\">RepeatedTest</span>";
                    }
                    if(eachTestCase.hasDataDriven()){
                        tSubjectTag += "<span class=\"pnx-tag pnx-tag-dp\">DataDriven</span>";
                    }

                    tExpandedRow += "<tr class=\"data-item\" rel=\""+methodResult.getId()+"\"><td class=\"cell\" colspan=\"7\">";
                    tExpandedRow += "<div class=\"pnx-form-container\">";
                    tExpandedRow += "<div class=\"pnx-form-item\"><label>ID</label><div class=\"content\">"+eachTestCase.getId()+"</div></div>";
                    tExpandedRow += "<div class=\"pnx-form-item\"><label>Title</label><div class=\"content\">"+eachTestCase.getDisplayName()+"</div></div>";
                    tExpandedRow += "<div class=\"pnx-form-item\"><label>Method</label><div class=\"content\">"+eachTestCase.getFullName()+"</div></div>";
                    tExpandedRow += "<div class=\"pnx-form-item pnx-form-item-inline\">" +
                            "<div class=\"pnx-item-inline\"><label>Module</label><div class=\"content\">"+eachTestCase.getTestClass().getModule()+"</div></div>" +
                            "<div class=\"pnx-item-inline\"><label>Level</label><div class=\"content\">"+eachTestCase.getLevel()+"</div></div>" +
                            "</div>";
                    tExpandedRow += "<div class=\"pnx-form-item\"><label>Description</label><div class=\"content\">"+ StringUtil.replaceNewLine(eachTestCase.getDescription())+"</div></div>";

                    tExpandedRow += "<div class=\"pnx-status-container\">";
                    tExpandedRow += "<div class=\"pnx-form-item pnx-form-item-inline\">" +
                            "<div class=\"pnx-item-inline\"><label>Result</label><div class=\"content\">"+formatTestResult2(methodResult.getStatus())+"</div></div>" +
                            "<div class=\"pnx-item-inline\"><label style=\"margin-left:20px;\">Duration</label><div class=\"content\"><b>"+DateUtil.breakDownDurationToHumanInSecond(methodResult.getDuration())+"s</b></div></div>" +
                            "<div class=\"pnx-item-inline\"><label style=\"margin-left:20px;\">Thread Id</label><div class=\"content\"><b>"+methodResult.getThreadId()+"</b></div></div>" +
                            "</div>";

                    String combineRemark = clazzError;
                    if(!StringUtil.isEmpty(methodResult.getRemark())){
                        combineRemark = clazzError + methodResult.getRemark();
                    }

                    if(!StringUtil.isEmpty(combineRemark)) {
                        tExpandedRow += "<div class=\"pnx-form-item\"><label>Remark</label><div class=\"content\" style=\"color:red;\">" + StringUtil.escapeHtmlAndReplaceNewLine(combineRemark) + "</div></div>";
                    }

                    String logTable = "";
                    if(methodResult.getTestCase().getRepeatedCount()>1 || methodResult.getTestCase().hasDataDriven()){
                        logTable = printRepetitionLogs(methodResult);
                        if(StringUtil.isEmpty(logTable)){
                            if (methodResult.getStepLogs().size() > 0) {
                                logTable = printTreeTrace(getStepTree(methodResult.getStepLogs()));
                            }
                        }
                    }else {
                        if (methodResult.getStepLogs().size() > 0) {
                            logTable = printTreeTrace(getStepTree(methodResult.getStepLogs()));
                        }
                    }

                    if(!StringUtil.isEmpty(logTable)) {
                        tExpandedRow += "<div class=\"pnx-form-item\"><label>Logs</label><div class=\"content\"><div class=\"pnx-logs-viewer\">" + logTable + "</div></div></div>";
                    }

                    tExpandedRow += "</div>";

                    tExpandedRow += "</div></td></tr>";

                    testCaseBlock += "<tr class=\"record\">" +
                            "<td class=\"cell cell-expanded\" rel=\""+methodResult.getId()+"\"><div class=\"pnx-expanded-icon\"><span class=\"icon\"></span></div></td>" +
                            "<td class=\"cell cell-cate\">"+runNameInRow + eachTestCase.getTestClass().getModule()+"</td>" +
                            "<td class=\"cell cell-subject\">"+tSubjectTag+eachTestCase.getDisplayName()+"<span class=\"desc\">"+ StringUtil.escapeHtml(eachTestCase.getDescription())+"</span></td>" +
                            "<td class=\"cell cell-level\">"+eachTestCase.getLevel()+"</td>" +
                            "<td class=\"cell cell-duration\">"+DateUtil.breakDownDurationToHumanInSecond(methodResult.getDuration())+"</td>" +
                            "<td class=\"cell cell-operator\">"+eachTestCase.getTestClass().getMaintainer()+"</td>" +
                            "<td class=\"cell cell-status\">"+formatTestResult(methodResult.getStatus())+"</td>" +
                            "</tr>";

                }

            }
        }

        testCaseBlock += "</tbody>";
        testCaseBlock += "</table>";
        tExpandedRow += "</tbody></table>";

        testRunBlock += "</tbody></table>";
        if(runResultEntries.size()>1){
            printWriter.println(testRunBlock);
        }
        printWriter.println(testCaseBlock);
        printWriter.println(tExpandedRow);
        printWriter.flush();
    }

    private static String formatTestResult(Status status){
        if(status == Status.PASSED){
            return "<div class=\"status-passed\">Passed</div>";
        }

        if(status == Status.FAILED){
            return "<div class=\"status-failed\">Failed</div>";
        }

        if(status == Status.SKIPPED){
            return "<div class=\"status-skipped\">Skipped</div>";
        }

        return "<div class=\"status-nt\">"+ status +"</div>";

    }
    private static String formatTestResult2(Status status){
        if(status == Status.PASSED){
            return "<span class=\"stats-passed\"><b>Passed</b></span>";
        }

        if(status == Status.FAILED){
            return "<span class=\"stats-failed\"><b>Failed</b></span>";
        }

        if(status == Status.SKIPPED){
            return "<span class=\"stats-skipped\"><b>Skipped</b></span>";
        }

        return "<span class=\"stats-nt\"><b>"+ status +"</b></span>";
    }

    private static String printTreeTrace(List<StepTree> stepTrees){
        String treeHtml = "";
        int count = 0;
        for(StepTree stepTree:stepTrees){
            count++;
            String fixedLastTail = "";
            if(count == stepTrees.size() && stepTree.getChildren().size()>0){
                fixedLastTail = "<div class=\"pnx-tree-item-tail\"></div>";;
            }

            String content = "";

            if(stepTree.getComponent() != null){
                content += "<span class=\"step-tag step-tag-type\">"+stepTree.getComponent()+"</span>";
            }
            if(stepTree.getSubComponent() != null){
                content += "<span class=\"step-tag step-tag-subtype\">"+stepTree.getSubComponent()+"</span>";
            }

            String nodeTitle = stepTree.getTitle();
            String title = DateUtil.formatDateStripYear(new Date(stepTree.getStartTime()));
            if(!StringUtil.isEmpty(nodeTitle)){
                title += " - " + StringUtil.escapeHtml(nodeTitle);
            }

            StepMessage stepMessage = stepTree.getContent();
            if(stepMessage != null && stepMessage.getContent() != null){
                String messageContent = StringUtil.escapeHtmlAndReplaceNewLine(stepMessage.getContent());

                if(stepMessage.getType() == StepMessage.Type.SUCCESS){
                    content += "<div class=\"msg-success\">"+messageContent+"</div>";
                }else if(stepMessage.getType() == StepMessage.Type.ERROR){
                    content += "<div class=\"msg-error\">"+messageContent+"</div>";
                }else if(stepMessage.getType() == StepMessage.Type.WARNING){
                    content += "<div class=\"msg-warning\">"+messageContent+"</div>";
                } else{
                    content += "<div class=\"msg-info\">"+messageContent+"</div>";
                }
            }

            treeHtml += "<div class=\"pnx-tree-item expanded\">";

            treeHtml += "<div class=\"pnx-tree-item-tail\"></div>";
            treeHtml += "<div class=\"pnx-tree-item-node\"></div>";

            treeHtml += "<div class=\"pnx-tree-item-content-wrapper\">"+fixedLastTail;
            treeHtml += "<div class=\"desc\">";
            treeHtml += "<div class=\"title\">"+title+"</div>";
            treeHtml += "<div class=\"content\">"+content+"</div>";
            treeHtml += "</div>";
            treeHtml += "</div>";

            if(stepTree.getChildren().size()>0){
                treeHtml += "<div class=\"pnx-svg-box\">" +
                        "<svg height=\"40\" width=\"50\" style=\"position:absolute;left:0;top:0;\">" +
                        "<polyline points=\"4,0 29,10 29,33\" fill=\"none\" stroke=\"#e4e7ed\" stroke-width=\"2\"></polyline>" +
                        "</svg>" +
                        "</div>";

                treeHtml += "<ul class=\"children\">";
                treeHtml += printTreeTrace(stepTree.getChildren());
                treeHtml += "</ul>";
            }

            treeHtml += "</div>";

        }

        return treeHtml;
    }


    private static List<StepTree> getStepTree(List<StepLog> stepLogs) {
        return buildStepTree("0", stepLogs);
    }

    private static List<StepTree> buildStepTree(String pId, List<StepLog> stepLogs) {
        List<StepTree> ret= new LinkedList<>();

        if(pId == null) return ret;

        for(StepLog stepLog : stepLogs){
            if( stepLog.getId().equals(stepLog.getpId()) ) continue;
            if( !pId.equals(stepLog.getpId())) continue;

            StepTree stepTree = new StepTree();
            stepTree.setStartTime(stepLog.getStartTime());
            stepTree.setTitle(stepLog.getTitle());
            stepTree.setComponent(stepLog.getComponent());
            stepTree.setSubComponent(stepLog.getSubComponent());
            stepTree.setThreadId(stepLog.getThreadId());
            stepTree.setContent(stepLog.getContent());
            stepTree.setMethodName(stepLog.getMethodName());

            List<StepTree> childrenTree = buildStepTree(stepLog.getId(), stepLogs);
            if(childrenTree.size()>0){
                stepTree.setChildren(childrenTree);
            }

            ret.add(stepTree);

        }

        return ret;
    }


    private String printRepetitionLogs(MethodResult methodResult){
        List<RepetitionLog> repetitionLogs = methodResult.getRepetitionLogs();
        if(repetitionLogs.size() == 0) return "";
        if(repetitionLogs.size() == 1){
            return printDataDrivenLog(repetitionLogs.get(0).getDataProviderLogs());
        }


        //repetitions
        String h = "<table class=\"pnx-repetition-table\">";
        h += "<thead><tr><th class=\"no\">Repetition</th><th class=\"duration\">Duration(s)</th><th class=\"status\">Status</th><th class=\"remark\">Remark</th></tr></thead>";
        h += "<tbody>";

        long totalDuration = 0, maxDuration = 0, minDuration = repetitionLogs.get(0).getDuration(), totalPassedCount = 0;
        for(RepetitionLog eachRepetition: repetitionLogs){
            h += "<tr><td class=\"no\">"+(eachRepetition.getIndex()+1)+"</td><td class=\"duration\">"+DateUtil.breakDownDurationToHumanInSecond(eachRepetition.getDuration())+"</td><td class=\"status\">"+formatStatus(eachRepetition.getStatus())+"</td><td class=\"remark\">"+(eachRepetition.getRemark()==null?"":StringUtil.escapeHtmlAndReplaceNewLine(eachRepetition.getRemark()))+"</td></tr>";

            if(eachRepetition.getStatus() == Status.PASSED) totalPassedCount++;
            totalDuration += eachRepetition.getDuration();
            maxDuration = Math.max(eachRepetition.getDuration(), maxDuration);
            minDuration = Math.min(eachRepetition.getDuration(), minDuration);

            List<DataProviderLog> dataProviderLogs = eachRepetition.getDataProviderLogs();
            String d = printDataDrivenLog(dataProviderLogs);

            if(!StringUtil.isEmpty(d)){
                h += "<tr><td></td><td colspan=\"3\">"+d+"</td></tr>";
            }
        }

        h += "</tbody></table>";


        //summary stats
        //double totalTimeInDouble = BigDecimal.valueOf((float) totalDuration / 1000000000).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        //double maxTimeInDouble = BigDecimal.valueOf((float) maxDuration / 1000000000).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        //double minTimeInDouble = BigDecimal.valueOf((float) minDuration / 1000000000).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        //double avgTimeInDouble = BigDecimal.valueOf((float) totalDuration / (repetitionLogs.size() * 1000000000)).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        long avgTime = totalDuration / repetitionLogs.size();
        float actualSuccessRate = (float)(totalPassedCount * 100) / repetitionLogs.size();
        DecimalFormat decimalFormat=new DecimalFormat("#.##");
        String p = "";
        if(Float.compare(actualSuccessRate, methodResult.getTestCase().getExpectedSuccessRate())>=0){
            p = "<span class=\"pass-tick\" title=\"Expected success rate is "+methodResult.getTestCase().getExpectedSuccessRate()+"\">"+decimalFormat.format(actualSuccessRate)+"</span>";
        }else{
            p = "<span class=\"fail-fork\" title=\"Expected success rate is "+methodResult.getTestCase().getExpectedSuccessRate()+"\">"+decimalFormat.format(actualSuccessRate)+"</span>";
        }
        String s = "<table class=\"pnx-repetition-summary-table\">" +
                "<thead><tr><th class=\"total\">Total(s)</th><th class=\"max\">Max.(s)</th><th class=\"min\">Min.(s)</th><th class=\"avg\">Avg.(s)</th><th class=\"successRate\">Success Rate(%)</th><th></th></tr></thead>" +
                "<tbody><tr><td class=\"total\">"+DateUtil.breakDownDurationToHumanInSecond(totalDuration)+"</td><td class=\"max\">"+DateUtil.breakDownDurationToHumanInSecond(maxDuration)+"</td><td class=\"min\">"+DateUtil.breakDownDurationToHumanInSecond(minDuration)+"</td><td class=\"avg\">"+DateUtil.breakDownDurationToHumanInSecond(avgTime)+"</td><td class=\"successRate\">"+p+"</td><td></td></tr></tbody>" +
                "</table>";

        return s+h;
    }

    private String printDataDrivenLog(List<DataProviderLog> dataProviderLogs){
        if(dataProviderLogs.size()>0){
            String d = "<table class=\"pnx-repetition-table\">";
            d += "<thead><tr><th class=\"no\">Record</th><th class=\"args\">Arguments</th><th class=\"duration\">Duration(s)</th><th class=\"status\">Status</th><th class=\"remark\">Remark</th></tr></thead>";
            d += "<tbody>";
            for(DataProviderLog eachRecord: dataProviderLogs){
                d += "<tr><td class=\"no\">"+(eachRecord.getIndex()+1)+"</td><td class=\"args\">"+eachRecord.getArgs()+"</td><td class=\"duration\">"+DateUtil.breakDownDurationToHumanInSecond(eachRecord.getDuration())+"</td><td class=\"status\">"+formatStatus(eachRecord.getStatus())+"</td><td class=\"remark\">"+(eachRecord.getRemark()==null?"":StringUtil.escapeHtmlAndReplaceNewLine(eachRecord.getRemark()))+"</td></tr>";
            }

            d += "</tbody></table>";

            return d;
        }

        return "";
    }

    private String formatStatus(Status status){
        if(status == Status.PASSED){
            return "<span class=\"pass-tick\">&#10003;</span>";
        }

        if(status == Status.FAILED){
            return "<span class=\"fail-fork\">&#10007;</span>";
        }

        return "<span>"+status+"</span>";
    }

}
