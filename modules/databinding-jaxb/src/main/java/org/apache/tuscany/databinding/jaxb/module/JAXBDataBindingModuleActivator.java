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

package org.apache.tuscany.databinding.jaxb.module;

import java.util.Map;

import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.jaxb.JAXB2Node;
import org.apache.tuscany.databinding.jaxb.JAXBDataBinding;
import org.apache.tuscany.databinding.jaxb.JAXWSJavaInterfaceProcessor;
import org.apache.tuscany.databinding.jaxb.Node2JAXB;
import org.apache.tuscany.databinding.jaxb.Reader2JAXB;
import org.apache.tuscany.databinding.jaxb.XMLStreamReader2JAXB;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;

/**
 * Module activator for JAXB databinding
 * 
 * @version $Rev$ $Date$
 */
public class JAXBDataBindingModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        dataBindings.addDataBinding(new JAXBDataBinding());

        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        transformers.addTransformer(new JAXB2Node());
        transformers.addTransformer(new Node2JAXB());
        transformers.addTransformer(new Reader2JAXB());
        transformers.addTransformer(new XMLStreamReader2JAXB());

        JavaInterfaceIntrospectorExtensionPoint introspectors = registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        introspectors.addInterfaceVisitor(new JAXWSJavaInterfaceProcessor());
        
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
