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
package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.TargetException;

/**
 * Returns a direct reference to a target service exposed in another aggregate, i.e. the factory avoids creating proxies
 * and returns the actual target instance
 * 
 * @version $Rev: 384135 $ $Date: 2006-03-07 22:53:58 -0800 (Tue, 07 Mar 2006) $
 */
public class InterAggregateReferenceFactory<T> implements ObjectFactory<T> {

    private ContextResolver resolver;

    private QualifiedName targetQualifiedName;

    /**
     * Reference source is an external service, target is in another module
     * 
     * @param service
     * @param parentContext
     */
    public InterAggregateReferenceFactory(String targetName) {
        targetQualifiedName = new QualifiedName(targetName);
    }

    public void setContextResolver(ContextResolver resolver){
        this.resolver = resolver;
    }
    
    public T getInstance() throws ObjectCreationException {
        // only return entry points since this is an inter-module wire
        Object o = resolver.getCurrentContext().getInstance(targetQualifiedName);
        if (o != null) {
            return (T) o;
        } else {
            // walk up the hierarchy of aggregate contexts
            AggregateContext ctx = resolver.getCurrentContext();
            do {
                if (ctx == null) {
                    break; // reached top of context hierarchy
                }
                InstanceContext compContext = ctx.getContext(targetQualifiedName.getPartName());
                if (compContext != null) {
                    o = compContext.getInstance(targetQualifiedName);
                    if (o != null) {
                        return (T) o;
                    }
                }
                ctx = ctx.getParent();
            } while (ctx != null);
            TargetException e = new TargetException("Target reference not found");
            e.setIdentifier(targetQualifiedName.getQualifiedName());
            throw e;
        }
    }

}
