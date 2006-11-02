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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Scope;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Loader for handling <binding.jms> elements.
 *
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */
@Scope("MODULE")
public class JMSBindingLoader extends LoaderExtension<JMSBinding> {
    public static final QName BINDING_JMS = new QName(
        "http://tuscany.apache.org/xmlns/binding/jms/1.0-SNAPSHOT", "binding.jms");

    public JMSBindingLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return BINDING_JMS;
    }
    
    
    /**
	 * FIXME 21.10.2006 rajith 
	 * The spec has changed quite a bit since I started the implementation, especially during the last 2 weeks.
	 * I will wait till a draft version is published before I make any changes 
	 *
	 */
    public JMSBinding load(CompositeComponent parent,
    		               ModelObject modelObject,
                           XMLStreamReader reader,
                           DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
    	
    	OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), reader);
        OMElement omElement = builder.getDocumentElement();
        
        //OMElement connectionOM = omElement.getFirstChildWithName(new QName("connection.jms"));
        OMElement connectionOM = omElement;
        
        
    	String activationSpecName =  connectionOM.getAttributeValue(new QName("activationSpecName"));
        String destinationName = connectionOM.getAttributeValue(new QName("destinationName"));
		String connectionFactoryName = connectionOM.getAttributeValue(new QName("connectionFactoryName")) ;
		String initialContextFactoryName = connectionOM.getAttributeValue(new QName("initialContextFactoryName")) ;
		String jNDIProviderURL = connectionOM.getAttributeValue(new QName("providerURL")) ;
		String deliveryMode = connectionOM.getAttributeValue(new QName(null, "deliveryMode")) ;
		String timeToLive = connectionOM.getAttributeValue(new QName(null, "timeToLive")) ;
		String priority = connectionOM.getAttributeValue(new QName(null, "priority")) ;
		String replyTo = connectionOM.getAttributeValue(new QName(null, "replyTo")) ;			
		String jmsResourceFactoryName = connectionOM.getAttributeValue(new QName(null, "jmsResourceFactory")) ;
			
		LoaderUtil.skipToEndElement(reader);
						
		//builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), reader);		
		//OMElement opSecOM = builder.getDocumentElement();
			//omElement.getFirstChildWithName(new QName("operationSelector"));		
		String operationSelector = ""; //opSecOM.getAttributeValue(new QName("name"));
		//OMElement opSecPropertyOM = opSecOM.getFirstChildWithName(new QName("property"));
		String jmsOpSecPropertyName = ""; // = opSecPropertyOM.getText();
		       
        
        JMSBinding binding = new JMSBinding();
        binding.setActivationSpecName(activationSpecName);
        binding.setConnectionFactoryName(connectionFactoryName);
        binding.setDestinationName(destinationName);
        binding.setInitialContextFactoryName(initialContextFactoryName);
        binding.setJNDIProviderURL(jNDIProviderURL);
        binding.setReplyTo(replyTo);
        binding.setJmsResourceFactoryName(jmsResourceFactoryName);
        binding.setOperationSelectorName(operationSelector);
        binding.setOperationSelectorPropertyName(jmsOpSecPropertyName);
        
        if (deliveryMode != null && deliveryMode.trim().equals("")){
        	try{
        		binding.setDeliveryMode(Integer.parseInt(deliveryMode));
        	}catch (Exception e){
        		
        	}
        }             
        
        if (priority != null && priority.trim().equals("")){
        	try{
        		binding.setPriority(Integer.parseInt(priority));
        	}catch (Exception e){
        		
        	}
        }
        
        if (timeToLive != null && timeToLive.trim().equals("")){
        	try{
        		binding.setTimeToLive(Integer.parseInt(timeToLive));
        	}catch (Exception e){
        		
        	}
        }
        
        return binding;
    }	
}
