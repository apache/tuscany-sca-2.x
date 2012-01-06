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

package org.apache.tuscany.sca.binding.sca.provider;

import java.io.StringReader;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclarationParser;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.provider.ProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.SCABindingMapper;
import org.apache.tuscany.sca.runtime.DomainRegistry;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Default implementation of SCABindingMapper
 */
public class DefaultSCABindingMapper implements SCABindingMapper {
    private final static Logger logger = Logger.getLogger(DefaultSCABindingMapper.class.getName());
    protected ExtensionPointRegistry registry;
    protected ProviderFactoryExtensionPoint providerFactories;
    protected StAXArtifactProcessorExtensionPoint processors;
    protected QName defaultMappedBinding;
    protected QName defaultLocalBinding;
    protected boolean alwaysDistributed;
    
    // Test to look at supporting concurrent binding.sca delegations
    protected QName [] activeDelegations = {new QName(Base.SCA11_TUSCANY_NS, "binding.local"),
                                            new QName(Base.SCA11_NS, "binding.ws")};

    public DefaultSCABindingMapper(ExtensionPointRegistry registry, Map<String, String> attributes) {
        this.registry = registry;
        providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        defaultMappedBinding = getDefaultMappedBinding(attributes);
        defaultLocalBinding = new QName(Base.SCA11_TUSCANY_NS, "binding.local");
        if (attributes != null && "true".equalsIgnoreCase(attributes.get("alwaysDistributed"))) {
            alwaysDistributed = true;
        }
    }

    protected QName getDefaultMappedBinding(Map<String, String> attributes) {
        QName defaultMappedBinding = null;
        if (attributes != null) {
            String qname = attributes.get("mappedBinding");
            if (qname != null) {
                defaultMappedBinding = ServiceDeclarationParser.getQName(qname);
            }
        }
        if (defaultMappedBinding == null) {
            String qname =
                System.getProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding");
            if (qname != null) {
                defaultMappedBinding = ServiceDeclarationParser.getQName(qname);
            } else {
                // By default, mapping to binding.ws or if thats not available then binding.rmi
                defaultMappedBinding = new QName(Base.SCA11_TUSCANY_NS, "binding.hazelcast");
                if (!isBindingSupported(defaultMappedBinding)) {
                    defaultMappedBinding = new QName(Base.SCA11_NS, "binding.ws");
                    if (!isBindingSupported(defaultMappedBinding)) {
                        defaultMappedBinding = new QName(Base.SCA11_TUSCANY_NS, "binding.rmi");
                    }
                }
            }
        }
        return defaultMappedBinding;
    }

    private BindingBuilder<?> getBindingBuilder(QName binding) {
        if (isBindingSupported(binding)) {
            BuilderExtensionPoint builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
            BindingBuilder<?> builder = builders.getBindingBuilder(binding);
            return builder;
        } else {
            return null;
        }
    }

