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
package org.apache.tuscany.core.deployer.federation;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.component.JavaPhysicalComponentDefinition;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.marshaller.ModelMarshaller;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;

/**
 * Federated deployer responsible for deploying Java components.
 * 
 * @version $Rev$ $Date$
 */
public class JavaComponentDeployer extends FederatedDeployer<JavaPhysicalComponentDefinition, JavaAtomicComponent> {

    /** QName of the serialized Java physical component definition.. */
    private static final QName MESSAGE_TYPE =
        new QName("http://tuscany.apache.org/xmlns/1.0-SNAPSHOT", "component-java");

    /**
     * Unmarshalls the XML stream to a component definition.
     * 
     * @param content XML content stream.
     * @return Physical component definition.
     * @throws MarshalException If unable to marshall the component definition.
     */
    @Override
    protected QName getQualifiedName() {
        return MESSAGE_TYPE;
    }

    /**
     * Builds the component from the physical component definition.
     * 
     * @param componentDefinition Component definition.
     * @return Component instance.
     * @throws BuilderException If unable to build the component.
     */
    @Override
    protected JavaAtomicComponent buildComponent(JavaPhysicalComponentDefinition componentDefinition)
        throws BuilderException {

        PhysicalComponentBuilderRegistry builderRegistry = getBuilderRegistry();
        PhysicalComponentBuilder<JavaPhysicalComponentDefinition, JavaAtomicComponent> builder =
            builderRegistry.getBuilder(JavaPhysicalComponentDefinition.class);
        return builder.build(componentDefinition);

    }

    /**
     * Returns the qualified name interested in by this deployer.
     * 
     * @return The qualified name of the document element.
     */
    @Override
    protected JavaPhysicalComponentDefinition unmarshallDefinition(XMLStreamReader content) throws MarshalException {

        ModelMarshallerRegistry marshallerRegistry = getMarshallerRegistry();
        ModelMarshaller<JavaPhysicalComponentDefinition> marshaller =
            marshallerRegistry.getMarshaller(JavaPhysicalComponentDefinition.class);
        return marshaller.unmarshall(content);

    }

}
