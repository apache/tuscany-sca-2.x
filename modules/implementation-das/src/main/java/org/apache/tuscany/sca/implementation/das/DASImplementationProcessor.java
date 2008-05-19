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
package org.apache.tuscany.sca.implementation.das;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.data.engine.ConnectionInfoArtifactProcessor;
import org.apache.tuscany.sca.data.engine.config.ConnectionInfo;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.monitor.Monitor;


/**
 * Implements a StAX artifact processor for DAS implementations.
 * 
 * The artifact processor is responsible for processing <implementation.das>
 * elements in SCA assembly XML composite files and populating the DAS
 * implementation model, resolving its references to other artifacts in the SCA
 * contribution, and optionally write the model back to SCA assembly XML.
 * 
 * @version $Rev$ $Date$
 */
public class DASImplementationProcessor implements StAXArtifactProcessor<DASImplementation> {
    private static final QName IMPLEMENTATION_DAS = new QName(Constants.SCA10_TUSCANY_NS, "implementation.das");
    
    private DASImplementationFactory dasFactory;
    
    private final AssemblyFactory assemblyFactory;
    private final JavaInterfaceFactory javaFactory;
    private Monitor monitor;
    private StAXArtifactProcessor<ConnectionInfo> connectionInfoProcessor;
    
    public DASImplementationProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        this.monitor = monitor;
        this.dasFactory = new DefaultDASImplementationFactory(assemblyFactory, javaFactory);        
        this.connectionInfoProcessor = new ConnectionInfoArtifactProcessor(modelFactories, this.monitor);
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_DAS;
    }

    public Class<DASImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return DASImplementation.class;
    }

    /*
     * <component name="CompanyDataComponent">
     *   <implementation.das config="/CompanyConfig.xml" dataAccessType="rdb">
     *      <connectionInfo>
     *         <connectionProperties 
     *          driverClass="org.apache.derby.jdbc.EmbeddedDriver" 
     *          databaseURL="jdbc:derby:target/test-classes/dastest; create = true" 
     *          loginTimeout="600000"/>
     *      </connectionInfo>
     *   </implementation.data>
     * </component>
     */
    public DASImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        assert IMPLEMENTATION_DAS.equals(reader.getName());

        // Read an <implementation.das> element

        // Read the das config file attribute.
        // This is das configuration side file to use
        String config = reader.getAttributeValue(null, "config");

        // Read the data access type attribute
        // This is the type of data store in use (e.g RDB, XML, etc)
        String dataAccessType = reader.getAttributeValue(null, "dataAccessType");

        // Create an initialize the DAS implementation model
        DASImplementation implementation = dasFactory.createDASImplementation();
        implementation.setConfig(config);
        implementation.setDataAccessType(dataAccessType);
        implementation.setUnresolved(true);

        while (true) {
            int event = reader.next();
            switch (event) {

            case START_ELEMENT:
                if (ConnectionInfoArtifactProcessor.CONNECTION_INFO.equals(reader.getName())) {

                    // Read connection info
                    ConnectionInfo connectionInfo = (ConnectionInfo) connectionInfoProcessor.read(reader);
                    implementation.setConnectionInfo(connectionInfo);
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (IMPLEMENTATION_DAS.equals(reader.getName())) {
                    return implementation;
                }
                break;
            }
        }
    }

    public void resolve(DASImplementation impl, ModelResolver resolver) throws ContributionResolveException {
        if( impl != null && impl.isUnresolved()) {
            //resolve component type
            mergeComponentType(resolver, impl);
                        
            //set current implementation resolved 
            impl.setUnresolved(false);
        }
    }

    public void write(DASImplementation implementation, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        writer.writeStartElement(IMPLEMENTATION_DAS.getNamespaceURI(), IMPLEMENTATION_DAS.getLocalPart());
        
        if (implementation.getConfig() != null) {
            writer.writeAttribute("config", implementation.getConfig());
        }
        if (implementation.getDataAccessType() != null) {
            writer.writeAttribute("dataAccessType", implementation.getDataAccessType());
        }
        
        if (implementation.getConnectionInfo() != null) { 
            connectionInfoProcessor.write(implementation.getConnectionInfo(), writer);
        }
        
        writer.writeEndElement();
    }
    
    /**
     * Merge the componentType from introspection and external file
     * @param resolver
     * @param impl
     */
    private void mergeComponentType(ModelResolver resolver, DASImplementation impl) {
        // FIXME: Need to clarify how to merge
        ComponentType componentType = getComponentType(resolver, impl);
        if (componentType != null && !componentType.isUnresolved()) {
            /*
            Map<String, Reference> refMap = new HashMap<String, Reference>();
            for (Reference ref : impl.getReferences()) {
                refMap.put(ref.getName(), ref);
            }
            for (Reference reference : componentType.getReferences()) {
                refMap.put(reference.getName(), reference);
            }
            impl.getReferences().clear();
            impl.getReferences().addAll(refMap.values());

            // Try to match references by type
            Map<String, JavaElementImpl> refMembers = impl.getReferenceMembers();
            for (Reference ref : impl.getReferences()) {
                if (ref.getInterfaceContract() != null) {
                    Interface i = ref.getInterfaceContract().getInterface();
                    if (i instanceof JavaInterface) {
                        Class<?> type = ((JavaInterface)i).getJavaClass();
                        if (!refMembers.containsKey(ref.getName())) {
                            JavaElementImpl e = getMemeber(impl, ref.getName(), type);
                            if (e != null) {
                                refMembers.put(ref.getName(), e);
                            }
                        }
                    }
                }
            }*/

            Map<String, Service> serviceMap = new HashMap<String, Service>();
            for (Service svc : impl.getServices()) {
                if(svc != null) {
                    serviceMap.put(svc.getName(), svc);    
                }
            }
            for (Service service : componentType.getServices()) {                
                serviceMap.put(service.getName(), service);
            }
            impl.getServices().clear();
            impl.getServices().addAll(serviceMap.values());

            Map<String, Property> propMap = new HashMap<String, Property>();
            for (Property prop : impl.getProperties()) {
                propMap.put(prop.getName(), prop);
            }
        }
    }

    private String getFileName(String filePath) {
        int pos = filePath.lastIndexOf(".");
        
        return filePath.substring(0, pos);
        
    }
    
    private ComponentType getComponentType(ModelResolver resolver, DASImplementation impl) {
        String dasConfig = this.getFileName(impl.getConfig());
        String componentTypeURI = dasConfig.replace('.', '/') + ".componentType";
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        componentType.setURI(componentTypeURI);
        componentType = resolver.resolveModel(ComponentType.class, componentType);
        if (!componentType.isUnresolved()) {
            return componentType;
        }
        return null;
    }
}
