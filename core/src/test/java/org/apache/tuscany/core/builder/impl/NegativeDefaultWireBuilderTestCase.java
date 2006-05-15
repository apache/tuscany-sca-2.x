/**
 *
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.builder.impl;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.wire.MethodHashMap;
import org.apache.tuscany.core.wire.WireSourceConfiguration;
import org.apache.tuscany.core.wire.WireTargetConfiguration;
import org.apache.tuscany.core.wire.SourceInvocationConfiguration;
import org.apache.tuscany.core.wire.TargetInvocationConfiguration;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.WireFactoryFactory;
import org.apache.tuscany.core.wire.jdk.JDKWireFactoryFactory;
import org.apache.tuscany.core.wire.mock.SimpleTarget;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

import java.lang.reflect.Method;
import java.util.Map;

public class NegativeDefaultWireBuilderTestCase extends TestCase {

    private Method hello;

    private WireFactoryFactory wireFactoryFactory = new JDKWireFactoryFactory();

    public NegativeDefaultWireBuilderTestCase() {
        super();
    }

    public NegativeDefaultWireBuilderTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testNoTargetInterceptorOrHandler() throws Exception {
        MessageFactory msgFactory = new MessageFactoryImpl();

        SourceInvocationConfiguration source = new SourceInvocationConfiguration(hello);

        SourceWireFactory sourceFactory = new JDKWireFactoryFactory().createSourceWireFactory();
        Map<Method, SourceInvocationConfiguration> sourceInvocationConfigs = new MethodHashMap<SourceInvocationConfiguration>();
        sourceInvocationConfigs.put(hello, source);
        WireSourceConfiguration sourceConfig = new WireSourceConfiguration("foo",new QualifiedName("target/SimpleTarget"),
                sourceInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        sourceFactory.setConfiguration(sourceConfig);
        sourceFactory.setBusinessInterface(SimpleTarget.class);

        TargetInvocationConfiguration target = new TargetInvocationConfiguration(hello);

        TargetWireFactory targetFactory = wireFactoryFactory.createTargetWireFactory();
        Map<Method, TargetInvocationConfiguration> targetInvocationConfigs = new MethodHashMap<TargetInvocationConfiguration>();
        targetInvocationConfigs.put(hello, target);
        WireTargetConfiguration targetConfig = new WireTargetConfiguration(new QualifiedName("target/SimpleTarget"),
                targetInvocationConfigs, Thread.currentThread().getContextClassLoader(), msgFactory);
        targetFactory.setConfiguration(targetConfig);
        targetFactory.setBusinessInterface(SimpleTarget.class);

        // connect the source to the target
        DefaultWireBuilder builder = new DefaultWireBuilder();
        try {
            builder.connect(sourceFactory, targetFactory, null, true, null);
            fail("Expected " + BuilderConfigException.class.getName());
        } catch (BuilderConfigException e) {
            // success
        }
    }

}
