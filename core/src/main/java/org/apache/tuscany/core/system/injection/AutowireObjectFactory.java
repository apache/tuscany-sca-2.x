/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.injection.FactoryInitException;
import org.apache.tuscany.core.injection.ObjectCreationException;

/**
 * Returns an instance by resolving against an AutowireContext.
 * 
 * @version $Rev: 385139 $ $Date: 2006-03-11 11:03:11 -0800 (Sat, 11 Mar 2006) $
 */
public class AutowireObjectFactory<T> implements ObjectFactory<T> {

    private ContextResolver resolver;

    private Class<T> autowireType;

    private boolean required;

    /**
     * 
     * @throws FactoryInitException
     */
    public AutowireObjectFactory(Class<T> autowireType, boolean required, ContextResolver resolver) {
        assert (autowireType != null) : "Target interface was null";
        this.resolver = resolver;
        this.required = required;
        this.autowireType = autowireType;
    }

    /**
     * Creates a new factory that resolves against the cuurent context using the given implementation type
     * 
     * @throws FactoryInitException
     */
    public AutowireObjectFactory(Class<T> implementationType) {
        this(implementationType, true, null);
    }

    public T getInstance() throws ObjectCreationException {
        AggregateContext parent = resolver.getCurrentContext();
        if (parent == null) {
            return null;// FIXME semantic here means required is not followed
        }
        if (!(parent instanceof AutowireContext)) {
            ObjectCreationException e = new ObjectCreationException("Parent does not implement "
                    + AutowireContext.class.getName());
            e.setIdentifier(parent.getName());
            throw e;
        }
        AutowireContext ctx = (AutowireContext) parent;
//        if (ctx == null && required) {
//            AutowireResolutionException e = new AutowireResolutionException("Required autowire not found");
//            e.setIdentifier(autowireType.getName());
//            throw e;
//        } else if (ctx == null) {
//            return null;
//        }
        return ctx.resolveInstance(autowireType);
    }

    public void setContextResolver(ContextResolver resolver) {
        this.resolver = resolver;
    }

}
