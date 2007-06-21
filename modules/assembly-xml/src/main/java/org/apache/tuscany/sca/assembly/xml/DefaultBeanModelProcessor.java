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

package org.apache.tuscany.sca.assembly.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;

public class DefaultBeanModelProcessor extends BaseArtifactProcessor implements StAXArtifactProcessor {

    private QName artifactType;
    private Class<Implementation> beanModelType;
    private BeanInfo beanInfo;
    private Map<String, PropertyDescriptor> propertyDescriptors = new HashMap<String, PropertyDescriptor>();

    public DefaultBeanModelProcessor(AssemblyFactory assemblyFactory,
                                       PolicyFactory policyFactory,
                                       QName artifactType,
                                       Class<Implementation> beanModelType) {
        super(assemblyFactory, policyFactory, null);
        this.artifactType = artifactType;
        this.beanModelType = beanModelType;
        
        // Introspect the bean model class
        try {
            beanInfo = Introspector.getBeanInfo(beanModelType);
            PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
            for (int i =0; i < pd.length; i++) {
                propertyDescriptors.put(pd[i].getName(), pd[i]);
            }
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object read(XMLStreamReader reader) throws ContributionReadException {

        try {

            // Read an element
            Object bean = beanModelType.newInstance();

            // Initialize the bean properties with the attributes found in the
            // XML element
            for (int i = 0, n = reader.getAttributeCount(); i < n; i++) {
                String attributeName = reader.getAttributeLocalName(i);
                PropertyDescriptor pd = propertyDescriptors.get(attributeName);
                if (pd != null) {
                    String value = reader.getAttributeValue(i);
                    pd.getWriteMethod().invoke(bean, value);
                }
            }

            // Read policies
            if (bean instanceof PolicySetAttachPoint) {
                readPolicies((PolicySetAttachPoint)bean, reader);
            } else if (bean instanceof IntentAttachPoint) {
                readIntents((IntentAttachPoint)bean, reader);
            }

            // TODO read extension elements
            
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && artifactType.equals(reader.getName())) {
                    break;
                }
            }
            return bean;

        } catch (Exception e) {
            throw new ContributionReadException(e);
        }
    }

    public void write(Object bean, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <bean>
            writer.writeStartElement(artifactType.getNamespaceURI(), artifactType.getLocalPart());

            // Write the bean properties as attributes
            for (PropertyDescriptor pd: propertyDescriptors.values()) {
                if (pd.getPropertyType() == String.class) {
                    String value = (String)pd.getReadMethod().invoke(bean);
                    writer.writeAttribute(pd.getName(), value);
                }
            }
            
            writer.writeEndElement();

        } catch (Exception e) {
            throw new ContributionWriteException(e);
        }
    }

    public void resolve(Object bean, ModelResolver resolver) throws ContributionResolveException {
    }

    public QName getArtifactType() {
        return artifactType;
    }

    public Class<?> getModelType() {
        return beanModelType;
    }

}
