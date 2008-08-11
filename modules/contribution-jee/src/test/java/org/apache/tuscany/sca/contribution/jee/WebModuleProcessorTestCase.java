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

package org.apache.tuscany.sca.contribution.jee;

import java.util.HashSet;
import java.util.Set;

import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.WebModule;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Reference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class WebModuleProcessorTestCase {

	WebModule webModule;
	
    @Before
    public void setUp() throws Exception {
    	String jarFilePath = "target/test-classes/ejb-injection-sample.ear";
    	JavaEEModuleHelper jmh = new JavaEEModuleHelper();
    	AppModule appModule = jmh.getMetadataCompleteModules(jarFilePath);
    	webModule = appModule.getWebModules().get(0);
    }
    
    @Test
    public void testWebAppContribution() throws Exception {
    	WebModuleProcessor wmp = new WebModuleProcessor(webModule);
    	
    	ComponentType ct = wmp.getWebAppComponentType();
    	Assert.assertEquals(2, ct.getReferences().size());
    	Set<String> referenceNames = new HashSet<String>();
    	for(Reference r : ct.getReferences()) {
    		referenceNames.add(r.getName());
    	}
    	
    	Assert.assertEquals(2, referenceNames.size());
    	Assert.assertTrue(referenceNames.contains("org.myorg.MyServlet_bank"));
    	Assert.assertTrue(referenceNames.contains("org.myorg.MyServlet_converter"));
    	
    	Assert.assertEquals(0, ct.getServices().size());
    	
    	Composite composite = wmp.getWebAppComposite();
    	Assert.assertEquals(2, composite.getReferences().size());
    }
}
