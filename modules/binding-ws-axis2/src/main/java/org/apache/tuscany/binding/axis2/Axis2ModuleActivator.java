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

package org.apache.tuscany.binding.axis2;

import java.util.Map;

import org.apache.tuscany.binding.ws.xml.WebServiceBindingProcessor;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.http.ServletHostExtensionPoint;
import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;
import org.apache.tuscany.spi.bootstrap.ModuleActivator;
import org.apache.tuscany.spi.builder.BuilderRegistry;

public class Axis2ModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry extensionPointRegistry) {

        StAXArtifactProcessorExtensionPoint artifactProcessorRegistry = extensionPointRegistry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        artifactProcessorRegistry.addExtension(new WebServiceBindingProcessor());

        ServletHostExtensionPoint servletHost = extensionPointRegistry.getExtensionPoint(ServletHostExtensionPoint.class);
        
        BuilderRegistry builderRegistry = extensionPointRegistry.getExtensionPoint(BuilderRegistry.class);
        Axis2BindingBuilder builder = new Axis2BindingBuilder();
        builder.setBuilderRegistry(builderRegistry);
        builder.setServletHost(servletHost);
        builder.init();

    }

    public void stop(ExtensionPointRegistry registry) {
    }

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

}
