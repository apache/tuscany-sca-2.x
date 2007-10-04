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

package org.apache.tuscany.sca.domain.impl;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.NodeInfo;

/**
 * Information relating to an exposed service
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class NodeInfoImpl implements NodeInfo, Serializable {
    
    static final long serialVersionUID = 7669181086005969428L;    
    
    private String nodeURI;
    private String nodeURL;
    private String contributionURI;
    private URL contributionURL;
    private List<QName> compositeNames = new ArrayList<QName>();
    
    public NodeInfoImpl(String nodeURI){
        this.nodeURI = nodeURI;
    }  
    
    public boolean match (String nodeURI){
        return (this.nodeURI.equals(nodeURI));
    }
    
    public String getNodeURI(){
        return nodeURI;
    } 
    
    public String getNodeURL(){
        return nodeURL;
    }     
    
    public void setNodeURL(String nodeURL){
        this.nodeURL = nodeURL;
    }
    
    public String getContributionURI(){
        return contributionURI;
    }
    
    public void setContributionURI(String contributionURI){
        this.contributionURI = contributionURI;
    }
    
    public URL getContributionURL(){
        return contributionURL;
    }
    
    public void setContributionURL(URL contributionURL){
        this.contributionURL = contributionURL;
    }    
    
    public List<QName> getCompositeNames(){
        return compositeNames;
    }
    
    public void addCompositeName(QName compositeName){
        this.compositeNames.add(compositeName);
    }
    
    @Override
    public String toString (){
        return "[" +
               nodeURI + " " +
               nodeURL + " " +
               contributionURI + " " +
               compositeNames.toString() + " " +
               "]";
    }
    
}
