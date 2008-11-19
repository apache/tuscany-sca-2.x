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

package org.apache.tuscany.sca.implementation.resource.impl;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.resource.ResourceImplementation;
import org.apache.tuscany.sca.implementation.resource.ResourceImplementationFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;

/**
 * Factory for the resource implementation model.
 *
 * @version $Rev$ $Date$
 */
public class ResourceImplementationFactoryImpl implements ResourceImplementationFactory {
    
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    
    public ResourceImplementationFactoryImpl(ModelFactoryExtensionPoint modelFactories) {
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
    }

    public ResourceImplementation createResourceImplementation() {
        return new ResourceImplementationImpl(assemblyFactory, javaFactory);
    }

}
