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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.container.javascript.utils.xmlfromxsd.XmlInstanceRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.xmlbeans.XmlObject;
import org.osoa.sca.annotations.Constructor;

/**
 * Extension point for creating {@link JavaScriptComponent}s from an assembly configuration
 */
public class JavaScriptComponentBuilder extends ComponentBuilderExtension<JavaScriptImplementation> {

    private static String head = "var xmlInstanceMap = new Array();";

    private static String part1 = "xmlInstanceMap[\"";

    private static String part2 = "\"] = ";

    private static String part3 = ";";

    private static String getXmlObjectFunction = 
        "function getXmlObject(xmlElementNamespace, xmlElementName){\n" +
        "return xmlInstanceMap[xmlElementNamespace + \"#\" + xmlElementName];\n}";
    
    XmlInstanceRegistry xmlInstRegistry;

    @Constructor({"xmlInstRegistry"})
    public JavaScriptComponentBuilder(@Autowire XmlInstanceRegistry reg) {
        this.xmlInstRegistry = reg;
    }

    protected Class<JavaScriptImplementation> getImplementationType() {
        return JavaScriptImplementation.class;
    }

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent, ComponentDefinition<JavaScriptImplementation> componentDefinition,
            DeploymentContext deploymentContext) throws BuilderConfigException {

        String name = componentDefinition.getName();
        JavaScriptImplementation implementation = componentDefinition.getImplementation();
        JavaScriptComponentType componentType = implementation.getComponentType();

        // get list of serviceBindings provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
            //do this for the set of references also
            enhanceRhinoScript(serviceDefinition, implementation);       
        }

        // get the properties for the component
        Collection<PropertyValue<?>> propertyValues = componentDefinition.getPropertyValues().values();
        Map<String, Object> properties = new HashMap<String, Object>();
        for (PropertyValue propertyValue : propertyValues) {
            properties.put(propertyValue.getName(), propertyValue.getValueFactory().getInstance());
        }

        RhinoScript rhinoScript = implementation.getRhinoScript();

        return new JavaScriptComponent(name, rhinoScript, properties, parent, wireService, workContext , monitor);
    }

    private void enhanceRhinoScript(ServiceDefinition serviceDefn, JavaScriptImplementation implementation) throws BuilderConfigException {
        //if the service interface of the component is a wsdl get the wsdl interface and generate 
        //xml instances for the elements in it.  Add these xml instances to the rhinoscript.
        //TODO : when interface.wsdl and wsdl registry is integrated remove this hardcoding and 
        //obtain wsdl from the interface.wsdl or wsdl registry
        String wsdlPath = "org/apache/tuscany/container/javascript/rhino/helloworld.wsdl";

        //this if block is a tempfix to get other testcases working. Again when a the interface.wsdl 
        //extension is in place this will be deleted.  Right now this is the only way we know that 
        //a js has to do with an interface that is wsdl.
        if (!implementation.getRhinoScript().getScriptName().endsWith("e4x.js")) {
            return;
        }

        try {
            Map<String, XmlObject> xmlInstanceMap = xmlInstRegistry.getXmlInstance(wsdlPath);
            StringBuffer sb = new StringBuffer();

            sb.append(head);
            sb.append("\n");
            for (String xmlInstanceKey : xmlInstanceMap.keySet()) {
                sb.append(part1);
                sb.append(xmlInstanceKey);
                sb.append(part2);
                sb.append(xmlInstanceMap.get(xmlInstanceKey).toString());
                sb.append(part3);
                sb.append("\n");
            }
            // System.out.println(" **** - " + sb.toString());

            sb.append(getXmlObjectFunction);

            RhinoScript rhinoScript = implementation.getRhinoScript();
            sb.append(rhinoScript.getScript());
            rhinoScript.setScript(sb.toString());
            rhinoScript.initScriptScope(rhinoScript.getScriptName(), sb.toString(), null, rhinoScript.getClassLoader());
            implementation.setRhinoScript(rhinoScript);

        } catch (Exception e) {
            throw new BuilderConfigException(e);
        }
    }

}
