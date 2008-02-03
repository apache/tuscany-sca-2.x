/* 
 * ========================================================================
 * 
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.apache.tuscany.sca.host.webapp.junit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Locale;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;

/**
 * Format the test results in XML.
 *
 * @version $Id: XMLFormatter.java 239169 2005-05-05 09:21:54Z vmassol $
 */
public class XMLFormatter implements TestListener {
    /**
     * Errors attribute for testsuite elements
     */
    public static final String ATTR_ERRORS = "errors";

    /**
     * Failures attribute for testsuite elements
     */
    public static final String ATTR_FAILURES = "failures";

    /**
     * Message attribute for failure elements (message of the exception)
     */
    public static final String ATTR_MESSAGE = "message";

    /**
     * Name attribute for property, testcase and testsuite elements
     */
    public static final String ATTR_NAME = "name";

    /**
     * Tests attribute for testsuite elements (number of tests executed)
     */
    public static final String ATTR_TESTS = "tests";

    /**
     * Time attribute for testcase and testsuite elements
     */
    public static final String ATTR_TIME = "time";

    /**
     * Type attribute for failure and error elements
     */
    public static final String ATTR_TYPE = "type";

    /**
     * Default stack filter patterns.
     */
    private static final String[] DEFAULT_STACK_FILTER_PATTERNS =
        new String[] {"junit.framework.TestCase", "junit.framework.TestResult", "junit.framework.TestSuite",
                      "junit.framework.Assert.", // don't filter AssertionFailure
                      "java.lang.reflect.Method.invoke("};

    /**
     * The error element (for a test case)
     */
    public static final String ERROR = "error";

    /**
     * The failure element (for a test case)
     */
    public static final String FAILURE = "failure";

    /**
     * A single testcase element
     */
    public static final String TESTCASE = "testcase";

    /**
     * A single test suite results.
     */
    public static final String TESTSUITE = "testsuite";

    /**
     * Root element for all test suites.
     */
    public static final String TESTSUITES = "testsuites";

    /**
     * Returns the stack trace of an exception as String.
     * 
     * @param theThrowable the exception from which to extract the stack trace
     *        as a String
     * @return the exception stack trace as a String
     */
    public static String exceptionToString(Throwable theThrowable) {
        return exceptionToString(theThrowable, null);
    }

    /**
     * Returns the stack trace of an exception as String, optionally filtering
     * out line from the stack trac
     * 
     * @param theThrowable the exception from which to extract the stack trace
     *        as a String
     * @param theFilterPatterns Array containing a list of patterns to filter 
     *        out from the stack trace
     * @return the exception stack trace as a String
     */
    public static String exceptionToString(Throwable theThrowable, String[] theFilterPatterns) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
    
