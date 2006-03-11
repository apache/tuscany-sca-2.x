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
package org.apache.tuscany.core.system.injection;

import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.injection.ObjectCreationException;

/**
 * Implementation of ObjectFactory that returns an instance by resolving against an AutowireContext.
 *
 * @version $Rev$ $Date$
 */
public class AutowireFactory<T> implements ObjectFactory<T> {

    private AutowireContext autoWireContext;

    private Class<T> implementationType;

    /**
     * Constructor specifying the context to wire against and the type of service required.
     *
     * @param implementationType the type of service required
     * @param autoWireContext    the context to wire against
     */
    public AutowireFactory(Class<T> implementationType, AutowireContext autoWireContext) {
        assert (implementationType != null) : "Implementation type was null";
        assert (autoWireContext != null) : "Autowire context was null";
        this.implementationType = implementationType;
        this.autoWireContext = autoWireContext;
    }

    public T getInstance() throws ObjectCreationException {
        // todo what about required? should this just return null?
        return autoWireContext.resolveInstance(implementationType);
    }
}
