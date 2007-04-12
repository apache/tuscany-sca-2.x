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

package org.apache.tuscany.databinding.sdo.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.databinding.sdo.DataObject2String;
import org.apache.tuscany.databinding.sdo.DataObject2XMLStreamReader;
import org.apache.tuscany.databinding.sdo.HelperContextProcessor;
import org.apache.tuscany.databinding.sdo.HelperContextRegistry;
import org.apache.tuscany.databinding.sdo.HelperContextRegistryImpl;
import org.apache.tuscany.databinding.sdo.ImportSDOProcessor;
import org.apache.tuscany.databinding.sdo.SDODataBinding;
import org.apache.tuscany.databinding.sdo.String2DataObject;
import org.apache.tuscany.databinding.sdo.XMLDocument2String;
import org.apache.tuscany.databinding.sdo.XMLDocument2XMLStreamReader;
import org.apache.tuscany.databinding.sdo.XMLStreamReader2DataObject;
import org.apache.tuscany.databinding.sdo.XMLStreamReader2XMLDocument;
import org.apache.tuscany.implementation.java.introspect.JavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessorRegistry;
import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;
import org.apache.tuscany.spi.bootstrap.ModuleActivator;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.TransformerRegistry;

/**
 * @version $Rev$ $Date$
 */
public class SDODataBindingModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        map.put(HelperContextRegistry.class, new HelperContextRegistryImpl());
        return map;
    }

    public void start(ExtensionPointRegistry registry) {
        DataBindingRegistry dataBindingRegistry = registry.getExtensionPoint(DataBindingRegistry.class);
        dataBindingRegistry.register(new SDODataBinding());

        StAXArtifactProcessorRegistry processorRegistry = registry
            .getExtensionPoint(StAXArtifactProcessorRegistry.class);
        HelperContextRegistry contextRegistry = registry.getExtensionPoint(HelperContextRegistry.class);
        processorRegistry.addArtifactProcessor(new ImportSDOProcessor(contextRegistry));

        TransformerRegistry transformerRegistry = registry.getExtensionPoint(TransformerRegistry.class);
        transformerRegistry.registerTransformer(new DataObject2String());
        transformerRegistry.registerTransformer(new DataObject2XMLStreamReader());
        transformerRegistry.registerTransformer(new XMLDocument2String());
        transformerRegistry.registerTransformer(new String2DataObject());
        transformerRegistry.registerTransformer(new XMLDocument2XMLStreamReader());
        transformerRegistry.registerTransformer(new XMLStreamReader2DataObject());
        transformerRegistry.registerTransformer(new XMLStreamReader2XMLDocument());
        
        JavaClassIntrospectorExtensionPoint introspectorExtensionPoint = registry.getExtensionPoint(JavaClassIntrospectorExtensionPoint.class);
        introspectorExtensionPoint.addExtension(new HelperContextProcessor(contextRegistry));

    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
