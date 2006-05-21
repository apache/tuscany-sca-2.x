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
package org.apache.tuscany.core.builder.impl;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.WireFactory;

/**
 * Uses a proxy factory to return an object instance
 * 
 * @version $Rev$ $Date$
 */
public class ProxyObjectFactory implements ObjectFactory {

    private WireFactory factory;

    public ProxyObjectFactory(WireFactory factory) {
        this.factory = factory;
    }

    public Object getInstance() throws ObjectCreationException {
        try {
            return factory.createProxy();
        } catch (ProxyCreationException e) {
            throw new ObjectCreationException(e);
        }
    }

}
