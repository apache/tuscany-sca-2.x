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
package org.apache.tuscany.container.ruby;

import java.net.URL;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentType;

/**
 * @version $Rev$ $Date$
 */
public class RubyComponentTypeLoader extends ComponentTypeLoaderExtension<RubyImplementation> {

    private JavaInterfaceProcessorRegistry processorRegistry;

    public RubyComponentTypeLoader(@Autowire
    JavaInterfaceProcessorRegistry processorRegistry) {
        this.processorRegistry = processorRegistry;
    }

    @Override
    protected Class<RubyImplementation> getImplementationClass() {
        return RubyImplementation.class;
    }

    protected String getResourceName(RubyImplementation implementation) {
        return implementation.getRubyScript().getScriptName();
    }

    // TODO: must be possible to move all the following up in to ComponentTypeLoaderExtension

    public void load(CompositeComponent<?> parent,
                     RubyImplementation implementation,
                     DeploymentContext deploymentContext) throws LoaderException {

        URL resource = implementation.getRubyScript().getClassLoader()
                .getResource(getSideFileName(implementation));
        RubyComponentType componentType;
        componentType = loadFromSidefile(resource,
                                         deploymentContext);
        implementation.setComponentType(componentType);
    }

    protected RubyComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        ComponentType ct = loaderRegistry.load(null,
                                               url,
                                               ComponentType.class,
                                               deploymentContext);
        RubyComponentType jsct = new RubyComponentType(ct);
        return jsct;
    }

    private String getSideFileName(RubyImplementation implementation) {
        String baseName = getResourceName(implementation);
        int lastDot = baseName.lastIndexOf('.');
        if (lastDot != -1) {
            baseName = baseName.substring(0,
                                          lastDot);
        }
        return baseName + ".componentType";
    }

}
