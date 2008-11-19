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

package org.apache.tuscany.sca.core.databinding.module;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.sca.core.databinding.transformers.Array2ArrayTransformer;
import org.apache.tuscany.sca.core.databinding.transformers.CallableReference2XMLStreamReader;
import org.apache.tuscany.sca.core.databinding.transformers.CallableReferenceDataBinding;
import org.apache.tuscany.sca.core.databinding.transformers.CallableReferenceXMLAdapter;
import org.apache.tuscany.sca.core.databinding.transformers.Exception2ExceptionTransformer;
import org.apache.tuscany.sca.core.databinding.transformers.Input2InputTransformer;
import org.apache.tuscany.sca.core.databinding.transformers.Output2OutputTransformer;
import org.apache.tuscany.sca.core.databinding.transformers.XMLStreamReader2CallableReference;
import org.apache.tuscany.sca.core.databinding.wire.DataBindingRuntimeWireProcessor;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.Group2GroupTransformer;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.databinding.jaxb.XMLAdapterExtensionPoint;
import org.apache.tuscany.sca.interfacedef.FaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSFaultExceptionMapper;
import org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSJavaInterfaceProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;
import org.osoa.sca.CallableReference;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);

        XMLAdapterExtensionPoint xmlAdapterExtensionPoint = registry.getExtensionPoint(XMLAdapterExtensionPoint.class);
        xmlAdapterExtensionPoint.addAdapter(CallableReference.class, CallableReferenceXMLAdapter.class);
        FaultExceptionMapper faultExceptionMapper = new JAXWSFaultExceptionMapper(dataBindings, xmlAdapterExtensionPoint);
        
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        Input2InputTransformer input2InputTransformer = new Input2InputTransformer();
        input2InputTransformer.setMediator(mediator);
        transformers.addTransformer(input2InputTransformer, true);

        Output2OutputTransformer output2OutputTransformer = new Output2OutputTransformer();
        output2OutputTransformer.setMediator(mediator);
        transformers.addTransformer(output2OutputTransformer, true);

        Exception2ExceptionTransformer exception2ExceptionTransformer = new Exception2ExceptionTransformer(mediator, faultExceptionMapper);
        transformers.addTransformer(exception2ExceptionTransformer, false);
        
        Array2ArrayTransformer array2ArrayTransformer = new Array2ArrayTransformer();
        array2ArrayTransformer.setMediator(mediator);
        transformers.addTransformer(array2ArrayTransformer, true);

        Group2GroupTransformer group2GroupTransformer = new Group2GroupTransformer();
        group2GroupTransformer.setMediator(mediator);
        transformers.addTransformer(group2GroupTransformer, true);
        
        dataBindings.addDataBinding(new CallableReferenceDataBinding());
        transformers.addTransformer(new CallableReference2XMLStreamReader(), true);
        transformers.addTransformer(new XMLStreamReader2CallableReference(), false);

        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        JavaInterfaceFactory javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);

        // [rfeng] The JAX-WS processor should come before the Databinding processor to make sure @WebService
        // is honored as Remoteable
        javaFactory.addInterfaceVisitor(new JAXWSJavaInterfaceProcessor(dataBindings, faultExceptionMapper, xmlAdapterExtensionPoint));

        javaFactory.addInterfaceVisitor(new DataBindingJavaInterfaceProcessor(dataBindings));

        RuntimeWireProcessorExtensionPoint wireProcessorExtensionPoint = registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        if (wireProcessorExtensionPoint != null) {
            wireProcessorExtensionPoint.addWireProcessor(new DataBindingRuntimeWireProcessor(mediator, dataBindings, faultExceptionMapper));
        }
        
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
