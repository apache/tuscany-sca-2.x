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

package org.apache.tuscany.sca.binding.sca.axis2;

import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.SCANode;

/**
 * A factory that always returns the same domain object
 * 
 * @version $Rev: 556897 $ $Date: 2007-09-15 12:27:27 +0100 (Sat, 15 Sep 2007) $
 */
public class NodeFactoryImpl implements NodeFactory {
	
	SCANode node = null;
	
	public NodeFactoryImpl(SCANode node){
		this.node = node;
	}
    
    /**
     * Returns the domain object
     * 
     * @return the domain 
     */
    public SCANode getNode(){
    	return node;
    }
    
}
