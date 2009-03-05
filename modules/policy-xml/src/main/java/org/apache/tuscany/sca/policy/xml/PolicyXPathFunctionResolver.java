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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

/**
 * A resolver that handles SCA-defined XPath functions
 * 
 * Interface Related Functions
 * <ul>
 * <li>InterfaceRef( InterfaceName )
 * <li>OperationRef( InterfaceName/OperationName )
 * <li>MessageRef( InterfaceName/OperationName/MessageName )
 * </ul>
 * 
 * Intent Related Functions
 * <ul>
 * <li>IntentRefs( IntentList )
 * </ul>
 * 
 * URI Based Function
 * <ul>
 * <li>URIRef( URI )
 * </ul>
 */
public class PolicyXPathFunctionResolver implements XPathFunctionResolver {
    private NamespaceContext namespaceContext;

    public PolicyXPathFunctionResolver(NamespaceContext namespaceContext) {
        super();
        this.namespaceContext = namespaceContext;
    }

    public XPathFunction resolveFunction(QName functionName, int arity) {
        if (functionName == null) {
            throw new NullPointerException("Function name is null");
        }
        if (PolicyXPathFunction.functions.contains(functionName)) {
            if (arity >= 1) {
                // We are relaxing the arity here so that we can pass in the context node
                // by modifying the original xpath so that sca functions take self::node()
                // as the 2nd argument
                return new PolicyXPathFunction(namespaceContext, functionName);
            } else {
                throw new IllegalArgumentException("Invalid number of arguments: " + arity);
            }
        }
        return null;
    }

}
