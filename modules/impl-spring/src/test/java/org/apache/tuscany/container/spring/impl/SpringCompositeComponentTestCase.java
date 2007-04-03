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

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @version $Rev$ $Date$
 */
public class SpringCompositeComponentTestCase extends TestCase {
    private SpringCompositeComponent component;

    public void testChildStart() throws Exception {
        AbstractApplicationContext appContext = EasyMock.createNiceMock(AbstractApplicationContext.class);
        EasyMock.replay(appContext);
        Service service = EasyMock.createMock(Service.class);
        URI uri = URI.create("composite#service");
        EasyMock.expect(service.getUri()).andReturn(uri).atLeastOnce();
        service.start();
        EasyMock.replay(service);
        component.setSpringContext(appContext);
        component.register(service);
        component.start();
        EasyMock.verify(service);
    }

    public void testTargetInvokerCreation() throws Exception {
        ServiceContract<Type> contract = new ServiceContract<Type>(Foo.class) {
        };
        Operation<Type> operation = new Operation<Type>("operation", null, null, null);
        Map<String, Operation<Type>> operations = new HashMap<String, Operation<Type>>();
        operations.put("operation", operation);
        contract.setOperations(operations);
        operation.setServiceContract(contract);
        component.createTargetInvoker("bean", operation);
    }


    protected void setUp() throws Exception {
        super.setUp();
        URI compositeUri = URI.create("composite");
        component = new SpringCompositeComponent(compositeUri, null, null, null, null);

    }

    private interface Foo {
        void operation();
    }
}
