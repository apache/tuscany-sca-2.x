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
package org.apache.tuscany.sca.tools.registryinspector;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.util.PrintUtil;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.tools.registryinspector.inspector.ExtensionPointRegistryInspector;

/**
 * This shows how to test the Calculator service component.
 */
public class RegistryInspectorTestCase extends TestCase {

    private ExtensionPointRegistryInspector eprInspector;
    private SCADomain scaDomain;

    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("registryinspector.composite");
        eprInspector = scaDomain.getService(ExtensionPointRegistryInspector.class, 
                                                 "ExtensionPointRegistryInspectorComponent");
    }

    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testCalculator() throws Exception {
        // Inspect the extension point registry
        System.out.println(eprInspector.eprAsString());
        
        // inspect the model 
        Field domainCompositeField = scaDomain.getClass().getDeclaredField("domainComposite");
        domainCompositeField.setAccessible(true);
        Composite domainComposite = (Composite) domainCompositeField.get(scaDomain);
        
        OutputStream os = new ByteArrayOutputStream();
        PrintUtil printUtil = new PrintUtil(os);
        printUtil.print(domainComposite);
        System.out.println("Assembly \n " + os.toString());
    }
}
