package org.apache.tuscany.core.system.component;

import junit.framework.TestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ChildLocateTestCase extends TestCase {

    public void testChildLocate() throws Exception {
//         WorkContext workContext = new WorkContextImpl();
//         ModuleScopeContainer scopeContainer = new ModuleScopeContainer(workContext);
//         scopeContainer.start();
//         SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", null, null);
//         SystemCompositeComponent child1 = new SystemCompositeComponentImpl("child1", null, null);
//         child1.setParent(parent);
//         parent.registerContext(child1);
//         parent.start();
//         SystemAtomicComponent context = MockContextFactory.createSystemAtomicContext("source", SourceImpl.class);
//         scopeContainer.register(context);
//         context.setScopeContext(scopeContainer);
//         scopeContainer.publish(new CompositeStart(this, parent));
//
//         assertNotNull(source);
//         CompositeComponent composite1 = (CompositeComponent) parent.getContext("child1");
//         CompositeComponent composite2 = (CompositeComponent) composite1.getContext("child2");
//         AtomicComponent ctx2 = (AtomicComponent) composite2.getContext("source");
//         Source source2 = (Source) ctx2.getInstance();
//         assertSame(source, source2);
//         scopeContainer.onEvent(new CompositeStop(this, parent));
//         parent.stop();
//         scopeContainer.stop();
/////

//        system.start();
//        ComponentDefinition compositeComponent = MockContextFactory.createCompositeComponent("system.child");
//        system.registerModelObject(compositeComponent);
//        CompositeComponent childContext = (CompositeComponent) system.getContext("system.child");
//        Assert.assertNotNull(childContext);
//
//        ComponentDefinition component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
//        EntryPoint ep = MockContextFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1", component);
//        childContext.registerModelObject(component);
//        childContext.registerModelObject(ep);
//        childContext.publish(new CompositeStart(this));
//        Assert.assertNotNull(system.getContext("system.child").getInstance(new QualifiedName("./TestService1EP")));
//        childContext.publish(new CompositeStop(this));
    }
}
