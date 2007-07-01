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

package calculator;

import java.io.File;
import java.net.URL;


import org.apache.tuscany.sca.assembly.Composite;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.distributed.host.impl.DistributedSCADomainImpl;
import org.apache.tuscany.sca.distributed.node.impl.NodeImpl;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This is an example node implementation that uses the 
 * distributed runtime to run the calculator sample
 * We need to remove some of the function from here and 
 * put it in the runtime proper when we decide how nodes 
 * will be configured remotely. For now the node is hardcoded
 * to run the calculator sample. 
 */
public class CalculatorNode {
    
    private NodeImpl node;
    private DistributedSCADomainImpl domain;
    private String domainName;
    private String nodeName;
    
    public CalculatorNode(String domainName, String nodeName) {
        this.domainName = domainName;
        this.nodeName = nodeName;
    }
    

    public SCADomain start()
      throws Exception {
        ClassLoader cl = CalculatorNodeExe.class.getClassLoader();        
        
        // create the node implementation. The node implementation
        // will read the node configuration and then wait until you
        // contribute assemblies
        node = new NodeImpl("MyRuntime",
                            nodeName,
                            cl);
        
        // start the node to load the configuration
        // and create the intial domains
        node.start();          
        
        // load an application into a domain
        // TODO - how are applications really going to be contributed?
        domain = (DistributedSCADomainImpl)node.getDomain(domainName);
        
        // find the current directory as a URL. This is where our contribution 
        // will come from
        File currentDirectory = new File (".");
        URL contributionURL = new URL("file:/" + currentDirectory.getCanonicalPath() + "/src/main/resources/");        
        
        // Contribute the SCA application
        ContributionService contributionService = domain.getContributionService();
        ModelResolver resolver = new ModelResolverImpl(cl);
        Contribution contribution = contributionService.contribute("http://calculator", 
                                                                   contributionURL, 
                                                                   resolver, 
                                                                   false);
        Composite composite = contribution.getDeployables().get(0);
        
        // add the contributed composite to the domain
        domain.getDomainCompositeHelper().addComposite(composite);  
        
        // activate the domain, i.e. build the composite and
        // create the wires
        domain.getDomainCompositeHelper().activateDomain();
        
        // start the components, i.e. bring any exposed services on line
        domain.getDomainCompositeHelper().startComponents(); 
        
        return domain;
    
    }
    
    public void stop() throws ActivationException {
        //Stop the domains for this node
        node.stop();
    }

}
