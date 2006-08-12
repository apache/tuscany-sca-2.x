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
package org.apache.tuscany.equinox;

import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.internal.core.OSGi;
import org.eclipse.osgi.framework.internal.defaultadaptor.DefaultAdaptor;
import org.osgi.framework.Bundle;
import junit.framework.TestCase;

/**
 * @version $$Rev$$ $$Date$$
 */
public class EquinoxStartTestCase extends TestCase {

    public void testStart() throws Exception {
        FrameworkAdaptor adaptor = new DefaultAdaptor(new String[]{});
        OSGi osgi = new OSGi(adaptor);
        osgi.launch();
//        FileInputStream stream = new FileInputStream("/Users/jmarino/workspace/tuscany/tuscany/tuscany/sandbox/jboynes/sca/runtime/equinox/src/test/resources/http.jar");
//        osgi.getBundleContext().installBundle("foo", stream);
        for (Bundle bundle : osgi.getBundleContext().getBundles()) {

        }
    }
}
