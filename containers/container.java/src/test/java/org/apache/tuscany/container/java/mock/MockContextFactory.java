package org.apache.tuscany.container.java.mock;

import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.container.java.config.JavaContextFactory;
import org.apache.tuscany.container.java.scopes.OrderedDependentPojo;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockContextFactory {

    private MockContextFactory(){}

    /**
     * Wires together a source and target
     * @throws NoSuchMethodException
     */
    public static List<ContextFactory<Context>> createWiredContexts(Scope scope, ScopeContext context) throws NoSuchMethodException{

        Constructor constructor = JavaIntrospectionHelper.getDefaultConstructor(OrderedDependentPojo.class);
        Method getPojo = OrderedDependentPojo.class.getMethod("setPojo",OrderedDependentPojo.class);
        Method init = OrderedDependentPojo.class.getMethod("init",(Class[])null);
        EventInvoker<Object> initInvoker = new MethodEventInvoker<Object>(init);
        Method destroy = OrderedDependentPojo.class.getMethod("destroy",(Class[])null);
        EventInvoker<Object> destroyInvoker = new MethodEventInvoker<Object>(destroy);

        JavaContextFactory source = new JavaContextFactory("source",constructor,scope);
        source.setInitInvoker(initInvoker);
        source.setDestroyInvoker(destroyInvoker);
        JavaContextFactory target = new JavaContextFactory("target",constructor,scope);
        target.setInitInvoker(initInvoker);
        target.setDestroyInvoker(destroyInvoker);
        List<Injector> injectors = new ArrayList<Injector>();
        injectors.add(new MethodInjector(getPojo, new MockTargetFactory("target",context)));
        source.setSetters(injectors);
        List<ContextFactory<Context>> list = new ArrayList<ContextFactory<Context>>();
        list.add((ContextFactory)source);
        list.add((ContextFactory)target);
        return list;
    }

    private static class MockTargetFactory implements ObjectFactory{

        private String name;
        private ScopeContext context;

        public MockTargetFactory (String name, ScopeContext context){
            this.name = name;
            this.context = context;
        }

        public Object getInstance() throws ObjectCreationException {
            return context.getContext(name).getInstance(null);
        }
    }

}
