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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

/**
 * The SCA-defined XPath function
 */
public class PolicyXPathFunction implements XPathFunction {
    static final QName InterfaceRef = new QName(PolicyConstants.SCA11_NS, "InterfaceRef");
    static final QName OperationRef = new QName(PolicyConstants.SCA11_NS, "OperationRef");
    static final QName MessageRef = new QName(PolicyConstants.SCA11_NS, "MessageRef");
    static final QName IntentRefs = new QName(PolicyConstants.SCA11_NS, "IntentRefs");
    static final QName URIRef = new QName(PolicyConstants.SCA11_NS, "URIRef");

    static final Set<QName> functions =
        new HashSet<QName>(Arrays.asList(InterfaceRef, OperationRef, MessageRef, IntentRefs, URIRef));

    private NamespaceContext namespaceContext;
    private final QName functionName;

    public PolicyXPathFunction(NamespaceContext namespaceContext, QName functionName) {
        super();
        this.namespaceContext = namespaceContext;
        this.functionName = functionName;
    }

    public Object evaluate(List args) throws XPathFunctionException {
        // FIXME: [rfeng] To be implemented
        String arg = (String)args.get(0);
        System.out.println(functionName + "(" + arg + ")");
        return Boolean.FALSE;
    }

}
