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

package org.apache.tuscany.sca.binding.ws.axis2.policy.configurator;



import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.context.MessageContext;
import org.apache.tuscany.sca.binding.ws.axis2.policy.header.Axis2HeaderPolicyUtil;
import org.apache.tuscany.sca.binding.ws.axis2.policy.header.Axis2SOAPHeader;
import org.apache.tuscany.sca.invocation.Message;


/**
 * Policy handler to handle PolicySet that contain Axis2ConfigParamPolicy instances
 *
 * @version $Rev$ $Date$
 */
public class Axis2BindingHeaderConfigurator {
    
    
    public static void setHeader(MessageContext messageContext, Message msg, QName headerQName) {
        
        if (headerQName != null){
            SOAPEnvelope envelope = messageContext.getEnvelope();
            OMFactory factory = envelope.getOMFactory();
            SOAPHeader soapHeader = envelope.getHeader();
            
            Axis2SOAPHeader header = Axis2HeaderPolicyUtil.getHeader(msg, headerQName) ;
            
            if (header != null){
                soapHeader.addChild(header.getAsSOAPHeaderBlock(factory));
            } 
        }
    }
    
    public static void getHeader(MessageContext messageContext, Message msg, QName headerQName, Axis2SOAPHeader header) {
        
        SOAPEnvelope sev = messageContext.getEnvelope();
        SOAPHeader sh = sev.getHeader();
        OMElement omHeader = sh.getFirstChildWithName(headerQName);
        
        header.setAsSOAPHeaderBlock(omHeader);
        
        msg.getHeaders().add(header); 
    }  

}
