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
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescription;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptions;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptionsFactory;
import org.osgi.framework.Constants;

/*
<?xml version="1.0" encoding="UTF-8"?>
<endpoint-descriptions xmlns="http://www.osgi.org/xmlns/rsa/v1.0.0">
    <endpoint-description>
        <property name="service.intents">
            <list>
                <value>SOAP</value>
                <value>HTTP</value>
            </list>
        </property>
        <property name="endpoint.id" value="http://ws.acme.com:9000/hello"/>
        <property name="objectClass" value="com.acme.Foo"/>
        <property name="endpoint.package.version.com.acme" value="4.2"/>
        <property name="service.imported.configs" value="com.acme"/>
        <property name="com.acme.ws.xml">
            <xml>
                <config xmlns="http://acme.com/defs">
                    <port>1029</port>
                    <host>www.acme.com</host>
                </config>
            </xml>
        </property>
    </endpoint-description>
</endpoint-descriptions>
*/
public class ServiceDescriptionsProcessor extends BaseStAXArtifactProcessor implements
    StAXArtifactProcessor<ServiceDescriptions> {
    private ServiceDescriptionsFactory factory;
    private StAXArtifactProcessor processor;

    public ServiceDescriptionsProcessor(ExtensionPointRegistry registry, StAXArtifactProcessor processor) {
        this.processor = processor;
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.factory = modelFactories.getFactory(ServiceDescriptionsFactory.class);
    }

    public ServiceDescriptions read(XMLStreamReader reader, ProcessorContext context) throws XMLStreamException,
        ContributionReadException {
        int event = reader.getEventType();
        ServiceDescriptions sds = null;
        ServiceDescription sd = null;
        String propertyName = null;
        String propertyType = "String";
        Object propertyValue = null;
        String propertyLiteral = null;
        boolean xml = false;
        boolean multiValued = false;
        while (true) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    if (ServiceDescriptions.SERVICE_DESCRIPTIONS_QNAME.equals(name)) {
                        sds = factory.createServiceDescriptions();
                    } else if (ServiceDescriptions.SERVICE_DESCRIPTION_QNAME.equals(name)) {
                        sd = factory.createServiceDescription();
                        sds.add(sd);
                    } else if ("property".equals(name.getLocalPart())) {
                        multiValued = false;
                        propertyName = reader.getAttributeValue(null, "name");
                        propertyType = reader.getAttributeValue(null, "value-type");
                        if (propertyType == null) {
                            propertyType = "String";
                        }
                        propertyLiteral = reader.getAttributeValue(null, "value");
                        //                        if (propertyLiteral == null) {
                        //                            propertyLiteral = reader.getElementText();
                        //                        }
                        if (propertyLiteral != null) {
                            propertyLiteral = propertyLiteral.trim();
                            propertyValue = getPropertyValue(reader, propertyName, propertyLiteral, propertyType);
                        }
                    } else if ("list".equals(name.getLocalPart())) {
                        if (propertyValue != null) {
                            throw new IllegalArgumentException("@value and <list> are both present");
                        }
                        propertyValue = new ArrayList<Object>();
                        multiValued = true;
                    } else if ("array".equals(name.getLocalPart())) {
                        if (propertyValue != null) {
                            throw new IllegalArgumentException("@value and <array> are both present");
                        }
                        propertyValue = new ArrayList<Object>();
                        multiValued = true;
                    } else if ("set".equals(name.getLocalPart())) {
                        if (propertyValue != null) {
                            throw new IllegalArgumentException("@value and <set> are both present");
                        }
                        propertyValue = new HashSet<Object>();
                        multiValued = true;
                    } else if ("xml".equals(name.getLocalPart())) {
                        xml = true;
                    } else if ("value".equals(name.getLocalPart())) {
                        propertyLiteral = reader.getElementText();
                        if (propertyLiteral != null) {
                            propertyLiteral = propertyLiteral.trim();
                            Object value = getPropertyValue(reader, propertyName, propertyLiteral, propertyType);
                            if (multiValued && (propertyValue instanceof Collection)) {
                                ((Collection)propertyValue).add(value);
                            } else if (propertyValue == null) {
                                propertyValue = value;
                            }
                        }
                    } else {
                        // FIXME: [rfeng] The rsa spec says the XML should be saved as String
                        Object value = processor.read(reader, context);
                        if (xml) {
                            if (multiValued && (propertyValue instanceof Collection)) {
                                ((Collection)propertyValue).add(value);
                            } else if (propertyValue == null) {
                                propertyValue = value;
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    name = reader.getName();
                    if (ServiceDescriptions.SERVICE_DESCRIPTION_QNAME.equals(name)) {
                        // Reset the sd
                        sd = null;
                    } else if (ServiceDescriptions.PROPERTY_QNAME.equals(name)) {
                        if (sd != null && propertyName != null) {
                            if (propertyValue == null) {
                                throw new IllegalArgumentException("No value is defined for " + propertyName);
                            }
                            sd.getProperties().put(propertyName, propertyValue);
                        }
                        propertyName = null;
                        propertyType = "String";
                        propertyValue = null;
                        multiValued = false;
                    } else if (ServiceDescriptions.XML_QNAME.equals(name)) {
                        xml = false;
                    } else if (ServiceDescriptions.SERVICE_DESCRIPTIONS_QNAME.equals(name)) {
                        return sds;
                    }
                    break;
            }
            if (reader.hasNext()) {
                event = reader.next();
            } else {
                return sds;
            }
        }
    }

    private Object getPropertyValue(XMLStreamReader reader, String propertyName, String propertyLiteral, String propType) {
        Object propertyValue = null;
        propertyValue = propertyLiteral;
        if ("Integer".equals(propType) || "int".equals(propType)) {
            propertyValue = Integer.valueOf(propertyLiteral);
        } else if ("Long".equals(propType) || "long".equals(propType)) {
            propertyValue = Long.valueOf(propertyLiteral);
        } else if ("Float".equals(propType) || "float".equals(propType)) {
            propertyValue = Float.valueOf(propertyLiteral);
        } else if ("Double".equals(propType) || "double".equals(propType)) {
            propertyValue = Double.valueOf(propertyLiteral);
        } else if ("Short".equals(propType) || "short".equals(propType)) {
            propertyValue = Short.valueOf(propertyLiteral);
        } else if ("Character".equals(propType) || "char".equals(propType)) {
            propertyValue = propertyLiteral.charAt(0);
        } else if ("Byte".equals(propType) || "byte".equals(propType)) {
            propertyValue = Byte.valueOf(propertyLiteral);
        } else if ("Boolean".equals(propType) || "boolean".equals(propType)) {
            propertyValue = Boolean.valueOf(propertyLiteral);
        }
        if (propertyName.endsWith(".intents")) {
            propertyValue = toQNames(reader, propertyLiteral);
        }
        if (Constants.OBJECTCLASS.equals(propertyName)) {
            return propertyLiteral.split("( |\t|\n|\r|\f)+");
        }
        return propertyValue;
    }

    /**
     * Convert ns1:e1 ns2:e2 to {http://ns1}e1 {http://ns2}e2
     * @param reader
     * @param value
     * @return
     */
    private String toQNames(XMLStreamReader reader, String value) {
        if (value == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (StringTokenizer tokens = new StringTokenizer(value, " \t\n\r\f,"); tokens.hasMoreTokens();) {
            QName qname = getQNameValue(reader, tokens.nextToken());
            sb.append(qname.toString()).append(' ');
        }
        return sb.toString().trim();
    }

    public QName getArtifactType() {
        return ServiceDescriptions.SERVICE_DESCRIPTIONS_QNAME;
    }

    public void write(ServiceDescriptions model, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {
        // TODO: To be implemented
    }

    public Class<ServiceDescriptions> getModelType() {
        return ServiceDescriptions.class;
    }

    public void resolve(ServiceDescriptions model, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
    }
}
