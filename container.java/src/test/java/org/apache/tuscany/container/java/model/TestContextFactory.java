/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.container.java.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.container.java.context.JavaComponentContext;
import org.apache.tuscany.container.java.mock.MockAssemblyFactory;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * A factory for Java component contexts
 * 
 * @version $Rev$ $Date$
 */
public class TestContextFactory {

    private TestContextFactory() {
        super();
    }

    public static JavaComponentContext createPojoContext(String name, Class implType, Scope scope,
                                                         AggregateContext moduleComponentContext) throws NoSuchMethodException {
        SimpleComponent component = MockAssemblyFactory.createComponent(name, implType, scope);

        Set<Field> fields = JavaIntrospectionHelper.getAllFields(implType);
        Set<Method> methods = JavaIntrospectionHelper.getAllUniqueMethods(implType);
        List<Injector> injectors = new ArrayList();
        EventInvoker initInvoker = null;
        boolean eagerInit = false;
        EventInvoker destroyInvoker = null;
        for (Field field : fields) {
            ComponentName compName = field.getAnnotation(ComponentName.class);
            if (compName != null) {
                Injector injector = new FieldInjector(field, new SingletonObjectFactory(name));
                injectors.add(injector);
            }
            Context context = field.getAnnotation(Context.class);
            if (context != null) {
                Injector injector = new FieldInjector(field, new SingletonObjectFactory(moduleComponentContext));
                injectors.add(injector);
            }
        }
        for (Method method : methods) {
            // FIXME Java5
            Init init = method.getAnnotation(Init.class);
            if (init != null && initInvoker == null) {
                initInvoker = new MethodEventInvoker(method);
                eagerInit = init.eager();
                continue;
            }
            Destroy destroy = method.getAnnotation(Destroy.class);
            if (destroy != null && destroyInvoker == null) {
                destroyInvoker = new MethodEventInvoker(method);
                continue;
            }
            ComponentName compName = method.getAnnotation(ComponentName.class);
            if (compName != null) {
                Injector injector = new MethodInjector(method, new SingletonObjectFactory(name));
                injectors.add(injector);
            }
            Context context = method.getAnnotation(Context.class);
            if (context != null) {
                Injector injector = new MethodInjector(method, new SingletonObjectFactory(moduleComponentContext));
                injectors.add(injector);
            }
        }

        boolean stateless = (scope == Scope.INSTANCE);
        JavaComponentContext context = new JavaComponentContext("foo", new PojoObjectFactory(JavaIntrospectionHelper
                .getDefaultConstructor(implType), null, injectors), eagerInit, initInvoker, destroyInvoker, stateless);

        return context;
    }
}
