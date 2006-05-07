/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.container.rhino.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.container.rhino.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.rhino.config.JavaScriptContextFactory;
import org.apache.tuscany.container.rhino.rhino.RhinoE4XScript;
import org.apache.tuscany.container.rhino.rhino.RhinoScript;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.extension.ContextFactoryBuilderSupport;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;

import commonj.sdo.helper.TypeHelper;

/**
 * Builds {@link org.apache.tuscany.container.rhino.config.JavaScriptContextFactory}s from a JavaScript component type
 * 
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaScriptContextFactoryBuilder extends ContextFactoryBuilderSupport<JavaScriptImplementation> {

    @Override
    protected ContextFactory createContextFactory(String componentName, JavaScriptImplementation jsImplementation, Scope scope) {
        Map<String, Class> services = new HashMap<String, Class>();

        Boolean isWSDLService = null;
        for (Service service : jsImplementation.getComponentType().getServices()) {
            ServiceContract sc = service.getServiceContract();
            if (sc instanceof WSDLServiceContract) {
                if (isWSDLService != null && !isWSDLService.booleanValue()) {
                    BuilderException e = new BuilderConfigException("mixed service interface types not supportted");
                    e.setIdentifier(componentName);
                    throw e;
                }
                isWSDLService = Boolean.TRUE;
            } else {
                isWSDLService = Boolean.FALSE;
            }

            services.put(service.getName(), sc.getInterface());
        }

        Map<String, Object> defaultProperties = new HashMap<String, Object>();
        for (org.apache.tuscany.model.assembly.Property property : jsImplementation.getComponentType().getProperties()) {
            defaultProperties.put(property.getName(), property.getDefaultValue());
        }

        String script = jsImplementation.getScript();
        ClassLoader cl = jsImplementation.getResourceLoader().getClassLoader();

        RhinoScript invoker;
        if (Boolean.TRUE.equals(isWSDLService)) {
            TypeHelper typeHelper = jsImplementation.getTypeHelper();
            invoker = new RhinoE4XScript(componentName, script, defaultProperties, cl, typeHelper);
        } else {
            invoker = new RhinoScript(componentName, script, defaultProperties, cl);
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        JavaScriptContextFactory contextFactory = new JavaScriptContextFactory(componentName, scope, services, properties, invoker);

        return contextFactory;
    }
}
