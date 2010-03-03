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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    protected Properties expectedErrorMessages;
    
    TestConfiguration testConfiguration = null;

    public TuscanyRuntimeBridge() {
        // read test error mapping
        expectedErrorMessages = new Properties();
        try {
            InputStream propertiesStream = this.getClass().getResourceAsStream("/tuscany-oasis-sca-tests-errors.properties");
            expectedErrorMessages.load(propertiesStream);
        } catch (IOException e) {   
            System.out.println("Unable to read oasis-sca-tests-errors.properties file");
        } 
    }

    public TestConfiguration getTestConfiguration() {
        return testConfiguration;
    }

    public void setTestConfiguration(TestConfiguration testConfiguration) {
        this.testConfiguration = testConfiguration;
    }

	public boolean startContribution(String compositeName,
			String contributionLocation, String[] contributionNames)
			throws Exception {
		//TODO:
		return startContribution(contributionLocation, contributionNames);
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
            // Start the node
            node.start();
            
            // For debugging 
            // print out the composites that have been read in success cases
            // System.out.println(((NodeImpl)node).dumpDomainComposite());
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
                // Looks like bugs in the oasis code that sometimes still uses jars for some
                if (aLocation.endsWith("_POJO.zip") && !aLocation.endsWith("ASM_8005_Java-1.0.zip")) {
                    aLocation = aLocation.substring(0, aLocation.length()-3) + "jar";                	
                }
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
        } // end if
        if (launcher != null) {
            launcher.destroy();
        } // end if
    } // end method stopContribution

    public String getContributionLocation(Class<?> testClass) {
        return ContributionLocationHelper.getContributionLocation(testConfiguration.getTestClass());
    } // end method getContributionLocation
    
    public void checkError(String testName, Throwable ex) throws Throwable { 
              
        String expectedMessage = expectedErrorMessages.getProperty(testName);
        String receivedMessage = ex.getMessage();
        
        if (expectedMessage == null){
            writeMissingMessage(testName, ex);
            fail("Null expected error message for test " + testName + 
                 "Please add message to oasis-sca-tests-errors.properties");
        } // end if
        
        if (receivedMessage == null){
            ex.printStackTrace();
            fail("Null received error message for test " + testName);
        } // end if

        if (expectedMessage.startsWith("*")) {
            // allow using * to ignore a message comparison
            return;
        }
        
        // Deal with the case where the message has variable parts within it
        // marked with the characters ***. Here we tokenize the epected string 
        // and make sure all the individual parts are present in the results string
        String expectedMessageParts[] = expectedMessage.split("\\*\\*\\*");
        
        if (expectedMessageParts.length > 1){
            int foundParts = 0;
            for(int i = 0; i < expectedMessageParts.length; i++){
                if (receivedMessage.indexOf(expectedMessageParts[i]) > -1 ){
                    foundParts++;
                }
            }
            
            if (foundParts == expectedMessageParts.length){
                return;
            }
        }

        
        // Deal with the case where the end of the message is variable (eg contains absolute filenames) 
        // and where the only relevant part is the start of the message - in this case the expected
        // message only contains the stem section which is unchanging...
        if( receivedMessage.length() > expectedMessage.length() ) {
            // Truncate the received message to the length of the expected message
            receivedMessage = receivedMessage.substring(0, expectedMessage.length() );
        } // end if

        if (!expectedMessage.equals(receivedMessage)) {
            writeIncorrectMessage(testName, expectedMessage, receivedMessage);
        }
        
        assertEquals( expectedMessage, receivedMessage );
        
        return;
       
    }

    protected void writeMissingMessage(String testName, Throwable ex) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("target/OTestMissingMsgs.txt", true));
            out.write(testName + "=*");
            out.newLine();
            out.close();
        } catch (IOException e) {
        } 
    }

    protected void writeIncorrectMessage(String testName, String expected, String received) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("target/OTestIncorrectMsgs.txt", true));
            out.write(testName); out.newLine();
            out.write("    " + expected); out.newLine();
            out.write("    " + received); out.newLine();
            out.close();
        } catch (IOException e) {
        } 
    }

} // end class TuscanyRuntimeBridge
