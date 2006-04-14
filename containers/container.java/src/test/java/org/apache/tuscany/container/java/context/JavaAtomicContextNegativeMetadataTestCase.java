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
package org.apache.tuscany.container.java.context;

import junit.framework.TestCase;

import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.BadContextPojo;
import org.apache.tuscany.container.java.mock.components.BadNamePojo;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Performs rudimentary negative testing by using malformed metadata on a POJO
 *
 * @version $Rev $Date
 */
public class JavaAtomicContextNegativeMetadataTestCase extends TestCase {

    /**
     * Tests that a pojo with <code>@ComponentName</code> specified on a non-String type generates an error.
     * <p/>
     * <strong>NB:</strong> the test assumes an error with a message containing
     * "@ComponentName" is generated
     */
    public void testBadNameType() throws Exception {
        CompositeContext mc = new CompositeContextImpl();
        mc.setName("mc");
        try {
            MockFactory.createPojoContext("BadNamePojo", BadNamePojo.class, Scope.MODULE, mc);
        } catch (NoSuchMethodException e) {
            if (e.getMessage().indexOf("@ComponentName") < 0) {
                throw e;
            }
        }

    }

    /**
     * Tests that a pojo with <code>@Context</code> specified on a non-ModuleContext type generates an error.
     * <p/>
     * <strong>NB:</strong> the test assumes an error with a message containing
     * "@Context" is generated
     */
    public void testContextType() throws Exception {
        CompositeContext mc = new CompositeContextImpl();
        mc.setName("mc");
        try {
            MockFactory.createPojoContext("BadContextPojo", BadContextPojo.class, Scope.MODULE, mc);
        } catch (NoSuchMethodException e) {
            if (e.getMessage().indexOf("@Context") < 0) {
                throw e;
            }
        }

    }

}
