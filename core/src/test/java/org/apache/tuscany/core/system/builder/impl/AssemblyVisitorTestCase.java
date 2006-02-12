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
package org.apache.tuscany.core.system.builder.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitor;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.system.assembly.pojo.PojoSystemBinding;
import org.apache.tuscany.core.system.assembly.pojo.PojoSystemImplementation;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ConfiguredRuntimeObject;
import org.apache.tuscany.model.assembly.pojo.PojoConfiguredReference;
import org.apache.tuscany.model.assembly.pojo.PojoEntryPoint;
import org.apache.tuscany.model.assembly.pojo.PojoModule;
import org.apache.tuscany.model.assembly.pojo.PojoReference;
import org.apache.tuscany.model.assembly.pojo.PojoSimpleComponent;

/**
 * Tests decorating a logical configuration model
 * 
 * @version $Rev$ $Date$
 */
public class AssemblyVisitorTestCase extends TestCase {

    private static final Object MARKER = new Object();

    public void testModelVisit() throws Exception {
        PojoSimpleComponent component = new PojoSimpleComponent();
        PojoSystemImplementation impl = new PojoSystemImplementation();
        component.setComponentImplementation(impl);
        PojoConfiguredReference cRef = new PojoConfiguredReference();
        PojoReference ref = new PojoReference();
        cRef.setReference(ref);
        component.getConfiguredReferences().add(cRef);

        PojoEntryPoint ep = new PojoEntryPoint();
        PojoSystemBinding binding = new PojoSystemBinding();
        ep.addBinding(binding);
        PojoConfiguredReference cEpRef = new PojoConfiguredReference();
        PojoReference epRef = new PojoReference();
        cEpRef.setReference(epRef);
        ep.setConfiguredReference(cEpRef);

        PojoModule module = new PojoModule();
        module.getComponents().add(component);
        module.getEntryPoints().add(ep);

        List<RuntimeConfigurationBuilder> builders = new ArrayList();
        builders.add(new TestBuilder());
        AssemblyVisitor visitor = new AssemblyVisitor(null, builders);
        visitor.start(module);

        Assert.assertSame(MARKER, impl.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cRef.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cRef.getProxyFactory());
        Assert.assertSame(MARKER, binding.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cEpRef.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cEpRef.getProxyFactory());
        Assert.assertSame(MARKER, module.getRuntimeConfiguration());

    }

    private static class TestBuilder implements RuntimeConfigurationBuilder {
        public void build(AssemblyModelObject model, Context context) throws BuilderException {
            if (model instanceof ConfiguredPort) {
                ((ConfiguredPort) model).setProxyFactory(MARKER);
            }
            if (model instanceof ConfiguredRuntimeObject) {
                ((ConfiguredRuntimeObject) model).setRuntimeConfiguration(MARKER);
            }
        }

    }

}
