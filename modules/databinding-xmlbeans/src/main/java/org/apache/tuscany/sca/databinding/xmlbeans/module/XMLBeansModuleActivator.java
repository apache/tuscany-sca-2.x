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

package org.apache.tuscany.sca.databinding.xmlbeans.module;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.xmlbeans.Node2XmlObject;
import org.apache.tuscany.sca.databinding.xmlbeans.XMLBeansDataBinding;
import org.apache.tuscany.sca.databinding.xmlbeans.XMLStreamReader2XmlObject;
import org.apache.tuscany.sca.databinding.xmlbeans.XmlObject2Node;
import org.apache.tuscany.sca.databinding.xmlbeans.XmlObject2XMLStreamReader;

/**
 * Module activator for SDO/AXIOM databinding
 * 
 * @version $Rev$ $Date$
 */
public class XMLBeansModuleActivator implements ModuleActivator {

    public Object[] getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        dataBindings.addDataBinding(new XMLBeansDataBinding());

        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        transformers.addTransformer(new Node2XmlObject());
        transformers.addTransformer(new XmlObject2Node());
        transformers.addTransformer(new XmlObject2XMLStreamReader());
        transformers.addTransformer(new XMLStreamReader2XmlObject());
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
