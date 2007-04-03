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
package org.apache.tuscany.core.loader;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.IntentMap;
import org.apache.tuscany.spi.model.IntentName;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.PolicySet;
import org.apache.tuscany.spi.model.PolicySetReference;
import org.apache.tuscany.spi.model.Qualifier;
import org.apache.tuscany.spi.model.WSPolicyAttachment;

/**
 * Loads a PolicySet definition from an SCDL file.
 *
 * @version $Rev$ $Date$
 */
public class PolicySetLoader extends LoaderExtension<PolicySet> {

    private static final String WSPOLICY_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/09/policy";

    private static final QName POLICYSET = new QName(SCA_NS, "policySet");

    private static final QName INTENTMAP = new QName(SCA_NS, "intentMap");

    private static final QName QUALIFIER = new QName(SCA_NS, "qualifier");

    private static final QName POLICYSETREFERENCE = new QName(SCA_NS, "policySetReference");

    private static final QName WSPOLICYATTACHMENT = new QName(WSPOLICY_NAMESPACE, "PolicyAttachment");

    @Constructor
    public PolicySetLoader(@Reference LoaderRegistry registry) {
        super(registry);

    }

    @Override
    public QName getXMLType() {
        return POLICYSET;
    }

    public PolicySet load(ModelObject object, XMLStreamReader reader,
                          DeploymentContext deploymentContext)
        throws XMLStreamException {
        assert POLICYSET.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String provides = reader.getAttributeValue(null, "provides");
        String appliesTo = reader.getAttributeValue(null, "appliesTo");
        PolicySet policySet = new PolicySet(new QName(SCA_NS, name), parseIntentName(provides));
        String[] appliesToArtifact = split(appliesTo);
        for (String artifact : appliesToArtifact) {
            policySet.addAppliedArtifacts(new QName(SCA_NS, artifact));
        }
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    if (INTENTMAP.equals(qname)) {
                        policySet.addIntentMap(loadIntentMap(reader, deploymentContext));
                    } else if (POLICYSETREFERENCE.equals(qname)) {
                        policySet.addPolicySetReference(loadPolicyReference(reader, deploymentContext));
                    } else if (WSPOLICYATTACHMENT.equals(qname)) {
                        policySet.addWsPolicyAttachment(loadWSPolicyAttachment(reader, deploymentContext));
                    }

                    reader.next();
                    break;
                case END_ELEMENT:
                    if (reader.getName().equals(POLICYSET)) {
                        return policySet;
                    }
                    break;
            }
        }

    }

    private PolicySetReference loadPolicyReference(XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException {
        assert POLICYSETREFERENCE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        LoaderUtil.skipToEndElement(reader);
        return new PolicySetReference(new QName(SCA_NS, name));
    }

    private IntentMap loadIntentMap(XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException {
        assert INTENTMAP.equals(reader.getName());
        String defaultIntentAttr = reader.getAttributeValue(null, "default");
        String provides = reader.getAttributeValue(null, "provides");
        IntentMap intentMap = new IntentMap(defaultIntentAttr, java.util.Arrays.asList(split(provides)));
        //parentPolicySet.addIntentMap(intentMap);

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    if (QUALIFIER.equals(qname)) {
                        intentMap.addQualifier(loadQualifier(reader, deploymentContext));
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (reader.getName().equals(INTENTMAP)) {
                        return intentMap;
                    }
            }
        }

    }

    private Qualifier loadQualifier(XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException {
        assert QUALIFIER.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        Qualifier qualifier = new Qualifier(name);
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    if (INTENTMAP.equals(qname)) {
                        qualifier.setIntentMap(loadIntentMap(reader, deploymentContext));
                    } else if (WSPOLICYATTACHMENT.equals(qname)) {
                        qualifier.addWsPolicyAttachment(loadWSPolicyAttachment(reader, deploymentContext));
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (reader.getName().equals(QUALIFIER)) {
                        return qualifier;
                    }
            }
        }

    }

    private WSPolicyAttachment loadWSPolicyAttachment(XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException {
        return new WSPolicyAttachment();
    }

    /**
     * Split a string to string array separated by " "
     */
    private static String[] split(String string) {
        if (string == null) {
            return new String[0];
        }
        String[] intents = string.split("[ ]+");
        return intents;
    }

    private static List<IntentName> parseIntentName(String attributes) {
        String[] intents = split(attributes);
        List<IntentName> result = new ArrayList<IntentName>(intents.length);
        for (String intent : intents) {
            result.add(new IntentName(intent));
        }
        return result;
    }

}
