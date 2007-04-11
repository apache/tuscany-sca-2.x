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

package org.apache.tuscany.implementation.script;

import org.apache.tuscany.spi.extension.ComponentBuilderExtension;

public class ScriptComponentBuilder extends ComponentBuilderExtension<ScriptImplementation> {

    public ScriptComponentBuilder() {
    }

//    @Override
//    protected Class<ScriptImplementation> getImplementationType() {
//        return ScriptImplementation.class;
//    }
//
//    public Component build(ComponentDefinition componentDefinition, DeploymentContext context) throws BuilderException {
//
//        // setup reference injection sites
//        ComponentType componentType = componentDefinition.getImplementation().getComponentType();
//
//        for (Object o : componentType.getReferences().values()) {
//            ReferenceDefinition reference = (ReferenceDefinition) o;
//            System.out.println(reference);
//        }
//        
//        URI name = componentDefinition.getUri();
//        ScriptImplementation impl = (ScriptImplementation)componentDefinition.getImplementation();
//        URI groupId = context.getComponentId();
//
//        Component scriptComponent = new ScriptComponent(name, impl, proxyService, workContext, groupId, 0);
//        return scriptComponent;
//    }
//
//    public Component build(org.apache.tuscany.assembly.Component arg0, DeploymentContext arg1) throws BuilderException {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
