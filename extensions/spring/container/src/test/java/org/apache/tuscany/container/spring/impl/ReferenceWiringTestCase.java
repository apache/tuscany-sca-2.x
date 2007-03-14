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
package org.apache.tuscany.container.spring.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.osoa.sca.CallableReference;

import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.ChainHolder;
import org.apache.tuscany.spi.wire.IncompatibleServiceContractException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.extension.ReferenceBindingExtension;

import junit.framework.TestCase;
import org.apache.tuscany.container.spring.mock.TestBean;
import org.easymock.EasyMock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Verifies wiring from a Spring bean to an SCA composite reference
 *
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceWiringTestCase extends TestCase {

    public void testInvocation() throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource("META-INF/sca/testReferenceContext.xml");
        Resource resource = new UrlResource(url);
        URI uri = URI.create("spring");
        ProxyService proxyService = new MockProxyService();
        SpringCompositeComponent parent = new SpringCompositeComponent(uri, resource, proxyService, null, loader);
        Wire wire = EasyMock.createMock(Wire.class);
        ServiceContract<?> contract = new ServiceContract(TestBean.class) {
        };
        EasyMock.expect(wire.getSourceContract()).andReturn(contract).atLeastOnce();
        EasyMock.replay(wire);
        ReferenceBinding referenceBinding = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(referenceBinding.getWire()).andStubReturn(wire);
        referenceBinding.start();
        EasyMock.replay(referenceBinding);

        URI referenceUri = URI.create("spring#testReference");
        ReferenceBinding binding = new ReferenceBindingExtension(referenceUri, null){

            public QName getBindingType() {
                return null;
            }

            public TargetInvoker createTargetInvoker(String targetName, Operation operation)
            throws TargetInvokerCreationException {
                throw new UnsupportedOperationException();
            }

            public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation)
                throws TargetInvokerCreationException {
                return null;
            }
        };
        binding.setWire(wire);
        List<ReferenceBinding> bindings = new ArrayList<ReferenceBinding>();
        bindings.add(binding);
        Reference reference = EasyMock.createMock(Reference.class);
        EasyMock.expect(reference.getUri()).andReturn(referenceUri).anyTimes();
        EasyMock.expect(reference.getReferenceBindings()).andReturn(bindings).atLeastOnce();
        reference.start();
        EasyMock.replay(reference);
        parent.register(reference);
        parent.start();
        TestBean bean = parent.getBean(TestBean.class, "testBean");
        assertNotNull(bean.getBean());
        EasyMock.verify(reference);
    }

    private class MockInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    private class MockProxyService implements ProxyService {

        public <T> T createProxy(Class<T> interfaze, Wire wire) throws ProxyCreationException {
            ClassLoader cl = interfaze.getClassLoader();
            Class[] interfaces = new Class[]{interfaze};
            MockInvocationHandler handler = new MockInvocationHandler();
            return interfaze.cast(Proxy.newProxyInstance(cl, interfaces, handler));
        }

        public <T> T createProxy(Class<T> interfaze, Wire wire, Map<Method, ChainHolder> mapping)
            throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public Object createCallbackProxy(Class<?> interfaze, List<Wire> wires) throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public Object createCallbackProxy(Class<?> interfaze) throws ProxyCreationException {
            throw new UnsupportedOperationException();
        }

        public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public boolean checkCompatibility(ServiceContract<?> source,
                                          ServiceContract<?> target,
                                          boolean ignoreCallback,
                                          boolean silent) throws IncompatibleServiceContractException {
            throw new UnsupportedOperationException();
        }
    }

}
