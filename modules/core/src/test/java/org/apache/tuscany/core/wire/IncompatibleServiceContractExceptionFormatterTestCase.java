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

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.host.monitor.FormatterRegistry;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.idl.impl.OperationImpl;
import org.apache.tuscany.idl.java.JavaInterface;
import org.apache.tuscany.idl.java.impl.DefaultJavaFactory;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class IncompatibleServiceContractExceptionFormatterTestCase extends TestCase {
    FormatterRegistry registry = EasyMock.createNiceMock(FormatterRegistry.class);
    IncompatibleContractExceptionFormatter formatter = new IncompatibleContractExceptionFormatter(registry);

    private <S> ComponentService createContract(Class<S> type) {
        AssemblyFactory factory = new DefaultAssemblyFactory();
        ComponentService contract = factory.createComponentService();
        JavaInterface javaInterface = new DefaultJavaFactory().createJavaInterface();
        javaInterface.setJavaClass(type);
        contract.setInterface(javaInterface);
        return contract;
    }

    public void testFormat() throws Exception {
        Contract source = createContract(Foo.class);
        Contract target = createContract(Bar.class);
        Operation sourceOp = new OperationImpl("sourceOp");
        Operation targetOp = new OperationImpl("targetOp");

        IncompatibleServiceContractException e = new IncompatibleServiceContractException("message", source, target,
                                                                                          sourceOp, targetOp);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        formatter.write(pw, e);
        String buffer = writer.toString();
        assertTrue(buffer.indexOf("message") >= 0);
        assertTrue(buffer.indexOf("Foo") >= 0);
        assertTrue(buffer.indexOf("Bar") >= 0);
        assertTrue(buffer.indexOf("sourceOp") >= 0);
        assertTrue(buffer.indexOf("targetOp") >= 0);
    }

    public void testFormatNulls() throws Exception {
        Contract source = createContract(Foo.class);
        Contract target = createContract(Bar.class);

        IncompatibleServiceContractException e = new IncompatibleServiceContractException("message", source, target);
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        formatter.write(pw, e);
        String buffer = writer.toString();
        assertTrue(buffer.indexOf("message") >= 0);
        assertTrue(buffer.indexOf("Foo") >= 0);
        assertTrue(buffer.indexOf("Bar") >= 0);
    }

    private static interface Foo {

    }

    private static interface Bar {

    }
}
