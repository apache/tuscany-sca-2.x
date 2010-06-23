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
import static org.apache.tuscany.sca.policy.xml.PolicyConstants.SCA11_NS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
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
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
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

    public PolicySetProcessor(ExtensionPointRegistry registry, StAXArtifactProcessor<Object> extensionProcessor) {
        FactoryExtensionPoint modelFactories = registry.getExtensionPoint(FactoryExtensionPoint.class);
        this.policyFactory = modelFactories.getFactory(PolicyFactory.class);
        this.extensionProcessor = extensionProcessor;
        this.xpathHelper = XPathHelper.getInstance(registry);
    }

    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(Monitor monitor, String message, Object model, Exception ex) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      Messages.RESOURCE_BUNDLE,
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
    private void error(Monitor monitor, String message, Object model, Object... messageParameters) {
        if (monitor != null) {
            Problem problem =
                monitor.createProblem(this.getClass().getName(),
                                      Messages.RESOURCE_BUNDLE,
                                      Severity.ERROR,
                                      model,
                                      message,
                                      (Object[])messageParameters);
            monitor.problem(problem);
        }
    }

    public PolicySet read(XMLStreamReader reader, ProcessorContext context) throws ContributionReadException,
        XMLStreamException {
        PolicySet policySet = null;
        Monitor monitor = context.getMonitor();
        String policySetName = reader.getAttributeValue(null, NAME);
        String appliesTo = reader.getAttributeValue(null, APPLIES_TO);
        if (policySetName == null || appliesTo == null) {
            if (policySetName == null)
                error(monitor, "PolicySetNameMissing", reader);
            if (appliesTo == null)
                error(monitor, "PolicySetAppliesToMissing", reader);
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
                NamespaceContext nsContext = xpathHelper.getNamespaceContext(appliesTo, reader.getNamespaceContext());
                // path.setXPathFunctionResolver(new PolicyXPathFunctionResolver(context));
                XPathExpression expression = xpathHelper.compile(path, nsContext, appliesTo);
                policySet.setAppliesToXPathExpression(expression);
            } catch (XPathExpressionException e) {
                ContributionReadException ce = new ContributionReadException(e);
                error(monitor, "ContributionReadException", policySet, ce);
                //throw ce;
            }
        }

        String attachTo = reader.getAttributeValue(null, ATTACH_TO);
        if (attachTo != null) {
            try {
                XPath path = xpathHelper.newXPath();
                NamespaceContext nsContext = xpathHelper.getNamespaceContext(attachTo, reader.getNamespaceContext());
                path.setXPathFunctionResolver(new PolicyXPathFunctionResolver(nsContext));                
                                           
                attachTo = PolicyXPathFunction.normalize(attachTo,getSCAPrefix(nsContext));
                XPathExpression expression = xpathHelper.compile(path, nsContext, attachTo);
                policySet.setAttachTo(attachTo);
                policySet.setAttachToXPathExpression(expression);
            } catch (XPathExpressionException e) {
                ContributionReadException ce = new ContributionReadException(e);
                error(monitor, "ContributionReadException", policySet, ce);
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
                                readIntentMap(reader, policySet, mappedIntent, context);
                            } else {
                                error(monitor, "IntentNotSpecified", policySet, policySetName);
                            }
                        } else {
                            error(monitor, "IntentMapProvidesMissing", reader, policySetName);
                        }
                    } else if (POLICY_SET_REFERENCE_QNAME.equals(name)) {
                        PolicySet referredPolicySet = policyFactory.createPolicySet();
                        String referencename = reader.getAttributeValue(null, NAME);
                        if (referencename != null) {
                            referredPolicySet.setName(getQName(reader, NAME));
                            policySet.getReferencedPolicySets().add(referredPolicySet);
                        } else {
                            error(monitor, "PolicySetReferenceNameMissing", reader, policySetName);
                        }
                    } /*else if ( WS_POLICY_QNAME.equals(name) )  {
                        OMElement policyElement = loadElement(reader);
                        org.apache.neethi.Policy wsPolicy = PolicyEngine.getPolicy(policyElement);
                        policySet.getPolicies().add(wsPolicy);
                      } */else {
                        Object extension = extensionProcessor.read(reader, context);
                        if (extension != null) {
                            PolicyExpression exp = policyFactory.createPolicyExpression();
                            exp.setName(name);
                            exp.setPolicy(extension);
                            // check that all the policies in the policy set are 
                            // expressed in the same language. Compare against the 
                            // first expression we added
                            if ((policySet.getPolicies().size() > 0) && (!policySet.getPolicies().get(0).getName()
                                .equals(name))) {
                                error(monitor, "PolicyLanguageMissmatch", reader, policySet.getName(), policySet
                                    .getPolicies().get(0).getName(), name);
                            } else {
                                policySet.getPolicies().add(exp);
                            }
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

    private String getSCAPrefix(NamespaceContext nsContext) {   
    	
    	Iterator iter = nsContext.getPrefixes(SCA11_NS);
    	while ( iter.hasNext()) {
    		String prefix = (String)iter.next();
    		if ( !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
    			return prefix;
    	}
    	
    	// We have to have some prefix here to use before the function name. Otherwise the 
    	// XPathFunctionResolver will never be called. 
    	xpathHelper.registerPrefix("sca_internal", SCA11_NS, nsContext);
    
    	return "sca_internal";
	}

	public void readIntentMap(XMLStreamReader reader, PolicySet policySet, Intent mappedIntent, ProcessorContext context)
        throws ContributionReadException {
        Monitor monitor = context.getMonitor();
        QName name = reader.getName();
        if (POLICY_INTENT_MAP_QNAME.equals(name)) {

            IntentMap intentMap = policyFactory.createIntentMap();
            QName intentName = getQName(reader, INTENT_MAP);
            intentMap.setProvidedIntent(mappedIntent);

            if (!policySet.getIntentMaps().contains(intentMap)) {
                policySet.getIntentMaps().add(intentMap);
            } else {
                Monitor.error(context.getMonitor(), this, Messages.RESOURCE_BUNDLE, "IntentMapIsNotUnique", policySet
                    .getName().toString(), mappedIntent.getName().getLocalPart());
            }

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
                                    error(monitor, "QualifierNameMissing", reader, policySet.getName());
                                }
                            } else if (POLICY_INTENT_MAP_QNAME.equals(name)) {
                                QName providedIntent = getQName(reader, PROVIDES);
                                if (qualifierName.equals(providedIntent.getLocalPart())) {
                                    readIntentMap(reader, policySet, qualifiedIntent, context);
                                } else {
                                    error(monitor,
                                          "IntentMapDoesNotMatch",
                                          providedIntent,
                                          providedIntent,
                                          qualifierName,
                                          policySet);
                                    //throw new ContributionReadException("Intent provided by IntentMap " + 
                                    //providedIntent + " does not match parent qualifier " + qualifierName +
                                    //" in policyset - " + policySet);
                                }
                            } else {
                                Object extension = extensionProcessor.read(reader, context);
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
                error(monitor, "ContributionReadException", reader, ce);
                throw ce;
            }
        }
    }

    public void write(PolicySet policySet, XMLStreamWriter writer, ProcessorContext context)
        throws ContributionWriteException, XMLStreamException {

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

    private void resolvePolicies(PolicySet policySet, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        boolean unresolved = false;
        if (policySet != null) {
            for (Object o : policySet.getPolicies()) {
                extensionProcessor.resolve(o, resolver, context);
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

    private void resolveProvidedIntents(PolicySet policySet, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        if (policySet != null) {
            //resolve all provided intents
            List<Intent> providedIntents = new ArrayList<Intent>();
            for (Intent providedIntent : policySet.getProvidedIntents()) {
                if (providedIntent.isUnresolved()) {
                    Intent resolved = resolver.resolveModel(Intent.class, providedIntent, context);
                    if (!resolved.isUnresolved() || resolved != providedIntent) {
                        providedIntents.add(resolved);
                    } else {
                        error(context.getMonitor(), "ProvidedIntentNotFound", policySet, providedIntent, policySet);
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

    private void resolveIntentsInMappedPolicies(PolicySet policySet, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        Monitor monitor = context.getMonitor();
        for (IntentMap intentMap : policySet.getIntentMaps()) {
            Intent intent = intentMap.getProvidedIntent();
            if (intent.isUnresolved()) {
                Intent resolved = resolver.resolveModel(Intent.class, intent, context);
                if (!resolved.isUnresolved() || resolved != intent) {
                    intentMap.setProvidedIntent(resolved);
                } else {
                    error(monitor, "MappedIntentNotFound", policySet, intent, policySet);
                    return;
                    //throw new ContributionResolveException("Mapped Intent - " + mappedIntent
                    //+ " not found for PolicySet " + policySet);    
                }
            }
            for (Qualifier qualifier : intentMap.getQualifiers()) {
                intent = qualifier.getIntent();
                if (intent.isUnresolved()) {
                    Intent resolved = resolver.resolveModel(Intent.class, intent, context);
                    if (!resolved.isUnresolved() || resolved != intent) {
                        qualifier.setIntent(resolved);
                    } else {
                        error(monitor, "MappedIntentNotFound", policySet, intent, policySet);
                        return;
                        //throw new ContributionResolveException("Mapped Intent - " + mappedIntent
                        //+ " not found for PolicySet " + policySet);    
                    }
                }
                for (PolicyExpression exp : qualifier.getPolicies()) {
                    // FIXME: How to resolve the policies?
                }
            }
            // validate that the intent map has a qualifier for each 
            // intent qualifier. The above code has already checked that the
            // qualifiers that are there are resolved
            Intent providedIntent = intentMap.getProvidedIntent();
            if (intentMap.getQualifiers().size() != providedIntent.getQualifiedIntents().size()) {
                String missingQualifiers = "";
                for (Intent loopQualifiedIntent : providedIntent.getQualifiedIntents()) {
                    boolean found = false;
                    for (Qualifier loopQualifier : intentMap.getQualifiers()) {
                        if (loopQualifier.getIntent().getName().equals(loopQualifiedIntent.getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        missingQualifiers += loopQualifiedIntent.getName().getLocalPart() + " ";
                    }
                }
                if (missingQualifiers.length() > 0) {
                    Monitor.error(context.getMonitor(),
                                  this,
                                  Messages.RESOURCE_BUNDLE,
                                  "IntentMapMissingQualifiers",
                                  policySet.getName().toString(),
                                  providedIntent.getName().getLocalPart(),
                                  missingQualifiers);
                }
            }
        }

    }

    private void resolveReferredPolicySets(PolicySet policySet, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {

        List<PolicySet> referredPolicySets = new ArrayList<PolicySet>();
        for (PolicySet referredPolicySet : policySet.getReferencedPolicySets()) {
            if (referredPolicySet.isUnresolved()) {
                PolicySet resolved = resolver.resolveModel(PolicySet.class, referredPolicySet, context);
                if (!resolved.isUnresolved() || resolved != referredPolicySet) {
                    referredPolicySets.add(resolved);
                } else {
                    error(context.getMonitor(), "ReferredPolicySetNotFound", policySet, referredPolicySet, policySet);
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

    public void resolve(PolicySet policySet, ModelResolver resolver, ProcessorContext context)
        throws ContributionResolveException {
        if (policySet != null && policySet.isUnresolved()) {
            resolveProvidedIntents(policySet, resolver, context);
            resolveIntentsInMappedPolicies(policySet, resolver, context);
            resolveReferredPolicySets(policySet, resolver, context);

            for (PolicySet referredPolicySet : policySet.getReferencedPolicySets()) {
                includeReferredPolicySets(policySet, referredPolicySet);
            }

            if (policySet.isUnresolved()) {
                //resolve the policy attachments
                resolvePolicies(policySet, resolver, context);

                /*if ( !policySet.isUnresolved() ) {
                     resolver.addModel(policySet);
                }*/
            }

            policySet.setUnresolved(false);
        }
    }
}
