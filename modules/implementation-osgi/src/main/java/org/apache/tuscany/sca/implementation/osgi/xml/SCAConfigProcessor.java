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

package org.apache.tuscany.sca.implementation.osgi.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.SCAConfig;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptionsFactory;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/*
<?xml version="1.0" encoding="UTF-8"?>
<scact:sca-config targetNamespace="http://www.foocorp.com/definitions"
  xmlns:scact="http://www.osgi.org/xmlns/scact/v1.0.0" 
  xmlns:sca="http://docs.oasis-open.org/ns/opencsa/sca/200903"
  xmlns:foocorp="http://www.foocorp.com/definitions"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.osgi.org/xmlns/scact/v1.0.0 http://www.osgi.org/xmlns/scact/v1.0.0/sca-config.xsd">
 
  <sca:binding.ws name="com.foocorp.FooOrderWebServiceBinding"
    uri="http://www.foocorp.com/FooOrderService" requires="sca:soap.1_2" />
  <foocorp:binding.rmi name="com.foocorp.FooOrderRMIBinding"
    host="www.foocorp.com" port="8099" serviceName="FooOrderService" />
</scact:sca-config>
*/
public class SCAConfigProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<SCAConfig> {
    private static final QName SCA_CONFIG_QNAME = new QName("http://www.osgi.org/xmlns/scact/v1.0.0", "sca-config");
    private ServiceDescriptionsFactory factory;
    private StAXArtifactProcessor processor;

    public SCAConfigProcessor(ExtensionPointRegistry registry, StAXArtifactProcessor processor) {
        this.processor = processor;
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.factory = modelFactories.getFactory(ServiceDescriptionsFactory.class);
    }

    public SCAConfig read(XMLStreamReader reader, ProcessorContext context) throws XMLStreamException,
        ContributionReadException {
        int event = reader.getEventType();
        SCAConfig definitions = factory.createSCAConfig();
        String targetNamespace = null;
        while (true) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    if (SCA_CONFIG_QNAME.equals(name)) {
                        targetNamespace = reader.getAttributeValue(null, "targetNamespace");
                        definitions.setTargetNamespace(targetNamespace);
                    } else {
                        Object extension = processor.read(reader, context);
                        if (extension != null) {
                            if (extension instanceof Intent) {
                                Intent intent = (Intent)extension;
                                intent.setName(new QName(targetNamespace, intent.getName().getLocalPart()));
                                definitions.getIntents().add(intent);
                                for (Intent i : intent.getQualifiedIntents()) {
                                    i.setName(new QName(targetNamespace, i.getName().getLocalPart()));
                                }
                            } else if (extension instanceof PolicySet) {
                                PolicySet policySet = (PolicySet)extension;
                                policySet.setName(new QName(targetNamespace, policySet.getName().getLocalPart()));
                                definitions.getPolicySets().add(policySet);
                            } else if (extension instanceof Binding) {
                                Binding binding = (Binding)extension;
                                definitions.getBindings().add(binding);
                            } 
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    name = reader.getName();
                    if (SCA_CONFIG_QNAME.equals(name)) {
                        return definitions;
                    }
                    break;
            }
            if (reader.hasNext()) {
                event = reader.next();
            } else {
                return definitions;
            }
        }
    }

    public QName getArtifactType() {
        return SCA_CONFIG_QNAME;
    }

    public void write(SCAConfig definitions, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {

        writeStartDocument(writer,
                           SCA_CONFIG_QNAME.getNamespaceURI(),
                           SCA_CONFIG_QNAME.getLocalPart(),
                           new XAttr("targetNamespace", definitions.getTargetNamespace()));

        for (Intent policyIntent : definitions.getIntents()) {
            processor.write(policyIntent, writer, context);
        }

        for (PolicySet policySet : definitions.getPolicySets()) {
            processor.write(policySet, writer, context);
        }

        writeEndDocument(writer);
    }

    public void resolve(SCAConfig scaDefns, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        // start by adding all of the top level artifacts into the resolver as there
        // are many cross artifact references in a definitions file and we don't want
        // to be dependent on the order things appear

        List<Intent> intents = new ArrayList<Intent>();
        List<PolicySet> policySets = new ArrayList<PolicySet>();
        List<PolicySet> referredPolicySets = new ArrayList<PolicySet>();

        for (Intent intent : scaDefns.getIntents()) {
            intents.add(intent);
            resolver.addModel(intent, context);
            for (Intent i : intent.getQualifiedIntents()) {
                intents.add(i);
                resolver.addModel(i, context);
            }
        }

        for (PolicySet policySet : scaDefns.getPolicySets()) {
            if (policySet.getReferencedPolicySets().isEmpty()) {
                policySets.add(policySet);
            } else {
                referredPolicySets.add(policySet);
            }

            resolver.addModel(policySet, context);
        }

        // now resolve everything to ensure that any references between
        // artifacts are satisfied

        for (Intent policyIntent : intents)
            processor.resolve(policyIntent, resolver, context);

        for (PolicySet policySet : policySets)
            processor.resolve(policySet, resolver, context);

        for (PolicySet policySet : referredPolicySets)
            processor.resolve(policySet, resolver, context);

    }

    public Class<SCAConfig> getModelType() {
        return SCAConfig.class;
    }

}
