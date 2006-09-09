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
package org.apache.tuscany.spi.extension;

import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class AtomicComponentExtensionTestCase extends TestCase {

    public void testIsEagerInit() throws Exception {
        TestExtension ext = new TestExtension();
        ext.isEagerInit();
    }

    public void testPrepare() throws Exception {
        TestExtension ext = new TestExtension();
        ext.prepare();
    }

    public void testInit() throws Exception {
        TestExtension ext = new TestExtension();
        ext.init(null);
    }

    public void testDestroy() throws Exception {
        TestExtension ext = new TestExtension();
        ext.destroy(null);
    }

    public void testInboundWire() throws Exception {
        TestExtension ext = new TestExtension();
        ext.getInboundWire(null);
    }

    private class TestExtension<T> extends AtomicComponentExtension<T> {
        public TestExtension() {
            super(null, null, null, null, null, null, 0);
        }

        public T getServiceInstance() throws TargetException {
            return null;
        }

        public Object createInstance() throws ObjectCreationException {
            return null;
        }

        public Object getServiceInstance(String name) throws TargetException {
            return null;
        }

        public List<Class<?>> getServiceInterfaces() {
            return null;
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
            return null;
        }

    }
}
