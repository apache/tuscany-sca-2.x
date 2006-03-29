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

package org.apache.tuscany.core.system.context;

import junit.framework.TestCase;

import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.AutowireSourceImpl;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;

/**
 * Tests autowiring for serveral scenarios according to the following runtime scheme:
 * 
 * <code>
 *       tuscany.runtime
 *        |
 *        + tuscany.system
 *        |   |
 *        |   + system1
 *        |   |   |
 *        |   |   + system1a
 *        |   |
 *        |   + system2
 *        |
 *        + tuscany.root
 *            |
 *            + app1
 *                |
 *                + app1a
 *                |
 *                + app1b
 * </code>
 * 
 * @version $Rev$ $Date$
 */
public class AutowireTestCase extends TestCase {

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    /**
     * Covers the case where a component in app1a requests autowire, which is resolved by the runtime to a service
     * exposed on an entry point in tuscany.system. The entry point is wired to an entry point on system1, which itself
     * is wired to a component in system1
     */
    public void testScenario1() throws Exception {
        RuntimeContext runtime = createScenario1Runtime();
        AggregateContext root = runtime.getRootContext();
        SystemAggregateContext system = runtime.getSystemContext();
        AggregateContext system1 = (AggregateContext) system.getContext("system1");
        system1.fireEvent(EventContext.MODULE_START, null);
        Target target = (Target) system.getContext("target.system.ep").getInstance(null);
        assertNotNull(target);
        AggregateContext app1 = (AggregateContext) root.getContext("app1");
        app1.fireEvent(EventContext.MODULE_START, null);
        AggregateContext app1a = (AggregateContext) app1.getContext("app1a");
        app1a.fireEvent(EventContext.MODULE_START, null);
        app1a.fireEvent(EventContext.MODULE_STOP, null);
        app1.fireEvent(EventContext.MODULE_STOP, null);
        Source source = (Source) app1a.getContext("source").getInstance(null);
        assertEquals(target, source.getTarget());
        source.getTarget().getString();
        runtime.stop();
    }

    /**
     * Covers the case where a component in app1a requests autowire, which is resolved to service exposed as an entry
     * point on app1b. The entry point is wired to a component in app1b.
     */
    public void testScenario2() throws Exception {
        RuntimeContext runtime = createScenario2Runtime();
        AggregateContext root = runtime.getRootContext();
        AggregateContext app1 = (AggregateContext) root.getContext("app1");
        app1.fireEvent(EventContext.MODULE_START, null);
        AggregateContext app1b = (AggregateContext) app1.getContext("app1b");
        app1b.fireEvent(EventContext.MODULE_START, null);
        AggregateContext app1a = (AggregateContext) app1.getContext("app1a");
        app1a.fireEvent(EventContext.MODULE_START, null);
        Target target = (Target) app1b.getContext("target.ep").getInstance(null);
        assertNotNull(target);
        Source source = (Source) app1a.getContext("source").getInstance(null);
        assertEquals(target, source.getTarget());
        source.getTarget().getString();
        runtime.stop();
    }

    /**
     * Covers the case where a component in system1a requests autowire, which is resolved to an entry point exposed on
     * system2. The entry point is wired to a component in system2.
     */
    public void testScenario3() throws Exception {
        RuntimeContext runtime = createScenario3Runtime();
        AggregateContext root = runtime.getRootContext();
        SystemAggregateContext system = runtime.getSystemContext();

        AggregateContext system2 = (AggregateContext) system.getContext("system2");
        system2.fireEvent(EventContext.MODULE_START, null);
        Target target = (Target) system2.getContext("target.ep").getInstance(null);
        assertNotNull(target);

        AggregateContext system1 = (AggregateContext) system.getContext("system1");
        system1.fireEvent(EventContext.MODULE_START, null);
        AggregateContext system1a = (AggregateContext) system1.getContext("system1a");
        system1a.fireEvent(EventContext.MODULE_START, null);

        Source source = (Source) system1a.getContext("source").getInstance(null);
        assertEquals(target, source.getTarget());
        source.getTarget().getString();
        runtime.stop();
    }

