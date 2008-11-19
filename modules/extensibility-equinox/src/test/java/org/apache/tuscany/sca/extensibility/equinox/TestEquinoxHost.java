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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.core.runtime.adaptor.LocationManager;
import org.osgi.framework.BundleContext;

/**
 * A test host that starts/stops Equinox.
 *
 * @version $Rev: $ $Date: $
 */
public class TestEquinoxHost  {

    private final static String systemPackages =
        "org.osgi.framework; version=1.3.0," + "org.osgi.service.packageadmin; version=1.2.0, "
            + "org.osgi.service.startlevel; version=1.0.0, "
            + "org.osgi.service.url; version=1.0.0, "
            + "org.osgi.util.tracker; version=1.3.2, "
            + "javax.xml, "
            + "javax.xml.datatype, "
            + "javax.xml.namespace, "
            + "javax.xml.parsers, "
            + "javax.xml.transform, "
            + "javax.xml.transform.dom, "
            + "javax.xml.transform.sax, "
            + "javax.xml.transform.stream, "
            + "javax.xml.validation, "
            + "javax.xml.xpath, "
            // Force the classes to be imported from the system bundle
            + "javax.xml.stream, "
            + "javax.xml.stream.util, "
            + "javax.sql,"
            + "org.w3c.dom, "
            + "org.xml.sax, "
            + "org.xml.sax.ext, "
            + "org.xml.sax.helpers, "
            + "javax.security.auth, "
            + "javax.security.cert, "
            + "javax.security.auth.login, "
            + "javax.security.auth.callback, "
            + "javax.naming, "
            + "javax.naming.spi, "
            + "javax.naming.directory, "
            + "javax.management, "
            + "javax.imageio, "
            + "sun.misc, "
            + "javax.net, "
            + "javax.net.ssl, "
            + "javax.crypto, "
            + "javax.rmi, "
            + "javax.transaction, "
            + "javax.transaction.xa";

    public BundleContext start() {
        try {
            Map<Object, Object> props = new HashMap<Object, Object>();
            props.put("org.osgi.framework.system.packages", systemPackages);
            props.put(EclipseStarter.PROP_CLEAN, "true");
            props.put(LocationManager.PROP_INSTANCE_AREA, new File("target/workspace").toURI().toString());
            props.put(LocationManager.PROP_INSTALL_AREA, new File("target/eclipse").toURI().toString());
            EclipseStarter.setInitialProperties(props);
            BundleContext context = EclipseStarter.startup(new String[]{}, null);
            return context;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void stop() {
        try {
            EclipseStarter.shutdown();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
