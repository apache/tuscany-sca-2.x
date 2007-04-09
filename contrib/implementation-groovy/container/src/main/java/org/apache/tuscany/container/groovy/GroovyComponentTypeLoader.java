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
package org.apache.tuscany.container.groovy;

import java.net.URL;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $Rev$ $Date$
 */
public class GroovyComponentTypeLoader extends ComponentTypeLoaderExtension<GroovyImplementation> {
    
    @Override
    protected Class<GroovyImplementation> getImplementationClass() {
        return GroovyImplementation.class;
    }

    public void load(CompositeComponent parent, GroovyImplementation implementation, DeploymentContext context)
        throws LoaderException {
        URL resource = implementation.getApplicationLoader().getResource(getSideFileName(implementation));
        GroovyComponentType componentType;
        if (resource == null) {
            //TODO this should be replaced by loadFromIntrospection,
            componentType = new GroovyComponentType();
        } else {
            componentType = loadFromSidefile(resource, context);
        }
        
        // for now, default to composite
        componentType.setLifecycleScope(Scope.COMPOSITE);
        implementation.setComponentType(componentType);
    }
    
    protected GroovyComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext)
        throws LoaderException {
        GroovyComponentType ct = new GroovyComponentType();
        return (GroovyComponentType)loaderRegistry.load(null,ct, url, GroovyComponentType.class, deploymentContext);
    }

    private String getSideFileName(GroovyImplementation implementation) {
        String baseName = getResourceName(implementation);
        int lastDot = baseName.lastIndexOf('.');
        if (lastDot != -1) {
            baseName = baseName.substring(0, lastDot);
        }
        return baseName + ".componentType";
    }
    
    protected String getResourceName(GroovyImplementation implementation) {
        return implementation.getScriptResourceName();
    }
}
