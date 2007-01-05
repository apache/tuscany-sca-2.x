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
package org.apache.tuscany.core.wire;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;

import junit.framework.TestCase;
import org.apache.tuscany.host.monitor.FormatterRegistry;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class IncompatibleServiceContractExceptionFormatterTestCase extends TestCase {
    FormatterRegistry registry = EasyMock.createNiceMock(FormatterRegistry.class);
    IncompatibleServiceContractExceptionFormatter formatter =
        new IncompatibleServiceContractExceptionFormatter(registry);

    public void testFormat() throws Exception {
        ServiceContract<Object> source = new ServiceContract<Object>() {
        };
        source.setInterfaceName("sourceInterface");
        ServiceContract<Object> target = new ServiceContract<Object>() {
        };
        target.setInterfaceName("targetInterface");
        Operation<Object> sourceOp = new Operation<Object>("sourceOp", null, null, null);
        Operation<Object> targetOp = new Operation<Object>("targetOp", null, null, null);

        IncompatibleServiceContractException e =
            new IncompatibleServiceContractException("message", source, target, sourceOp, targetOp);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        formatter.write(pw, e);
        String buffer = writer.toString();
        assertTrue(buffer.indexOf("message") >= 0);
        assertTrue(buffer.indexOf("sourceInterface") >= 0);
        assertTrue(buffer.indexOf("targetInterface") >= 0);
        assertTrue(buffer.indexOf("sourceOp") >= 0);
        assertTrue(buffer.indexOf("targetOp") >= 0);
    }


    public void testFormatNulls() throws Exception {
        ServiceContract<Object> source = new ServiceContract<Object>() {
        };
        source.setInterfaceName("sourceInterface");
        ServiceContract<Object> target = new ServiceContract<Object>() {
        };
        target.setInterfaceName("targetInterface");

        IncompatibleServiceContractException e =
            new IncompatibleServiceContractException("message", source, target);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        formatter.write(pw, e);
        String buffer = writer.toString();
        assertTrue(buffer.indexOf("message") >= 0);
        assertTrue(buffer.indexOf("sourceInterface") >= 0);
        assertTrue(buffer.indexOf("targetInterface") >= 0);
    }
}
