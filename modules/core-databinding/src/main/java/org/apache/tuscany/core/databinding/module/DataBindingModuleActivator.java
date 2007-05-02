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

package org.apache.tuscany.core.databinding.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.core.databinding.transformers.Exception2ExceptionTransformer;
import org.apache.tuscany.core.databinding.transformers.Input2InputTransformer;
import org.apache.tuscany.core.databinding.transformers.Output2OutputTransformer;
import org.apache.tuscany.core.databinding.wire.DataBindingWirePostProcessor;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.databinding.DefaultTransformerExtensionPoint;
import org.apache.tuscany.databinding.TransformerExtensionPoint;
import org.apache.tuscany.databinding.impl.DefaultMediator;
import org.apache.tuscany.databinding.impl.Group2GroupTransformer;
import org.apache.tuscany.databinding.javabeans.DOMNode2JavaBeanTransformer;
import org.apache.tuscany.databinding.javabeans.JavaBean2DOMNodeTransformer;
import org.apache.tuscany.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.databinding.xml.DOMDataBinding;
import org.apache.tuscany.databinding.xml.InputSource2Node;
import org.apache.tuscany.databinding.xml.InputSource2SAX;
import org.apache.tuscany.databinding.xml.InputStream2Node;
import org.apache.tuscany.databinding.xml.InputStream2SAX;
import org.apache.tuscany.databinding.xml.Node2OutputStream;
import org.apache.tuscany.databinding.xml.Node2String;
import org.apache.tuscany.databinding.xml.Node2Writer;
import org.apache.tuscany.databinding.xml.Node2XMLStreamReader;
import org.apache.tuscany.databinding.xml.Reader2Node;
import org.apache.tuscany.databinding.xml.Reader2SAX;
import org.apache.tuscany.databinding.xml.SAX2DOMPipe;
import org.apache.tuscany.databinding.xml.Source2ResultTransformer;
import org.apache.tuscany.databinding.xml.StreamDataPipe;
import org.apache.tuscany.databinding.xml.String2Node;
import org.apache.tuscany.databinding.xml.String2SAX;
import org.apache.tuscany.databinding.xml.String2XMLStreamReader;
import org.apache.tuscany.databinding.xml.Writer2ReaderDataPipe;
import org.apache.tuscany.databinding.xml.XMLGroupDataBinding;
import org.apache.tuscany.databinding.xml.XMLStreamReader2Node;
import org.apache.tuscany.databinding.xml.XMLStreamReader2SAX;
import org.apache.tuscany.databinding.xml.XMLStreamReader2String;
import org.apache.tuscany.databinding.xml.XMLStringDataBinding;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        DefaultDataBindingExtensionPoint dataBindingRegistryImpl = new DefaultDataBindingExtensionPoint();
        map.put(DataBindingExtensionPoint.class, dataBindingRegistryImpl);
        DefaultTransformerExtensionPoint transformerRegistryImpl = new DefaultTransformerExtensionPoint();
        transformerRegistryImpl.setDataBindingRegistry(dataBindingRegistryImpl);
        map.put(TransformerExtensionPoint.class, transformerRegistryImpl);
        return map;
    }

    public void start(ExtensionPointRegistry registry) {
        DataBindingExtensionPoint dataBindingRegistry = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformerRegistry = registry.getExtensionPoint(TransformerExtensionPoint.class);

        DefaultMediator mediator = new DefaultMediator(dataBindingRegistry, transformerRegistry);
        Input2InputTransformer input2InputTransformer = new Input2InputTransformer();
        input2InputTransformer.setMediator(mediator);

        Output2OutputTransformer output2OutputTransformer = new Output2OutputTransformer();
        output2OutputTransformer.setMediator(mediator);

        Exception2ExceptionTransformer exception2ExceptionTransformer = new Exception2ExceptionTransformer();
        exception2ExceptionTransformer.setMediator(mediator);

        transformerRegistry.addTransformer(input2InputTransformer);
        transformerRegistry.addTransformer(output2OutputTransformer);
        transformerRegistry.addTransformer(exception2ExceptionTransformer);

        JavaInterfaceIntrospectorExtensionPoint javaIntrospectorExtensionPoint = registry
            .getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        javaIntrospectorExtensionPoint.addExtension(new DataBindingJavaInterfaceProcessor(mediator
            .getDataBindings()));

        WirePostProcessorRegistry wirePostProcessorRegistry = registry
            .getExtensionPoint(WirePostProcessorRegistry.class);
        ComponentManager componentManager = registry.getExtensionPoint(ComponentManager.class);
        wirePostProcessorRegistry.register(new DataBindingWirePostProcessor(componentManager, mediator));
        
        DOMDataBinding domDataBinding = new DOMDataBinding();
        domDataBinding.setDataBindingRegistry(dataBindingRegistry);
        dataBindingRegistry.addDataBinding(domDataBinding);
        XMLStringDataBinding xmlStringDataBinding = new XMLStringDataBinding();
        xmlStringDataBinding.setDataBindingRegistry(dataBindingRegistry);
        dataBindingRegistry.addDataBinding(xmlStringDataBinding);
        XMLGroupDataBinding xmlGroupDataBinding = new XMLGroupDataBinding();
        xmlGroupDataBinding.setDataBindingRegistry(dataBindingRegistry);
        dataBindingRegistry.addDataBinding(xmlGroupDataBinding);
        JavaBeansDataBinding javaBeansDataBinding = new JavaBeansDataBinding();
        javaBeansDataBinding.setDataBindingRegistry(dataBindingRegistry);
        dataBindingRegistry.addDataBinding(javaBeansDataBinding);

        Group2GroupTransformer group2GroupTransformer= new Group2GroupTransformer();
        group2GroupTransformer.setMediator(mediator);
        transformerRegistry.addTransformer(group2GroupTransformer);
        
        transformerRegistry.addTransformer(new InputSource2Node());
        transformerRegistry.addTransformer(new InputSource2SAX());
        transformerRegistry.addTransformer(new InputStream2Node());
        transformerRegistry.addTransformer(new InputStream2SAX());

        transformerRegistry.addTransformer(new DOMNode2JavaBeanTransformer());
        transformerRegistry.addTransformer(new Node2OutputStream());
        transformerRegistry.addTransformer(new Node2String());
        transformerRegistry.addTransformer(new Node2Writer());
        transformerRegistry.addTransformer(new Node2XMLStreamReader());

        transformerRegistry.addTransformer(new JavaBean2DOMNodeTransformer());
        transformerRegistry.addTransformer(new Reader2Node());

        transformerRegistry.addTransformer(new Reader2SAX());
        transformerRegistry.addTransformer(new SAX2DOMPipe());
        
        transformerRegistry.addTransformer(new Source2ResultTransformer());
        transformerRegistry.addTransformer(new StreamDataPipe());
        transformerRegistry.addTransformer(new String2Node());
        transformerRegistry.addTransformer(new String2SAX());
        transformerRegistry.addTransformer(new String2XMLStreamReader());
        transformerRegistry.addTransformer(new Writer2ReaderDataPipe());

        transformerRegistry.addTransformer(new XMLStreamReader2Node());
        transformerRegistry.addTransformer(new XMLStreamReader2SAX());
        transformerRegistry.addTransformer(new XMLStreamReader2String());
    }
    
    public void stop(ExtensionPointRegistry registry) {
    }
}
