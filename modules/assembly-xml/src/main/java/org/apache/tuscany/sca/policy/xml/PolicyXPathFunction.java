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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The SCA-defined XPath function
 */
public class PolicyXPathFunction implements XPathFunction {
    private static Logger logger = Logger.getLogger(PolicyXPathFunction.class.getName());

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

    private Node getContextNode(List args) {
        if (args.size() >= 2) {
            NodeList nodeList = (NodeList)args.get(1);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0);
            }
        }
        return null;
    }

    public Object evaluate(List args) throws XPathFunctionException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(functionName + "(" + args + ")");
        }

        String arg = (String)args.get(0);
        Node node = getContextNode(args);
        if (InterfaceRef.equals(functionName)) {
            return evaluateInterface(arg, node);
        } else if (OperationRef.equals(functionName)) {
            String[] params = arg.split("/");
            if (params.length != 2) {
                throw new IllegalArgumentException("Invalid argument: " + arg);
            }
            String interfaceName = params[0];
            String operationName = params[1];
            return evaluateOperation(interfaceName, operationName, node);
        } else if (MessageRef.equals(functionName)) {
            String[] params = arg.split("/");
            if (params.length != 3) {
                throw new IllegalArgumentException("Invalid argument: " + arg);
            }
            String interfaceName = params[0];
            String operationName = params[1];
            String messageName = params[2];
            return evaluateMessage(interfaceName, operationName, messageName, node);
        } else if (URIRef.equals(functionName)) {
            return evaluateURI(arg, node);
        } else if (IntentRefs.equals(functionName)) {
            String[] intents = arg.split("(\\s)+");
            return evaluateIntents(intents, node);
        } else {
            return Boolean.FALSE;
        }
    }

    private Boolean evaluateInterface(String interfaceName, Node node) {
        return Boolean.FALSE;
    }

    private Boolean evaluateOperation(String interfaceName, String operationName, Node node) {
        return Boolean.FALSE;
    }

    private Boolean evaluateMessage(String interfaceName, String operationName, String messageName, Node node) {
        return Boolean.FALSE;
    }

    private Boolean evaluateURI(String uri, Node node) {
        return Boolean.FALSE;
    }

    private Boolean evaluateIntents(String[] intents, Node node) {
        return Boolean.FALSE;
    }

    private static Pattern FUNCTION;
    static {
        String functionPattern = "(URIRef|InterfaceRef|OperationRef|MessageRef|IntentRefs)\\s*\\((.*)\\)";
        FUNCTION = Pattern.compile(functionPattern);
    }

    public static String normalize(String attachTo) {
        Matcher matcher = FUNCTION.matcher(attachTo);
        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                String function = matcher.group(1);
                String args = matcher.group(2);
                String replacement = null;
                if (args.trim().length() > 0) {
                    replacement = function + "(" + args + "," + "self::node())";
                } else {
                    replacement = function + "(self::node())";
                }
                matcher.appendReplacement(sb, replacement);
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return attachTo;
    }

}
