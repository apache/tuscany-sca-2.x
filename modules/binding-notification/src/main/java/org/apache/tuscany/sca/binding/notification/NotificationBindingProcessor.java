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
package org.apache.tuscany.sca.binding.notification;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWireException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * @version $Rev$ $Date$
 */
public class NotificationBindingProcessor implements StAXArtifactProcessor<NotificationBinding> {

    protected static final QName BINDING_NOTIFICATION =
        new QName(Constants.SCA10_NS, "binding.notification");
    
    private NotificationBindingFactory bindingFactory;

    public NotificationBindingProcessor(AssemblyFactory assemblyFactory,
                                        PolicyFactory policyFactory,
                                        NotificationBindingFactory bindingFactory) {
        this.bindingFactory = bindingFactory;
    }

    public QName getArtifactType() {
        return BINDING_NOTIFICATION;
    }
    
    public Class<NotificationBinding> getModelType() {
        return NotificationBinding.class;
    }

    public NotificationBinding read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        assert BINDING_NOTIFICATION.equals(reader.getName());
        String bindingUri = reader.getAttributeValue(null, "uri");
        String name = reader.getAttributeValue(null, "name");
        String ntm = reader.getAttributeValue(null, "ntm");
        String notificationType = reader.getAttributeValue(null, "notificationType");

        NotificationBinding binding = bindingFactory.createNotificationBinding();
        if (name != null) {
            binding.setName(name);
        }
        if (bindingUri != null) {
            binding.setURI(bindingUri);
        }
        if (ntm != null) {
            binding.setNtmAddress(ntm);
        }
        if (notificationType != null) {
            try {
                binding.setNotificationType(new URI(notificationType));
            } catch(URISyntaxException e) {
                throw new ContributionReadException(e);
            }
        }
        return binding;
    }

    public void write(NotificationBinding notificationBinding, XMLStreamWriter writer)
            throws ContributionWriteException, XMLStreamException {
    }

    public void resolve(NotificationBinding notificationBinding, ModelResolver resolver) throws ContributionResolveException {
    }

    public void wire(NotificationBinding notificationBinding) throws ContributionWireException {
    }
}
