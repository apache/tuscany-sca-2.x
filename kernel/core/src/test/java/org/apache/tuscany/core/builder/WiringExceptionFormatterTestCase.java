/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.core.builder;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.tuscany.spi.builder.WiringException;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class WiringExceptionFormatterTestCase extends TestCase {
    WiringExceptionFormatter formatter = new WiringExceptionFormatter();

    public void testFormat() throws Exception {
        WiringException e = new MockWiringException("message",
            "identifier",
            "source name",
            "ref name",
            "target name",
            "service name");
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        formatter.write(pw, e);
        String buffer = writer.toString();
        assertTrue(buffer.indexOf("message") >= 0);
        assertTrue(buffer.indexOf("identifier") >= 0);
        assertTrue(buffer.indexOf("source name") >= 0);
        assertTrue(buffer.indexOf("ref name") >= 0);
        assertTrue(buffer.indexOf("target name") >= 0);
        assertTrue(buffer.indexOf("service name") >= 0);
    }


    public void testFormatNulls() throws Exception {
        WiringException e = new MockWiringException("message",
            "identifier",
            "source name",
            null,
            "target name",
            null);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        formatter.write(pw, e);
        String buffer = writer.toString();
        assertTrue(buffer.indexOf("message") >= 0);
        assertTrue(buffer.indexOf("identifier") >= 0);
        assertTrue(buffer.indexOf("source name") >= 0);
        assertTrue(buffer.indexOf("target name") >= 0);
    }

    private class MockWiringException extends WiringException {

        public MockWiringException(String message, String
            identifier,
                                   String sourceName,
                                   String referenceName,
                                   String targetName,
                                   String serviceName) {
            super(message, identifier);
            setSourceName(sourceName);
            setReferenceName(referenceName);
            setTargetName(targetName);
            setTargetServiceName(serviceName);
        }
    }
}
