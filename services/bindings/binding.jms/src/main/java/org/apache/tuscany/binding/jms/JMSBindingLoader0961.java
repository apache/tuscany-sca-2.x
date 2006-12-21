/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.jms;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.osoa.sca.annotations.Scope;

/**
 * Loader for handling <binding.jms> elements based on the 0.96 draft 1 spec.
 */
@Scope("COMPOSITE")
public class JMSBindingLoader0961 extends LoaderExtension<JMSBinding> {

    public static final QName BINDING_JMS =
        new QName("http://tuscany.apache.org/xmlns/binding/jms/1.0-SNAPSHOT", "binding.jms");

    public static final List<String> VALID_CORRELATION_SCHEMES =
        Arrays.asList(new String[] {"requestmsgidtocorrelid", "requestcorrelidtocorrelid", "none"});

    public JMSBindingLoader0961(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return BINDING_JMS;
    }

    public JMSBinding load(CompositeComponent parent,
                           ModelObject modelObject,
                           XMLStreamReader reader,
                           DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {

        JMSBinding jmsBinding = new JMSBinding();

        String uri = reader.getAttributeValue(null, "uri");
        if (uri != null && uri.length() > 0) {
            parseURI(jmsBinding, uri);
        }

        String correlationScheme = reader.getAttributeValue(null, "correlationScheme");
        if (correlationScheme != null && correlationScheme.length() > 0) {
            if (VALID_CORRELATION_SCHEMES.contains(correlationScheme.toLowerCase())) {
                jmsBinding.setCorrelationScheme(correlationScheme);
            } else {
                throw new LoaderException("invalid correlationScheme: " + correlationScheme);
            }
        }

        String initialContextFactory = reader.getAttributeValue(null, "initialContextFactory");
        if (initialContextFactory != null && initialContextFactory.length() > 0) {
            jmsBinding.setInitialContextFactoryName(initialContextFactory);
        }

        String jndiProviderURL = reader.getAttributeValue(null, "JNDIProviderURL");
        if (jndiProviderURL != null && jndiProviderURL.length() > 0) {
            jmsBinding.setJNDIProviderURL(jndiProviderURL);
        }

        String connectionFactory = reader.getAttributeValue(null, "connectionFactory");
        if (connectionFactory != null && connectionFactory.length() > 0) {
            jmsBinding.setConnectionFactoryName(connectionFactory);
        }

        String activationSpec = reader.getAttributeValue(null, "activationSpec");
        if (activationSpec != null && activationSpec.length() > 0) {
            jmsBinding.setActivationSpecName(activationSpec);
        }

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    String elementName = reader.getName().getLocalPart();
                    if ("destination".equals(elementName)) {
                        parseDestination(reader, jmsBinding);
                    } else if ("response".equals(elementName)) {
                        parseResponse(reader, jmsBinding);
                    } else if ("headers".equals(elementName)) {
                        parseHeaders(reader, jmsBinding);
                    } else if ("operationAndDataBinding".equals(elementName)) {
                        parseOperationAndDataBinding(reader, jmsBinding);
                    } else if ("operation".equals(elementName)) {
                        parseOperation(reader, jmsBinding);
                    } else if ("resourceAdapter".equals(elementName)) {
                        parseResourceAdapter(reader, jmsBinding);
                    }
                    reader.next();
                    break;

                case END_ELEMENT:
                    QName x = reader.getName();
                    if (x.equals(BINDING_JMS)) {
                        return jmsBinding;
                    }
                    throw new RuntimeException("Incomplete binding.jms definition");
            }
        }
    }

    protected void parseResponse(XMLStreamReader reader, JMSBinding jmsBinding) {
        // TODO Auto-generated method stub

    }

    protected void parseResourceAdapter(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    protected void parseOperation(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    protected void parseOperationAndDataBinding(XMLStreamReader reader, JMSBinding jmsBinding)
        throws XMLStreamException {
        String name = reader.getAttributeValue(null, "name");
        String use = reader.getAttributeValue(null, "use");
        if (name != null && name.length() > 0) {
            if ("request".equalsIgnoreCase(use)) {
                jmsBinding.setRequestOperationAndDatabindingName(name);
            } else if ("response".equalsIgnoreCase(use)) {
                jmsBinding.setResponseOperationAndDatabindingName(name);
            } else {
                jmsBinding.setRequestOperationAndDatabindingName(name);
                jmsBinding.setResponseOperationAndDatabindingName(name);
            }
        }
    }

    protected void parseHeaders(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    protected void parseDestination(XMLStreamReader reader, JMSBinding jmsBinding) throws XMLStreamException {
        String name = reader.getAttributeValue(null, "name");
        if (name != null && name.length() > 0) {
            jmsBinding.setDestinationName(name);
        }
        String type = reader.getAttributeValue(null, "type");
        if (type != null && type.length() > 0) {
            if ("queue".equalsIgnoreCase(type)) {
                jmsBinding.setDestinationType(JMSBinding.DESTINATION_TYPE_QUEUE);
            } else if ("topic".equalsIgnoreCase("type")) {
                jmsBinding.setDestinationType(JMSBinding.DESTINATION_TYPE_TOPIC);
            } else {
                throw new RuntimeException("invalid destination type: " + type);
            }
        }
        String create = reader.getAttributeValue(null, "create");
        if (create != null && create.length() > 0) {
            jmsBinding.setCreateDestination(create);
        }
    }

    protected void parseURI(JMSBinding jmsBinding, String uri) {
        // TODO Auto-generated method stub
    }
}