    /**
     * Covers the case where a component in system1a requests autowire, which is resolved to component in its parent,
     * system1.
     */
    public void testScenario4() throws Exception {
        RuntimeContext runtime = createScenario4Runtime();
        AggregateContext root = runtime.getRootContext();
        SystemAggregateContext system = runtime.getSystemContext();
        AggregateContext system1 = (AggregateContext) system.getContext("system1");
        system1.fireEvent(EventContext.MODULE_START, null);
        Target target = (Target) system1.getContext("target").getInstance(null);
        assertNotNull(target);
        AggregateContext system1a = (AggregateContext) system1.getContext("system1a");
        system1a.fireEvent(EventContext.MODULE_START, null);

        Source source = (Source) system1a.getContext("source").getInstance(null);
        assertEquals(target, source.getTarget());
        source.getTarget().getString();
        runtime.stop();
    }

    /**
     * Covers the case where a component in system1a requests autowire, which is resolved to component in the parent of
     * its parent (grandparent), system.
     */
    public void testScenario5() throws Exception {
        RuntimeContext runtime = createScenario5Runtime();
        AggregateContext root = runtime.getRootContext();
        SystemAggregateContext system = runtime.getSystemContext();
        AggregateContext system1 = (AggregateContext) system.getContext("system1");
        system1.fireEvent(EventContext.MODULE_START, null);
        Target target = (Target) system.getContext("target").getInstance(null);
        assertNotNull(target);
        AggregateContext system1a = (AggregateContext) system1.getContext("system1a");
        system1a.fireEvent(EventContext.MODULE_START, null);

        Source source = (Source) system1a.getContext("source").getInstance(null);
        assertEquals(target, source.getTarget());
        source.getTarget().getString();
        runtime.stop();
    }

    private RuntimeContext createScenario1Runtime() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        runtime.start();
        SystemAggregateContext system = runtime.getSystemContext();
        ModuleComponent system1Component = MockFactory.createSystemAggregateComponent("system1");
        ModuleComponent system1aComponent = MockFactory.createSystemAggregateComponent("system1a");
        system1Component.getModuleImplementation().getComponents().add(system1aComponent);
        Component target = MockFactory.createSystemComponent("target", Target.class, TargetImpl.class, Scope.MODULE);
        system1Component.getModuleImplementation().getComponents().add(target);

        EntryPoint ep = MockFactory.createEPSystemBinding("target.ep", Target.class, "target", target);
        system1Component.getModuleImplementation().getEntryPoints().add(ep);
        system.registerModelObject(system1Component);
        EntryPoint systemEp = MockFactory.createEPSystemBinding("target.system.ep", Target.class, "ref");

        systemEp.getBindings().add(systemFactory.createSystemBinding());
        Service service = systemFactory.createService();
        service.setName("system1/target.ep");
        ((ConfiguredService) systemEp.getConfiguredReference().getTargetConfiguredServices().get(0)).setService(service);

