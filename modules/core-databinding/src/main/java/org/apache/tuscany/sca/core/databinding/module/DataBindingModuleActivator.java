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

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.sca.core.databinding.processor.WrapperJavaInterfaceProcessor;
import org.apache.tuscany.sca.core.databinding.wire.DataBindingRuntimeWireProcessor;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.jaxws.JAXWSJavaInterfaceProcessor;
import org.apache.tuscany.sca.interfacedef.java.jaxws.WebServiceInterfaceProcessor;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {
        /*
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        */

        /*
        Input2InputTransformer input2InputTransformer = new Input2InputTransformer(registry);
        transformers.addTransformer(input2InputTransformer, false);

        Output2OutputTransformer output2OutputTransformer = new Output2OutputTransformer(registry);
        transformers.addTransformer(output2OutputTransformer, false);

        Exception2ExceptionTransformer exception2ExceptionTransformer = new Exception2ExceptionTransformer(registry);
        transformers.addTransformer(exception2ExceptionTransformer, false);

        Array2ArrayTransformer array2ArrayTransformer = new Array2ArrayTransformer(registry);
        transformers.addTransformer(array2ArrayTransformer, false);

        Group2GroupTransformer group2GroupTransformer = new Group2GroupTransformer(registry);
        transformers.addTransformer(group2GroupTransformer, false);
        */

//        dataBindings.addDataBinding(new CallableReferenceDataBinding());
//        transformers.addTransformer(new CallableReference2XMLStreamReader(), true);
//        transformers.addTransformer(new XMLStreamReader2CallableReference(), false);

        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        JavaInterfaceFactory javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        // Add the WebServiceInterfaceProcessor to mark the interface remotable
        //javaFactory.addInterfaceVisitor(new WebServiceInterfaceProcessor());
        // Introspect the data types
        //javaFactory.addInterfaceVisitor(new DataBindingJavaInterfaceProcessor(registry));
        //javaFactory.addInterfaceVisitor(new JAXWSJavaInterfaceProcessor(registry));
        //javaFactory.addInterfaceVisitor(new WrapperJavaInterfaceProcessor(registry));

        RuntimeWireProcessorExtensionPoint wireProcessorExtensionPoint = registry.getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        if (wireProcessorExtensionPoint != null) {
            wireProcessorExtensionPoint.addWireProcessor(new DataBindingRuntimeWireProcessor(registry));
        }
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
