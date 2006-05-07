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
package org.apache.tuscany.container.java.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.core.context.PojoAtomicContext;
import org.apache.tuscany.core.context.PojoInstanceContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.injection.NoAccessorException;
import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.core.mock.component.DataObject;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.InstanceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.databinding.sdo.SDOObjectFactory;

/**
 * Manages Java component implementation instances
 *
 * @version $Rev$ $Date$
 */
public class JavaAtomicContext extends PojoAtomicContext {

    private Map<String, TargetWireFactory> targetWireFactories = new HashMap<String, TargetWireFactory>();
    private List<SourceWireFactory> sourceWireFactories = new ArrayList<SourceWireFactory>();

    public JavaAtomicContext(String name, ObjectFactory<?> objectFactory, boolean eagerInit, EventInvoker<Object> initInvoker,
                             EventInvoker<Object> destroyInvoker) {
        super(name, objectFactory, eagerInit, initInvoker, destroyInvoker);
        this.objectFactory = objectFactory;
    }

    public InstanceContext createInstance() throws ObjectCreationException {
        InstanceContext ctx = new PojoInstanceContext(this, objectFactory.getInstance());
        ctx.start();
        return ctx;
    }

    public void prepare() {
    }

    public Object getInstance(QualifiedName qName) throws TargetException {
        return getTargetInstance();
    }

    public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {
        targetWireFactories.put(serviceName, factory);
    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        return targetWireFactories.get(serviceName);
    }

    public Map<String, TargetWireFactory> getTargetWireFactories() {
        return targetWireFactories;
    }

    public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {
        sourceWireFactories.add(factory);
        //setters.add(createReferenceInjector(referenceName, factory, false));
    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factories, boolean multiplicity) {
        sourceWireFactories.addAll(factories);
        //setters.add(createReferenceInjector(referenceName, factories, multiplicity));
    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return sourceWireFactories;
    }

}
