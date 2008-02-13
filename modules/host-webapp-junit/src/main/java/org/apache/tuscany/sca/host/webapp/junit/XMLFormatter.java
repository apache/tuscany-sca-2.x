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

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Format the test results in XML.
 *
 * @version $Id: XMLFormatter.java 239169 2005-05-05 09:21:54Z vmassol $
 */
public class XMLFormatter {
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
     * The number format used to convert durations into strings. Don't use the
     * default locale for that, because the resulting string needs to use 
     * dotted decimal notation for an XSLT transformation to work correctly.
     */
    private static NumberFormat durationFormat = NumberFormat.getInstance(Locale.US);

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
     * Escapes reserved XML characters.
     *
     * @param theString the string to escape
     * @return the escaped string
     */
    public static String escape(String theString) {
        String newString;

        // It is important to replace the "&" first as the other replacements
        // also introduces "&" chars ...
        newString = theString.replace("&", "&amp;");

        newString = newString.replace("<", "&lt;");
        newString = newString.replace(">", "&gt;");
        newString = newString.replace("\"", "&quot;");

        return newString;
    }

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
     * Comvert a duration expressed as a long into a string.
     *
     * @param theDuration the duration to convert to string
     * @return the total duration as a string
     */
    public static String getDurationAsString(long theDuration) {
        return durationFormat.format((double)theDuration / 1000);
    }

    public static String toXML(Failure failure) {
        StringBuffer xml = new StringBuffer();
        Throwable ex = failure.getException();
        String tag = (ex instanceof AssertionFailedError) ? FAILURE : ERROR;
        xml.append("<" + tag
            + " "
            + ATTR_MESSAGE
            + "=\""
            + escape(ex.getMessage())
            + "\" "
            + ATTR_TYPE
            + "=\""
            + ex.getClass().getName()
            + "\">");
        xml.append(escape(exceptionToString(ex, DEFAULT_STACK_FILTER_PATTERNS)));
        xml.append("</" + tag + ">");

        return xml.toString();
    }

    /**
     * Formats the test result as an XML string.
     *
     * @param result the test result object
     * @return the XML string representation of the test results
     */
    public static String toXML(Result result, Class<?> cls) {
        int failures = 0, errors = 0;
        for (Failure f : result.getFailures()) {
            if (f.getException() instanceof AssertionFailedError) {
                failures++;
            } else {
                errors++;
            }
        }
        StringBuffer xml = new StringBuffer();

        xml.append("<" + TESTCASE
            + " "
            + ATTR_NAME
            + "=\""
            + cls.getName()
            + "\" "
            + ATTR_TESTS
            + "=\""
            + result.getRunCount()
            + "\" "
            + ATTR_FAILURES
            + "=\""
            + failures
            + "\" "
            + ATTR_ERRORS
            + "=\""
            + errors
            + "\" "
            + ATTR_TIME
            + "=\""
            + getDurationAsString(result.getRunTime())
            + "\">");

        for (Failure f : result.getFailures()) {
            xml.append(toXML(f));
        }

        xml.append("</" + TESTCASE + ">");

        return xml.toString();
    }
}
