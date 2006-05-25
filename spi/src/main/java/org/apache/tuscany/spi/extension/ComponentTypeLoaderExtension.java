/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Destroy;

import org.apache.tuscany.spi.loader.ComponentTypeLoader;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Implementation;

/**
 * @version $Rev$ $Date$
 */
public abstract class ComponentTypeLoaderExtension<T extends Implementation> implements ComponentTypeLoader<T> {
    private LoaderRegistry loaderRegistry;

    @Property
    public void setLoaderRegistry(LoaderRegistry loaderRegistry) {
        this.loaderRegistry = loaderRegistry;
    }

    protected abstract Class<T> getTypeClass();
    
    @Init
    public void start() {
        loaderRegistry.registerLoader(getTypeClass(), this);
    }

    @Destroy
    public void stop() {
        loaderRegistry.unregisterLoader(getTypeClass());
    }
}
