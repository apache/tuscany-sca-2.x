/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.spi.extension;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.spi.loader.ComponentTypeLoader;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Implementation;

/**
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@EagerInit
public abstract class ComponentTypeLoaderExtension<I extends Implementation> implements ComponentTypeLoader<I> {
    protected LoaderRegistry loaderRegistry;

    protected ComponentTypeLoaderExtension() {
    }

    protected ComponentTypeLoaderExtension(LoaderRegistry loaderRegistry) {
        this.loaderRegistry = loaderRegistry;
    }

    @Reference
    public void setLoaderRegistry(LoaderRegistry loaderRegistry) {
        this.loaderRegistry = loaderRegistry;
    }

    @Init
    public void start() {
        loaderRegistry.registerLoader(getImplementationClass(), this);
    }

    @Destroy
    public void stop() {
        loaderRegistry.unregisterLoader(getImplementationClass());
    }

    protected abstract Class<I> getImplementationClass();

}
