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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.distributed.host.impl.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.DefaultSCADomain;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceContractMapperImpl;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.topology.DefaultTopologyFactory;
import org.apache.tuscany.sca.topology.Node;
import org.apache.tuscany.sca.topology.TopologyFactory;
import org.apache.tuscany.sca.topology.xml.TopologyDocumentProcessor;
import org.apache.tuscany.sca.topology.xml.TopologyProcessor;

/**
 * This is an example node implementation that uses the 
 * distributed runtime to run the calculator sample
 * We need to remove some of the function from here and 
 * put it in the runtime proper 
 */
public class CalculatorNode {
    
    private DistributedSCADomain domain;
    private String domainName;
    private String nodeName;
    
    public CalculatorNode(String domainName, String nodeName) {
        this.domainName = domainName;
        this.nodeName = nodeName;
    }
    

    public SCADomain startDomain()
      throws Exception {
              
        ClassLoader cl = CalculatorNode.class.getClassLoader();
        domain = new DistributedSCADomain(cl,
                                          domainName,
                                          nodeName);
        //Start the domain
        domain.start();
        
        // configure the topology - should be done by file or remotely
        ComponentRegistry componentRegistry = 
            domain.getNodeService(ComponentRegistry.class, "ComponentRegistry");
        
        componentRegistry.setComponentNode("CalculatorServiceComponent", "nodeA");
        componentRegistry.setComponentNode("AddServiceComponent", "nodeB");  
        componentRegistry.setComponentNode("SubtractServiceComponent", "nodeC"); 
        componentRegistry.setComponentNode("MultiplyServiceComponent", "nodeA");
        componentRegistry.setComponentNode("DivideServiceComponent", "nodeA");
        
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
    
    public void stopDomain() {
        //Stop the domain
        domain.close();        
    }

}
