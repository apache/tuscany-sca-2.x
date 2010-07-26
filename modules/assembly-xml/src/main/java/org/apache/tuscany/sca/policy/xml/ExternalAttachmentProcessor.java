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
import org.apache.tuscany.sca.policy.ExternalAttachment;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Processor for handling XML models of ExternalAttachment definitions
 *
 * @version $Rev: 961010 $ $Date: 2010-07-06 13:34:54 -0700 (Tue, 06 Jul 2010) $
 */
public class ExternalAttachmentProcessor extends BaseStAXArtifactProcessor
		implements StAXArtifactProcessor<ExternalAttachment>, PolicyConstants {

	
	private PolicyFactory policyFactory;
	private XPathHelper xpathHelper;
	
	public ExternalAttachmentProcessor(ExtensionPointRegistry registry) {
		FactoryExtensionPoint factories = registry.getExtensionPoint(FactoryExtensionPoint.class);
		this.policyFactory = factories.getFactory(PolicyFactory.class);
		this.xpathHelper = XPathHelper.getInstance(registry);
	}
	
	public Class<ExternalAttachment> getModelType() {
		return ExternalAttachment.class;
	}

	public void resolve(ExternalAttachment attachment, ModelResolver resolver,
			ProcessorContext context) throws ContributionResolveException {
	      if (attachment != null && attachment.isUnresolved()) {
	            resolveIntents(attachment, resolver, context);
	            resolvePolicySets(attachment, resolver, context);	           		          

	            attachment.setUnresolved(false);
	      }
		
	}

	private void resolvePolicySets(ExternalAttachment attachment,
			ModelResolver resolver, ProcessorContext context) {
	     List<PolicySet> referredPolicySets = new ArrayList<PolicySet>();
	        for (PolicySet referredPolicySet : attachment.getPolicySets()) {
	            if (referredPolicySet.isUnresolved()) {
	                PolicySet resolved = resolver.resolveModel(PolicySet.class, referredPolicySet, context);
	                if (!resolved.isUnresolved() || resolved != referredPolicySet) {
	                    referredPolicySets.add(resolved);
	                } else {
	                    error(context.getMonitor(), "ReferredPolicySetNotFound", attachment, referredPolicySet, attachment);
	                    return;	                 
	                }
	            } else {
	                referredPolicySets.add(referredPolicySet);
	            }
	        }
	        attachment.getPolicySets().clear();
	        attachment.getPolicySets().addAll(referredPolicySets);
		
	}

	private void resolveIntents(ExternalAttachment attachment,
			ModelResolver resolver, ProcessorContext context) {
	       if (attachment != null) {
	            //resolve all provided intents
	            List<Intent> providedIntents = new ArrayList<Intent>();
	            for (Intent providedIntent : attachment.getIntents()) {
	                if (providedIntent.isUnresolved()) {
	                    Intent resolved = resolver.resolveModel(Intent.class, providedIntent, context);
	                    if (!resolved.isUnresolved() || resolved != providedIntent) {
	                        providedIntents.add(resolved);
	                    } else {
	                        error(context.getMonitor(), "ProvidedIntentNotFound", attachment, providedIntent, attachment);
	                        return;	                      
	                    }
	                } else {
	                    providedIntents.add(providedIntent);
	                }
	            }
	            attachment.getIntents().clear();
	            attachment.getIntents().addAll(providedIntents);
	        }
		
	}

	public QName getArtifactType() {
		return EXTERNAL_ATTACHMENT_QNAME;
	}

	public ExternalAttachment read(XMLStreamReader reader, ProcessorContext context)
			throws ContributionReadException, XMLStreamException {
			ExternalAttachment attachment = policyFactory.createExternalAttachment();
			
			readPolicySets(attachment, reader);
			readIntents(attachment, reader, context);
			readAttachTo(attachment, reader, context);
			
			return attachment;
	}

	private void readIntents(ExternalAttachment attachment,
			XMLStreamReader reader, ProcessorContext context) {
	      String value = reader.getAttributeValue(null, INTENTS);
	        if (value != null) {
	            List<Intent> intents = attachment.getIntents();
	            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
	                QName qname = getQNameValue(reader, tokens.nextToken());
	                Intent intent = policyFactory.createIntent();
	                intent.setName(qname);	               
	                intents.add(intent);
	            }
	        }
		
	}

	private void readAttachTo(ExternalAttachment attachment,
			XMLStreamReader reader, ProcessorContext context) {
		Monitor monitor = context.getMonitor();
		
		String attachTo = reader.getAttributeValue(null, ATTACH_TO);
		if ( attachTo != null ) {
		   try {
               XPath path = xpathHelper.newXPath();
               NamespaceContext nsContext = xpathHelper.getNamespaceContext(attachTo, reader.getNamespaceContext());
               path.setXPathFunctionResolver(new PolicyXPathFunctionResolver(nsContext));                
                                          
               attachTo = PolicyXPathFunction.normalize(attachTo,getSCAPrefix(nsContext));
               XPathExpression expression = xpathHelper.compile(path, nsContext, attachTo);
               attachment.setAttachTo(attachTo);
               attachment.setAttachToXPathExpression(expression);
           } catch (XPathExpressionException e) {
               ContributionReadException ce = new ContributionReadException(e);
               error(monitor, "ContributionReadException", attachment, ce);              
           }
		}
		
	}

	   private String getSCAPrefix(NamespaceContext nsContext) {

	        Iterator<String> iter = nsContext.getPrefixes(SCA11_NS);
	        while (iter.hasNext()) {
	            String prefix = iter.next();
	            if (!prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
	                return prefix;
	            }
	        }

	        return "__sca";
	    }
	/**
     * Reads policy sets associated with an external attachment element.
     * @param subject
     * @param operation
     * @param reader
     */
    private void readPolicySets(ExternalAttachment attachment, XMLStreamReader reader) {
      
     
        String value = reader.getAttributeValue(null, POLICY_SETS);
        if (value != null) {
            List<PolicySet> policySets = attachment.getPolicySets();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                PolicySet policySet = policyFactory.createPolicySet();
                policySet.setName(qname);              
                policySets.add(policySet);
            }
        }
    }
	public void write(ExternalAttachment attachment, XMLStreamWriter writer,
			ProcessorContext context) throws ContributionWriteException,
			XMLStreamException {
		writePolicySets(attachment, writer, context);
		writeIntents(attachment, writer, context);
		writeAttachTo(attachment, writer, context);
		
	}
	
	   private void writeAttachTo(ExternalAttachment attachment,
			XMLStreamWriter writer, ProcessorContext context) throws XMLStreamException {
		if ( attachment.getAttachTo() != null ) {
			writer.writeAttribute(PolicyConstants.ATTACH_TO, attachment.getAttachTo());
		}
		
	}

	private void writeIntents(ExternalAttachment attachment,
			XMLStreamWriter writer, ProcessorContext context) throws XMLStreamException {
	     if (!attachment.getIntents().isEmpty()) {
	    	 StringBuffer sb = new StringBuffer();
	    	 for (Intent intent : attachment.getIntents()) {
	    		 sb.append(getQualifiedName(intent.getName(), writer));
	    		 sb.append(" ");
	    	 }
	    	 // Remove the last space
	    	 sb.deleteCharAt(sb.length() - 1);
	    	 writer.writeAttribute(PolicyConstants.INTENTS, sb.toString());
	     }
		
	}

	private void writePolicySets(ExternalAttachment attachment,
			XMLStreamWriter writer, ProcessorContext context) throws XMLStreamException {
		if ( !attachment.getPolicySets().isEmpty()) {
			 StringBuffer sb = new StringBuffer();
	    	 for (PolicySet ps : attachment.getPolicySets()) {
	    		 sb.append(getQualifiedName(ps.getName(), writer));
	    		 sb.append(" ");
	    	 }
	    	 // Remove the last space
	    	 sb.deleteCharAt(sb.length() - 1);
	    	 writer.writeAttribute(PolicyConstants.POLICY_SETS, sb.toString());
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

}
