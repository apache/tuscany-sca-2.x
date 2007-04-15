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

import java.net.URI;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AtomicComponentExtensionTestCase extends TestCase {
    private AtomicComponentExtension ext;
    private URI uri;

    public void testURI() {
        assertSame(uri, ext.getUri());
    }

    public void testRemoveInstance() throws Exception {
        ScopeContainer scopeContainer = EasyMock.createMock(ScopeContainer.class);
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.COMPOSITE);
        scopeContainer.remove(ext);
        EasyMock.replay(scopeContainer);
        ext.setScopeContainer(scopeContainer);
        ext.removeInstance();
        EasyMock.verify(scopeContainer);
    }

    protected void setUp() throws Exception {
        super.setUp();
        uri = URI.create("http://example.com/foo");
        ext = new TestExtension(uri);
    }

    private static class TestExtension extends AtomicComponentExtension {

        public TestExtension(URI uri) {
            super(uri, null, null, URI.create("composite"), 0, -1, -1);
        }

        public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback)
            throws TargetInvokerCreationException {
            throw new AssertionFailedError();
        }

        public List<Wire> getWires(String name) {
            throw new AssertionFailedError();
        }

        public void attachWire(Wire wire) {
            throw new AssertionFailedError();
        }

        public void attachWires(List<Wire> wires) {
            throw new AssertionFailedError();
        }

        public void attachCallbackWire(Wire wire) {
            throw new AssertionFailedError();
        }
        
        public void configureProperty(String propertyName) {
            throw new AssertionFailedError();
        }

        public Object createInstance() throws ObjectCreationException {
            throw new AssertionFailedError();
        }

        public Object getTargetInstance() throws TargetResolutionException {
            throw new AssertionFailedError();
        }

        public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
            throw new AssertionFailedError();
        }
    }
}
