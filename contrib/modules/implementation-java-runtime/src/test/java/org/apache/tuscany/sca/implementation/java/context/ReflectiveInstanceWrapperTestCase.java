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
package org.apache.tuscany.sca.implementation.java.context;

import static org.easymock.EasyMock.createMock;
import junit.framework.TestCase;

import org.apache.tuscany.sca.core.scope.TargetDestructionException;
import org.apache.tuscany.sca.core.scope.TargetInitializationException;
import org.apache.tuscany.sca.implementation.java.invocation.EventInvoker;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ReflectiveInstanceWrapperTestCase extends TestCase {
    private ReflectiveInstanceWrapper<Object> wrapper;
    private Object instance;
    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;

    public void testWithNoCallbacks() {
        wrapper = new ReflectiveInstanceWrapper<Object>(instance, null, null);
        try {
            wrapper.start();
        } catch (TargetInitializationException e) {
            fail();
        }
        try {
            wrapper.stop();
        } catch (TargetDestructionException e) {
            fail();
        }
    }

    public void testWithStartCallback() {
        initInvoker.invokeEvent(instance);
        EasyMock.replay(initInvoker);
            wrapper = new ReflectiveInstanceWrapper<Object>(instance, initInvoker, null);
        try {
            wrapper.start();
        } catch (TargetInitializationException e) {
            fail();
        }
        EasyMock.verify(initInvoker);
    }

    public void testWithStopCallback() {
        destroyInvoker.invokeEvent(instance);
        EasyMock.replay(destroyInvoker);
            wrapper = new ReflectiveInstanceWrapper<Object>(instance, null, destroyInvoker);
        try {
            wrapper.stop();
        } catch (TargetDestructionException e) {
            fail();
        }
        EasyMock.verify(destroyInvoker);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        instance = new Object();
        initInvoker = createMock(EventInvoker.class);
        destroyInvoker = createMock(EventInvoker.class);
    }
}
