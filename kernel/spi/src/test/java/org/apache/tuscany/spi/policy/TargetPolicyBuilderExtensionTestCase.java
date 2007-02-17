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
package org.apache.tuscany.spi.policy;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.model.ServiceDefinition;
import static org.apache.tuscany.spi.policy.PolicyBuilderRegistry.EXTENSION;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class TargetPolicyBuilderExtensionTestCase extends TestCase {

    public void testRegister() throws Exception {
        PolicyBuilderRegistry registry = EasyMock.createMock(PolicyBuilderRegistry.class);
        registry.registerTargetBuilder(EasyMock.eq(EXTENSION), EasyMock.isA(MockPolicyBuilderExtension.class));
        EasyMock.replay(registry);
        TargetPolicyBuilderExtension extension = new MockPolicyBuilderExtension();
        extension.setRegistry(registry);
        extension.init();
        EasyMock.verify(registry);
    }

    private static class MockPolicyBuilderExtension extends TargetPolicyBuilderExtension {

        public void build(ServiceDefinition definition, Wire wire) throws BuilderException {

        }
    }
}
