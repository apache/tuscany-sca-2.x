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

package org.apache.tuscany.sca.extensibility.equinox;

import java.util.Dictionary;
import java.util.Enumeration;

import junit.framework.Assert;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Test start/stop the Equinox runtime.
 * 
 * @version $Rev: $ $Date: $
 */
public class EquinoxHostTestCase {
    @Test
    public void testStartThenStop() {
        TestEquinoxHost host = new TestEquinoxHost();
        BundleContext context = host.start();
        Assert.assertNotNull(context);
        for (Bundle b : context.getBundles()) {
            System.out.println(toString(b, false));
        }
        host.stop();
    }

    @Test
    public void testStartTwice() {
        TestEquinoxHost host = new TestEquinoxHost();
        host.start();
        try {
            host.start();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertTrue(IllegalStateException.class.isInstance(e.getCause()));
        } finally {
            host.stop();
        }
    }

    public static String toString(Bundle b, boolean verbose) {
        StringBuffer sb = new StringBuffer();
        sb.append(b.getBundleId()).append(" ").append(b.getSymbolicName());
        int s = b.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append(" UNINSTALLED");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append(" INSTALLED");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append(" RESOLVED");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append(" STARTING");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append(" STOPPING");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append(" ACTIVE");
        }

        sb.append(" ").append(b.getLocation());
        if (verbose) {
            Dictionary<Object, Object> dict = b.getHeaders();
            Enumeration<Object> keys = dict.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                sb.append(" ").append(key).append("=").append(dict.get(key));
            }
        }
        return sb.toString();
    }
}
