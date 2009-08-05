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
package org.apache.tuscany.sca.otest;

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

import client.RuntimeBridge;
import client.TestConfiguration;

/**
 * An implementation of the Runtime Bridge for the Apache Tuscany SCA runtime (version 2.x)
 *
 */
public class TuscanyRuntimeBridge implements RuntimeBridge {

    static final String CONTRIBUTION_LOCATION_PROPKEY = "OASIS_TESTENV_CONTRIBUTION_LOCATION";

    protected NodeFactory launcher;
    protected Node node;
    TestConfiguration testConfiguration = null;

    public TuscanyRuntimeBridge() {

    }

    public TestConfiguration getTestConfiguration() {
        return testConfiguration;
    }

    public void setTestConfiguration(TestConfiguration testConfiguration) {
        this.testConfiguration = testConfiguration;
    }

    public boolean startContribution(String contributionLocation, String[] contributionNames) throws Exception {
        try {
            // Tuscany specific code which starts the contribution(s) holding the test
            launcher = NodeFactory.newInstance();

            Contribution[] contributions = new Contribution[contributionNames.length];
            String[] contributionURIs = getContributionURIs(contributionLocation);
            for (int i = 0; i < contributions.length; i++) {
                contributions[i] = new Contribution(contributionNames[i], contributionURIs[i]);
            } // end for

            node = launcher.createNode(testConfiguration.getComposite(), contributions);
            System.out.println("SCA Node API ClassLoader: " + node.getClass().getClassLoader());
            // Start the node
            node.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        } // end try

        return true;
    } // end method startContribution

    /**
     * Gets the location of the Contributions as URIs
     * @param contributionLocation - a location pattern URI, which contains one or more "%1"
     * substrings, which are substituted with the name of the contribution to get the URI of
     * the contribution
     * @return the contribution locations as an array of Strings
     */
    protected String[] getContributionURIs(String contributionLocation) throws Exception {
        String[] locations;
        locations = testConfiguration.getContributionNames();

        if (locations != null && contributionLocation != null) {

            for (int i = 0; i < locations.length; i++) {
                String aLocation = contributionLocation.replaceAll("%1", locations[i]);

                locations[i] = aLocation;
            } // end for    	  	
        } else {
            if (locations == null) {
                // No contribution specified - throw an Exception
                throw new Exception("Unable to start SCA runtime - no contribution supplied - error");
            } else {
                // No contribution location supplied - throw an Exception
                throw new Exception("Unable to start SCA runtime - no contribution location supplied - error");
            } // end if 
        } // end if

        return locations;
    } // end getContributionURI

    public void stopContribution() {
        if (node != null) {
            node.stop();
            node.destroy();
        } // end if
        if (launcher != null) {
            launcher.destroy();
        } // end if
    } // end method stopContribution

    public String getContributionLocation(Class<?> testClass) {
        return ContributionLocationHelper.getContributionLocation(testConfiguration.getTestClass());
    } // end method getContributionLocation

} // end class TuscanyRuntimeBridge
