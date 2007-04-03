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

package org.apache.tuscany.container.crud;

import org.apache.tuscany.core.bootstrap.ExtensionActivator;
import org.apache.tuscany.core.bootstrap.ExtensionRegistry;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.loader.LoaderRegistry;

/**
 * @version $Rev$ $Date$
 */
public class CRUDExtensionActivator implements ExtensionActivator {
    private CRUDComponentTypeLoader componentTypeLoader;
    private CRUDImplementationLoader implementationLoader;
    private CRUDComponentBuilder builder;

    public void start(ExtensionRegistry registry) {
        LoaderRegistry loaderRegistry = registry.getExtension(LoaderRegistry.class);
        BuilderRegistry builderRegistry = registry.getExtension(BuilderRegistry.class);
        componentTypeLoader = new CRUDComponentTypeLoader(loaderRegistry);
        componentTypeLoader.start();

        implementationLoader = new CRUDImplementationLoader(loaderRegistry);
        implementationLoader.start();

        builder = new CRUDComponentBuilder();
        builder.setBuilderRegistry(builderRegistry);
        builder.init();
    }

    public void stop(ExtensionRegistry registry) {
        componentTypeLoader.stop();
        implementationLoader.stop();
    }
}
