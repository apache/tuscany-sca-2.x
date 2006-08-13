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
package org.apache.tuscany.container.javascript;

import java.net.URL;

import org.apache.tuscany.container.javascript.rhino.RhinoSCAConfig;
import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.core.implementation.IntrospectionRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentType;
import org.osoa.sca.annotations.Constructor;

/**
 * @version $Rev$ $Date$
 */
public class JavaScriptComponentTypeLoader extends ComponentTypeLoaderExtension<JavaScriptImplementation> {
//    private Introspector introspector;

    @Constructor( { "registry", "introspector" })
    public JavaScriptComponentTypeLoader(@Autowire LoaderRegistry loaderRegistry, @Autowire IntrospectionRegistry introspector) {
        super(loaderRegistry);
        // this.introspector = introspector;
    }

    @Override
    protected Class<JavaScriptImplementation> getImplementationClass() {
        return JavaScriptImplementation.class;
    }

    protected ComponentType loadByIntrospection(CompositeComponent<?> parent, JavaScriptImplementation implementation,
            DeploymentContext deploymentContext) {

        RhinoScript rhinoScript = implementation.getRhinoScript();
        RhinoSCAConfig scaConfig = rhinoScript.getSCAConfig();
        if (!scaConfig.hasSCAConfig()) {
            throw new IllegalArgumentException("must use either .componentType side file or JS SCA varriable definition");
        }

        ComponentType componentType = new JavaScriptIntrospector(null).introspectScript(scaConfig,rhinoScript.getClassLoader());

        return componentType;
    }

    protected String getResourceName(JavaScriptImplementation implementation) {
        return implementation.getRhinoScript().getScriptName();
    }

    // TODO: must be possible to move all the following up in to ComponentTypeLoaderExtension 
    
    public void load(CompositeComponent<?> parent, JavaScriptImplementation implementation, DeploymentContext deploymentContext)
            throws LoaderException {

        URL resource = implementation.getRhinoScript().getClassLoader().getResource(getSideFileName(implementation));
        ComponentType componentType;
        if (resource == null) {
            componentType = loadByIntrospection(parent, implementation, deploymentContext);
        } else {
            componentType = loadFromSidefile(resource, deploymentContext);
        }

        implementation.setComponentType(componentType);
    }

    protected ComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(null, url, ComponentType.class, deploymentContext);
    }

    private String getSideFileName(JavaScriptImplementation implementation) {
        String baseName = getResourceName(implementation);
        int lastDot = baseName.lastIndexOf('.');
        if (lastDot != -1) {
            baseName = baseName.substring(0, lastDot);
        }
        return baseName + ".componentType";
    }

}