        theThrowable.printStackTrace(pw);
        String stackTrace = sw.toString();
        return filterStackTrace(stackTrace, theFilterPatterns);
    }

    /**
     * 
     * 
     * @param theLine The line to check
     * @param theFilterPatterns The patterns to filter out
     * @return boolean Whether the specified line should be filtered from the
     *         stack trace
     */
    public static boolean filterLine(String theLine, String[] theFilterPatterns) {
        for (int i = 0; i < theFilterPatterns.length; i++) {
            if (theLine.indexOf(theFilterPatterns[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * 
     * @param theStackTrace The original, unfiltered stack trace
     * @param theFilterPatterns The patterns to filter out
     * @return The filtered stack trace
     */
    static String filterStackTrace(String theStackTrace, String[] theFilterPatterns) {
        if ((theFilterPatterns == null) || (theFilterPatterns.length == 0) || (theStackTrace == null)) {
            return theStackTrace;
        }
    
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        StringReader stringReader = new StringReader(theStackTrace);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
    
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (!filterLine(line, theFilterPatterns)) {
                    printWriter.println(line);
                }
            }
        } catch (IOException e) {
            return theStackTrace;
        }
        return stringWriter.toString();
    }

    /**
     * Replaces a character in a string by a substring.
     *
     * @param theBaseString the base string in which to perform replacements
     * @param theChar the char to look for
     * @param theNewString the string with which to replace the char
     * @return the string with replacements done or null if the input string
     *          was null
     */
    public static String replace(String theBaseString, char theChar, String theNewString) {
        if (theBaseString == null) {
            return null;
        }
    
        int pos = theBaseString.indexOf(theChar);
        if (pos < 0) {
            return theBaseString;
        }
    
        int lastPos = 0;
        StringBuffer result = new StringBuffer();
        while (pos > -1) {
            result.append(theBaseString.substring(lastPos, pos));
            result.append(theNewString);
    
            lastPos = pos + 1;
            pos = theBaseString.indexOf(theChar, lastPos);
        }
    
        if (lastPos < theBaseString.length()) {
            result.append(theBaseString.substring(lastPos));
        }
    
        return result.toString();
    }

    /**
     * Escapes reserved XML characters.
     *
     * @param theString the string to escape
     * @return the escaped string
     */
    public static String escape(String theString) {
        String newString;

        // It is important to replace the "&" first as the other replacements
        // also introduces "&" chars ...
        newString = replace(theString, '&', "&amp;");

        newString = replace(newString, '<', "&lt;");
        newString = replace(newString, '>', "&gt;");
        newString = replace(newString, '\"', "&quot;");

        return newString;
    }

    /**
     * XML string containing executed test case results
     */
    private StringBuffer currentTestCaseResults = new StringBuffer();

    /**
     * Current test failure (XML string) : failure or error.
     */
    private String currentTestFailure;

    /**
     * Time current test was started
     */
    private long currentTestStartTime;

    /**
     * The number format used to convert durations into strings. Don't use the
     * default locale for that, because the resulting string needs to use 
     * dotted decimal notation for an XSLT transformation to work correctly.
     */
    private NumberFormat durationFormat = NumberFormat.getInstance(Locale.US);

    /**
     * The name of the test suite class.
     */
    private String suiteClassName;

    /**
     * Duration it took to execute all the tests.
     */
    private long totalDuration;

    /**
     * Event called by the base test runner when the test fails with an error.
     *
     * @param theTest the test object that failed
     * @param theThrowable the exception that was thrown
     */
    public void addError(Test theTest, Throwable theThrowable) {
        TestFailure failure = new TestFailure(theTest, theThrowable);
        StringBuffer xml = new StringBuffer();

        xml.append("<" + XMLFormatter.ERROR
            + " "
            + XMLFormatter.ATTR_MESSAGE
            + "=\""
            + escape(failure.thrownException().getMessage())
            + "\" "
            + XMLFormatter.ATTR_TYPE
            + "=\""
            + failure.thrownException().getClass().getName()
            + "\">");
        xml.append(escape(XMLFormatter.exceptionToString(failure.thrownException(), DEFAULT_STACK_FILTER_PATTERNS)));
        xml.append("</" + XMLFormatter.ERROR + ">");

        this.currentTestFailure = xml.toString();
    }

    /**
     * Event called by the base test runner when the test fails with a failure.
     *
     * @param theTest the test object that failed
     * @param theError the exception that was thrown
     */
    public void addFailure(Test theTest, AssertionFailedError theError) {
        TestFailure failure = new TestFailure(theTest, theError);
        StringBuffer xml = new StringBuffer();

        xml.append("<" + XMLFormatter.FAILURE
            + " "
            + XMLFormatter.ATTR_MESSAGE
            + "=\""
            + escape(failure.thrownException().getMessage())
            + "\" "
            + XMLFormatter.ATTR_TYPE
            + "=\""
            + failure.thrownException().getClass().getName()
            + "\">");
        xml.append(escape(XMLFormatter.exceptionToString(failure.thrownException(), DEFAULT_STACK_FILTER_PATTERNS)));
        xml.append("</" + XMLFormatter.FAILURE + ">");

        this.currentTestFailure = xml.toString();
    }

    /**
     * Event called by the base test runner when the test ends.
     *
     * @param theTest the test object being executed
     */
    public void endTest(Test theTest) {
        StringBuffer xml = new StringBuffer();
        String duration = getDurationAsString(System.currentTimeMillis() - this.currentTestStartTime);

        xml.append("<" + XMLFormatter.TESTCASE + " " + XMLFormatter.ATTR_NAME + "=\"" + theTest + "\" " + XMLFormatter.ATTR_TIME + "=\"" + duration + "\">");

        if (this.currentTestFailure != null) {
            xml.append(this.currentTestFailure);
        }

        xml.append("</" + XMLFormatter.TESTCASE + ">");

        this.currentTestCaseResults.append(xml.toString());
    }

    /**
     * Comvert a duration expressed as a long into a string.
     *
     * @param theDuration the duration to convert to string
     * @return the total duration as a string
     */
    private String getDurationAsString(long theDuration) {
        return durationFormat.format((double)theDuration / 1000);
    }

    /**
     * @return the suite class name
     */
    public String getSuiteClassName() {
        return this.suiteClassName;
    }

    /**
     * @return the total duration as a string
     */
    public String getTotalDurationAsString() {
        return getDurationAsString(this.totalDuration);
    }

    /**
     * Sets the suite class name that was executed.
     *
     * @param theSuiteClassName the suite class name
     */
    public void setSuiteClassName(String theSuiteClassName) {
        this.suiteClassName = theSuiteClassName;
    }

    /**
     * Sets the duration it took to execute all the tests.
     *
     * @param theDuration the time it took
     */
    public void setTotalDuration(long theDuration) {
        this.totalDuration = theDuration;
    }

    /**
     * Event called by the base test runner when the test starts.
     *
     * @param theTest the test object being executed
     */
    public void startTest(Test theTest) {
        this.currentTestStartTime = System.currentTimeMillis();
        this.currentTestFailure = null;
    }

    /**
     * Formats the test result as an XML string.
     *
     * @param theResult the test result object
     * @return the XML string representation of the test results
     */
    public String toXML(TestResult theResult) {
        StringBuffer xml = new StringBuffer();

        // xml.append("<?xml version=\"1.0\" encoding=\"" + getEncoding() + "\"?>");

        // xml.append("<" + TESTSUITES + ">");

        xml.append("<" + XMLFormatter.TESTSUITE
            + " "
            + XMLFormatter.ATTR_NAME
            + "=\""
            + getSuiteClassName()
            + "\" "
            + XMLFormatter.ATTR_TESTS
            + "=\""
            + theResult.runCount()
            + "\" "
            + XMLFormatter.ATTR_FAILURES
            + "=\""
            + theResult.failureCount()
            + "\" "
            + XMLFormatter.ATTR_ERRORS
            + "=\""
            + theResult.errorCount()
            + "\" "
            + XMLFormatter.ATTR_TIME
            + "=\""
            + getTotalDurationAsString()
            + "\">");

        xml.append(this.currentTestCaseResults.toString());

        xml.append("</" + XMLFormatter.TESTSUITE + ">");
        // xml.append("</" + TESTSUITES + ">");

        return xml.toString();
    }
}