        system.registerModelObject(systemEp);
        ModuleComponent app1Component = createAppModuleComponent("app1");
        ModuleComponent app1aComponent = createAppModuleComponent("app1a");
        Component source = MockFactory.createSystemComponent("source", Source.class, AutowireSourceImpl.class, Scope.MODULE);
        app1aComponent.getModuleImplementation().getComponents().add(source);
        app1Component.getModuleImplementation().getComponents().add(app1aComponent);
        AggregateContext root = runtime.getRootContext();
        root.registerModelObject(app1Component);
        system.fireEvent(EventContext.MODULE_START, null);
        return runtime;
    }

    private RuntimeContext createScenario2Runtime() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        runtime.start();

        ModuleComponent app1Component = createAppModuleComponent("app1");
        ModuleComponent app1aComponent = createAppModuleComponent("app1a");
        ModuleComponent app1bComponent = createAppModuleComponent("app1b");
        Component source = MockFactory.createSystemComponent("source", Source.class, AutowireSourceImpl.class, Scope.MODULE);
        app1aComponent.getModuleImplementation().getComponents().add(source);
        app1Component.getModuleImplementation().getComponents().add(app1aComponent);
        app1Component.getModuleImplementation().getComponents().add(app1bComponent);

        Component target = MockFactory.createSystemComponent("target", Target.class, TargetImpl.class, Scope.MODULE);
        app1bComponent.getModuleImplementation().getComponents().add(target);

        EntryPoint ep = MockFactory.createEPSystemBinding("target.ep", Target.class, "target", target);
        ep.getBindings().add(systemFactory.createSystemBinding());
        Service service = systemFactory.createService();
        service.setName("target.ep");
        ((ConfiguredService) ep.getConfiguredReference().getTargetConfiguredServices().get(0)).setService(service);
        app1bComponent.getModuleImplementation().getEntryPoints().add(ep);

        AggregateContext root = runtime.getRootContext();
        root.registerModelObject(app1Component);
        return runtime;
    }

    private RuntimeContext createScenario3Runtime() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        runtime.start();
        SystemAggregateContext system = runtime.getSystemContext();
        ModuleComponent system1Component = MockFactory.createSystemAggregateComponent("system1");
        ModuleComponent system2Component = MockFactory.createSystemAggregateComponent("system2");
        ModuleComponent system1aComponent = MockFactory.createSystemAggregateComponent("system1a");
        system1Component.getModuleImplementation().getComponents().add(system1aComponent);

        Component target = MockFactory.createSystemComponent("target", Target.class, TargetImpl.class, Scope.MODULE);
        system2Component.getModuleImplementation().getComponents().add(target);
        EntryPoint ep = MockFactory.createEPSystemBinding("target.ep", Target.class, "target", target);
        system2Component.getModuleImplementation().getEntryPoints().add(ep);
        system.registerModelObject(system2Component);

        Component source = MockFactory.createSystemComponent("source", Source.class, AutowireSourceImpl.class, Scope.MODULE);
        system1aComponent.getModuleImplementation().getComponents().add(source);
        system.registerModelObject(system1Component);
        system.fireEvent(EventContext.MODULE_START, null);
        return runtime;
    }

    private RuntimeContext createScenario4Runtime() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        runtime.start();
        SystemAggregateContext system = runtime.getSystemContext();
        ModuleComponent system1Component = MockFactory.createSystemAggregateComponent("system1");
        ModuleComponent system1aComponent = MockFactory.createSystemAggregateComponent("system1a");
        system1Component.getModuleImplementation().getComponents().add(system1aComponent);

        Component target = MockFactory.createSystemComponent("target", Target.class, TargetImpl.class, Scope.MODULE);
        system1Component.getModuleImplementation().getComponents().add(target);

        Component source = MockFactory.createSystemComponent("source", Source.class, AutowireSourceImpl.class, Scope.MODULE);
        system1aComponent.getModuleImplementation().getComponents().add(source);
        system.registerModelObject(system1Component);
        system.fireEvent(EventContext.MODULE_START, null);
        return runtime;
    }

    private RuntimeContext createScenario5Runtime() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        runtime.start();
        SystemAggregateContext system = runtime.getSystemContext();
        ModuleComponent system1Component = MockFactory.createSystemAggregateComponent("system1");
        ModuleComponent system1aComponent = MockFactory.createSystemAggregateComponent("system1a");
        system1Component.getModuleImplementation().getComponents().add(system1aComponent);

        Component target = MockFactory.createSystemComponent("target", Target.class, TargetImpl.class, Scope.MODULE);
        system.registerModelObject(target);

        Component source = MockFactory.createSystemComponent("source", Source.class, AutowireSourceImpl.class, Scope.MODULE);
        system1aComponent.getModuleImplementation().getComponents().add(source);
        system.registerModelObject(system1Component);
        system.fireEvent(EventContext.MODULE_START, null);
        return runtime;
    }

    private ModuleComponent createAppModuleComponent(String name) {
        AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(systemFactory, null, null);
        ModuleComponent mc = systemFactory.createModuleComponent();
        mc.setName(name);
        Module module = systemFactory.createModule();
        module.setName(name);
        module.initialize(assemblyContext);
        mc.setComponentImplementation(module);
        return mc;
    }

}
