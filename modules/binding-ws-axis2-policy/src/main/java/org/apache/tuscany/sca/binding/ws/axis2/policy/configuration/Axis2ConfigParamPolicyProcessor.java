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
package org.apache.tuscany.sca.binding.ws.axis2.policy.configuration;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 *
 * @version $Rev$ $Date$
 */
public class Axis2ConfigParamPolicyProcessor implements StAXArtifactProcessor<Axis2ConfigParamPolicy> {
    public static final QName AXIS2_CONFIG_PARAM_POLICY_QNAME = Axis2ConfigParamPolicy.NAME;
    public static final String PARAMETER = "parameter";
    public QName getArtifactType() {
        return AXIS2_CONFIG_PARAM_POLICY_QNAME;
    }
    
    public Axis2ConfigParamPolicyProcessor(ModelFactoryExtensionPoint modelFactories, Monitor monitor) {
    }

    public Axis2ConfigParamPolicy read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        Axis2ConfigParamPolicy policy = new Axis2ConfigParamPolicy();
        int event = reader.getEventType();
        QName name = null;
        OMElement parameterElement = null;
        String paramName = null;
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT : {
                    name = reader.getName();
                    if ( PARAMETER.equals(name.getLocalPart()) ) {
                        paramName = reader.getAttributeValue(null, Constants.NAME);
                        parameterElement = loadElement(reader);
                        policy.getParamElements().put(paramName, parameterElement);
                    }
                    break;
                }
            }
            
            if ( event == END_ELEMENT ) {
                if ( AXIS2_CONFIG_PARAM_POLICY_QNAME.equals(reader.getName()) ) {
                    break;
                } 
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
         
        return policy;
    }

    public void write(Axis2ConfigParamPolicy arg0, XMLStreamWriter arg1) throws ContributionWriteException,
                                                        XMLStreamException {
    }

    public Class<Axis2ConfigParamPolicy> getModelType() {
        return Axis2ConfigParamPolicy.class;
    }

    public void resolve(Axis2ConfigParamPolicy arg0, ModelResolver arg1) throws ContributionResolveException {

    }
    
    private OMElement loadElement(XMLStreamReader reader) throws XMLStreamException {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMElement head = fac.createOMElement(reader.getName());
        OMElement current = head;
        while (true) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    //since the axis2 code checks against a no namespace we need to generate accordingly
                    QName name = new QName(reader.getName().getLocalPart());
                    OMElement child = fac.createOMElement(name, current);

                    int count = reader.getNamespaceCount();
                    for (int i = 0; i < count; i++) {
                        String prefix = reader.getNamespacePrefix(i);
                        String ns = reader.getNamespaceURI(i);
                        child.declareNamespace(ns, prefix);
                    }

                    if(!"".equals(name.getNamespaceURI())) {
                        child.declareNamespace(name.getNamespaceURI(), name.getPrefix());
                    }

                    // add the attributes for this element
                    count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String ns = reader.getAttributeNamespace(i);
                        String prefix = reader.getAttributePrefix(i);
                        String qname = reader.getAttributeLocalName(i);
                        String value = reader.getAttributeValue(i);
                        
                        if (ns != null) {
                            child.addAttribute(qname, value, fac.createOMNamespace(ns, prefix));
                            child.declareNamespace(ns, prefix);
                        } else {
                            child.addAttribute(qname, value, null);
                        }
                    }
                    current = child;
                    break;
                case XMLStreamConstants.CDATA:
                    fac.createOMText(current, reader.getText());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    fac.createOMText(current, reader.getText());
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if ( current == head ) {
                        return head;
                    } else {
                        current = (OMElement)current.getParent();
                    }
            }
        }
    }
}
