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

package org.apache.tuscany.core.databinding.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.core.databinding.transformers.Exception2ExceptionTransformer;
import org.apache.tuscany.core.databinding.transformers.Input2InputTransformer;
import org.apache.tuscany.core.databinding.transformers.Output2OutputTransformer;
import org.apache.tuscany.core.databinding.wire.DataBindingWirePostProcessor;
import org.apache.tuscany.databinding.impl.DataBindingRegistryImpl;
import org.apache.tuscany.databinding.impl.Group2GroupTransformer;
import org.apache.tuscany.databinding.impl.MediatorImpl;
import org.apache.tuscany.databinding.impl.TransformerRegistryImpl;
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
import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;
import org.apache.tuscany.spi.bootstrap.ModuleActivator;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.databinding.TransformerRegistry;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        DataBindingRegistryImpl dataBindingRegistryImpl = new DataBindingRegistryImpl();
        map.put(DataBindingRegistry.class, dataBindingRegistryImpl);
        TransformerRegistryImpl transformerRegistryImpl = new TransformerRegistryImpl();
        transformerRegistryImpl.setDataBindingRegistry(dataBindingRegistryImpl);
        map.put(TransformerRegistry.class, transformerRegistryImpl);
        MediatorImpl mediatorImpl = new MediatorImpl();
        mediatorImpl.setDataBindingRegistry(dataBindingRegistryImpl);
        mediatorImpl.setTransformerRegistry(transformerRegistryImpl);
        map.put(Mediator.class, mediatorImpl);
        return map;
    }

    public void start(ExtensionPointRegistry registry) {
        TransformerRegistry transformerRegistry = registry.getExtensionPoint(TransformerRegistry.class);
        Mediator mediator = registry.getExtensionPoint(Mediator.class);
        Input2InputTransformer input2InputTransformer = new Input2InputTransformer();
        input2InputTransformer.setMediator(mediator);

        Output2OutputTransformer output2OutputTransformer = new Output2OutputTransformer();
        output2OutputTransformer.setMediator(mediator);

        Exception2ExceptionTransformer exception2ExceptionTransformer = new Exception2ExceptionTransformer();
        exception2ExceptionTransformer.setMediator(mediator);

        transformerRegistry.registerTransformer(input2InputTransformer);
        transformerRegistry.registerTransformer(output2OutputTransformer);
        transformerRegistry.registerTransformer(exception2ExceptionTransformer);

        JavaInterfaceIntrospectorExtensionPoint javaIntrospectorExtensionPoint = registry
            .getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        javaIntrospectorExtensionPoint.addExtension(new DataBindingJavaInterfaceProcessor(mediator
            .getDataBindingRegistry()));

        WirePostProcessorRegistry wirePostProcessorRegistry = registry
            .getExtensionPoint(WirePostProcessorRegistry.class);
        ComponentManager componentManager = registry.getExtensionPoint(ComponentManager.class);
        wirePostProcessorRegistry.register(new DataBindingWirePostProcessor(componentManager, mediator));
        
        DataBindingRegistry dataBindingRegistry = registry.getExtensionPoint(DataBindingRegistry.class);
        dataBindingRegistry.register(new DOMDataBinding());
        dataBindingRegistry.register(new XMLStringDataBinding());
        dataBindingRegistry.register(new XMLGroupDataBinding());
        dataBindingRegistry.register(new JavaBeansDataBinding());

        Group2GroupTransformer group2GroupTransformer= new Group2GroupTransformer();
        group2GroupTransformer.setMediator(mediator);
        transformerRegistry.registerTransformer(group2GroupTransformer);
        
        transformerRegistry.registerTransformer(new InputSource2Node());
        transformerRegistry.registerTransformer(new InputSource2SAX());
        transformerRegistry.registerTransformer(new InputStream2Node());
        transformerRegistry.registerTransformer(new InputStream2SAX());

        transformerRegistry.registerTransformer(new DOMNode2JavaBeanTransformer());
        transformerRegistry.registerTransformer(new Node2OutputStream());
        transformerRegistry.registerTransformer(new Node2String());
        transformerRegistry.registerTransformer(new Node2Writer());
        transformerRegistry.registerTransformer(new Node2XMLStreamReader());

        transformerRegistry.registerTransformer(new JavaBean2DOMNodeTransformer());
        transformerRegistry.registerTransformer(new Reader2Node());

        transformerRegistry.registerTransformer(new Reader2SAX());
        transformerRegistry.registerTransformer(new SAX2DOMPipe());
        
        transformerRegistry.registerTransformer(new Source2ResultTransformer());
        transformerRegistry.registerTransformer(new StreamDataPipe());
        transformerRegistry.registerTransformer(new String2Node());
        transformerRegistry.registerTransformer(new String2SAX());
        transformerRegistry.registerTransformer(new String2XMLStreamReader());
        transformerRegistry.registerTransformer(new Writer2ReaderDataPipe());

        transformerRegistry.registerTransformer(new XMLStreamReader2Node());
        transformerRegistry.registerTransformer(new XMLStreamReader2SAX());
        transformerRegistry.registerTransformer(new XMLStreamReader2String());
    }
    
    public void stop(ExtensionPointRegistry registry) {
    }
}
