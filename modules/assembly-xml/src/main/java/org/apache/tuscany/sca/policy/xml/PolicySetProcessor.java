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

package org.apache.tuscany.sca.policy.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tuscany.sca.common.xml.xpath.XPathHelper;
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ContributionReadException;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ContributionWriteException;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.Qualifier;

/**
 * Processor for handling XML models of PolicySet definitions
 *
 * @version $Rev$ $Date$
 */
public class PolicySetProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<PolicySet>,
    PolicyConstants {

    private PolicyFactory policyFactory;
    private StAXArtifactProcessor<Object> extensionProcessor;
    private XPathHelper xpathHelper;
    // private XPathFactory xpathFactory;
    private Monitor monitor;

    public PolicySetProcessor(ExtensionPointRegistry registry,
                              StAXArtifactProcessor<Object> extensionProcessor,
                              Monitor monitor) {
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.extensionProcessor = extensionProcessor;
        this.monitor = monitor;
        this.xpathHelper = XPathHelper.getInstance(registry);
        // this.xpathFactory = modelFactories.getFactory(XPathFactory.class);
    }

    /*
    public PolicySetProcessor(PolicyFactory policyFactory,
                              StAXArtifactProcessor<Object> extensionProcessor,
                              Monitor monitor) {
        this.policyFactory = policyFactory;
        this.extensionProcessor = extensionProcessor;
        this.monitor = monitor;
    }
    */

    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "policy-xml-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      ex);
            monitor.problem(problem);
        }
    }

    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      "policy-xml-validation-messages",
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    public PolicySet read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        PolicySet policySet = null;

        String policySetName = reader.getAttributeValue(null, NAME);
        String appliesTo = reader.getAttributeValue(null, APPLIES_TO);
        if (policySetName == null || appliesTo == null) {
            if (policySetName == null)
                error("PolicySetNameMissing", reader);
            if (appliesTo == null)
                error("PolicySetAppliesToMissing", reader);
            return policySet;
        }

        policySet = policyFactory.createPolicySet();
        policySet.setName(new QName(policySetName));

        //TODO: with 1.0 version of specs the applies to xpath is given related to the immediate
        //parent whereas the runtime evaluates the xpath aginst the composite element.  What the runtime
        //is doing is what the future version of the specs could be tending towards.  When that happens
        //this 'if' must be deleted
        if (appliesTo != null && !appliesTo.startsWith("/")) {
            appliesTo = "//" + appliesTo;
        }

        policySet.setAppliesTo(appliesTo);

        if (appliesTo != null) {
            try {
                XPath path = xpathHelper.newXPath();
                NamespaceContext context = xpathHelper.getNamespaceContext(appliesTo, reader.getNamespaceContext());
                // path.setXPathFunctionResolver(new PolicyXPathFunctionResolver(context));
                XPathExpression expression = xpathHelper.compile(path, context, appliesTo);
                policySet.setAppliesToXPathExpression(expression);
            } catch (XPathExpressionException e) {
                ContributionReadException ce = new ContributionReadException(e);
                error("ContributionReadException", policySet, ce);
                //throw ce;
            }
        }
        
        String attachTo = reader.getAttributeValue(null, ATTACH_TO);
        if (attachTo != null) {
            try {
                XPath path = xpathHelper.newXPath();
                NamespaceContext context = xpathHelper.getNamespaceContext(attachTo, reader.getNamespaceContext());
                path.setXPathFunctionResolver(new PolicyXPathFunctionResolver(context));
                XPathExpression expression = xpathHelper.compile(path, context, attachTo);
                policySet.setAttachTo(attachTo);
                policySet.setAttachToXPathExpression(expression);
            } catch (XPathExpressionException e) {
                ContributionReadException ce = new ContributionReadException(e);
                error("ContributionReadException", policySet, ce);
                //throw ce;
            }

        }

        readProvidedIntents(policySet, reader);

        int event = reader.getEventType();
        QName name = null;
        reader.next();
        while (reader.hasNext()) {
            event = reader.getEventType();
            switch (event) {
                case START_ELEMENT: {
                    name = reader.getName();
                    if (POLICY_INTENT_MAP_QNAME.equals(name)) {
                        Intent mappedIntent = policyFactory.createIntent();
                        String provides = reader.getAttributeValue(null, PROVIDES);
                        if (provides != null) {
                            mappedIntent.setName(getQName(reader, PROVIDES));
                            if (policySet.getProvidedIntents().contains(mappedIntent)) {
                                readIntentMap(reader, policySet, mappedIntent);
                            } else {
                                error("IntentNotSpecified", policySet, policySetName);
                                //throw new ContributionReadException("Intent Map provides for Intent not specified as provided by parent PolicySet - " + policySetName);
                            }
                        } else {
                            error("IntentMapProvidesMissing", reader, policySetName);
                        }
                    } else if (POLICY_SET_REFERENCE_QNAME.equals(name)) {
                        PolicySet referredPolicySet = policyFactory.createPolicySet();
                        String referencename = reader.getAttributeValue(null, NAME);
                        if (referencename != null) {
                            referredPolicySet.setName(getQName(reader, NAME));
                            policySet.getReferencedPolicySets().add(referredPolicySet);
                        } else {
                            error("PolicySetReferenceNameMissing", reader, policySetName);
                        }
                    } /*else if ( WS_POLICY_QNAME.equals(name) )  {
                                                                                                                                                                                OMElement policyElement = loadElement(reader);
                                                                                                                                                                                org.apache.neethi.Policy wsPolicy = PolicyEngine.getPolicy(policyElement);
                                                                                                                                                                                policySet.getPolicies().add(wsPolicy);
                                                                                                                                                                            } */else {
                        Object extension = extensionProcessor.read(reader);
                        if (extension != null) {
                            PolicyExpression exp = policyFactory.createPolicyExpression();
                            exp.setName(name);
                            exp.setPolicy(extension);
                            policySet.getPolicies().add(exp);
                        }
                    }
                    break;
                }
            }
            if (event == END_ELEMENT) {
                if (POLICY_SET_QNAME.equals(reader.getName())) {
                    break;
                }
            }

            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return policySet;
    }

    public void readIntentMap(XMLStreamReader reader, PolicySet policySet, Intent mappedIntent)
        throws ContributionReadException {
        QName name = reader.getName();
        if (POLICY_INTENT_MAP_QNAME.equals(name)) {

            IntentMap intentMap = policyFactory.createIntentMap();
            QName intentName = getQName(reader, INTENT_MAP);
            intentMap.setProvidedIntent(mappedIntent);

            policySet.getIntentMaps().add(intentMap);

            String qualifierName = null;
            String qualfiedIntentName = null;
            Intent qualifiedIntent = null;

            Qualifier qualifier = null;

            int event = reader.getEventType();
            try {
                reader.next();
                while (reader.hasNext()) {
                    event = reader.getEventType();
                    switch (event) {
                        case START_ELEMENT: {
                            name = reader.getName();
                            if (POLICY_INTENT_MAP_QUALIFIER_QNAME.equals(name)) {
                                qualifierName = getString(reader, NAME);
                                if (qualifierName != null) {
                                    qualfiedIntentName =
                                        mappedIntent.getName().getLocalPart() + QUALIFIER + qualifierName;
                                    qualifiedIntent = policyFactory.createIntent();
                                    qualifiedIntent.setName(new QName(mappedIntent.getName().getNamespaceURI(),
                                                                      qualfiedIntentName));
                                    qualifier = policyFactory.createQualifier();
                                    qualifier.setIntent(qualifiedIntent);
                                    intentMap.getQualifiers().add(qualifier);

                                } else {
                                    error("QualifierNameMissing", reader, policySet.getName());
                                }
                            } else if (POLICY_INTENT_MAP_QNAME.equals(name)) {
                                QName providedIntent = getQName(reader, PROVIDES);
                                if (qualifierName.equals(providedIntent.getLocalPart())) {
                                    readIntentMap(reader, policySet, qualifiedIntent);
                                } else {
                                    error("IntentMapDoesNotMatch",
                                          providedIntent,
                                          providedIntent,
                                          qualifierName,
                                          policySet);
                                    //throw new ContributionReadException("Intent provided by IntentMap " + 
                                    //providedIntent + " does not match parent qualifier " + qualifierName +
                                    //" in policyset - " + policySet);
                                }
                            } else {
                                Object extension = extensionProcessor.read(reader);
                                if (extension != null && qualifier != null) {
                                    PolicyExpression exp = policyFactory.createPolicyExpression();
                                    exp.setName(name);
                                    exp.setPolicy(extension);
                                    qualifier.getPolicies().add(exp);
                                }
                            }
                            break;
                        }
                    }
                    if (event == END_ELEMENT && POLICY_INTENT_MAP_QNAME.equals(reader.getName())) {
                        break;
                    }
                    //Read the next element
                    if (reader.hasNext()) {
                        reader.next();
                    }
                }
            } catch (XMLStreamException e) {
                ContributionReadException ce = new ContributionReadException(e);
                error("ContributionReadException", reader, ce);
                throw ce;
            }
        }
    }

    public void write(PolicySet policySet, XMLStreamWriter writer) throws ContributionWriteException,
        XMLStreamException {

        // Write an <sca:policySet>
        writer.writeStartElement(SCA11_NS, POLICY_SET);
        writer.writeNamespace(policySet.getName().getPrefix(), policySet.getName().getNamespaceURI());
        writer.writeAttribute(NAME, policySet.getName().getPrefix() + COLON + policySet.getName().getLocalPart());
        writer.writeAttribute(APPLIES_TO, policySet.getAppliesTo());
        writer.writeAttribute(ATTACH_TO, policySet.getAttachTo());

        writeProvidedIntents(policySet, writer);

        writer.writeEndElement();
    }

    private void readProvidedIntents(PolicySet policySet, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, PROVIDES);
        if (value != null) {
            List<Intent> providedIntents = policySet.getProvidedIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                providedIntents.add(intent);
            }
        }
    }

    private void writeProvidedIntents(PolicySet policySet, XMLStreamWriter writer) throws XMLStreamException {
        if (!policySet.getProvidedIntents().isEmpty()) {
            StringBuffer sb = new StringBuffer();
            for (Intent providedIntents : policySet.getProvidedIntents()) {
                sb.append(getQualifiedName(providedIntents.getName(), writer));
                sb.append(" ");
            }
            // Remove the last space
            sb.deleteCharAt(sb.length() - 1);
            writer.writeAttribute(PolicyConstants.PROVIDES, sb.toString());
        }
    }

    private String getQualifiedName(QName name, XMLStreamWriter writer) throws XMLStreamException {
        String local = name.getLocalPart();
        String prefix = writer.getPrefix(name.getNamespaceURI());
        if (prefix != null && prefix.length() > 0) {
            return prefix + ':' + local;
        } else {
            return local;
        }
    }

    private void resolvePolicies(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
        boolean unresolved = false;
        if (policySet != null) {
            for (Object o : policySet.getPolicies()) {
                extensionProcessor.resolve(o, resolver);
                /*if ( o instanceof Policy && ((Policy)o).isUnresolved() ) {
                   unresolved = true;
                }*/
            }
            policySet.setUnresolved(unresolved);
        }
    }

    public QName getArtifactType() {
        return POLICY_SET_QNAME;
    }

    public Class<PolicySet> getModelType() {
        return PolicySet.class;
    }

    private void resolveProvidedIntents(PolicySet policySet, ModelResolver resolver)
        throws ContributionResolveException {
        if (policySet != null) {
            //resolve all provided intents
            List<Intent> providedIntents = new ArrayList<Intent>();
            for (Intent providedIntent : policySet.getProvidedIntents()) {
                if (providedIntent.isUnresolved()) {
                    Intent resolved = resolver.resolveModel(Intent.class, providedIntent);
                    if (!resolved.isUnresolved() || resolved != providedIntent) {
                        providedIntents.add(resolved);
                    } else {
                        error("ProvidedIntentNotFound", policySet, providedIntent, policySet);
                        return;
                        //throw new ContributionResolveException("Provided Intent - " + providedIntent
                        //+ " not found for PolicySet " + policySet);
                    }
                } else {
                    providedIntents.add(providedIntent);
                }
            }
            policySet.getProvidedIntents().clear();
            policySet.getProvidedIntents().addAll(providedIntents);
        }
    }

    private void resolveIntentsInMappedPolicies(PolicySet policySet, ModelResolver resolver)
        throws ContributionResolveException {
        for (IntentMap intentMap : policySet.getIntentMaps()) {
            Intent intent = intentMap.getProvidedIntent();
            if (intent.isUnresolved()) {
                Intent resolved = resolver.resolveModel(Intent.class, intent);
                if (!resolved.isUnresolved() || resolved != intent) {
                    intentMap.setProvidedIntent(resolved);
                } else {
                    error("MappedIntentNotFound", policySet, intent, policySet);
                    return;
                    //throw new ContributionResolveException("Mapped Intent - " + mappedIntent
                    //+ " not found for PolicySet " + policySet);    
                }
            }
            for (Qualifier qualifier : intentMap.getQualifiers()) {
                intent = qualifier.getIntent();
                if (intent.isUnresolved()) {
                    Intent resolved = resolver.resolveModel(Intent.class, intent);
                    if (!resolved.isUnresolved() || resolved != intent) {
                        qualifier.setIntent(resolved);
                    } else {
                        error("MappedIntentNotFound", policySet, intent, policySet);
                        return;
                        //throw new ContributionResolveException("Mapped Intent - " + mappedIntent
                        //+ " not found for PolicySet " + policySet);    
                    }
                }
                for (PolicyExpression exp : qualifier.getPolicies()) {
                    // FIXME: How to resolve the policies?
                }
            }
        }

    }

    private void resolveReferredPolicySets(PolicySet policySet, ModelResolver resolver)
        throws ContributionResolveException {

        List<PolicySet> referredPolicySets = new ArrayList<PolicySet>();
        for (PolicySet referredPolicySet : policySet.getReferencedPolicySets()) {
            if (referredPolicySet.isUnresolved()) {
                PolicySet resolved = resolver.resolveModel(PolicySet.class, referredPolicySet);
                if (!resolved.isUnresolved() || resolved != referredPolicySet) {
                    referredPolicySets.add(resolved);
                } else {
                    error("ReferredPolicySetNotFound", policySet, referredPolicySet, policySet);
                    return;
                    //throw new ContributionResolveException("Referred PolicySet - " + referredPolicySet
                    //+ "not found for PolicySet - " + policySet);
                }
            } else {
                referredPolicySets.add(referredPolicySet);
            }
        }
        policySet.getReferencedPolicySets().clear();
        policySet.getReferencedPolicySets().addAll(referredPolicySets);
    }

    private void includeReferredPolicySets(PolicySet policySet, PolicySet referredPolicySet) {
        for (PolicySet furtherReferredPolicySet : referredPolicySet.getReferencedPolicySets()) {
            includeReferredPolicySets(referredPolicySet, furtherReferredPolicySet);
        }
        policySet.getPolicies().addAll(referredPolicySet.getPolicies());
        policySet.getIntentMaps().addAll(referredPolicySet.getIntentMaps());
    }

    public void resolve(PolicySet policySet, ModelResolver resolver) throws ContributionResolveException {
        resolveProvidedIntents(policySet, resolver);
        resolveIntentsInMappedPolicies(policySet, resolver);
        resolveReferredPolicySets(policySet, resolver);

        for (PolicySet referredPolicySet : policySet.getReferencedPolicySets()) {
            includeReferredPolicySets(policySet, referredPolicySet);
        }

        if (policySet.isUnresolved()) {
            //resolve the policy attachments
            resolvePolicies(policySet, resolver);

            /*if ( !policySet.isUnresolved() ) {
                 resolver.addModel(policySet);
            }*/
        }

        policySet.setUnresolved(false);
    }
}
