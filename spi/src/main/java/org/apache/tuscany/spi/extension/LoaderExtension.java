/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;

import org.apache.tuscany.model.ModelObject;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * Support class for extending the Loader mechanism.
 *
 * @version $Rev$ $Date$
 */
public abstract class LoaderExtension<T extends ModelObject> implements StAXElementLoader<T> {
    protected LoaderRegistry registry;

    @Autowire
    public void setRegistry(LoaderRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void start() {
        registry.registerLoader(getXMLType(), this);
    }

    @Destroy
    public void stop() {
        registry.unregisterLoader(getXMLType(), this);
    }

    /**
     * Returns the QName of the element that this implementation handles.
     *
     * @return the QName of the element that this implementation handles
     */
    protected abstract QName getXMLType();
}
