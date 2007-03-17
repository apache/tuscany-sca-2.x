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
package org.apache.tuscany.core.integration.wire;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.integration.mock.MockFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ReferenceInjectionTestCase extends TestCase {
    private Map<String, Member> members;

    public void testProxiedReferenceInjection() throws Exception {
        ScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        URI groupId = URI.create("composite");
        scope.createGroup(groupId);
        scope.startContext(groupId, groupId);
        Map<String, AtomicComponent> components = MockFactory.createWiredComponents("source",
            SourceImpl.class,
            scope,
            members,
            "target",
            Target.class,
            TargetImpl.class,
            scope);
        AtomicComponent sourceComponent = components.get("source");
        Source source = (Source) sourceComponent.getTargetInstance();
        Target target = source.getTarget();
        assertTrue(Proxy.isProxyClass(target.getClass()));

        assertNotNull(target);
        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        members = new HashMap<String, Member>();
        Method m = SourceImpl.class.getMethod("setTarget", Target.class);
        members.put("target", m);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


}
