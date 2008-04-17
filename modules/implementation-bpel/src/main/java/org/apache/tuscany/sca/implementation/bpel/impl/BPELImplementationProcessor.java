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
package org.apache.tuscany.sca.implementation.bpel.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.implementation.bpel.BPELFactory;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.BPELProcessDefinition;
import org.apache.tuscany.sca.implementation.bpel.DefaultBPELFactory;

/**
 * Implements a STAX artifact processor for BPEL implementations.
 * 
 * The artifact processor is responsible for processing <implementation.bpel>
 * elements in SCA assembly XML composite files and populating the BPEL
 * implementation model, resolving its references to other artifacts in the SCA
 * contribution, and optionally write the model back to SCA assembly XML.
 * 
 *  @version $Rev$ $Date$
 */
public class BPELImplementationProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<BPELImplementation> {
    private static final QName IMPLEMENTATION_BPEL = new QName(Constants.SCA10_NS, "implementation.bpel");
    
    private AssemblyFactory assemblyFactory;
    private BPELFactory bpelFactory;
    
    public BPELImplementationProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        this.bpelFactory = new DefaultBPELFactory(modelFactories);
    }

    public QName getArtifactType() {
        // Returns the QName of the XML element processed by this processor
        return IMPLEMENTATION_BPEL;
    }

    public Class<BPELImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return BPELImplementation.class;
    }

    public BPELImplementation read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        assert IMPLEMENTATION_BPEL.equals(reader.getName());
        
        // Read an <implementation.bpel> element

        // Read the process attribute. 
        QName process = getAttributeValueNS(reader, "process");


        // Create an initialize the BPEL implementation model
        BPELImplementation implementation = bpelFactory.createBPELImplementation();
        implementation.setProcess(process);
        implementation.setUnresolved(true);
        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && IMPLEMENTATION_BPEL.equals(reader.getName())) {
                break;
            }
        }
        
        return implementation;
    }

    public void resolve(BPELImplementation impl, ModelResolver resolver) throws ContributionResolveException {
        if( impl != null && impl.isUnresolved()) {
            BPELProcessDefinition processDefinition = resolveBPELProcessDefinition(impl, resolver);
            if(processDefinition.isUnresolved()) {
                throw new ContributionResolveException("Can't find BPEL Process : " + processDefinition.getName());
            }
            
            impl.setProcessDefinition(processDefinition);
            
            //resolve component type
            mergeComponentType(resolver, impl);
                        
            //set current implementation resolved 
            impl.setUnresolved(false);
        }
        
    }

    public void write(BPELImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
        //FIXME Implement
    }

    private BPELProcessDefinition resolveBPELProcessDefinition(BPELImplementation impl, ModelResolver resolver) throws ContributionResolveException {
        QName processName = impl.getProcess();
        BPELProcessDefinition processDefinition = this.bpelFactory.createBPELProcessDefinition();
        processDefinition.setName(processName);
        processDefinition.setUnresolved(true);
        
        return resolver.resolveModel(BPELProcessDefinition.class, processDefinition);
    }
    
    
    /**
     * Merge the componentType from introspection and external file
     * @param resolver
     * @param impl
     */
    private void mergeComponentType(ModelResolver resolver, BPELImplementation impl) {
        // FIXME: Need to clarify how to merge
        ComponentType componentType = getComponentType(resolver, impl);
        if (componentType != null && !componentType.isUnresolved()) {
            
            Map<String, Reference> refMap = new HashMap<String, Reference>();
            for (Reference ref : impl.getReferences()) {
                refMap.put(ref.getName(), ref);
            }
            for (Reference reference : componentType.getReferences()) {
            	//set default dataBinding to DOM to help on reference invocation
            	reference.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
                refMap.put(reference.getName(), reference);
            }
            impl.getReferences().clear();
            impl.getReferences().addAll(refMap.values());

            Map<String, Service> serviceMap = new HashMap<String, Service>();
            for (Service svc : impl.getServices()) {
                if(svc != null) {
                    serviceMap.put(svc.getName(), svc);    
                }
            }
            for (Service service : componentType.getServices()) {
                //set default dataBinding to DOM
                service.getInterfaceContract().getInterface().resetDataBinding(DOMDataBinding.NAME);
                
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


    private ComponentType getComponentType(ModelResolver resolver, BPELImplementation impl) {
        String bpelProcessURI = impl.getProcessDefinition().getURI().toString();
        String componentTypeURI = bpelProcessURI.replace(".bpel", ".componentType");
        ComponentType componentType = assemblyFactory.createComponentType();
        componentType.setUnresolved(true);
        componentType.setURI(componentTypeURI);
        componentType = resolver.resolveModel(ComponentType.class, componentType);
        if (!componentType.isUnresolved()) {
            return componentType;
        }
        return null;
    }

    private QName getAttributeValueNS(XMLStreamReader reader, String attribute) {
        String fullValue = reader.getAttributeValue(null, "process");
        if (fullValue.indexOf(":") < 0)
            throw new BPELProcessException("Attribute " + attribute + " with value " + fullValue +
                    " in your composite should be prefixed (process=\"prefix:name\").");
        String prefix = fullValue.substring(0, fullValue.indexOf(":"));
        String name = fullValue.substring(fullValue.indexOf(":") + 1);
        String nsUri = reader.getNamespaceContext().getNamespaceURI(prefix);
        if (nsUri == null)
            throw new BPELProcessException("Attribute " + attribute + " with value " + fullValue +
                    " in your composite has un unrecognized namespace prefix.");
        return new QName(nsUri, name, prefix);
    }

}