    private boolean isBindingSupported(QName binding) {
        if (binding == null) {
            return false;
        }
        // Check if there is a processor
        StAXArtifactProcessor<?> processor = processors.getProcessor(binding);
        if (processor == null) {
            // logger.warning("Mapped binding for binding.sca is not supported: " + binding);
            return false;
        }

        try {
            if (processor != null) {
                // Futher check if the provider is present
                Binding bindingTemplate = createDelegatingBinding(binding);
                ProviderFactory<?> providerFactory = providerFactories.getProviderFactory(bindingTemplate.getClass());
                if (providerFactory == null) {
                    // logger.warning("Mapped binding for binding.sca is not supported: " + binding);
                    processor = null;
                    return false;
                }
            }
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
        return true;
    }

    public RuntimeEndpoint map(RuntimeEndpoint endpoint) {
        QName bindingType = chooseBinding(endpoint);
        if (!isBindingSupported(bindingType)) {
            logger.warning("Mapped binding for binding.sca is not supported: " + bindingType);
            return null;
        }

        // create a copy of the endpoint ready to set the remote binding. The original endpoint
        // retains binding.sca
        RuntimeEndpoint ep = null;
        try {
            ep = (RuntimeEndpoint)endpoint.clone();
        } catch (Exception ex) {
            // we know we can clone endpoint references
        }

        Binding binding = map(endpoint.getBinding(), bindingType);
        ep.setBinding(binding);
        BindingBuilder builder = getBindingBuilder(bindingType);
        if (builder != null) {
            builder.build(ep.getComponent(), ep.getService(), binding, new BuilderContext(registry), false);
        }
        return ep;
    }

    public RuntimeEndpointReference map(RuntimeEndpointReference endpointReference) {
        QName bindingType = chooseBinding(endpointReference);
        if (!isBindingSupported(bindingType)) {
            logger.warning("Mapped binding for binding.sca is not supported: " + bindingType);
            return null;
        }

        // create a copy of the endpoint reference ready to set the remote binding. The original
        // endpoint reference reatins binding.sca
        RuntimeEndpointReference epr = null;
        try {
            epr = (RuntimeEndpointReference)endpointReference.clone();
        } catch (Exception ex) {
            // we know we can clone endpoint references
        }

        Binding binding = map(endpointReference.getBinding(), bindingType);
        epr.setBinding(binding);

        // epr.setTargetEndpoint(map((RuntimeEndpoint)epr.getTargetEndpoint()));

        BindingBuilder builder = getBindingBuilder(bindingType);
        if (builder != null) {
            builder.build(epr.getComponent(), epr.getReference(), binding, new BuilderContext(registry), false);
        }

        return epr;
    }

    protected Binding map(Binding scaBinding, QName newBindingType) {
        try {
            Binding binding = createDelegatingBinding(newBindingType);
            binding.setName(scaBinding.getName());
            if (((SCABinding)scaBinding).getDelegateBindingURI() != null){
                // if this is an SCA binding that's been resolved via the registry then 
                // the URI of the target service is delegate specific and is stored
                // in this new slot in the binding model 
                binding.setURI(((SCABinding)scaBinding).getDelegateBindingURI());
            } else {
                binding.setURI(scaBinding.getURI());
            }
            binding.setOperationSelector(scaBinding.getOperationSelector());
            binding.setRequestWireFormat(scaBinding.getRequestWireFormat());
            binding.setResponseWireFormat(scaBinding.getResponseWireFormat());
            if (binding instanceof PolicySubject && scaBinding instanceof PolicySubject) {
                PolicySubject subject1 = (PolicySubject)binding;
                PolicySubject subject2 = (PolicySubject)scaBinding;
                subject1.getPolicySets().addAll(subject2.getPolicySets());
                subject1.getRequiredIntents().addAll(subject2.getRequiredIntents());
            }
            return binding;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }

    }

    protected Binding createDelegatingBinding(QName bindingType) throws XMLStreamException, ContributionReadException {
        StAXArtifactProcessor processor = processors.getProcessor(bindingType);
        if (processor == null) {
            logger.warning("Mapped binding for binding.sca is not supported: " + bindingType);
        }

        StringBuffer xml = new StringBuffer();
        xml.append("<").append(bindingType.getLocalPart()).append(" xmlns:b=\"").append(bindingType.getNamespaceURI())
            .append("\"/>");

        StAXHelper staxHelper = StAXHelper.getInstance(registry);
        XMLStreamReader reader = staxHelper.createXMLStreamReader(new StringReader(xml.toString()));
        reader.nextTag();
        Binding binding = (Binding)processor.read(reader, new ProcessorContext(registry));

        return binding;
    }

    /*
    public boolean isRemotable(RuntimeEndpoint endpoint) {
        return supportsDistributedSCA && isBindingSupported(chooseBinding(endpoint));
    }

    public boolean isRemotable(RuntimeEndpointReference endpointReference) {
        return supportsDistributedSCA && isBindingSupported(chooseBinding(endpointReference));
    }
    */

    /**
     * Choose the physical binding for service-side remotable binding.sca 
     * @param endpoint
     * @return
     */
    protected QName chooseBinding(RuntimeEndpoint endpoint) {
        DomainRegistry domainRegistry = endpoint.getCompositeContext().getEndpointRegistry();
        boolean distributed = alwaysDistributed || domainRegistry.isDistributed();
        InterfaceContract interfaceContract = endpoint.getService().getInterfaceContract();
        if(interfaceContract != null
           && interfaceContract.getInterface() != null
           && interfaceContract.getInterface().isRemotable()
           && distributed
           && isBindingSupported(defaultMappedBinding)) {
        	return defaultMappedBinding;
        }
        
        return defaultLocalBinding;
    }

    /**
     * Choose the physical binding for reference-side remotable binding.sca
     * @param endpointReference
     * @return
     */
    protected QName chooseBinding(RuntimeEndpointReference endpointReference) {
        DomainRegistry domainRegistry = endpointReference.getCompositeContext().getEndpointRegistry();
        boolean distributed = alwaysDistributed || domainRegistry.isDistributed();
        if(endpointReference.getTargetEndpoint().isRemote()) {
            RuntimeComponentReference ref = (RuntimeComponentReference)endpointReference.getReference();
            if(ref.getInterfaceContract() != null && !ref.getInterfaceContract().getInterface().isRemotable()) {
                throw new ServiceRuntimeException("Reference interface not remotable for component: "
                        + endpointReference.getComponent().getName()
                        + " and reference: "
                        + ref.getName());
            }
            
            if(distributed && isBindingSupported(defaultMappedBinding)) {
                return defaultMappedBinding;
            }
        }
        
        return defaultLocalBinding;
    }
    
    public QName[] getActiveDelegations(){
        return activeDelegations;
    }

}
