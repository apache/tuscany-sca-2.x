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

package bigbank.demo;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

import bigbank.account.savings.SavingsAccountService;



/**
 * This client program shows how to create an SCA runtime, start it,
 * and locate and invoke a SCA component
 */
public class BigBankServer {

    public static void main(String[] args) throws Exception {
        long timeout = -1L;
        if (args.length > 0) {
            timeout = Long.parseLong(args[0]);
        }
        
        System.out.println("Starting the Sample SCA BigBank server...");
        ClassLoader cl = BigBankServer.class.getClassLoader();
        EmbeddedSCADomain domain = new EmbeddedSCADomain(cl, "http://localhost");

        //Start the domain
        domain.start();

        // Contribute the SCA contribution
        ContributionService contributionService = domain.getContributionService();
        
        URL javaContribURL = getContributionURL(SavingsAccountService.class);
        Contribution bigbankAcContribution = contributionService.contribute("http://bigbank-account", javaContribURL, false);
        for (Composite deployable : bigbankAcContribution.getDeployables()) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.buildComposite(deployable);
        }
        
        URL bigbankContribUrl = getContributionURL(BigBankServer.class);
        Contribution bigbankContribution = contributionService.contribute("http://bigbank", bigbankContribUrl, false);
        for (Composite deployable : bigbankContribution.getDeployables()) {
            domain.getDomainComposite().getIncludes().add(deployable);
            domain.buildComposite(deployable);
        }


        //Start Components from  composite
        for (Composite deployable : bigbankAcContribution.getDeployables()) {
            domain.getCompositeActivator().activate(deployable);
            domain.getCompositeActivator().start(deployable);
        }

        for (Composite deployable : bigbankContribution.getDeployables()) {
            domain.getCompositeActivator().activate(deployable);
            domain.getCompositeActivator().start(deployable);
        }
        
        if (timeout < 0) {
            System.out.println("Press Enter to Exit...");
            System.in.read();
        } else {
            Thread.sleep(timeout);
        }
        
        contributionService.remove("http://bigbank-account");
        contributionService.remove("http://bigbank");

        // Stop Components from  composite
        for (Composite deployable : bigbankContribution.getDeployables()) {
            domain.getCompositeActivator().stop(deployable);
            domain.getCompositeActivator().deactivate(deployable);
        }

        domain.stop();

        domain.close();
        
        
        /*SCADomain domain = SCADomain.newInstance("BigBank.composite");

        if (timeout < 0) {
            System.out.println("Press Enter to Exit...");
            System.in.read();
        } else {
            Thread.sleep(timeout);
        }

        domain.close();*/
        
        System.out.println("Bye");
    }
    
    private static URL getContributionURL(Class<?> cls) throws MalformedURLException {
        String flag = "/" + cls.getName().replace('.', '/') + ".class";
        URL url = cls.getResource(flag);
        String root = url.toExternalForm();
        root = root.substring(0, root.length() - flag.length() + 1);
        if (root.startsWith("jar:") && root.endsWith("!/")) {
            root = root.substring(4, root.length() - 2);
        }
        url = new URL(root);
        return url;
    }

}
