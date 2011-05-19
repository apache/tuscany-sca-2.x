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

package org.apache.tuscany.sca.impl;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistryLocator;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.ActiveNodes;

public class RemoteCommand implements Callable<String>, Serializable {
    private static final long serialVersionUID = 1L;

    // all fields MUST be Serializable
    private String domainName;
    private String command;
    private String contributionURI;
    private String compositeURI;

    public RemoteCommand(String domainName, String command, String contributionURI, String compositeURI) {
        this.domainName = domainName;
        this.command = command;
        this.contributionURI = contributionURI;
        this.compositeURI = compositeURI;
    }

    public String call() throws Exception {
        String response;
        try {
            Node node = getNode();

            if ("start".equals(command)) {
                node.startComposite(contributionURI, compositeURI);
                response = "Started.";
            } else if ("stop".equals(command)) {
                node.stopComposite(contributionURI, compositeURI);
                response = "Stopped.";
            } else {
                response = "Unknown command: " + command;
            }
        } catch (Exception e) {
               response = "REMOTE EXCEPTION: " + e.getClass() + ":" + e.getMessage();
        }
        return response;
    }

    private Node getNode() {
        // TODO Several places in Tuscany need to do this type of thing, for example, processing
        // async responses, so we need to design a "proper" way to do it
        
        for (ExtensionPointRegistry xpr : ExtensionPointRegistryLocator.getExtensionPointRegistries()) {
            ActiveNodes activeNodes = xpr.getExtensionPoint(UtilityExtensionPoint.class).getUtility(ActiveNodes.class);
            for (Object o : activeNodes.getActiveNodes()) {
                Node node = (Node)o;
                if (node.getDomainName().equals(domainName)) {
                    return node;
                }
            }
        }
        throw new IllegalStateException("No remote Node found for domain: " + domainName);
    }

}
