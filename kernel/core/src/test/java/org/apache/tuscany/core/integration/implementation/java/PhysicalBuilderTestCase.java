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
package org.apache.tuscany.core.integration.implementation.java;

import java.lang.annotation.ElementType;
import java.net.URI;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.builder.physical.DefaultPhysicalComponentBuilderRegistry;
import org.apache.tuscany.core.builder.physical.WireAttacherRegistryImpl;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.component.SimpleWorkContext;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderRegistry;
import org.apache.tuscany.core.component.instancefactory.impl.DefaultIFProviderBuilderRegistry;
import org.apache.tuscany.core.component.instancefactory.impl.ReflectiveIFProviderBuilder;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.deployer.federation.FederatedDeployer;
import org.apache.tuscany.core.implementation.PojoWorkContextTunnel;
import org.apache.tuscany.core.implementation.java.JavaPhysicalComponentBuilder;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteMapping;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import org.apache.tuscany.core.model.physical.instancefactory.MemberSite;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireSourceDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireTargetDefinition;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.builder.physical.WireAttacherRegistry;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;

/**
 * @version $Rev$ $Date$
 */
public class PhysicalBuilderTestCase extends TestCase {
    private URI groupId;
    private URI sourceId;
    private URI targetId;
    private ClassLoaderRegistry classLoaderRegistry;
    private JavaPhysicalComponentBuilder builder;
    private ScopeContainer<URI> scopeContainer;
    private ScopeRegistry scopeRegistry;
    private InstanceFactoryProviderDefinition<TargetImpl> targetProviderDefinition;
    private Connector connector;
    private ComponentManager componentManager;
    private FederatedDeployer deployer;
    private PhysicalChangeSet pcs;

    public void testWireTwoComponents() throws Exception {
        pcs.addComponentDefinition(createSourceComponentDefinition());
        pcs.addComponentDefinition(createTargetComponentDefinition());
        pcs.addWireDefinition(createOptimizedWire());
        deployer.applyChangeSet(pcs);

        WorkContext workContext = new SimpleWorkContext();
        workContext.setIdentifier(Scope.COMPOSITE, groupId);
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            AtomicComponent<?> sourceComponent = (AtomicComponent<?>) componentManager.getComponent(sourceId);
            InstanceWrapper<?> wrapper = scopeContainer.getWrapper(sourceComponent, groupId);
            SourceImpl s = (SourceImpl) wrapper.getInstance();
            assertSame(s.target.getClass(), TargetImpl.class);
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(null);
        }
    }

    private JavaPhysicalComponentDefinition<SourceImpl> createSourceComponentDefinition() {
        ReflectiveIFProviderDefinition sourceProviderDefinition = new ReflectiveIFProviderDefinition();
        sourceProviderDefinition.setImplementationClass(SourceImpl.class.getName());
        InjectionSiteMapping mapping = new InjectionSiteMapping();
        mapping.setSource(new InjectionSource(InjectionSource.ValueSourceType.REFERENCE, "target"));
        mapping.setSite(new MemberSite(ElementType.FIELD, "target"));
        sourceProviderDefinition.addInjectionSite(mapping);

        JavaPhysicalComponentDefinition<SourceImpl> source = new JavaPhysicalComponentDefinition<SourceImpl>();
        source.setComponentId(sourceId);
        source.setGroupId(groupId);
        source.setClassLoaderId(groupId);
        source.setScope(Scope.COMPOSITE);
        source.setInstanceFactoryProviderDefinition(sourceProviderDefinition);
        return source;
    }

    private JavaPhysicalComponentDefinition<TargetImpl> createTargetComponentDefinition() {
        ReflectiveIFProviderDefinition targetProviderDefinition = new ReflectiveIFProviderDefinition();
        targetProviderDefinition.setImplementationClass(TargetImpl.class.getName());

        JavaPhysicalComponentDefinition<TargetImpl> target = new JavaPhysicalComponentDefinition<TargetImpl>();
        target.setComponentId(targetId);
        target.setGroupId(groupId);
        target.setClassLoaderId(groupId);
        target.setScope(Scope.COMPOSITE);
        target.setInstanceFactoryProviderDefinition(targetProviderDefinition);
        return target;
    }

    private PhysicalWireDefinition createOptimizedWire() {
        JavaPhysicalWireSourceDefinition wireSource = new JavaPhysicalWireSourceDefinition();
        wireSource.setUri(sourceId.resolve("#target"));
        wireSource.setOptimizable(true);
        JavaPhysicalWireTargetDefinition wireTarget = new JavaPhysicalWireTargetDefinition();
        wireTarget.setUri(targetId);
        PhysicalWireDefinition wireDefinition = new PhysicalWireDefinition();
        wireDefinition.setSource(wireSource);
        wireDefinition.setTarget(wireTarget);
        return wireDefinition;
    }

    protected void setUp() throws Exception {
        super.setUp();
        groupId = URI.create("sca://./composite");
        sourceId = groupId.resolve("composite/source");
        targetId = groupId.resolve("composite/target");
        classLoaderRegistry = EasyMock.createMock(ClassLoaderRegistry.class);
        ClassLoader classLoader = getClass().getClassLoader();
        EasyMock.expect(classLoaderRegistry.getClassLoader(groupId)).andStubReturn(classLoader);
        EasyMock.replay(classLoaderRegistry);

        scopeContainer = new CompositeScopeContainer<URI>(null);
        scopeContainer.start();
        scopeContainer.startContext(groupId, groupId);

        scopeRegistry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(scopeRegistry.getScopeContainer(Scope.COMPOSITE)).andStubReturn(scopeContainer);
        EasyMock.replay(scopeRegistry);

        IFProviderBuilderRegistry providerBuilders = new DefaultIFProviderBuilderRegistry();
        providerBuilders.register(ReflectiveIFProviderDefinition.class, new ReflectiveIFProviderBuilder());

        DefaultPhysicalComponentBuilderRegistry builderRegistry = new DefaultPhysicalComponentBuilderRegistry();
        WireAttacherRegistry wireAttacherRegistry = new WireAttacherRegistryImpl();
        builder = new JavaPhysicalComponentBuilder(null, scopeRegistry, providerBuilders, classLoaderRegistry);
        builderRegistry.register(JavaPhysicalComponentDefinition.class, builder);
        wireAttacherRegistry.register(JavaPhysicalWireSourceDefinition.class, builder);
        wireAttacherRegistry.register(JavaPhysicalWireTargetDefinition.class, builder);

        componentManager = new ComponentManagerImpl();
        connector = new ConnectorImpl(null, wireAttacherRegistry, null, componentManager, null, null);

        deployer = new FederatedDeployer();
        deployer.setBuilderRegistry(builderRegistry);
        deployer.setComponentManager(componentManager);
        deployer.setConnector(connector);

        pcs = new PhysicalChangeSet();
    }

    public static class SourceImpl {
        public TargetImpl target;
    }

    public static class TargetImpl {
    }
}
