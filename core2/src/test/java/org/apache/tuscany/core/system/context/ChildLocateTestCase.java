package org.apache.tuscany.core.system.context;

import junit.framework.TestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ChildLocateTestCase extends TestCase {

     public void testChildLocate() throws Exception {
//         WorkContext workContext = new WorkContextImpl();
//         ModuleScopeContext scopeContext = new ModuleScopeContext(workContext);
//         scopeContext.start();
//         SystemCompositeContext parent = new SystemCompositeContextImpl("parent", null, null);
//         SystemCompositeContext child1 = new SystemCompositeContextImpl("child1", null, null);
//         child1.setParent(parent);
//         parent.registerContext(child1);
//         parent.start();
//         SystemAtomicContext context = MockContextFactory.createSystemAtomicContext("source", SourceImpl.class);
//         scopeContext.register(context);
//         context.setScopeContext(scopeContext);
//         scopeContext.publish(new ModuleStart(this, parent));
//
//         assertNotNull(source);
//         CompositeContext composite1 = (CompositeContext) parent.getContext("child1");
//         CompositeContext composite2 = (CompositeContext) composite1.getContext("child2");
//         AtomicContext ctx2 = (AtomicContext) composite2.getContext("source");
//         Source source2 = (Source) ctx2.getInstance();
//         assertSame(source, source2);
//         scopeContext.onEvent(new ModuleStop(this, parent));
//         parent.stop();
//         scopeContext.stop();
/////

//        system.start();
//        Component compositeComponent = MockContextFactory.createCompositeComponent("system.child");
//        system.registerModelObject(compositeComponent);
//        CompositeContext childContext = (CompositeContext) system.getContext("system.child");
//        Assert.assertNotNull(childContext);
//
//        Component component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
//        EntryPoint ep = MockContextFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1", component);
//        childContext.registerModelObject(component);
//        childContext.registerModelObject(ep);
//        childContext.publish(new ModuleStart(this));
//        Assert.assertNotNull(system.getContext("system.child").getInstance(new QualifiedName("./TestService1EP")));
//        childContext.publish(new ModuleStop(this));
    }
}
