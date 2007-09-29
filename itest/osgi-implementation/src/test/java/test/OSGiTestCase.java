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
package test;

import java.io.File;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import util.OSGiTestUtil;

/**
 * Test case setup base code - it is invoked with different composite files to test 
 * various scenarios.
 */
public abstract class OSGiTestCase extends TestCase {

    private String compositeName;
    private String contributionLocation;
    public SCADomain scaDomain;

    public OSGiTestCase(String compositeName, String contributionLocation) {
        super();
        this.compositeName = compositeName;
        this.contributionLocation = contributionLocation;
        try {
            if (contributionLocation != null) {
                File f = new File("target/classes/" + contributionLocation);
                this.contributionLocation = f.toURL().toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected void setUp() throws Exception {

        OSGiTestUtil.setUpOSGiTestRuntime();

        scaDomain = SCADomain.newInstance("http://localhost", contributionLocation, compositeName);
    }

    protected void tearDown() throws Exception {
        scaDomain.close();

        OSGiTestUtil.shutdownOSGiRuntime();
    }

}
