/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.tests;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.model.scdl.Component;
import org.apache.tuscany.model.scdl.EntryPoint;
import org.apache.tuscany.model.scdl.Module;
import org.apache.tuscany.model.scdl.loader.impl.SCDLXMLReader;

/**
 */
public class SCDLXMLReaderTestCase extends TestCase {

    /**
     *
     */
    public SCDLXMLReaderTestCase() {
        super();
    }

    @SuppressWarnings("unchecked")
    public void testLoader() {

        SCDLXMLReader loader = new SCDLXMLReader();
        Module module = loader.getModule(getClass().getResource("sca.module").toString());
        Assert.assertEquals("tuscany.model.assembly.tests.bigbank.account", module.getName());

        Component foundComponent=null;
        for (Component component : (List<Component>)module.getComponent()) {
            if ("AccountServiceComponent".equals(component.getName())) {
                foundComponent=component;
            }
        }
        Assert.assertTrue(foundComponent!= null);

        EntryPoint foundEntryPoint=null;
        for (EntryPoint entryPoint: (List<EntryPoint>)module.getEntryPoint()) {
            if ("AccountService".equals(entryPoint.getName())) {
                foundEntryPoint=entryPoint;
            }
        }
        Assert.assertTrue(foundEntryPoint!= null);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

}
