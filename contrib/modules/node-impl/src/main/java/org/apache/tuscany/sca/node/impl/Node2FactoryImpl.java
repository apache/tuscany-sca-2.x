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
import org.apache.tuscany.sca.node.SCANode2Factory;

/**
 * Default implementation of an SCA node factory.
 * 
 * @version $Rev$ $Date$
 * @deprecated
 */
@Deprecated
public class Node2FactoryImpl extends SCANode2Factory {
    public Node2FactoryImpl() {
    }
    
    @Override
    public SCANode2 createSCANodeFromClassLoader(String compositeURI, ClassLoader classLoader) {
        return new Node2Impl(classLoader, compositeURI);
    }

    @Override
    public SCANode2 createSCANodeFromURL(String configurationURI) {
        return new Node2Impl(configurationURI);
    }
    
    @Override
    public SCANode2 createSCANode(String compositeURI, SCAContribution... contributions) {
        return new Node2Impl(compositeURI, contributions);
    }

    @Override
    public SCANode2 createSCANode(String compositeURI, String compositeContent, SCAContribution... contributions) {
        return new Node2Impl(compositeURI, compositeContent, contributions);
    }

}
