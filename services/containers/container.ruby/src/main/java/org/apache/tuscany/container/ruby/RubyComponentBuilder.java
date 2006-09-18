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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.container.ruby.rubyscript.RubyScript;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Extension point for creating {@link RubyComponent}s from an assembly configuration
 */
public class RubyComponentBuilder extends ComponentBuilderExtension<RubyImplementation> {

    private static String head = "var xmlInstanceMap = new Array();";

    private static String part1 = "xmlInstanceMap[\"";

    private static String part2 = "\"] = ";

    private static String part3 = ";";

    private static String getXmlObjectFunction = "function getXmlObject(xmlElementNamespace, xmlElementName){\n"
            + "return xmlInstanceMap[xmlElementNamespace + \"#\" + xmlElementName];\n}";

    /*
     * XmlInstanceRegistry xmlInstRegistry; @Constructor({"xmlInstRegistry"}) public RubyComponentBuilder(@Autowire XmlInstanceRegistry reg) {
     * this.xmlInstRegistry = reg; }
     */
    protected Class<RubyImplementation> getImplementationType() {
        return RubyImplementation.class;
    }

    @SuppressWarnings("unchecked")
    public Component build(CompositeComponent parent,
                              ComponentDefinition<RubyImplementation> componentDefinition,
                              DeploymentContext deploymentContext) throws BuilderConfigException {

        String name = componentDefinition.getName();
        RubyImplementation implementation = componentDefinition.getImplementation();
        RubyComponentType componentType = implementation.getComponentType();

        // get list of services provided by this component
        Collection<ServiceDefinition> collection = componentType.getServices().values();
        List<Class<?>> services = new ArrayList<Class<?>>(collection.size());
        for (ServiceDefinition serviceDefinition : collection) {
            services.add(serviceDefinition.getServiceContract().getInterfaceClass());
        }

        RubyScript rubyScript = implementation.getRubyScript();

        // TODO: have ComponentBuilderExtension pass ScopeContainer in on build method?
        ScopeContainer scopeContainer;
        Scope scope = componentType.getLifecycleScope();
        if (Scope.MODULE == scope) {
            scopeContainer = deploymentContext.getModuleScope();
        } else {
            scopeContainer = scopeRegistry.getScopeContainer(scope);
        }

        return new RubyComponent(name,
                                 rubyScript,
                                 implementation.getRubyClassName(),
                                 services,
                                 parent,
                                 scopeContainer,
                                 wireService,
                                 workContext);
    }
}
