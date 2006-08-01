/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.container.javascript;

import java.net.URL;

import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.core.implementation.IntrospectionRegistry;
import org.apache.tuscany.core.implementation.Introspector;
import org.apache.tuscany.core.implementation.JavaServiceContract;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.mozilla.javascript.Scriptable;
import org.osoa.sca.annotations.Constructor;

/**
 * @version $Rev: 423297 $ $Date: 2006-07-19 00:56:32 +0100 (Wed, 19 Jul 2006) $
 */
public class JavaScriptComponentTypeLoader extends ComponentTypeLoaderExtension<JavaScriptImplementation> {
    private Introspector introspector;

    @Constructor( { "registry", "introspector" })
    public JavaScriptComponentTypeLoader(@Autowire LoaderRegistry loaderRegistry, @Autowire IntrospectionRegistry introspector) {
        super(loaderRegistry);
        // this.introspector = introspector;
    }

    @Override
    protected Class<JavaScriptImplementation> getImplementationClass() {
        return JavaScriptImplementation.class;
    }

    public void load(CompositeComponent<?> parent, JavaScriptImplementation implementation, DeploymentContext deploymentContext)
            throws LoaderException {
        String scriptName = implementation.getRhinoScript().getScriptName();
        URL resource = implementation.getClassLoader().getResource(getBaseName(scriptName) + ".componentType");
        ComponentType componentType;
        if (resource == null) {
            componentType = loadByIntrospection(parent, implementation, deploymentContext);
            if (componentType == null) {
                throw new RuntimeException("must use .componentType side file or JS SCA variable definition");
            }
        } else {
            componentType = loadFromSidefile(resource, deploymentContext);
        }

        implementation.setComponentType(componentType);
    }

    protected ComponentType loadByIntrospection(CompositeComponent<?> parent, JavaScriptImplementation implementation,
            DeploymentContext deploymentContext) throws ProcessingException {
        ComponentType componentType = null;

        RhinoScript rhinoScript = implementation.getRhinoScript();
        Scriptable scope = rhinoScript.getScriptScope();
        Scriptable sca = (Scriptable) scope.get("SCA", scope);
        if (sca != null) {
            componentType = new ComponentType();

            Object serviceClass = sca.get("serviceClass", scope);
            if (serviceClass != null) {
                ServiceDefinition service = new ServiceDefinition();
                ServiceContract sc = new JavaServiceContract();
                try {
                    sc.setInterfaceClass(Class.forName(serviceClass.toString(), true, implementation.getClassLoader()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                service.setServiceContract(sc);
                componentType.add(service);
            }
        }

        return componentType;
    }

    protected ComponentType loadFromSidefile(URL url, DeploymentContext deploymentContext) throws LoaderException {
        return loaderRegistry.load(null, url, ComponentType.class, deploymentContext);
    }

    private String getBaseName(String scriptName) {
        String baseName = scriptName;
        int lastDot = baseName.lastIndexOf('.');
        if (lastDot != -1) {
            baseName = baseName.substring(0, lastDot);
        }
        return baseName;
    }

}
