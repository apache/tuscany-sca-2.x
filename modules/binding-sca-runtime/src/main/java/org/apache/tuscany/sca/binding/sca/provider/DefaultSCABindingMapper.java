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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.builder.BindingBuilder;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.BuilderExtensionPoint;
import org.apache.tuscany.sca.common.xml.stax.StAXHelper;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclarationParser;
import org.apache.tuscany.sca.policy.PolicySubject;
import org.apache.tuscany.sca.provider.ProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.DomainRegistryFactory;
import org.apache.tuscany.sca.runtime.DomainRegistryFactoryExtensionPoint;
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
    protected StAXArtifactProcessor processor;
    protected BindingBuilder builder;
    protected QName mappedBinding;
    private Binding bindingTemplate;
    private boolean remotable;

    public DefaultSCABindingMapper(ExtensionPointRegistry registry, Map<String, String> attributes) {
        this.registry = registry;
        if (attributes != null) {
            String qname = attributes.get("mappedBinding");
            if (qname != null) {
                mappedBinding = ServiceDeclarationParser.getQName(qname);
            }
        }
        if (mappedBinding == null) {
            String qname =
                System.getProperty("org.apache.tuscany.sca.binding.sca.provider.SCABindingMapper.mappedBinding");
            if (qname != null) {
                mappedBinding = ServiceDeclarationParser.getQName(qname);
            } else {
                // By default, mapping to binding.ws or if thats not available then binding.rmi
                mappedBinding = new QName(Base.SCA11_TUSCANY_NS, "binding.hazelcast");
                if (registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class).getProcessor(mappedBinding) == null) {
                    mappedBinding = new QName(Base.SCA11_NS, "binding.ws");
                    if (registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class).getProcessor(mappedBinding) == null) {
                        mappedBinding = new QName(Base.SCA11_TUSCANY_NS, "binding.rmi");
                    }
                }
            }
        }

        providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        StAXArtifactProcessorExtensionPoint processors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processor = processors.getProcessor(mappedBinding);
        if (processor == null) {
            logger.warning("Mapped binding for binding.sca is not supported: " + mappedBinding);
        }

        try {
            if (processor != null) {
                bindingTemplate = createDelegatingBinding();
                ProviderFactory providerFactory = providerFactories.getProviderFactory(bindingTemplate.getClass());
                if (providerFactory == null) {
                    logger.warning("Mapped binding for binding.sca is not supported: " + mappedBinding);
                    processor = null;
                }
            }
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
        BuilderExtensionPoint builders = registry.getExtensionPoint(BuilderExtensionPoint.class);
        builder = builders.getBindingBuilder(mappedBinding);
        remotable = isDistributed() && processor != null;
    }

    // FIXME: [rfeng] This is a HACK to check if we should make binding.sca remotable
    // by checking if we have distributed domain registry present
    private boolean isDistributed() {
        DomainRegistryFactoryExtensionPoint factories =
            registry.getExtensionPoint(DomainRegistryFactoryExtensionPoint.class);
        List<DomainRegistryFactory> list = factories.getDomainRegistryFactories();
        if (list.size() == 1) {
            String[] schemes = list.get(0).getSupportedSchemes();
            if (Arrays.asList(schemes).contains("local")) {
                return false;
            }
        }
        return true;
    }

    public RuntimeEndpoint map(RuntimeEndpoint endpoint) {
        if (processor == null) {
            return null;
        }
        // create a copy of the endpoint  but with the web service binding in
        RuntimeEndpoint ep = null;
        try {
            ep = (RuntimeEndpoint)endpoint.clone();
        } catch (Exception ex) {
            // we know we can clone endpoint references
        }

        Binding binding = map(endpoint.getBinding());
        ep.setBinding(binding);
        if (builder != null) {
            builder.build(ep.getComponent(), ep.getService(), binding, new BuilderContext(registry));
        }
        return ep;
    }

    public RuntimeEndpointReference map(RuntimeEndpointReference endpointReference) {
        if (processor == null) {
            return null;
        }
        // create a copy of the endpoint  but with the web service binding in
        RuntimeEndpointReference epr = null;
        try {
            epr = (RuntimeEndpointReference)endpointReference.clone();
        } catch (Exception ex) {
            // we know we can clone endpoint references
        }

        Binding binding = map(endpointReference.getBinding());
        epr.setBinding(binding);

        // epr.setTargetEndpoint(map((RuntimeEndpoint)epr.getTargetEndpoint()));

        if (builder != null) {
            builder.build(epr.getComponent(), epr.getReference(), binding, new BuilderContext(registry));
        }

        return epr;
    }

    protected Binding map(Binding scaBinding) {
        try {
            Binding binding = createDelegatingBinding();
            binding.setName(scaBinding.getName());
            binding.setURI(scaBinding.getURI());
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

    private Binding createDelegatingBinding() throws XMLStreamException, ContributionReadException {
        if (bindingTemplate != null) {
            try {
                return (Binding)bindingTemplate.clone();
            } catch (CloneNotSupportedException e) {
                // Ignore
            }
        }
        // This is a hack to create an instance of the binding using the XML QName
        StringBuffer xml = new StringBuffer();
        xml.append("<").append(mappedBinding.getLocalPart()).append(" xmlns:b=\"").append(mappedBinding
            .getNamespaceURI()).append("\"/>");

        StAXHelper staxHelper = StAXHelper.getInstance(registry);
        XMLStreamReader reader = staxHelper.createXMLStreamReader(new StringReader(xml.toString()));
        reader.nextTag();
        Binding binding = (Binding)processor.read(reader, new ProcessorContext(registry));
        return binding;
    }

    public boolean isRemotable() {
        return remotable;
    }

}
