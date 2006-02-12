/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.builder;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.ContextConstants;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockSystemAssemblyFactory;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredProperty;
import org.apache.tuscany.model.assembly.pojo.PojoProperty;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;

/**
 * Tests to that system components are built properly
 * 
 * @version $Rev$ $Date$
 */
public class SystemComponentContextBuilderTestCase extends TestCase {

    public void testComponentContextBuilder() throws Exception {
        SystemComponentContextBuilder builder = new SystemComponentContextBuilder();
        Component component = MockSystemAssemblyFactory.createComponent("test", SystemComponentImpl.class.getName(),
                ContextConstants.AGGREGATE_SCOPE_ENUM);

        ConfiguredProperty cProp = new PojoConfiguredProperty();
        Property prop = new PojoProperty();
        prop.setName("testInt");
        cProp.setValue(1);
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        cProp = new PojoConfiguredProperty();
        prop = new PojoProperty();
        prop.setName("testString");
        cProp.setValue("test");
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        cProp = new PojoConfiguredProperty();
        prop = new PojoProperty();
        prop.setName("testDouble");
        cProp.setValue(1d);
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        cProp = new PojoConfiguredProperty();
        prop = new PojoProperty();
        prop.setName("testFloat");
        cProp.setValue(1f);
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        cProp = new PojoConfiguredProperty();
        prop = new PojoProperty();
        prop.setName("testShort");
        cProp.setValue((short) 1);
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        cProp = new PojoConfiguredProperty();
        prop = new PojoProperty();
        prop.setName("testByte");
        cProp.setValue((byte) 1);
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        cProp = new PojoConfiguredProperty();
        prop = new PojoProperty();
        prop.setName("testBoolean");
        cProp.setValue(Boolean.TRUE);
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        cProp = new PojoConfiguredProperty();
        prop = new PojoProperty();
        prop.setName("testChar");
        cProp.setValue('1');
        cProp.setProperty(prop);
        component.getConfiguredProperties().add(cProp);

        builder.build(component, createContext());
        RuntimeConfiguration config = (RuntimeConfiguration) component.getComponentImplementation().getRuntimeConfiguration();
        Assert.assertNotNull(config);
        InstanceContext ctx = (InstanceContext) config.createInstanceContext();

        ctx.start();
        SystemComponentImpl instance = (SystemComponentImpl) ctx.getInstance(null);
        Assert.assertNotNull(instance.getConfigContext());
        Assert.assertNotNull(instance.getParentContext());
        Assert.assertNotNull(instance.getAutowireContext());
        Assert.assertNotNull(instance.getConfigContextSetter());
        Assert.assertNotNull(instance.getParentContextSetter());
        Assert.assertNotNull(instance.getAutowireContextSetter());
        Assert.assertEquals(1, instance.getTestInt());
        Assert.assertEquals(1d, instance.getTestDouble());
        Assert.assertEquals(1f, instance.getTestFloat());
        Assert.assertEquals((short)1, instance.getTestShort());
        Assert.assertTrue(instance.getTestBoolean());
        Assert.assertEquals('1', instance.getTestChar());
        Assert.assertEquals((byte)1, instance.getTestByte());
        Assert.assertEquals("test", instance.getTestString());

        Assert.assertTrue(instance.initialized());
        ctx.stop();
        Assert.assertTrue(instance.destroyed());
    }

    private static AggregateContext createContext() {
        return new AggregateContextImpl("test.parent", null, new DefaultScopeStrategy(), new EventContextImpl(), new MockConfigContext(null), new NullMonitorFactory());
    }

}
