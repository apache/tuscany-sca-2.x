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
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ModelObject;

import org.apache.tuscany.container.ruby.rubyscript.RubySCAConfig;
import org.apache.tuscany.container.ruby.rubyscript.RubyScript;

/**
 * @version $Rev$ $Date$
 */
public class RubyComponentTypeLoader extends ComponentTypeLoaderExtension<RubyImplementation> {

    private JavaInterfaceProcessorRegistry processorRegistry;

    public RubyComponentTypeLoader(@Autowire JavaInterfaceProcessorRegistry processorRegistry) {
        this.processorRegistry = processorRegistry;
    }

    @Override
    protected Class<RubyImplementation> getImplementationClass() {
        return RubyImplementation.class;
    }

    protected RubyComponentType loadByIntrospection(CompositeComponent parent,
                                                    ModelObject object,
                                                    RubyImplementation implementation,
                                                    DeploymentContext deploymentContext) throws
                                                                                         MissingResourceException,
                                                                                         InvalidServiceContractException {

        RubyScript rubyScript = implementation.getRubyScript();
        RubySCAConfig scaConfig = rubyScript.getSCAConfig();
        if (!scaConfig.hasSCAConfig()) {
            throw new IllegalArgumentException(
                "must use either .componentType side file or Ruby Global variable $SCA definition");
        }

        // FIXME this should be a system service, not instantiated here
        RubyComponentType componentType = new RubyIntrospector(null, processorRegistry)
            .introspectScript(scaConfig, rubyScript.getClassLoader());

        return componentType;
    }

    protected String getResourceName(RubyImplementation implementation) {
        return implementation.getRubyScript().getScriptName();
    }

    // TODO: must be possible to move all the following up in to ComponentTypeLoaderExtension

    public void load(CompositeComponent parent,
                     RubyImplementation implementation,
                     DeploymentContext deploymentContext) throws LoaderException {

        URL resource = implementation.getRubyScript().getClassLoader().getResource(getSideFileName(implementation));
        RubyComponentType componentType;
        if (resource == null) {
            try {
                componentType = loadByIntrospection(parent, null, implementation, deploymentContext);
            } catch (InvalidServiceContractException e) {
                throw new LoaderException("Invalid service contract", e);
            }
        } else {
            componentType = loadFromSidefile(resource, deploymentContext);
        }

        implementation.setComponentType(componentType);


    }

    @SuppressWarnings("unchecked")
    protected RubyComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        // This should be fixed to pass in a RubyComponentType instead of null
        RubyComponentType componentType = new RubyComponentType();
        return (RubyComponentType) loaderRegistry.load(null,
            componentType,
            url,
            ComponentType.class,
            deploymentContext);
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
