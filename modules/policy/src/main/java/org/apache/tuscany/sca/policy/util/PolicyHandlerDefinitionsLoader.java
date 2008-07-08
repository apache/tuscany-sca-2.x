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

package org.apache.tuscany.sca.policy.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * Utility class for loading policy handler definitions from META-INF/services directories
 *
 * @version $Rev$ $Date$
 */
public class PolicyHandlerDefinitionsLoader {
    
    public static List<PolicyHandlerTuple> loadPolicyHandlerClassnames()  {
        // Get the processor service declarations
        Set<ServiceDeclaration> sds;
        try {
            sds = ServiceDiscovery.getInstance().getServiceDeclarations(PolicyHandler.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        List<PolicyHandlerTuple> handlerTupleList = new ArrayList<PolicyHandlerTuple>();
        
        Map<Object, List<PolicyHandlerTuple>> handlerTuples = new Hashtable<Object, List<PolicyHandlerTuple>>();
        for (ServiceDeclaration sd : sds) {
            Map<String, String> attributes = sd.getAttributes();
            String intentName = attributes.get("intent");
            QName intentQName = getQName(intentName);
            String policyModelClassName = attributes.get("model");
            String appliesTo = attributes.get("appliesTo");
            if ( appliesTo != null && !appliesTo.startsWith("/") ) {
                appliesTo = "//" + appliesTo;
            }
            handlerTupleList.add(new PolicyHandlerTuple(sd, sd.getClassName(), intentQName, policyModelClassName, appliesTo));
        }

        return handlerTupleList;
    }
    
    private static QName getQName(String qname) { 
        if (qname == null) {
            return null;
        }
        qname = qname.trim();
        if (qname.startsWith("{")) {
            int h = qname.indexOf('}');
            if (h != -1) {
                return new QName(qname.substring(1, h), qname.substring(h + 1));
            }
        } else {
            int h = qname.indexOf('#');
            if (h != -1) {
                return new QName(qname.substring(0, h), qname.substring(h + 1));
            }
        }
        throw new IllegalArgumentException("Invalid qname: " + qname);
    }

}
