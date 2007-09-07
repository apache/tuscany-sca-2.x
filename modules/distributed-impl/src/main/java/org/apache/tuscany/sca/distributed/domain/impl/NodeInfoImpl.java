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

package org.apache.tuscany.sca.distributed.domain.impl;

import java.io.Serializable;

import org.apache.tuscany.sca.distributed.domain.NodeInfo;

/**
 * Information relating to an exposed service
 * 
 * @version $Rev: 552343 $ $Date$
 */
public class NodeInfoImpl implements NodeInfo, Serializable {
    
    private String domainUri;
    private String nodeUri;
    private String nodeManagerUrl;
    
    public NodeInfoImpl(String domainUri, String nodeUri){
        this.domainUri = domainUri;
        this.nodeUri = nodeUri;
    }  
    
    public boolean match (String domainUri, String nodeUri){
        return ((this.domainUri.equals(domainUri)) &&
                (this.nodeUri.equals(nodeUri)));
    }
    
    public String getDomainUri(){
        return domainUri;
    } 
    
    public String getNodeUri(){
        return nodeUri;
    }     
    
    public void setNodeManagerUrl(String nodeManagerUrl){
        this.nodeManagerUrl = nodeManagerUrl;
    }
    
    public String getNodeManagerUrl(){
        return nodeManagerUrl;
    }
    
    @Override
    public String toString (){
        return "[" +
               domainUri + " " +
               nodeUri + 
               "]";
    }
    
}
