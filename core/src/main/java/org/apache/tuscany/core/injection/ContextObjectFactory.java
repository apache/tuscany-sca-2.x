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
package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.CompositeContext;

/**
 * An implementation of <code>ObjectFactory</code> that resolves the current parent context
 * 
 * @version $Rev: 380903 $ $Date: 2006-02-25 00:53:26 -0800 (Sat, 25 Feb 2006) $
 */
public class ContextObjectFactory implements ObjectFactory<CompositeContext> {
    
    private final ContextResolver resolver;

    public ContextObjectFactory(ContextResolver resolver) {
        assert (resolver != null) : "Resolver cannot be null";
        this.resolver = resolver;
    }

    public CompositeContext getInstance() {
        return resolver.getCurrentContext();
    }

}
