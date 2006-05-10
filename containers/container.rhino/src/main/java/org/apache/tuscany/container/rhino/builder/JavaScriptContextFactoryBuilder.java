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
import java.util.List;
import java.util.Map;

import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;

import org.apache.tuscany.container.rhino.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.rhino.config.JavaScriptContextFactory;
import org.apache.tuscany.container.rhino.rhino.E4XDataBinding;
import org.apache.tuscany.container.rhino.rhino.RhinoE4XScript;
import org.apache.tuscany.container.rhino.rhino.RhinoScript;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.extension.ContextFactoryBuilderSupport;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;

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
        for (Service service : jsImplementation.getComponentType().getServices()) {
            services.put(service.getName(), service.getServiceContract().getInterface());
        }

        Map<String, Object> defaultProperties = new HashMap<String, Object>();
        for (org.apache.tuscany.model.assembly.Property property : jsImplementation.getComponentType().getProperties()) {
            defaultProperties.put(property.getName(), property.getDefaultValue());
        }

        String script = jsImplementation.getScript();
        ClassLoader cl = jsImplementation.getResourceLoader().getClassLoader();

        RhinoScript invoker;
        if (isE4XStyle(componentName, jsImplementation.getComponentType().getServices())) {
            E4XDataBinding dataBinding = createDataBinding(jsImplementation);
            invoker = new RhinoE4XScript(componentName, script, defaultProperties, cl, dataBinding);
        } else {
            invoker = new RhinoScript(componentName, script, defaultProperties, cl);
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        JavaScriptContextFactory contextFactory = new JavaScriptContextFactory(componentName, scope, services, properties, invoker);

        return contextFactory;
    }

    /**
     * Tests if this should be an E4X style service
     * Its E4X if the JavaScript component uses WSDL to define its interface
     */
    protected boolean isE4XStyle(String componentName, List<Service> services) {
        Boolean isE4XStyle = null;
        for (Service service : services) {
            ServiceContract sc = service.getServiceContract();
            if (sc instanceof WSDLServiceContract) {
                if (isE4XStyle != null && !isE4XStyle.booleanValue()) {
                    throw new BuilderConfigException("mixed service interface types not supportted");
                }
                isE4XStyle = Boolean.TRUE;
            } else {
                isE4XStyle = Boolean.FALSE;
            }
        }
        return isE4XStyle.booleanValue();
    }

    /**
     * Create the data binding for the component initialized for each operation in the service
     */
    protected E4XDataBinding createDataBinding(JavaScriptImplementation jsImplementation) {
        E4XDataBinding dataBinding = new E4XDataBinding(jsImplementation.getTypeHelper());
        for (Service service : jsImplementation.getComponentType().getServices()) {
            ServiceContract sc = service.getServiceContract();
            if (sc instanceof WSDLServiceContract) {
                PortType pt = ((WSDLServiceContract) sc).getPortType();
                for (Object o : pt.getOperations()) {
                    Operation operation = (Operation) o;
                    Input input = operation.getInput();
                    if (input != null) {
                        Message message = input.getMessage();
                        if (message != null) {
                            List parts = message.getOrderedParts(null);
                            if (parts != null && parts.size() > 0) {
                                Part part = (Part) parts.get(0);
                                dataBinding.addElementQName(operation.getName(), part.getElementName());
                            }
                        }
                    }
                }
            }
        }
        return dataBinding;
    }

}
