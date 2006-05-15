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
package org.apache.tuscany.core.sdo;

import commonj.sdo.helper.DataFactory;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.sdo.util.SDOUtil;

/**
 * @version $$Rev$$ $$Date$$
 */
public class DataFactoryObjectFactory implements ObjectFactory<DataFactory> {

    private ContextResolver resolver;

    /**
     * @throws org.apache.tuscany.core.injection.FactoryInitException
     *
     */
    public DataFactoryObjectFactory(ContextResolver resolver) {
        this.resolver = resolver;
    }


    public DataFactory getInstance() throws ObjectCreationException {
        CompositeContext parent = resolver.getCurrentContext();
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
        AssemblyContext assemblyContext = ctx.resolveInstance(AssemblyContext.class);
        return SDOUtil.createDataFactory(assemblyContext.getTypeHelper());
    }


}
