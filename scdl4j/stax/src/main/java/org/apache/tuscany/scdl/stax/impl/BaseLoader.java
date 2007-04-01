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

package org.apache.tuscany.scdl.stax.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.model.AbstractProperty;
import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.ConstrainingType;
import org.apache.tuscany.assembly.model.Property;
import org.apache.tuscany.policy.model.Intent;
import org.apache.tuscany.policy.model.IntentAttachPoint;
import org.apache.tuscany.policy.model.PolicyFactory;
import org.apache.tuscany.policy.model.PolicySet;
import org.apache.tuscany.policy.model.PolicySetAttachPoint;
import org.apache.tuscany.sca.idl.Operation;
import org.apache.tuscany.scdl.stax.Constants;
import org.apache.tuscany.scdl.stax.LoaderRegistry;

/**
 * A test handler to test the usability of the assembly model API when loading
 * SCDL
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseLoader implements Constants {

    protected AssemblyFactory factory;
    protected PolicyFactory policyFactory;
    protected LoaderRegistry registry;

    public BaseLoader(AssemblyFactory factory, PolicyFactory policyFactory, LoaderRegistry registry) {

        this.factory = factory;
        this.policyFactory = policyFactory;
        this.registry = registry;
    }

    protected String getString(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }

    protected QName getQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getQNameValue(reader, qname);
    }

    protected QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }

    protected boolean getBoolean(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        return Boolean.valueOf(value);
    }

    protected List<QName> getQNames(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value != null) {
            List<QName> qnames = new ArrayList<QName>();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                qnames.add(getQName(reader, tokens.nextToken()));
            }
            return qnames;
        } else {
            return Collections.emptyList();
        }
    }

    protected void readIntents(IntentAttachPoint attachPoint, XMLStreamReader reader) {
    	readIntents(attachPoint, null, reader);
    }
    
    protected void readIntents(IntentAttachPoint attachPoint, Operation operation, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, Constants.REQUIRES);
        if (value != null) {
            List<Intent> requiredIntents = attachPoint.getRequiredIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
    			if (operation != null) {
    				intent.getOperations().add(operation);
    			}
                requiredIntents.add(intent);
            }
        }
    }

    protected void readPolicies(PolicySetAttachPoint attachPoint, XMLStreamReader reader) {
    	readPolicies(attachPoint, null, reader);
    }

    protected void readPolicies(PolicySetAttachPoint attachPoint, Operation operation, XMLStreamReader reader) {
    	readIntents(attachPoint, operation, reader);
    	
        String value = reader.getAttributeValue(null, Constants.POLICY_SETS);
        if (value != null) {
            List<PolicySet> policySets = attachPoint.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);
    			if (operation != null) {
    				policySet.getOperations().add(operation);
    			}
                policySets.add(policySet);
            }
        }
    }

    protected ConstrainingType getConstrainingType(XMLStreamReader reader) {
        QName constrainingTypeName = getQName(reader, "constrainingType");
        if (constrainingTypeName != null) {
            ConstrainingType constrainingType = factory.createConstrainingType();
            constrainingType.setName(constrainingTypeName);
            constrainingType.setUnresolved(true);
            return constrainingType;
        } else {
            return null;
        }
    }

    protected void readAbstractProperty(AbstractProperty prop, XMLStreamReader reader) {
        prop.setName(getString(reader, "name"));
        prop.setMany(getBoolean(reader, "many"));
        prop.setMustSupply(getBoolean(reader, "mustSupply"));
        prop.setXSDElement(getQName(reader, "element"));
        prop.setXSDType(getQName(reader, "type"));
    }

    protected void readProperty(Property prop, XMLStreamReader reader) {
        readAbstractProperty(prop, reader);
    }

    protected boolean nextChildElement(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT) {
                return false;
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Advance the stream to the next END_ELEMENT event skipping any nested
     * content.
     * 
     * @param reader the reader to advance
     * @throws XMLStreamException if there was a problem reading the stream
     */
    protected void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (depth == 0) {
                    return;
                }
                depth--;
            }
        }
    }

}
