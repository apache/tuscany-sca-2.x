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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescription;
import org.apache.tuscany.sca.implementation.osgi.ServiceDescriptions;
import org.apache.tuscany.sca.implementation.osgi.impl.ServiceDescriptionImpl;
import org.apache.tuscany.sca.implementation.osgi.impl.ServiceDescriptionsImpl;

/*
<?xml version="1.0" encoding="UTF-8"?>
<service-descriptions xmlns="http://www.osgi.org/xmlns/sd/v1.0.0">
    <service-description>
        <provide interface="com.iona.soa.pojo.hello.HelloService"/>
        <property name="service.intents">SOAP HTTP</property>
        <property name="osgi.remote.configuration.type">pojo</property>
        <property name="osgi.remote.configuration.pojo.address">
            http://localhost:9000/hello
        </property>
    </service-description>
    <service-description>
        <provide interface="com.iona.soa.pojo.hello.GreeterService"/>
        <property name="service.intents">SOAP HTTP</property>
        <property name="osgi.remote.configuration.type">pojo</property>
        <property name="osgi.remote.configuration.pojo.address">
            http://localhost:9005/greeter
        </property>
    </service-description>
</service-descriptions>
*/
public class ServiceDescriptionsProcessor implements StAXArtifactProcessor<ServiceDescriptions> {

    public ServiceDescriptions read(XMLStreamReader reader) throws XMLStreamException {
        int event = reader.getEventType();
        ServiceDescriptions sds = new ServiceDescriptionsImpl();
        ServiceDescription sd = null;
        while (true) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    if (ServiceDescription.SERVICE_DESCRIPTION_QNAME.equals(name)) {
                        sd = new ServiceDescriptionImpl();
                        sds.add(sd);
                    } else if ("provide".equals(name.getLocalPart())) {
                        String interfaceName = reader.getAttributeValue(null, "interface");
                        if (interfaceName != null) {
                            sd.getInterfaces().add(interfaceName);
                        }
                    } else if ("property".equals(name.getLocalPart())) {
                        String propName = reader.getAttributeValue(null, "name");
                        String propValue = reader.getAttributeValue(null, "value");
                        String propType = reader.getAttributeValue(null, "type");
                        if (propType == null) {
                            propType = "String";
                        }
                        if (propValue == null) {
                            propValue = reader.getElementText();
                        }
                        if (propValue != null) {
                            propValue = propValue.trim();
                        }
                        Object prop = propValue;
                        if ("Integer".equals(propType)) {
                            prop = Integer.valueOf(propValue);
                        } else if ("Long".equals(propType)) {
                            prop = Long.valueOf(propValue);
                        } else if ("Float".equals(propType)) {
                            prop = Float.valueOf(propValue);
                        } else if ("Double".equals(propType)) {
                            prop = Double.valueOf(propValue);
                        } else if ("Short".equals(propType)) {
                            prop = Short.valueOf(propValue);
                        } else if ("Character".equals(propType)) {
                            prop = propValue.charAt(0);
                        } else if ("Byte".equals(propType)) {
                            prop = Byte.valueOf(propValue);
                        } else if ("Boolean".equals(propType)) {
                            prop = Boolean.valueOf(propValue);
                        }
                        sd.getProperties().put(propName, prop);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    name = reader.getName();
                    if (ServiceDescription.SERVICE_DESCRIPTION_QNAME.equals(name)) {
                        // Reset the sd
                        sd = null;
                    }
                    if (ServiceDescription.SERVICE_DESCRIPTIONS_QNAME.equals(name)) {
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

    public QName getArtifactType() {
        return ServiceDescription.SERVICE_DESCRIPTIONS_QNAME;
    }

    public void write(ServiceDescriptions model, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {
        // TODO: To be implemented
    }

    public Class<ServiceDescriptions> getModelType() {
        return ServiceDescriptions.class;
    }

    public void resolve(ServiceDescriptions model, ModelResolver resolver) throws ContributionResolveException {
        // TODO: To be implemented
    }
}
