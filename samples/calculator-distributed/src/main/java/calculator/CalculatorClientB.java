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

import java.io.IOException;
import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.impl.ModelResolverImpl;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.distributed.host.impl.DistributedSCADomain;
import org.apache.tuscany.sca.distributed.node.ComponentRegistry;
import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 */
public class CalculatorClientB {
    public static void main(String[] args) throws Exception {
        ClassLoader cl = CalculatorClientA.class.getClassLoader();
        DistributedSCADomain domain = new DistributedSCADomain(cl,
                                                               "TheDomain",
                                                               "nodeB");
        //Start the domain
        domain.start();
        
        // configure the topology - should be done by file or remotely
        ComponentRegistry componentRegistry = 
            domain.getNodeService(ComponentRegistry.class, "ComponentRegistry");
        
        componentRegistry.setComponentNode("CalculatorServiceComponent", "nodeA");
        componentRegistry.setComponentNode("AddServiceComponent", "nodeB");        
        
        // Contribute the SCA application
        ContributionService contributionService = domain.getContributionService();
        ModelResolver resolver = new ModelResolverImpl(cl);
        URL contributionURL = new URL("file:/C:/simon/Projects/Tuscany/java/java-head/sca/samples/calculator-distributed/target/sample-calculator-distributed.jar");
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
        
        try {
            System.out.println("Runtime sca://mydomain/B started (press enter to shutdown)");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        domain.close();
    }

}
