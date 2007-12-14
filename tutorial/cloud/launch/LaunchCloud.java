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

package launch;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.domain.SCADomainFactory;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.util.SCAContributionUtil;


public class LaunchCloud {
    public static void main(String[] args) throws Exception {

        System.out.println("Starting ...");
        SCADomainFactory domainFactory = SCADomainFactory.newInstance();
        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        SCADomain domain = domainFactory.createSCADomain("http://localhost:9998");
        System.out.println("Domain controller ready for big business !!!");
        
        SCANode catalogsNode = nodeFactory.createSCANode("http://localhost:8200/cloud", "http://localhost:9998");
        URL catalogsContribution = SCAContributionUtil.findContributionFromClass(LaunchCloud.class);
        catalogsNode.addContribution("http://cloud", catalogsContribution);
        catalogsNode.addToDomainLevelComposite(new QName("http://cloud", "catalogs"));
        // the ejb component simply provides the meta data required to locate the 
        // EJB running in Geronimo
        catalogsNode.addToDomainLevelComposite(new QName("http://store", "catalog-jee"));
        catalogsNode.start();
        System.out.println("catalogs.composite ready for big business !!!");
        
        SCANode currencyNode = nodeFactory.createSCANode("http://localhost:8300/cloud", "http://localhost:9998");
        URL currencyContribution = SCAContributionUtil.findContributionFromClass(LaunchCloud.class);
        currencyNode.addContribution("http://cloud", currencyContribution);
        currencyNode.addToDomainLevelComposite(new QName("http://cloud", "currency"));
        currencyNode.start();
        System.out.println("currency.composite ready for big business !!!");    
     
        System.in.read();
        System.out.println("Stopping ...");
        currencyNode.destroy();
        catalogsNode.destroy();
        domain.destroy();
        System.out.println();
        System.exit(0);
    }
    
}
