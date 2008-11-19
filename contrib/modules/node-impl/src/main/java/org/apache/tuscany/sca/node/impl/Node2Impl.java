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

package org.apache.tuscany.sca.node.impl;

import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode2;

/**
 * Node2 Impl
 */
public class Node2Impl extends NodeImpl implements SCANode2 {

    /**
     * @param configurationURI
     */
    public Node2Impl(String configurationURI) {
        super(configurationURI);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param classLoader
     * @param compositeURI
     */
    public Node2Impl(ClassLoader classLoader, String compositeURI) {
        super(classLoader, compositeURI);
    }

    /**
     * @param compositeURI
     * @param contributions
     */
    public Node2Impl(String compositeURI, SCAContribution[] contributions) {
        super(compositeURI, contributions);
    }

    /**
     * @param compositeURI
     * @param compositeContent
     * @param contributions
     */
    public Node2Impl(String compositeURI, String compositeContent, SCAContribution[] contributions) {
        super(compositeURI, compositeContent, contributions);
    }

}
