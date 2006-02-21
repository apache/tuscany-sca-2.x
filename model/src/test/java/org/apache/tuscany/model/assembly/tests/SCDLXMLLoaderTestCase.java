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

import org.apache.tuscany.model.assembly.loader.impl.SCDLXMLLoader;
import org.apache.tuscany.model.assembly.scdl.Component;
import org.apache.tuscany.model.assembly.scdl.EntryPoint;
import org.apache.tuscany.model.assembly.scdl.Module;

/**
 */
public class SCDLXMLLoaderTestCase extends TestCase {

    /**
     *
     */
    public SCDLXMLLoaderTestCase() {
        super();
    }

    public void testLoader() {

        SCDLXMLLoader loader = new SCDLXMLLoader();
        Module module = loader.getModule(getClass().getResource("sca.module").toString());
        Assert.assertTrue(module.getName().equals("tuscany.model.assembly.tests.bigbank.account"));

        Component foundComponent=null;
        for (Component component : (List<Component>)module.getComponent()) {
            if (component.getName().equals("AccountServiceComponent"))
                foundComponent=component;
        }
        Assert.assertTrue(foundComponent!= null);

        EntryPoint foundEntryPoint=null;
        for (EntryPoint entryPoint: (List<EntryPoint>)module.getEntryPoint()) {
            if (entryPoint.getName().equals("AccountService"))
                foundEntryPoint=entryPoint;
        }
        Assert.assertTrue(foundEntryPoint!= null);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

}
